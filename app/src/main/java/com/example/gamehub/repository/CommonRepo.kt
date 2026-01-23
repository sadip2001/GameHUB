package com.example.gamehub.repository

import android.content.Context
import android.net.Uri

interface commonRepo {
    fun  uploadImage(context: Context, imageUri: Uri?, callback: (Boolean,String?)-> Unit)

    fun getFileNameFromUri(context: Context,imageUri: Uri?):String?
}