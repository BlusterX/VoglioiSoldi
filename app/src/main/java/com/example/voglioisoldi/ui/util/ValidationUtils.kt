package com.example.voglioisoldi.ui.util

import android.util.Patterns

object ValidationUtils {
    private const val ERROR_REQUIRED_FIELD = "%s è obbligatorio"
    private const val ERROR_MIN_LENGTH = "%s deve contenere almeno %d caratteri"
    private const val ERROR_MAX_LENGTH = "%s può contenere al massimo %d caratteri"
    private const val REGEX_FIRST_CHAR_UPPERCASE = "^\\p{Lu}.*$"
    private const val REGEX_NAME_FORMAT = "^\\p{Lu}[\\p{L}'-]*(?:\\s\\p{Lu}[\\p{L}'-]*)*$"
    private const val REGEX_SPECIAL_CHARS = ".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"

    fun validateName(name: String): Pair<Boolean, String?> {
        val fieldName = "Il nome"
        return when {
            name.isEmpty() -> false to ERROR_REQUIRED_FIELD.format(fieldName)
            name.length < 2 -> false to ERROR_MIN_LENGTH.format(fieldName, 2)
            name.length > 50 -> false to ERROR_MAX_LENGTH.format(fieldName, 50)
            !name.matches(Regex(REGEX_FIRST_CHAR_UPPERCASE)) ->
                false to "Il nome deve inizare con una maiuscola"
            !name.matches(Regex(REGEX_NAME_FORMAT)) ->
                false to "Il nome può contenere solo lettere, spazi, trattini e apostrofi"
            else -> true to null
        }
    }

    fun validateSurname(surname: String): Pair<Boolean, String?> {
        val fieldName = "Il cognome"
        return when {
            surname.isEmpty() -> false to ERROR_REQUIRED_FIELD.format(fieldName)
            surname.length < 2 -> false to ERROR_MIN_LENGTH.format(fieldName, 2)
            surname.length > 50 -> false to ERROR_MAX_LENGTH.format(fieldName, 50)
            !surname.matches(Regex(REGEX_FIRST_CHAR_UPPERCASE)) ->
                false to "Il cognome deve inizare con una maiuscola"
            !surname.matches(Regex(REGEX_NAME_FORMAT)) ->
                false to "Il cognome deve iniziare può contenere solo lettere, trattini, spazi e apostrofi"
            else -> true to null
        }
    }

    fun validateUsername(username: String): Pair<Boolean, String?> {
        val fieldName = "Lo username"
        return when {
            username.isEmpty() -> false to ERROR_REQUIRED_FIELD.format(fieldName)
            username.length < 3 -> false to ERROR_MIN_LENGTH.format(fieldName, 3)
            username.length > 20 -> false to ERROR_MAX_LENGTH.format(fieldName, 20)
            !username.matches(Regex("^[a-zA-Z0-9_.-]+$")) ->
                false to "L'username non può contenere spazi o caratteri speciali"
            username.startsWith(".") || username.endsWith(".") ->
                false to "L'username non può iniziare o terminare con un punto"
            else -> true to null
        }
    }

    fun validateEmail(email: String): Pair<Boolean, String?> {
        val fieldName = "L'email"
        return when {
            email.isEmpty() -> false to ERROR_REQUIRED_FIELD
                .format(fieldName)
                .replace("obbligatorio", "obbligatoria")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                false to "Formato email non valido"
            email.length > 50 -> false to ERROR_MAX_LENGTH.format(fieldName, 50)
            else -> true to null
        }
    }

    fun validatePassword(password: String): Pair<Boolean, String?> {
        val fieldName = "La password"
        return when {
            password.isEmpty() -> false to ERROR_REQUIRED_FIELD
                .format(fieldName)
                .replace("obbligatorio", "obbligatoria")
            password.length < 8 -> false to ERROR_MIN_LENGTH.format(fieldName, 8)
            password.length > 50 -> false to ERROR_MAX_LENGTH.format(fieldName, 50)
            !password.any { it.isUpperCase() } -> false to "La password deve contenere almeno una lettera maiuscola"
            !password.any { it.isLowerCase() } -> false to "La password deve contenere almeno una lettera minuscola"
            !password.any { it.isDigit() } -> false to "La password deve contenere almeno un numero"
            !password.matches(Regex(REGEX_SPECIAL_CHARS)) -> false to "La password deve contenere almeno un carattere speciale"
            password.contains(" ") -> false to "La password non può contenere spazi"
            else -> true to null
        }
    }

    fun validateConfirmPassword(confirmPassword: String, password: String): Pair<Boolean, String?> {
        return when {
            confirmPassword.isEmpty() -> false to "La conferma della password è obbligatoria"
            confirmPassword != password -> false to "Le password non coincidono"
            else -> true to null
        }
    }
}