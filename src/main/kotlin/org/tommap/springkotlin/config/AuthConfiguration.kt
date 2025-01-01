package org.tommap.springkotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.tommap.springkotlin.user.repository.AppUserRepository

@Configuration
class AuthConfiguration(
    private val appUserRepository: AppUserRepository
) {
    //AppUser already extends UserDetails
    @Bean
    fun userDetailsService() = UserDetailsService { username ->
        appUserRepository.findByAppUsername(username) ?: throw UsernameNotFoundException("User not found")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        return DaoAuthenticationProvider().apply {
            this.setUserDetailsService(userDetailsService())
            this.setPasswordEncoder(passwordEncoder())
        }
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager = authConfig.authenticationManager
}