package com.example.witt.domain.repository

import com.example.witt.domain.model.profile.remote.ProfileUploadModel
import com.example.witt.domain.model.user.DuplicateEmailModel
import com.example.witt.domain.model.user.UserProfileModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface UserRepository {

    suspend fun duplicateEmail(email: String): Result<DuplicateEmailModel>

    //remote
    suspend fun uploadProfile(profile: File, Nickname: String, PhoneNum: String) : Result<ProfileUploadModel>

    //local
    suspend fun setProfile(profileUri: String, userName: String): Result<Unit>

    //local
    fun getProfile(): Flow<Result<UserProfileModel>>

}