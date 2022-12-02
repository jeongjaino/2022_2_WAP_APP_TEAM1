package com.example.witt.data.repository

import com.example.witt.data.model.user.request.DuplicateEmailRequest
import com.example.witt.data.mapper.toDuplicateEmailModel
import com.example.witt.data.mapper.toProfileUploadModel
import com.example.witt.data.mapper.toUserProfileModel
import com.example.witt.data.model.local.UserProfile
import com.example.witt.data.model.profile.request.ProfileUploadRequest
import com.example.witt.data.model.user.response.toGetUserInfoModel
import com.example.witt.data.source.local.user_profile.ProfileDataSource
import com.example.witt.data.source.remote.duplicate_check.DuplicateEmailDataSource
import com.example.witt.data.source.remote.profile.ProfileUploadDataSource
import com.example.witt.data.source.remote.user.GetUserInfoDataSource
import com.example.witt.domain.model.profile.remote.ProfileUploadModel
import com.example.witt.domain.model.user.DuplicateEmailModel
import com.example.witt.domain.model.user.GetUserInfoModel
import com.example.witt.domain.model.user.UserProfileModel
import com.example.witt.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val duplicateEmailDataSource: DuplicateEmailDataSource,
    private val profileDataSource: ProfileDataSource,
    private val profileUploadDataSource: ProfileUploadDataSource,
    private val getUserInfoDataSource: GetUserInfoDataSource
):UserRepository {
    override suspend fun duplicateEmail(email: String): Result<DuplicateEmailModel> {
        return duplicateEmailDataSource.duplicateEmailCheck(DuplicateEmailRequest(email))
            .mapCatching { response ->
                response.toDuplicateEmailModel()
            }
    }

    override suspend fun setProfile(profileUri: String, userName: String): Result<Unit> {
        return profileDataSource.setProfile(UserProfile(userName, profileUri))
    }

    override fun getProfile(): Flow<Result<UserProfileModel>> {
        return profileDataSource.getProfile().map { result ->
            result.mapCatching { userProfile ->
                userProfile.toUserProfileModel()
            }
        }
    }

    override suspend fun uploadProfile(
        profile: File,
        Nickname: String,
        PhoneNum: String,
    ): Result<ProfileUploadModel> {
        return profileUploadDataSource.uploadProfile(
            profileUploadRequest = ProfileUploadRequest(
                profile = profile,
                Nickname = Nickname,
                PhoneNum = PhoneNum
            )
        ).mapCatching { response ->
            response.toProfileUploadModel()
        }
    }

    override suspend fun getUserInfo(): Result<GetUserInfoModel> {
        return getUserInfoDataSource.getUserInfo().mapCatching { response ->
            response.toGetUserInfoModel()
        }
    }
}