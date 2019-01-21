package test.sales.common

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.security.MessageDigest

fun ByteArray.encodeMD5(): String {
    return StringBuffer().apply {
        for (b in MessageDigest.getInstance("MD5").digest(this@encodeMD5)) {
            append(Integer.toHexString(b.toInt() and 0xff).padStart(2, '0'))
        }
    }.toString()
}

fun Uri.encodeMD5(context: Context): String {
    return if (this == Uri.EMPTY)
        ""
    else {
        val input = context.contentResolver.openInputStream(this)!!
        val md5 = input.readBytes().encodeMD5()
        input.close()
        md5
    }
}

fun Uri.bytes(context: Context): ByteArray {
    return if (this == Uri.EMPTY)
        ByteArray(0)
    else {
        val input = context.contentResolver.openInputStream(this)
        val bytes = input?.readBytes() ?: ByteArray(0)
        input?.close()
        bytes
    }
}

fun File.uri(context: Context): Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", this)

fun Context.createTempFile(ext: String): File = createTempFile("tmp", ext, File(cacheDir, "tmp").apply { mkdirs() })

fun Context.createTempUri(ext: String): Uri = createTempFile(ext).uri(this)
