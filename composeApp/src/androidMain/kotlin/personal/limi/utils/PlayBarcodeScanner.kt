package personal.limi.utils

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.tasks.await

suspend fun Context.launchGMSQRCodeScanner(): List<String> {
    val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom().build()
    val scanner = GmsBarcodeScanning.getClient(this, options)
    val result = mutableListOf<String>()
    val barcode = scanner.startScan().await()
    val rawValue = barcode.rawValue
    if (rawValue != null) result.add(rawValue)
    return if (result.isEmpty()) emptyList() else result.toList()
}