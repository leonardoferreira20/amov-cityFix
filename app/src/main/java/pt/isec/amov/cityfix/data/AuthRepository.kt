package pt.isec.amov.cityfix.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.TotpMultiFactorGenerator
import com.google.firebase.auth.TotpSecret
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

sealed interface SignInResult {
    data class Success(val userId: String) : SignInResult
    data class MfaRequired(val resolver: MultiFactorResolver) : SignInResult
}

class AuthRepository {
    private val auth = Firebase.auth

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isEmailVerified: Boolean
        get() = auth.currentUser?.isEmailVerified == true

    val hasSecondFactor: Boolean
        get() = auth.currentUser?.multiFactor?.enrolledFactors?.isNotEmpty() == true

    suspend fun signIn(email: String, password: String): SignInResult = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        SignInResult.Success(result.user!!.uid)
    } catch (e: FirebaseAuthMultiFactorException) {
        SignInResult.MfaRequired(e.resolver)
    }

    suspend fun resolveSecondFactor(resolver: MultiFactorResolver, code: String): String {
        val hint = resolver.hints.first { it.factorId == TotpMultiFactorGenerator.FACTOR_ID }
        val assertion = TotpMultiFactorGenerator.getAssertionForSignIn(hint.uid, code)
        return resolver.resolveSignIn(assertion).await().user!!.uid
    }

    suspend fun register(email: String, password: String): String {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user!!
        user.sendEmailVerification()
        return user.uid
    }

    suspend fun deleteCurrentUser() {
        auth.currentUser?.delete()?.await()
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()?.await()
    }

    suspend fun reloadUser() {
        auth.currentUser?.reload()?.await()
    }

    suspend fun startTotpEnrollment(): TotpSecret {
        val session = auth.currentUser!!.multiFactor.session.await()
        return TotpMultiFactorGenerator.generateSecret(session).await()
    }

    suspend fun finishTotpEnrollment(secret: TotpSecret, code: String) {
        val assertion = TotpMultiFactorGenerator.getAssertionForEnrollment(secret, code)
        auth.currentUser!!.multiFactor.enroll(assertion, "TOTP").await()
    }

    fun signOut() {
        auth.signOut()
    }
}
