package com.dden.memeApp.screens

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.dden.memeApp.R
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@Composable
fun DetailsScreen(modifier: Modifier = Modifier, name: String?, url: String?) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()
                .align(Alignment.Center)
        ) {
            Column(
                modifier = modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 45.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (url != null) {
                    AsyncImage(
                        model = url,
                        contentDescription = name,
                        modifier,
                        placeholder = painterResource(id = R.drawable.loading_img),
                        error = painterResource(id = R.drawable.ic_broken_image)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (name != null) {
                    Text(
                        text = name,
                        Modifier
                            .fillMaxWidth(),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.comicneue_bold))
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Use rememberCoroutineScope here to launch coroutines on button click.
                val coroutineScope = rememberCoroutineScope()

                // Button to download the image.
                ElevatedButton(
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = colorResource(id = R.color.main_color),
                        contentColor = Color.Black),
                                onClick = { if (url != null) coroutineScope.launch { downloadImage(url, context) } }) {
                    Text(text = "Download Image")
                }
            }
        }
    }
}

private fun requestStoragePermissions(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Android 13+: Request READ_MEDIA_IMAGES permission
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
            1
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+: No need for storage permission, MediaStore is used
    } else {
        // Android 9 and below: Request WRITE_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }
}

private suspend fun downloadImage(imageUrl: String?, context: Context) {
    if (imageUrl != null) {
        try {
            // Create an image request to fetch the bitmap.
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .build()

            // Execute the request and get the result.
            val result = Coil.imageLoader(context).execute(request)

            if (result is SuccessResult) {
                val bitmap: Bitmap? = result.drawable?.toBitmap()

                if (bitmap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10 and above: Use MediaStore to save the image
                        saveImageToMediaStore(bitmap, context)
                    } else {
                        // Android 9 and below: Save image to external storage
                        saveImageToExternalStorage(bitmap)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any errors here (optional).
        }
    }
}

private fun saveImageToExternalStorage(bitmap: Bitmap) {
    val filePath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "downloaded_image.png"
    )
    val fos = FileOutputStream(filePath)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    fos.flush()
    fos.close()
}

private fun saveImageToMediaStore(bitmap: Bitmap, context: Context) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "downloaded_image.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        val fos: OutputStream? = context.contentResolver.openOutputStream(uri)
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}