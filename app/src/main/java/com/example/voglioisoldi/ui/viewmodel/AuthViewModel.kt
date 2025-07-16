package com.example.voglioisoldi.ui.viewmodel

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.R
import com.example.voglioisoldi.data.database.entities.Notification
import com.example.voglioisoldi.data.database.entities.User
import com.example.voglioisoldi.data.repositories.NotificationRepository
import com.example.voglioisoldi.data.repositories.SettingsRepository
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.repositories.hashPassword
import com.example.voglioisoldi.data.session.SessionManager
import com.example.voglioisoldi.ui.util.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val isNameValid: Boolean = true,
    val isSurnameValid: Boolean = true,
    val isUsernameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isConfirmPasswordValid: Boolean = true,
    val nameError: String? = null,
    val surnameError: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registeredUserId: Int? = null,
    val registrationSuccess: Boolean = false,
    val biometricAvailable: Boolean = false,
    val biometricUsername: String? = null
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val settingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        loadBiometricSettings()
    }

    private fun loadBiometricSettings() {
        viewModelScope.launch {
            val username = settingsRepository.getBiometricUsername().first()
            _uiState.value = _uiState.value.copy(biometricUsername = username)
        }
    }

    private suspend fun sendRegistrationNotification(user: User, context: Context) {
        val notificationTitle = "Benvenuto in VoglioISoldi!"
        val notificationMessage = "Ciao ${user.name}, la tua registrazione è stata completata con successo!"
        val timestamp = System.currentTimeMillis()

        // Invia notifica sul dispositivo
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "registration_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify((timestamp + Random.nextInt(1000)).toInt(), notification)

        // Salva notifica nel database
        val dbNotification = Notification(
            title = notificationTitle,
            message = notificationMessage,
            timestamp = timestamp,
            userId = user.id,
            isRead = false
        )
        notificationRepository.insertNotification(dbNotification)
    }

    fun setBiometricAvailable(available: Boolean) {
        _uiState.value = _uiState.value.copy(biometricAvailable = available)
    }

    fun onUsernameChanged(newUsername: String) {
        val input = newUsername.trim()
        val (isValid, error) = ValidationUtils.validateUsername(input)
        _uiState.value = _uiState.value.copy(
            username = newUsername,
            isUsernameValid = isValid,
            usernameError = error
        )
    }

    fun onPasswordChanged(newPassword: String) {
        val (isValid, error) = ValidationUtils.validatePassword(newPassword)
        _uiState.value = _uiState.value.copy(
            password = newPassword,
            isPasswordValid = isValid,
            passwordError = error
        )

        if (_uiState.value.confirmPassword.isNotEmpty()) {
            onConfirmPasswordChanged(_uiState.value.confirmPassword)
        }
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        val (isValid, error) = ValidationUtils.validateConfirmPassword(newConfirmPassword, _uiState.value.password)
        _uiState.value = _uiState.value.copy(
            confirmPassword = newConfirmPassword,
            isConfirmPasswordValid = isValid,
            confirmPasswordError = error
        )
    }

    fun onNameChanged(newName: String) {
        val input = newName.trim()
        val (isValid, error) = ValidationUtils.validateName(input)
        _uiState.value = _uiState.value.copy(
            name = newName,
            isNameValid = isValid,
            nameError = error
        )
    }

    fun onSurnameChanged(newSurname: String) {
        val input = newSurname.trim()
        val (isValid, error) = ValidationUtils.validateSurname(input)
        _uiState.value = _uiState.value.copy(
            surname = newSurname,
            isSurnameValid = isValid,
            surnameError = error
        )
    }

    fun onEmailChanged(newEmail: String) {
        val input = newEmail.trim()
        val (isValid, error) = ValidationUtils.validateEmail(input)
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            isEmailValid = isValid,
            emailError = error
        )
    }

    private fun isFormValid(): Boolean {
        val state = _uiState.value
        return state.isNameValid && state.isSurnameValid &&
                state.isUsernameValid && state.isEmailValid &&
                state.isPasswordValid &&
                state.name.isNotBlank() && state.surname.isNotBlank() &&
                state.username.isNotBlank() && state.email.isNotBlank() &&
                state.password.isNotBlank()
    }

    fun login() {
        viewModelScope.launch {
            val username = _uiState.value.username.trim()
            val password = _uiState.value.password

            if (username.isEmpty() || password.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Inserisci le credenziali di accesso",
                    isLoading = false
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val user = userRepository.getUser(username, password)
                if (user != null) {
                    sessionManager.saveLoggedInUser(username)
                    _uiState.value = _uiState.value.copy(
                        loginSuccess = true,
                        errorMessage = null,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Username o password non validi",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Errore di sistema, riprova",
                    isLoading = false
                )
            }
        }
    }

    fun biometricLogin() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val biometricUsername = settingsRepository.getBiometricUsername().first()
                if (biometricUsername == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Nessun utente registrato per l'accesso biometrico",
                        isLoading = false
                    )
                    return@launch
                }

                val user = userRepository.getUserByUsername(biometricUsername)
                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Utente non trovato",
                        isLoading = false
                    )
                    return@launch
                }

                sessionManager.saveLoggedInUser(biometricUsername)
                _uiState.value = _uiState.value.copy(
                    loginSuccess = true,
                    errorMessage = null,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Errore di sistema, riprova",
                    isLoading = false
                )
            }
        }
    }

    fun onBiometricError(error: String) {
        _uiState.value = _uiState.value.copy(
            errorMessage = error,
            isLoading = false
        )
    }

    fun register(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val state = _uiState.value

            if (!isFormValid()) {
                _uiState.value = state.copy(
                    errorMessage = "Campi non compilati correttamente",
                    isLoading = false
                )
                return@launch
            }

            try {
                if (userRepository.userExists(state.username, state.email)) {
                    _uiState.value = state.copy(
                        errorMessage = "Username o email già in uso",
                        isLoading = false
                    )
                    return@launch
                }

                val user = User(
                    username = state.username,
                    passwordHash = hashPassword(state.password),
                    name = state.name,
                    surname = state.surname,
                    email = state.email
                )

                userRepository.insertUser(user)
                sessionManager.saveLoggedInUser(state.username)
                val registeredUser = userRepository.getUserByUsername(state.username)

                if (registeredUser != null) {
                    sendRegistrationNotification(registeredUser, context)

                    _uiState.value = state.copy(
                        registrationSuccess = true,
                        errorMessage = null,
                        isLoading = false,
                        registeredUserId = registeredUser.id
                    )
                } else {
                    _uiState.value = state.copy(
                        errorMessage = "Errore durante il recupero dell'utente registrato",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    errorMessage = "Errore durante la registrazione dell'utente",
                    isLoading = false
                )
            }
        }
    }

    fun resetRegistrationFlag() {
        _uiState.value = _uiState.value.copy(registrationSuccess = false)
    }

    fun resetLoginFlag() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}