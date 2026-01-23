package com.example.gamehub.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.gamehub.repository.commonRepo

class CommonViewModel(val repo: commonRepo) : ViewModel()  {
    fun  uploadImage(context: Context, imageUri: Uri, callback: (Boolean, String?) -> Unit){
        repo.uploadImage(context,imageUri,callback)
    }
}