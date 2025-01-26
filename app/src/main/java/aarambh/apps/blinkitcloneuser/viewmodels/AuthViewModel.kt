package aarambh.apps.blinkitcloneuser.viewmodels

import aarambh.apps.blinkitcloneuser.Utils
import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel: ViewModel() {

    private val _verificationId = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false)
    private val _isSignedInSuccessfully = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    val isSignedInSuccessfully = _isSignedInSuccessfully
    val otpSent = _otpSent
    val error = _error

    fun sendOTP(userNumber: String,activity: Activity){

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {
                _otpSent.value = false
                when (e) {
                    is FirebaseTooManyRequestsException -> {
                        _error.value = "Too many requests. Please try again later."
                    }
                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        _error.value = "reCAPTCHA verification failed. Please try again."
                    }
                    else -> {
                        _error.value = e.message ?: "Verification failed. Please try again."
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationId.value = verificationId
                _otpSent.value = true
            }
        }
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun signInWithPhoneAuthCredential(otp: String,userNumber: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        Utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    _isSignedInSuccessfully.value = true
                    _error.value = null
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        _error.value = "Invalid verification code. Please try again."
                    } else {
                        _error.value = task.exception?.message ?: "Authentication failed. Please try again."
                    }
                }
            }
    }

}