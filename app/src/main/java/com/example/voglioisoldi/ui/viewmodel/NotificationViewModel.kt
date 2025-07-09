package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.voglioisoldi.data.database.entities.Notification
import com.example.voglioisoldi.data.repositories.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationState(
    val notifications: List<Notification> = emptyList(),
    val errorMessage: String = ""
)

interface NotificationActions {
    fun loadNotifications(userId: Int)
    fun markAsRead(notificationId: Int)
    fun markAllAsRead(userId: Int)
    fun deleteNotification(notificationId: Int)
    fun clearError()
}

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    val actions = object : NotificationActions {
        override fun loadNotifications(userId: Int) {
            viewModelScope.launch {
                try {
                    val notifications = notificationRepository.getNotificationsByUser(userId)
                    _state.update {
                        it.copy(
                            notifications = notifications
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            errorMessage = "Errore nel caricamento delle notifiche: ${e.message}"
                        )
                    }
                }
            }
        }

        override fun markAsRead(notificationId: Int) {
            viewModelScope.launch {
                try {
                    notificationRepository.markAsRead(notificationId)
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.map { notification ->
                                if (notification.id == notificationId) {
                                    notification.copy(isRead = true)
                                } else {
                                    notification
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(errorMessage = "Errore nell'aggiornamento della notifica: ${e.message}")
                    }
                }
            }
        }

        override fun markAllAsRead(userId: Int) {
            viewModelScope.launch {
                try {
                    notificationRepository.markAllAsRead(userId)
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.map { notification ->
                                notification.copy(isRead = true)
                            }
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(errorMessage = "Errore nell'aggiornamento delle notifiche: ${e.message}")
                    }
                }
            }
        }

        override fun deleteNotification(notificationId: Int) {
            viewModelScope.launch {
                try {
                    notificationRepository.deleteNotification(notificationId)
                    _state.update { currentState ->
                        currentState.copy(
                            notifications = currentState.notifications.filter { it.id != notificationId }
                        )
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(errorMessage = "Errore durante l'eliminazione della notifica: ${e.message}")
                    }
                }
            }
        }

        override fun clearError() {
            _state.update { it.copy(errorMessage = "") }
        }
    }
}