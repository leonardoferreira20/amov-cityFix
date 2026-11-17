const { setGlobalOptions } = require("firebase-functions/v2");
const { onDocumentUpdated } = require("firebase-functions/v2/firestore");
const { onSchedule } = require("firebase-functions/v2/scheduler");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const { getMessaging } = require("firebase-admin/messaging");

const REGION = "europe-west1";
const DAY_MS = 24 * 60 * 60 * 1000;
const REOPEN_WINDOW_MS = 5 * DAY_MS;
const OPERATORS_TOPIC = "operators";
const SYSTEM_USER_ID = "system";

initializeApp();
setGlobalOptions({ region: REGION });
const db = getFirestore();

exports.notifyIncidentChanges = onDocumentUpdated("incidents/{incidentId}", async (event) => {
  const before = event.data.before.data();
  const after = event.data.after.data();
  const incidentId = event.params.incidentId;
  const sends = [];

  const newRequests = addedItems(before.infoRequests, after.infoRequests);
  const operatorAsked = newRequests.some((request) => request.requesterRole === "OPERATOR");
  const lastChange = (after.history || []).at(-1);

  if (before.status !== after.status && !operatorAsked && lastChange?.changedBy !== after.authorId) {
    sends.push(notifyUser(after.authorId, "STATUS_CHANGED", incidentId, after, { status: after.status }));
  }

  newRequests.forEach((request) => {
    sends.push(
      request.requesterRole === "OPERATOR"
        ? notifyUser(after.authorId, "INFO_REQUESTED", incidentId, after)
        : notifyTopic(OPERATORS_TOPIC, "INFO_REQUESTED", incidentId, after)
    );
  });

  addedItems(before.messages, after.messages).forEach((message) => {
    if (message.senderRole === "OPERATOR") {
      sends.push(notifyUser(after.authorId, "MESSAGE", incidentId, after));
    } else {
      const request = (after.infoRequests || []).find((item) => item.id === message.infoRequestId);
      if (request) sends.push(notifyUser(request.requestedBy, "MESSAGE", incidentId, after));
    }
  });

  addedItems(before.proximityAlerts, after.proximityAlerts).forEach(() => {
    sends.push(notifyUser(after.authorId, "PROXIMITY", incidentId, after));
  });

  await Promise.all(sends);
});

exports.closeResolvedIncidents = onSchedule("every 60 minutes", async () => {
  const now = Date.now();
  const resolved = await db.collection("incidents").where("status", "==", "RESOLVED").get();

  await Promise.all(resolved.docs.map(async (doc) => {
    const incident = doc.data();
    if (!incident.resolvedAt) return;
    const deadline = incident.resolvedAt + REOPEN_WINDOW_MS;

    if (now > deadline) {
      await closeIncident(doc.ref, deadline);
    } else if (now >= deadline - DAY_MS && (incident.closingWarningSentAt || 0) < incident.resolvedAt) {
      await doc.ref.update({ closingWarningSentAt: now });
      await notifyUser(incident.authorId, "CLOSING_SOON", doc.id, incident);
    }
  }));
});

function addedItems(before, after) {
  return (after || []).slice((before || []).length);
}

async function closeIncident(ref, closedAt) {
  await db.runTransaction(async (transaction) => {
    const incident = (await transaction.get(ref)).data();
    if (incident.status !== "RESOLVED") return;
    transaction.update(ref, {
      status: "CLOSED",
      closedAt,
      history: [
        ...(incident.history || []),
        { fromStatus: "RESOLVED", toStatus: "CLOSED", timestamp: closedAt, changedBy: SYSTEM_USER_ID, note: null },
      ],
    });
  });
}

async function notifyUser(userId, type, incidentId, incident, extra = {}) {
  if (!userId) return;
  const tokenDoc = await db.collection("deviceTokens").doc(userId).get();
  const token = tokenDoc.get("token");
  if (!token) return;
  try {
    await getMessaging().send(buildMessage({ token }, type, incidentId, incident, extra));
  } catch (error) {
    if (error.code === "messaging/registration-token-not-registered") await tokenDoc.ref.delete();
  }
}

async function notifyTopic(topic, type, incidentId, incident) {
  await getMessaging().send(buildMessage({ topic }, type, incidentId, incident)).catch(() => {});
}

function buildMessage(target, type, incidentId, incident, extra = {}) {
  return {
    ...target,
    data: { type, incidentId, categoryName: incident.categoryName || "", ...extra },
    android: { priority: "high" },
  };
}
