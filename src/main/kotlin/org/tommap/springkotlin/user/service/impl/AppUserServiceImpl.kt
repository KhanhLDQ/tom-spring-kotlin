package org.tommap.springkotlin.user.service.impl

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.tommap.springkotlin.auth.service.IClientSessionService
import org.tommap.springkotlin.dto.UserInfoResponse
import org.tommap.springkotlin.dto.UserInfoUpdateRequest
import org.tommap.springkotlin.dto.UserPasswordUpdateRequest
import org.tommap.springkotlin.error.BadRequestException
import org.tommap.springkotlin.error.PasswordMismatchException
import org.tommap.springkotlin.user.entity.AppUser
import org.tommap.springkotlin.user.repository.AppUserRepository
import org.tommap.springkotlin.user.service.IAppUserService
import org.tommap.springkotlin.user.util.UserInfoMapper

@Service
class AppUserServiceImpl (
    private val passwordEncoder: PasswordEncoder,
    private val appUserRepository: AppUserRepository,
    private val userInfoMapper: UserInfoMapper,
    private val clientSessionService: IClientSessionService
) : IAppUserService {
    override fun changeEmail(request: Map<String, String>): UserInfoResponse {
        val currentUser: AppUser = clientSessionService.getCurrentLoggedInUser()

        val newEmail = request["email"] ?: throw BadRequestException("Email is required") //?: - handle null value
        validateEmail(newEmail, currentUser.id)

        currentUser.email = newEmail
        val savedUser = appUserRepository.save(currentUser)

        return userInfoMapper.toDto(savedUser)
    }

    override fun changePassword(request: UserPasswordUpdateRequest) {
        val currentUser: AppUser = clientSessionService.getCurrentLoggedInUser()

        if (!passwordEncoder.matches(request.currentPassword, currentUser.appPassword)) {
            throw PasswordMismatchException("Current password is incorrect")
        }

        if (request.newPassword != request.confirmPassword) {
            throw PasswordMismatchException("New password and confirm password do not match")
        }

        currentUser.appPassword = passwordEncoder.encode(request.newPassword)
        appUserRepository.save(currentUser)
    }

    override fun changeInfo(request: UserInfoUpdateRequest): UserInfoResponse {
        val currentUser: AppUser = clientSessionService.getCurrentLoggedInUser()

        currentUser.apply {
            this.firstName = request.firstName ?: firstName
            this.lastName = request.lastName ?: lastName
        }

        val savedUser = appUserRepository.save(currentUser)

        return userInfoMapper.toDto(savedUser)
    }

    override fun fetchInfo(): UserInfoResponse {
        val currentUser: AppUser = clientSessionService.getCurrentLoggedInUser()
        return userInfoMapper.toDto(currentUser)
    }

    fun validateEmail(email: String, currentUserId: Long) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()

        if (!email.matches(emailRegex)) {
            throw BadRequestException("Invalid email format")
        }

        appUserRepository.findByEmail(email)?.let { appUser -> //?.let - execute only appUser is not null
            if (appUser.id != currentUserId) {
                throw BadRequestException("Email is already used by another user")
            }
        }
    }
}