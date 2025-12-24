package com.example.gamehub.model

data class UserModel(
    var id : String = "",
    var fullName : String = "",
    var email : String = "",
    var phone : String = "",
    var dp : String = "",

){
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "id" to id,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "dp" to dp
        )
    }
}
