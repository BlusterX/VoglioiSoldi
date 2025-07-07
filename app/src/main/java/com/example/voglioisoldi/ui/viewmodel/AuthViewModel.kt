package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.User
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.repositories.hashPassword
import com.example.voglioisoldi.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val registeredUserId: Int? = null,
    val registrationSuccess: Boolean = false
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
): ViewModel() {


    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onUsernameChanged(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername)
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun onNameChanged(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun onSurnameChanged(newSurname: String) {
        _uiState.value = _uiState.value.copy(surname = newSurname)
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun login() {
        viewModelScope.launch {
            val username = _uiState.value.username.trim()
            val password = _uiState.value.password

            //Controlla che i campi non siano vuoti
            if (username.isEmpty() || password.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Inserisci sia username che password",
                    isLoading = false
                )
                return@launch
            }

            //Controlla che i campi abbiano una lunghez minima
            if (username.length < 3) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Lo username deve avere almeno 3 caratteri",
                    isLoading = false
                )
                return@launch
            }
            //Almeno 6 caratteri per le pass
            if (password.length < 6) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "La password deve avere almeno 6 caratteri",
                    isLoading = false
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

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

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val state = _uiState.value
            if (
                state.name.isBlank() || state.surname.isBlank() ||
                state.username.isBlank() || state.email.isBlank() || state.password.isBlank()
            ) {
                _uiState.value = state.copy(errorMessage = "Compila tutti i campi", isLoading = false)
                return@launch
            }
            if (userRepository.userExists(state.username, state.email)) {
                _uiState.value = state.copy(errorMessage = "Username o email già in uso", isLoading = false)
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
            _uiState.value = state.copy(
                registrationSuccess = true,
                errorMessage = null,
                isLoading = false,
                registeredUserId = registeredUser?.id
            )
        }
    }

    fun resetRegistrationFlag() {
        _uiState.value = _uiState.value.copy(registrationSuccess = false)
    }

    fun resetLoginFlag() {
        _uiState.value = _uiState.value.copy(loginSuccess = false)
    }
}
