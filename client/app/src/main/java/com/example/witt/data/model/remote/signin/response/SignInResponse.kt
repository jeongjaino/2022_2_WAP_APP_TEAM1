package com.example.witt.data.model.remote.signin.response

data class SignInResponse (
    val status : Boolean,
    val reason: String,
    val AccessToken : String,
    val RefreshToken : String,
    val isProfileExists: Boolean
)
