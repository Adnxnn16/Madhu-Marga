package com.madhum.marga.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.madhum.marga.data.db.MadhuDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * ExportManager — handles data export to CSV.
 * FR-14: Export hive history as CSV/PDF.
 */
object ExportManager {

    fun exportAllData(context: Context) {
        val session = SessionManager(context)
        val userId = session.getUserId()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = MadhuDatabase.getDatabase(context)
                val harvests = db.harvestLogDao().getAllHarvestsByUserSync(userId)
                
                if (harvests.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "No harvest data to export.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Create CSV Content
                val csvContent = StringBuilder()
                csvContent.append("Hive Name,Date,Quantity (kg),Season,Notes\n")
                
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                harvests.forEach { log ->
                    val line = "${log.hiveName},${sdf.format(Date(log.harvestDate))},${log.quantityKg},${log.season},${log.notes.replace("\n", " ")}\n"
                    csvContent.append(line)
                }

                // Save to file
                val fileName = "MadhuMarga_Harvest_Export_${System.currentTimeMillis()}.csv"
                val file = File(context.cacheDir, fileName)
                FileOutputStream(file).use { 
                    it.write(csvContent.toString().toByteArray())
                }

                // Share file
                withContext(Dispatchers.Main) {
                    shareFile(context, file)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun shareFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Madhu Marga Harvest Export")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Export File"))
    }
}
