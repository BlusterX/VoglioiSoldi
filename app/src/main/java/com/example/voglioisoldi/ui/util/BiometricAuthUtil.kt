package com.example.voglioisoldi.ui.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricStatus {
    AVAILABLE,
    NOT_ENROLLED,
    NOT_AVAILABLE
}

class BiometricAuthUtil(
    private val context: Context
) {
    fun getBiometricStatus(): BiometricStatus {
        return when (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            else -> BiometricStatus.NOT_AVAILABLE
        }
    }

    fun openBiometricSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Intent(Settings.ACTION_BIOMETRIC_ENROLL) // Disponibile solo per API 30+
            else Intent(Settings.ACTION_SECURITY_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback alle impostazioni di sicurezza generali
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                context.startActivity(intent)
            } catch (e2: Exception) {
                // Fallback alle impostazioni generali del dispositivo
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            }
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        // Oggetto per la gestione della finestra di autenticazione biometrica
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_CANCELED -> {
                        // Non mostrare messaggio di errore in caso di annullamento manuale
                        return
                    }
                    else -> {
                        onError(errString.toString())
                    }
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Autenticazione fallita")
            }
        })

        // Dialog per l'accesso con impronta
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Accesso con impronta digitale")
            .setSubtitle("Usa la tua impronta per accedere")
            .setNegativeButtonText("Annulla")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}