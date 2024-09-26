package com.dden.memeApp.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dden.memeApp.R
import com.dden.memeApp.models.Meme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    MemeList: List<Meme>,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(
                            R.string.app_name
                        ),
                        fontFamily = FontFamily(Font((R.font.bebasneue_regular))),
                        fontSize = 40.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFF6F61)
                )
            )
        },
        content = { padding ->
            Column(
                modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val textState = remember {
                    mutableStateOf(TextFieldValue(""))
                }
                SearchView(state = textState, placeholder = "Search here ...", modifier = modifier)
                val searchedText = textState.value.text
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(items = MemeList.filter {
                        it.name.contains(searchedText, ignoreCase = true)
                    }, key = { it.id }) { item ->
                        MemeItem(
                            itemName = item.name,
                            itemUrl = item.url,
                            navController = navController
                        )

                    }
                }
            }
        }
    )

}

@Composable
fun MemeItem(
    itemName: String,
    itemUrl: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier
            .wrapContentSize()
            .padding(7.dp)
            .clickable {
                navController.navigate("DetailsScreen?name=$itemName&url=$itemUrl")
            },
    ) {
        AsyncImage(
            model = itemUrl, contentDescription = itemName,
            modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .border(4.dp, Color.DarkGray, RoundedCornerShape(15.dp)),
            placeholder = painterResource(id = R.drawable.loading_img),
            error = painterResource(id = R.drawable.ic_broken_image)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(state: MutableState<TextFieldValue>, placeholder: String, modifier: Modifier) {
    TextField(
        value = state.value, onValueChange = { value ->
            state.value = value
        },
        modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 13.dp, start = 13.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .border(2.dp, Color.DarkGray, RoundedCornerShape(30.dp)),
        placeholder = {
            Text(text = placeholder)
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = colorResource(id = R.color.soft_gray)
        ),
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(color = Color.Black)
    )
}
