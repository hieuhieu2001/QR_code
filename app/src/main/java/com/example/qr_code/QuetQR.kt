package com.example.qr_code

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class QuetQR : AppCompatActivity() {

    private lateinit var camera : ImageView
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraPreview: SurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quet_qr)
       cameraPreview = findViewById(R.id.camera_preview)
        camera = findViewById(R.id.camera)


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        barcodeDetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()

        // Khởi tạo CameraSource
        cameraSource = CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).build()
        cameraPreview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(cameraPreview.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes: SparseArray<Barcode> = detections.detectedItems
                if (barcodes.size() > 0) {
                    val qrCode = barcodes.valueAt(0).displayValue
                    // Xử lý dữ liệu mã QR tại đây
                    runOnUiThread {
                        Toast.makeText(this@QuetQR, " $qrCode", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

       camera.setOnClickListener{
           val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
           intent.addCategory(Intent.CATEGORY_OPENABLE)
           intent.type = "image/*"
           startActivityForResult(intent, PICK_IMAGE_REQUEST)

       }

   }
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
           val selectedImageUri = data?.data
           // Bạn có thể sử dụng selectedImageUri để đọc hình ảnh và sau đó quét mã QR từ hình ảnh này
           if (selectedImageUri != null) {
               // Hiển thị thông tin mã QR lên TextView
               //   val qrResultText = findViewById<TextView>(R.id.qrResultText)
               //  qrResultText.text = qrCodeText
           }
       }
   }
   }
