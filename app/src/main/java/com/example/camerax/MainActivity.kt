package com.example.camerax

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.util.Arrays
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinder: PreviewView
    private lateinit var captureButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        captureButton = findViewById(R.id.captureButton)
        viewFinder = findViewById(R.id.viewFinder)

        camaraPermission()
        //openCamara()


        // ContextCompat.getMainExecutor(requireContext()) For Fregment
    }

    private fun openCamara() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this@MainActivity)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Set up image capture
            val imageCapture = ImageCapture.Builder()
                .build()

            // Bind use cases to camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                // Handle errors
            }

            // Set up capture button click listener
            captureButton.setOnClickListener {
                val file = File(
                    this.externalMediaDirs.first(),
                    "${System.currentTimeMillis()}.jpg"
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                imageCapture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Handle saved image
                        //val savedUri = outputOptions.
                        //var savedUri = outputOptions
                        Log.e(TAG, "onImageSaved: called "+file.path)

                        val intent = Intent(this@MainActivity,SecondActivity::class.java)
                        intent.putExtra("imageURL",file.path)
                        startActivity(intent)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Handle error
                    }
                })
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun camaraPermission() {
        Dexter.withContext(this)
            .withPermissions(
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                   /* report?.let {
                        if(report.areAllPermissionsGranted()){
                            //Todo: Call FetchCallLog function
                            Log.e(TAG, "onPermissionsChecked: GRUNTED")
                            openCamara()
                        } else {
                            Log.e(TAG, "onPermissionsChecked: NOT GRUNTED")
                        }
                    }*/
                    if (report?.areAllPermissionsGranted() == true) {
                        //Todo: Call FetchCallLog function
                        Log.e(TAG, "onPermissionsChecked: GRUNTED")
                        openCamara()
                    } else {
                        Log.e(TAG, "onPermissionsChecked: NOT GRUNTED")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).check();

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}