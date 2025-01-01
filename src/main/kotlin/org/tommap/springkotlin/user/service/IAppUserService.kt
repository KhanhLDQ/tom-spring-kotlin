package org.tommap.springkotlin.user.service

import org.tommap.springkotlin.dto.UserInfoResponse
import org.tommap.springkotlin.dto.UserInfoUpdateRequest
import org.tommap.springkotlin.dto.UserPasswordUpdateRequest

interface IAppUserService {
    fun changeEmail(request: Map<String, String>): UserInfoResponse
    fun changePassword(request: UserPasswordUpdateRequest)
    fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse
    fun fetchInfo(): UserInfoResponse
}