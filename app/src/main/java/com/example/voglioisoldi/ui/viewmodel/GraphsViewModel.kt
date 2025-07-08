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
import kotlinx.coroutines.launch
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
    private val accountRepository: AccountRepository,
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
                    val account = accountRepository.getAccountsByUser(it.id).firstOrNull()?.firstOrNull()
                    if (account != null) {
                        loadGraphPoints(it.id, _uiState.value.selectedPeriod, account)
                    }
                }
            }
        }
    }

    private fun loadGraphPoints(userId: Int, period: ChartPeriod, account: Account) {
        viewModelScope.launch {
            transactionRepository.getTransactionsByUser(userId).collect { transactions ->
                val points = when (period) {
                    ChartPeriod.DAILY -> computeDailyPoints(transactions, account)
                    ChartPeriod.WEEKLY -> computeWeeklyPoints(transactions, account)
                    ChartPeriod.MONTHLY -> computeMonthlyPoints(transactions, account)
                }
                _uiState.value = _uiState.value.copy(points = points, isLoading = false)
            }
        }
    }
    //Posso raggruppare tutti e 3 i calcoli in 1???
    private fun computeDailyPoints(transactions: List<Transaction>, account: Account): List<ChartPoint> {
        val today = LocalDate.now()
        val dayStart = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val initialBalance = getStartingBalanceForPeriod(account, transactions, dayStart)
        val txByHour = transactions
            .filter { it.date.toLocalDate() == today }
            .groupBy { it.date.toLocalDateTime().hour }

        val points = mutableListOf<ChartPoint>()
        var balance = initialBalance.toFloat()
        points.add(ChartPoint(0f, balance))
        for (hour in 0..23) {
            val sum = txByHour[hour]?.sumOf { it.amount } ?: 0.0
            balance += sum.toFloat()
            points.add(ChartPoint((hour + 1).toFloat(), balance))
        }
        return points
    }

    private fun computeWeeklyPoints(transactions: List<Transaction>, account: Account): List<ChartPoint> {
        val now = LocalDate.now()
        val weekStart = now.minusDays(6)
        val days = (0..6).map { weekStart.plusDays(it.toLong()) }
        val txByDay = transactions.groupBy { it.date.toLocalDate() }

        val points = mutableListOf<ChartPoint>()
        var balance = 0f
        points.add(ChartPoint(0f, balance))

        days.forEachIndexed { idx, day ->
            if (day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() == account.createdAt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                balance += account.balance.toFloat()
            }
            val dailySum = txByDay[day]?.sumOf { it.amount } ?: 0.0
            balance += dailySum.toFloat()
            points.add(ChartPoint((idx + 1).toFloat(), balance))
        }
        return points
    }

    private fun computeMonthlyPoints(transactions: List<Transaction>, account: Account): List<ChartPoint> {
        val now = LocalDate.now()
        val monthStart = now.withDayOfMonth(1)
        val monthStartMillis = monthStart.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val initialBalance = getStartingBalanceForPeriod(account, transactions, monthStartMillis)

        val daysInMonth = now.lengthOfMonth()
        val days = (0 until daysInMonth).map { monthStart.plusDays(it.toLong()) }
        val txByDay = transactions.groupBy { it.date.toLocalDate() }

        val points = mutableListOf<ChartPoint>()
        var balance = initialBalance.toFloat()
        points.add(ChartPoint(0f, balance))
        days.forEachIndexed { idx, day ->
            if (day.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() == account.createdAt.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                balance += account.balance.toFloat()
            }
            val dailySum = txByDay[day]?.sumOf { it.amount } ?: 0.0
            balance += dailySum.toFloat()
            points.add(ChartPoint((idx + 1).toFloat(), balance))
        }
        return points
    }

    private fun getStartingBalanceForPeriod(
        account: Account,
        transactions: List<Transaction>,
        periodStartMillis: Long
    ): Double {
        //Se il conto è stato creato DOPO l’inizio del periodo, saldo iniziale = 0
        if (account.createdAt > periodStartMillis) return 0.0
        //Se il conto esiste già all'inizio del periodo, calcola saldo iniziale come prima
        val txsBefore = transactions.filter { it.date < periodStartMillis }
        return account.balance + txsBefore.sumOf { it.amount }
    }
}

//Ha senso modificare i date nel db in un altro formato???
fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()