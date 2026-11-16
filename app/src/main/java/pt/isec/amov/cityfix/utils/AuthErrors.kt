package pt.isec.amov.cityfix.utils

import androidx.annotation.StringRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import pt.isec.amov.cityfix.R

@StringRes
fun authErrorMessage(error: Throwable): Int = when (error) {
    is FirebaseAuthWeakPasswordException -> R.string.error_password_weak
    is FirebaseAuthUserCollisionException -> R.string.error_email_taken
    is FirebaseAuthInvalidUserException,
    is FirebaseAuthInvalidCredentialsException -> R.string.error_credentials
    is FirebaseAuthRecentLoginRequiredException -> R.string.error_recent_login
    is FirebaseNetworkException -> R.string.error_network
    is FirebaseTooManyRequestsException -> R.string.error_too_many_requests
    else -> R.string.error_auth_generic
}
