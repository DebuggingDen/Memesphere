package com.dden.memeApp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.network.HttpException
import com.dden.memeApp.models.Meme
import com.dden.memeApp.screens.DetailsScreen
import com.dden.memeApp.screens.MainScreen
import com.dden.memeApp.ui.theme.memeAppTheme
import com.dden.memeApp.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            memeAppTheme {
                val navController = rememberNavController()
                var memeList by remember {
                    mutableStateOf(listOf<Meme>())
                }
                val scope = rememberCoroutineScope()
                
                LaunchedEffect(key1 = true) {
                    scope.launch (Dispatchers.IO){
                        val response = try {
                            RetrofitInstance.api.getThumbnailList()
                        }
                        catch (e: IOException){
                            Toast.makeText(this@MainActivity, "app error: ${e.message}", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        catch (e: HttpException){
                            Toast.makeText(this@MainActivity, "HTTP error: ${e.message}", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        if (response.isSuccessful && response.body() != null ){
                            withContext(Dispatchers.Main){
                                memeList = response.body()!!.data.memes
                            }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "MainScreen") {

                    composable(route = "MainScreen"){
                        MainScreen(MemeList = memeList, navController = navController)
                    }
                    composable(route = "DetailsScreen?name={name}&url={url}",
                        arguments = listOf(
                            navArgument(name = "name"){
                                type = NavType.StringType
                        },
                            navArgument(name = "url"){
                                type = NavType.StringType
                            }
                        )
                    ){
                        DetailsScreen(
                            name= it.arguments?.getString("name"),
                            url= it.arguments?.getString("url")
                        )
                    }

                }
            }
        }
    }
}
