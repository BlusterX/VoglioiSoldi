package com.example.voglioisoldi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voglioisoldi.data.database.entities.Account
import com.example.voglioisoldi.data.database.entities.Transaction
import com.example.voglioisoldi.data.repositories.AccountRepository
import com.example.voglioisoldi.data.repositories.TransactionRepository
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

data class ChartPoint(
    val x: Float,
    val y: Float
)

data class ChartsUiState(
    val points: List<ChartPoint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriod: ChartPeriod = ChartPeriod.DAILY
)

enum class ChartPeriod { DAILY, WEEKLY, MONTHLY }

class GraphsViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartsUiState())
    val uiState: StateFlow<ChartsUiState> = _uiState

    init { reloadGraph() }

    fun setPeriod(period: ChartPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        reloadGraph()
    }

    private fun reloadGraph() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            sessionManager.getLoggedInUser().collect { username ->
                val user = userRepository.getUserByUsername(username ?: return@collect)
                user?.let {
                    val accounts = accountRepository.getAccountsByUser(it.id).firstOrNull() ?: emptyList()
                    if (accounts.isNotEmpty()) {
                        loadGraphPoints(it.id, _uiState.value.selectedPeriod, accounts)
                    }
                }
            }
        }
    }

    private fun loadGraphPoints(userId: Int, period: ChartPeriod, accounts: List<Account>) {
        viewModelScope.launch {
            transactionRepository.getTransactionsByUser(userId).collect { transactions ->
                val filteredTransactions = transactions.filter { it.accountId in accounts.map { acc -> acc.id } }
                val points = when (period) {
                    ChartPeriod.DAILY -> computeDailyPoints(filteredTransactions, accounts)
                    ChartPeriod.WEEKLY -> computeWeeklyPoints(filteredTransactions, accounts)
                    ChartPeriod.MONTHLY -> computeMonthlyPoints(filteredTransactions, accounts)
                }
                _uiState.update { it.copy(points = points, isLoading = false) }
            }
        }
    }

    //In parole povere: calcola 48 step 1 ogni 30 minuti
    //Per avere la x più fitta...
    private fun computeDailyPoints(transactions: List<Transaction>, accounts: List<Account>): List<ChartPoint> {
        val today = LocalDate.now()
        val points = mutableListOf<ChartPoint>()
        val accountIds = accounts.map { it.id }
        val dayStartMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startingBalance = accounts.sumOf { it.balance } +
                transactions.filter { it.accountId in accountIds && it.date < dayStartMillis }.sumOf { it.amount }
        points.add(ChartPoint(0f, startingBalance.toFloat()))
        var balance = startingBalance.toFloat()
        val stepMinutes = 30
        val numSteps = 24 * 60 / stepMinutes
        val txToday = transactions.filter { it.accountId in accountIds && it.date.toLocalDate() == today }
        for (i in 0 until numSteps) {
            val fromMinute = i * stepMinutes
            val toMinute = (i + 1) * stepMinutes
            val sum = txToday.filter { tx ->
                val ldt = tx.date.toLocalDateTime()
                val minuteOfDay = ldt.hour * 60 + ldt.minute
                minuteOfDay in fromMinute until toMinute
            }.sumOf { it.amount }
            balance += sum.toFloat()
            points.add(ChartPoint((i + 1).toFloat(), balance))
        }
        return points
    }
    //Qui invece ogni giorno della settimana viene suddiviso in fascie da 6 ore
    private fun computeWeeklyPoints(transactions: List<Transaction>, accounts: List<Account>): List<ChartPoint> {
        val today = LocalDate.now()
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        val points = mutableListOf<ChartPoint>()
        val accountIds = accounts.map { it.id }
        val weekStartMillis = startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startingBalance = accounts.sumOf { it.balance } +
                transactions.filter { it.accountId in accountIds && it.date < weekStartMillis }.sumOf { it.amount }
        points.add(ChartPoint(0f, startingBalance.toFloat()))
        var balance = startingBalance.toFloat()

        for (day in 0..6) {
            val currentDay = startOfWeek.plusDays(day.toLong())
            val txOfDay = transactions.filter { it.accountId in accountIds && it.date.toLocalDate() == currentDay }
            for (slot in 0..3) {
                val fromHour = slot * 6
                val toHour = fromHour + 6
                val sum = txOfDay.filter { tx ->
                    val hour = tx.date.toLocalDateTime().hour
                    hour in fromHour until toHour
                }.sumOf { it.amount }
                balance += sum.toFloat()
                points.add(ChartPoint(day + slot / 4f, balance))
            }
        }
        return points
    }
    //Anche qui stesso discorso del settimanale(1 punto ogni 6 ore) TROPPO???
    private fun computeMonthlyPoints(transactions: List<Transaction>, accounts: List<Account>): List<ChartPoint> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val daysInMonth = today.lengthOfMonth()
        val points = mutableListOf<ChartPoint>()
        val accountIds = accounts.map { it.id }
        val monthStartMillis = startOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startingBalance = accounts.sumOf { it.balance } +
                transactions.filter { it.accountId in accountIds && it.date < monthStartMillis }.sumOf { it.amount }
        points.add(ChartPoint(0f, startingBalance.toFloat()))
        var balance = startingBalance.toFloat()

        for (day in 0 until daysInMonth) {
            val currentDay = startOfMonth.plusDays(day.toLong())
            val txOfDay = transactions.filter { it.accountId in accountIds && it.date.toLocalDate() == currentDay }
            for (slot in 0..3) {
                val fromHour = slot * 6
                val toHour = fromHour + 6
                val sum = txOfDay.filter { tx ->
                    val hour = tx.date.toLocalDateTime().hour
                    hour in fromHour until toHour
                }.sumOf { it.amount }
                balance += sum.toFloat()
                points.add(ChartPoint(day + slot / 4f, balance))
            }
        }
        return points
    }
}

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()