package org.tommap.springkotlin.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.tommap.springkotlin.user.entity.AppUser

@Repository
interface AppUserRepository : JpaRepository<AppUser, Long> {
    @Query("SELECT au FROM AppUser  au WHERE au.email = :email")
    fun findByEmail(
        @Param("email") email: String
    ): AppUser?

    @Query("SELECT au FROM AppUser  au WHERE au.appUsername = :username")
    fun findByAppUsername(
        @Param("username") appUsername: String
    ): AppUser?
}