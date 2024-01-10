package com.rui157953.andraemon.provider

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build

open class BaseProvider : ContentProvider() {
    @SuppressLint("StaticFieldLeak")
    lateinit var baseContext: Context

    override fun onCreate(): Boolean {
        baseContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext()
        } else {
            context!!
        }
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return -1
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?
    ): Int {
        return -1
    }
}