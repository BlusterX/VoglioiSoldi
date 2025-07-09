
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.composables.transaction.AddTransactionContent
import com.example.voglioisoldi.ui.composables.transaction.TransactionConfirmDialog
import com.example.voglioisoldi.ui.composables.transaction.TransactionErrorDialog
import com.example.voglioisoldi.ui.util.rememberCurrentUserId
import com.example.voglioisoldi.ui.viewmodel.AddTransactionViewModel
import com.example.voglioisoldi.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTransactionScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val viewModel: AddTransactionViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val actions = viewModel.actions
    val userId = rememberCurrentUserId()
    val accounts = homeViewModel.uiState.collectAsState().value.accounts
    val transactions = homeViewModel.uiState.collectAsState().value.transactions

    val accountBalances = remember(accounts, transactions) {
        accounts.map { account ->
            val updatedBalance = account.balance +
                    transactions.filter { it.accountId == account.id }.sumOf { it.amount }
            Pair(account, updatedBalance)
        }
    }
    if (accounts.isNotEmpty() && state.accountId == null) {
        actions.setAccountId(accounts.first().id)
    }

    val accountLabel = accountBalances
        .find { it.first.id == state.accountId }
        ?.let { "${it.first.type} (${String.format("%.2f", it.second)} €)" }
        ?: "Seleziona account"

    Scaffold(
        topBar = { TopBar(showBackButton = true, onBackClick = { navController.popBackStack() }) },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        AddTransactionContent(
            padding = padding,
            state = state,
            actions = actions,
            accountBalances = accountBalances,
        )
    }

    TransactionConfirmDialog(
        show = state.showConfirmDialog,
        state = state,
        onConfirm = {
            if (userId != null) {
                actions.saveTransaction(userId)
                navController.popBackStack()
            }
        },
        onDismiss = actions::hideConfirmDialog,
        accountLabel = accountLabel
    )

    TransactionErrorDialog(
        show = state.showErrorDialog,
        message = state.errorMessage,
        onDismiss = actions::hideErrorDialog
    )
}





