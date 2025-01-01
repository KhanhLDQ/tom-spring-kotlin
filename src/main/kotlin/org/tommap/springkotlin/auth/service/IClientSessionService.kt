package org.tommap.springkotlin.auth.service

import org.springframework.security.core.Authentication
import org.tommap.springkotlin.user.entity.AppUser

interface IClientSessionService {
    fun getAuthentication(): Authentication?
    fun getCurrentLoggedInUser(): AppUser
    fun getAuthenticatedUser(): AppUser
}