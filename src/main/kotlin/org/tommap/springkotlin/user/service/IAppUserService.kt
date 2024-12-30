package org.tommap.springkotlin.user.service

interface IAppUserService {
    fun changeEmail(request: Map<String, String>): UserInfoResponse
    fun changePassword(request: UserPasswordUpdateRequest)
    fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse
    fun fetchInfo(): UserInfoResponse
}