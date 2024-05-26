package com.credential.cubrism.view.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

object FileNameFromUri {
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        }

        return null
    }
}