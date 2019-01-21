package test.sales.screen.employee

import android.content.Context
import android.net.Uri
import okhttp3.ResponseBody
import test.sales.Mode
import test.sales.common.createTempUri

internal fun ResponseBody.writeImage(context: Context): Uri {
    val uri = context.createTempUri("")
    val output = context.contentResolver.openOutputStream(uri)!!
    byteStream().apply {
        copyTo(output)
        close()
    }
    output.close()
    return uri
}

open class Query(val mode: Mode, val emidRef: Int)
open class Load(mode: Mode, emidRef: Int) : Query(mode, emidRef)