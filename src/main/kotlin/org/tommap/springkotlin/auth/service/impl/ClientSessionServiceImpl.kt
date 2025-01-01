package org.tommap.springkotlin.auth.service.impl

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.tommap.springkotlin.auth.service.IClientSessionService
import org.tommap.springkotlin.user.entity.AppUser

@Service
class ClientSessionServiceImpl : IClientSessionService{
    override fun getAuthentication(): Authentication? {
        TODO("Not yet implemented")
    }

    override fun getCurrentLoggedInUser(): AppUser {
        TODO("Not yet implemented")
    }

    override fun getAuthenticatedUser(): AppUser {
        TODO("Not yet implemented")
    }
}