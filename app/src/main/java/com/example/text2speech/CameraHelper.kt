package com.example.text2speech

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.nio.ByteBuffer
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock


typealias LumaListener = (luma: Double) -> Unit

class CameraHelper(context: Context) {
    private var context = context
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var currentImageTook:Bitmap? = null


    fun startCamera(lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture:ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)

        Log.d("CameraHelper", "start camera before add listener")
        Log.e("CameraHelper",cameraProviderFuture.toString())
        cameraProviderFuture.addListener(Runnable {
            Log.e("CameraHelper inside",cameraProviderFuture.toString())

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview

            //ImageCapture

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

            try {
                Log.e("Try", "Use ")
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                //Preview Deleted in this part
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector,  imageCapture)

            } catch(exc: Exception) {
                Log.e( "Use case binding failed", exc.toString())
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(){
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        var bitmapImage:Bitmap? = null
        Log.e("take image", "in take photo")
        // Setup image capture listener which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context), object :
                ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(image: ImageProxy) {
//                    super.onCaptureSuccess(image)
                    Log.e("take image", image.height.toString() +' '+image.width.toString() )
                    Log.e("take image format", image.format.toString())
                    val planeProxy = image.planes[0]
                    val buffer: ByteBuffer = planeProxy.buffer
                    buffer.rewind()
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)
                    bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    currentImageTook = bitmapImage
                    Log.e("take image bitmap", bitmapImage.toString())
                    image.close()
                    super.onCaptureSuccess(image)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("take image",exception.toString())
                }
            })
        Log.e("take image return", "return value")
    }

    fun getImage():Bitmap?{

        return currentImageTook
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            context, it) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}