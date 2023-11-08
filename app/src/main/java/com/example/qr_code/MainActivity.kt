package com.example.qr_code

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.Writer
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.encoder.QRCode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var gen: Button
    private lateinit var save: Button
    private lateinit var edt1 : EditText
    private lateinit var edt2 : EditText
    private lateinit var qrCodeImageView : ImageView
    private  lateinit var next : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gen = findViewById(R.id.gen)
        save = findViewById(R.id.save)

        edt1 = findViewById(R.id.name)
        edt2 = findViewById(R.id.msv)
        qrCodeImageView = findViewById(R.id.qrCodeImageView)
        next = findViewById(R.id.next1)

        gen.setOnClickListener {
            val data1 = edt1.text.toString()
            val data2 = edt2.text.toString()

            val data = "$data1-$data2"
            if (data.isNotEmpty()) {
                val qrCode: Bitmap? = generateQRCode(data, 300, 300)
                qrCodeImageView.setImageBitmap(qrCode)
                saveQRCodeToDownloads(qrCode, data)

            }


        }

        next.setOnClickListener {
            val intent = Intent(this, QuetQR::class.java)
            startActivity(intent)
        }



    }
    private fun saveQRCodeToDownloads(qrCodeBitmap: Bitmap?, data: String) {
        if (qrCodeBitmap != null) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "QRCode_${data}_$timeStamp.jpg"

            // Lưu hình ảnh vào thư mục Downloads
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download")
            }

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
            uri?.let {
                try {
                    val outputStream = contentResolver.openOutputStream(it)
                    if (outputStream != null) {
                        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    outputStream?.close()
                    Toast.makeText(this, "Hình ảnh QR đã được lưu vào Downloads.", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Lỗi khi lưu hình ảnh.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Lỗi khi tạo hình ảnh QR.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun generateQRCode(data: String, width: Int, height: Int): Bitmap? {
        val qrCodeWriter: Writer = QRCodeWriter() //tao doi tuong trong thu vien de tao ma QR
        return try { //thu
            val bitMatrix: BitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height) // ma hoa du lieu da thanh qrcode voi chieu dai + chieu rong mac dinh
            val bitMatrixWidth: Int = bitMatrix.width // lay chieu dai
            val bitMatrixHeight: Int = bitMatrix.height // lay chieu rong duoi dang  ma trận  BitMatrix
            val pixels = IntArray(bitMatrixWidth * bitMatrixHeight) // de luu tru mau sac tung mang anh trong QR
            for (y in 0 until bitMatrixHeight) { // dung vong lap for de duyet qua tung diem anh
                for (x in 0 until bitMatrixWidth) {
                    pixels[y * bitMatrixWidth + x] = if (bitMatrix[x, y]) {
                        -0x1000000 // mau den
                    } else {
                        -0x1 //mau trang
                    }
                }
            }
            Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888).apply { setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight) }
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}