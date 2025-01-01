package org.tommap.springkotlin.user.util

import org.springframework.stereotype.Component
import org.tommap.springkotlin.dto.UserInfoResponse
import org.tommap.springkotlin.user.entity.AppUser

@Component
class UserInfoMapper {
    fun toDto(entity: AppUser) = UserInfoResponse(
        firstName = entity.firstName,
        lastName = entity.lastName,
        email = entity.email,
        appUsername = entity.appUsername
    )
}