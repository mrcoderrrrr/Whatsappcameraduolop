package com.example.camera.activity

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.camera.ui.theme.CameraTheme
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.Permission

class MainActivity : ComponentActivity() {
    private lateinit var getContent: ActivityResultLauncher<Intent>
    var isGranted=false
    private val requestPermission=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions->
        if (permissions[CAMERA]==true){
            isGranted=true
        }
        else{
            Toast.makeText(this,"Camera Permission Not Granted",Toast.LENGTH_SHORT).show()
        }
        if(permissions[READ_EXTERNAL_STORAGE]==true){
            isGranted=true
        }
        else{
            Toast.makeText(this,"Storage Permission Not Granted",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraTheme {
                MainUi()
            }
        }
        checkPermission()
    }
    @Composable
    fun MainUi(){
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
                            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            if (isGranted) {
                                getContent.launch(intent)
                            }
                            else{
                                Toast.makeText(applicationContext,"Please Allow Permision",Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(painter = painterResource(id = com.example.camera.R.drawable.ic_black_camera), contentDescription ="open camera" )
                        }
                    }
                },
                floatingActionButtonPosition = FabPosition.End){
            }
        }
    }

    private fun checkPermission() {
        if(ContextCompat.checkSelfPermission(this, CAMERA)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            requestPermission.launch(arrayOf(CAMERA,READ_EXTERNAL_STORAGE))
        }else{
            openCamera()
            requestPermission.launch(arrayOf(CAMERA,READ_EXTERNAL_STORAGE))
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        getContent=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                val bundle = it.data!!.extras
                val imageUri: Uri
                val bitmap: Bitmap = bundle!!.get("data") as Bitmap
                imageUri = saveImage(bitmap, this)
                /*if (bitmap!=null){
                }*/
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

