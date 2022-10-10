package com.example.camera.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import com.example.camera.ui.theme.CameraTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var getContent: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraTheme {
                mainUi()
            }
        }
        openCamera()
    }
    @Composable
    fun mainUi(){
        Column {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Camera")
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {},
                        backgroundColor = MaterialTheme.colors.primary
                    ){
                        IconButton(onClick = {
                          Toast.makeText(this@MainActivity,"Open Camera",Toast.LENGTH_SHORT).show()
                            val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            getContent.launch(intent)
                        }) {
                            Icon(painter = painterResource(id = com.example.camera.R.drawable.ic_black_camera), contentDescription ="open camera" )
                        }
                    }
                },
                floatingActionButtonPosition = FabPosition.End){
                    paddingValues -> paddingValues
            }
        }
    }
    private fun openCamera() {
        getContent=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult>{
                val data=it.data
                val bundle = it.data!!.extras
                val imageUri: Uri
                val bitmap: Bitmap = bundle!!.get("data") as Bitmap
                imageUri = saveImage(bitmap, this)
            })
    }

    private fun saveImage(camImage: Bitmap, context: Context): Uri {
        val camImageFolder = File(context.cacheDir, "topImages")
        var uri: Uri? = null
        try {
            camImageFolder.mkdirs()
            val file = File(camImageFolder, "captured_image.jpg")
            val fo = FileOutputStream(file)
            camImage.compress(Bitmap.CompressFormat.JPEG,100,fo)
            fo.flush()
            fo.close()
            uri = FileProvider.getUriForFile(
                context.applicationContext,
                "com.example.camera" + ".provider",
                file
            )
            Toast.makeText(context,"Save Image",Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri!!
    }
}

