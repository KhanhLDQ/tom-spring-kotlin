package org.tommap.springkotlin.user.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.tommap.springkotlin.api.UserResource
import org.tommap.springkotlin.dto.UserInfoResponse
import org.tommap.springkotlin.dto.UserInfoUpdateRequest
import org.tommap.springkotlin.dto.UserPasswordUpdateRequest
import org.tommap.springkotlin.user.service.IAppUserService

@RestController
class AppUserController(
    private val appUserService: IAppUserService
) : UserResource {
    override fun changeEmail(requestBody: Map<String, String>): ResponseEntity<UserInfoResponse>
    = ResponseEntity
        .status(HttpStatus.OK.value())
        .body(appUserService.changeEmail(requestBody))

    override fun changeInfo(userInfoUpdateRequest: UserInfoUpdateRequest): ResponseEntity<UserInfoResponse>
    = ResponseEntity
        .status(HttpStatus.OK.value())
        .body(appUserService.changeInfo(userInfoUpdateRequest))

    override fun changePassword(userPasswordUpdateRequest: UserPasswordUpdateRequest)
    = ResponseEntity
        .status(HttpStatus.OK.value())
        .body(appUserService.changePassword(userPasswordUpdateRequest))

    override fun fetchInfo(): ResponseEntity<UserInfoResponse>
    = ResponseEntity
        .status(HttpStatus.OK.value())
        .body(appUserService.fetchInfo())
}