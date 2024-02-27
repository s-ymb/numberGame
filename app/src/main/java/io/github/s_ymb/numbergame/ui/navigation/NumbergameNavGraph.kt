package io.github.s_ymb.numbergame.ui.navigation

// import com.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridEntryScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import io.github.s_ymb.numbergame.ui.home.GameScreen
import io.github.s_ymb.numbergame.ui.home.NumbergameScreenDestination
import io.github.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridDetailDestination
import io.github.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridDetailScreen
import io.github.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridTblDestination
import io.github.s_ymb.numbergame.ui.satisfiedGrid.SatisfiedGridTblScreen
import io.github.s_ymb.numbergame.ui.savedGrid.SavedDetailDestination
import io.github.s_ymb.numbergame.ui.savedGrid.SavedGridDetailScreen
import io.github.s_ymb.numbergame.ui.savedGrid.SavedTblDestination
import io.github.s_ymb.numbergame.ui.savedGrid.SavedTblScreen

/**
 * Provides Navigation graph for the application.
 */



@Composable
fun NumbergameNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NumbergameScreenDestination.routeWithArgs,
        modifier = modifier
    ) {
        navigation(startDestination = SatisfiedGridTblDestination.route, route = "SatisfiedGridGroup")
        {
            composable(route = SatisfiedGridTblDestination.route) {
                SatisfiedGridTblScreen(
                    navigateBack = { navController.navigateUp() },
                //    navigateToSatisfiedGridEntry = { navController.navigate(SatisfiedGridEntryDestination.route) },
                    navigateToSatisfiedGridDetail = {
                        navController.navigate("${SatisfiedGridDetailDestination.route}/${it}")
                    }
                )
            }
            /*
            composable(route = SatisfiedGridEntryDestination.route) {
                SatisfiedGridEntryScreen(
                    navigateBack = { navController.popBackStack() },
                )
            }
            */
            composable(
                route = SatisfiedGridDetailDestination.routeWithArgs,
                arguments = listOf(navArgument(SatisfiedGridDetailDestination.satisfiedGridIdArg) {
                    type = NavType.StringType
                })
            ) {
                SatisfiedGridDetailScreen(
                    navigateBack = { navController.navigateUp() },
                )
            }
        }
        navigation(startDestination = SavedTblDestination.route, route = "SavedGridGroup")
        {
            composable(route = SavedTblDestination.route) {
                SavedTblScreen(
                    navigateBack = { navController.navigateUp() },
                    navigateToSavedGridDetail = {
                        navController.navigate("${SavedDetailDestination.route}/${it}")
                    },
                )
            }

            composable(
                route = SavedDetailDestination.routeWithArgs,
                arguments = listOf(navArgument(SavedDetailDestination.savedIdArg) {
                    type = NavType.IntType
                })
            ) {
                SavedGridDetailScreen(
                    navigateBack = { navController.navigateUp() },
                    navigateToNumbergameScreen =
                    //                   {navController.navigate("${NumberGameScreenDestination.route}/${it}"}
                    {
                        navController.navigate("${NumbergameScreenDestination.route}/${it}")
                    },
                )
            }
        }
        composable(
            route = NumbergameScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(NumbergameScreenDestination.NumbergameScreenIdArg) {
                type = NavType.IntType
            })
        ){
            GameScreen(
                navigateToSatisfiedGridTbl = { navController.navigate(SatisfiedGridTblDestination.route) },
                navigateToSavedGridTbl = { navController.navigate(SavedTblDestination.route) },
            )
        }
    }
}



/*
@Composable
fun NumberGameNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NumberGameScreenDestination.routeWithArgs,
        modifier = modifier
    ) {
        composable(route = NumberGameScreenDestination.routeWithArgs,
            arguments = listOf(navArgument(NumberGameScreenDestination.NumberGameScreenIdArg) {
                type = NavType.IntType
            })
        ){
            GameScreen(
                navigateToSatisfiedGridTbl = { navController.navigate(SatisfiedGridTblDestination.route) },
                navigateToSavedGridTbl = { navController.navigate(SavedGridTblDestination.route) },
            )
        }
        composable(route = SatisfiedGridTblDestination.route) {
            SatisfiedGridTblScreen(
                navigateBack = { navController.navigateUp() },
                navigateToSatisfiedGridEntry = { navController.navigate(SatisfiedGridEntryDestination.route) },
                navigateToSatisfiedGridDetail = {
                    navController.navigate("${SatisfiedGridDetailDestination.route}/${it}")
                }
            )
        }
        composable(route = SatisfiedGridEntryDestination.route) {
            SatisfiedGridEntryScreen(
                navigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = SatisfiedGridDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(SatisfiedGridDetailDestination.satisfiedGridIdArg) {
                type = NavType.StringType
            })
        ) {
            SatisfiedGridDetailScreen(
                navigateBack = { navController.navigateUp() },
            )
        }

        composable(route = SavedGridTblDestination.route) {
            SavedGridTblScreen(
                navigateBack = { navController.navigateUp() },
                navigateToNumberGameScreen = {
                    navController.navigate("${NumberGameScreenDestination.route}/${it}")
                },
            )
        }

    }
}

*/