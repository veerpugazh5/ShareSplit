package com.sharesplit.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.sharesplit.app.R
import com.sharesplit.app.ui.screens.auth.SignInScreen
import com.sharesplit.app.ui.screens.dashboard.DashboardScreen
import com.sharesplit.app.ui.screens.groups.GroupsScreen
import com.sharesplit.app.ui.screens.expenses.ExpensesScreen
import com.sharesplit.app.ui.screens.profile.ProfileScreen
import com.sharesplit.app.viewmodel.AuthState

@Composable
fun ShareSplitNavigation(
    authState: AuthState,
    googleSignInClient: GoogleSignInClient,
    onSignInClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    val items = listOf(
        NavigationItem(
            title = stringResource(R.string.nav_home),
            icon = Icons.Default.Home,
            route = "dashboard"
        ),
        NavigationItem(
            title = stringResource(R.string.nav_groups),
            icon = Icons.Default.Group,
            route = "groups"
        ),
        NavigationItem(
            title = stringResource(R.string.nav_expenses),
            icon = Icons.Default.Receipt,
            route = "expenses"
        ),
        NavigationItem(
            title = stringResource(R.string.nav_profile),
            icon = Icons.Default.AccountCircle,
            route = "profile"
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (authState.user == null) {
        // Show sign-in screen
        SignInScreen(
            onSignInClick = onSignInClick,
            isLoading = authState.isLoading,
            error = authState.error
        )
    } else {
        // Show main app with navigation
        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                selectedTab = index
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("dashboard") {
                    DashboardScreen()
                }
                composable("groups") {
                    GroupsScreen()
                }
                composable("expenses") {
                    ExpensesScreen()
                }
                composable("profile") {
                    ProfileScreen(
                        user = authState.user,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
) 