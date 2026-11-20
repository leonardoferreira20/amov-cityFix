package pt.isec.amov.cityfix.ui.components

import pt.isec.amov.cityfix.ui.theme.CityFixTheme
import pt.isec.amov.cityfix.data.model.Message
import pt.isec.amov.cityfix.data.model.InfoRequest
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pt.isec.amov.cityfix.R
import pt.isec.amov.cityfix.data.model.UserRole
import pt.isec.amov.cityfix.viewmodel.InformationRequestItem

@Composable
fun InformationRequests(
    requests: List<InformationRequestItem>,
    canRequest: Boolean,
    onRequest: () -> Unit,
    onReply: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (canRequest) {
            Button(onClick = onRequest) {
                Text(stringResource(R.string.information_request_action))
            }
        }

        if (requests.isEmpty()) {
            Text(stringResource(R.string.information_requests_empty))
        }

        requests.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val requesterLabel = if (item.request.requesterRole == UserRole.OPERATOR) {
                        R.string.information_request_from_operator
                    } else {
                        R.string.information_request_from_citizen
                    }
                    Text(stringResource(requesterLabel), style = MaterialTheme.typography.titleSmall)
                    Text(item.request.text)

                    val reply = item.reply
                    if (reply == null) {
                        Text(
                            stringResource(R.string.information_request_waiting),
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(stringResource(R.string.information_reply_text, reply.text))
                    }

                    if (item.canReply) {
                        TextButton(onClick = { onReply(item.request.id) }) {
                            Text(stringResource(R.string.information_reply_action))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationRequestsPreview() {
    CityFixTheme {
        InformationRequests(
            requests = listOf(
                InformationRequestItem(
                    request = InfoRequest(
                        id = "request-1",
                        requestedBy = "operator-1",
                        requesterRole = UserRole.OPERATOR,
                        text = "Pode enviar uma foto mais próxima?"
                    ),
                    reply = Message(
                        infoRequestId = "request-1",
                        senderId = "citizen-1",
                        senderRole = UserRole.CITIZEN,
                        text = "Enviei agora uma foto nova."
                    ),
                    canReply = false
                ),
                InformationRequestItem(
                    request = InfoRequest(
                        id = "request-2",
                        requestedBy = "citizen-1",
                        requesterRole = UserRole.CITIZEN,
                        text = "Quando começa a reparação?"
                    ),
                    reply = null,
                    canReply = true
                )
            ),
            canRequest = true,
            onRequest = {},
            onReply = {}
        )
    }
}
