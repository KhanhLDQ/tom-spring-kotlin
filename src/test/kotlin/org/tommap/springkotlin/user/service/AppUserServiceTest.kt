package org.tommap.springkotlin.user.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.crypto.password.PasswordEncoder
import org.tommap.springkotlin.auth.service.IClientSessionService
import org.tommap.springkotlin.dto.UserInfoResponse
import org.tommap.springkotlin.dto.UserInfoUpdateRequest
import org.tommap.springkotlin.dto.UserPasswordUpdateRequest
import org.tommap.springkotlin.error.BadRequestException
import org.tommap.springkotlin.error.PasswordMismatchException
import org.tommap.springkotlin.user.Role
import org.tommap.springkotlin.user.entity.AppUser
import org.tommap.springkotlin.user.repository.AppUserRepository
import org.tommap.springkotlin.user.service.impl.AppUserServiceImpl
import org.tommap.springkotlin.user.util.UserInfoMapper
import java.util.stream.Stream

class AppUserServiceTest {
    private val mockPasswordEncoder = mockk<PasswordEncoder>(relaxed = true) //have default values for all methods & properties of mock
    private val mockAppUserRepository = mockk<AppUserRepository>(relaxed = true)
    private val mockUserInfoMapper = mockk<UserInfoMapper>(relaxed = true)
    private val mockClientSessionService = mockk<IClientSessionService>(relaxed = true)

    private val appUserServiceImpl = AppUserServiceImpl(
        mockPasswordEncoder, mockAppUserRepository, mockUserInfoMapper, mockClientSessionService
    )

    private val user = AppUser(
        id = 1,
        firstName = "Tom",
        lastName = "Map",
        email = "tom@gmail.com",
        appUsername = "TomLearnKotlin",
        appPassword = "123456",
        role = Role.USER,
        isVerified = true,
    )

    @Test
    @DisplayName("Test update user email successfully")
    fun testChangeEmail_WhenValidRequestProvided_ShouldUpdateUserEmail() {
        //arrange
        val updatedEmail = "khanh@gmail.com"
        val request = mapOf("email" to updatedEmail)

        val updatedUser = AppUser(
            id = 1,
            firstName = "Tom",
            lastName = "Map",
            email = "khanh@gmail.com",
            appUsername = "TomLearnKotlin",
            appPassword = "123456",
            role = Role.USER,
            isVerified = true,
        )

        val userInfoResponse = UserInfoResponse(
            firstName = "Tom",
            lastName = "Map",
            email = "khanh@gmail.com",
            appUsername = "TomLearnKotlin"
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockAppUserRepository.findByEmail(updatedEmail) } returns null
        every { mockAppUserRepository.save(any()) } returns updatedUser
        every { mockUserInfoMapper.toDto(updatedUser) } returns userInfoResponse

        //act
        val actualResponse = appUserServiceImpl.changeEmail(request)

        //assert
        assertNotNull(actualResponse, "Response should not be null")
        assertEquals("khanh@gmail.com", actualResponse.email, "Email should be updated to khanh@gmail.com")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 1) { mockAppUserRepository.findByEmail(updatedEmail) }
        verify(exactly = 1) { mockAppUserRepository.save(any()) }
        verify(exactly = 1) { mockUserInfoMapper.toDto(updatedUser) }
    }

    @ParameterizedTest(name = "[{index}] - {2}")
    @MethodSource("invalidEmailProvider")
    @DisplayName("Test update user email with invalid data")
    fun testChangeEmail_WhenInvalidRequestProvided_ShouldThrowException(
        request: Map<String, String>, errorMsg: String, description: String
    ) {
        //arrange
        every { mockClientSessionService.getCurrentLoggedInUser() } returns user

        //act & assert
        val exception = assertThrows<BadRequestException> {
            appUserServiceImpl.changeEmail(request)
        }

        assertEquals(errorMsg, exception.message, "Error message should be $errorMsg")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 0) { mockAppUserRepository.save(any()) }
    }

    companion object {
        @JvmStatic
        fun invalidEmailProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(emptyMap<String, String>(), "Email is required!", "email is required"),
                Arguments.of(mapOf("email" to "khanh"), "Invalid email format!", "invalid email format"),
            )
        }
    }

    @Test
    @DisplayName("Test update user with already used email")
    fun testChangeEmail_WhenEmailAlreadyUsed_ShouldThrowException() {
        //arrange
        val updatedEmail = "existing@gmail.com"
        val request = mapOf("email" to updatedEmail)

        val expectedErrorMsg = "Email is already used by another user!"

        val existingUser = AppUser(
            id = 2,
            firstName = "Khanh",
            lastName = "Le",
            email = "existing@gmail.com"
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockAppUserRepository.findByEmail(updatedEmail) } returns existingUser

        //act & assert
        val exception = assertThrows<BadRequestException> {
            appUserServiceImpl.changeEmail(request)
        }

        assertEquals(expectedErrorMsg, exception.message, "Error message should be $expectedErrorMsg")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 1) { mockAppUserRepository.findByEmail(updatedEmail) }
        verify(exactly = 0) { mockAppUserRepository.save(any()) }
    }

    @Test
    @DisplayName("Test update user password successfully")
    fun testChangePassword_WhenValidRequestProvided_ShouldUpdateUserPassword() {
        //arrange
        val userPasswordUpdateRequest = UserPasswordUpdateRequest(
            currentPassword = "123456", newPassword = "654321", confirmPassword = "654321"
        )

        val updatedUser = AppUser(
            id = 1,
            firstName = "Tom",
            lastName = "Map",
            email = "tom@gmail.com",
            appUsername = "TomLearnKotlin",
            appPassword = "654321",
            role = Role.USER,
            isVerified = true,
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockPasswordEncoder.matches(any(), any()) } returns true
        every { mockAppUserRepository.save(any()) } returns updatedUser

        //act
        appUserServiceImpl.changePassword(userPasswordUpdateRequest)

        //assert
        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 1) { mockPasswordEncoder.encode("654321") }
        verify(exactly = 1) { mockAppUserRepository.save(any()) }
    }

    @Test
    @DisplayName("Test update user with incorrect password")
    fun testChangePassword_WhenIncorrectPasswordProvided_ShouldThrowException() {
        //arrange
        val expectedErrorMsg = "Current password is incorrect!"

        val userPasswordUpdateRequest = UserPasswordUpdateRequest(
            currentPassword = "incorrect_old_password", newPassword = "654321", confirmPassword = "654321"
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockPasswordEncoder.matches(any(), any()) } returns false

        //act & assert
        val exception = assertThrows<PasswordMismatchException> {
            appUserServiceImpl.changePassword(userPasswordUpdateRequest)
        }

        assertEquals(expectedErrorMsg, exception.message, "Error message should be $expectedErrorMsg")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 0) { mockAppUserRepository.save(any()) }
    }

    @Test
    @DisplayName("Test update user with new password not matching")
    fun testChangePassword_WhenNewPasswordProvidedNotMatching_ShouldThrowException() {
        //arrange
        val expectedErrorMsg = "New password and confirm password do not match!"

        val userPasswordUpdateRequest = UserPasswordUpdateRequest(
            currentPassword = "123456", newPassword = "654321", confirmPassword = "not_matching_password"
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockPasswordEncoder.matches(any(), any()) } returns true

        //act & assert
        val exception = assertThrows<PasswordMismatchException> {
            appUserServiceImpl.changePassword(userPasswordUpdateRequest)
        }

        assertEquals(expectedErrorMsg, exception.message, "Error message should be $expectedErrorMsg")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 0) { mockAppUserRepository.save(any()) }
    }

    @Test
    @DisplayName("Test update user info successfully")
    fun testChangeInfo_WhenValidRequestProvided_ShouldUpdateUserInfo() {
        //arrange
        val userInfoUpdateRequest = UserInfoUpdateRequest(firstName = "Khanh", lastName = "Le")
        val updatedUser = AppUser(
            id = 1,
            firstName = "Khanh",
            lastName = "Le",
            email = "tom@gmail.com",
            appUsername = "TomLearnKotlin",
            appPassword = "123456",
            role = Role.USER,
            isVerified = true,
        )
        val userInfoResponse = UserInfoResponse(
            firstName = "Khanh",
            lastName = "Le",
            email = "tom@gmail.com",
            appUsername = "TomLearnKotlin"
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockAppUserRepository.save(any()) } returns updatedUser
        every { mockUserInfoMapper.toDto(updatedUser) } returns userInfoResponse

        //act
        val actualResponse = appUserServiceImpl.changeInfo(userInfoUpdateRequest)

        //assert
        assertNotNull(actualResponse, "Response should not be null")
        assertEquals("Khanh", actualResponse.firstName, "First name should be updated to Khanh")
        assertEquals("Le", actualResponse.lastName, "Last name should be updated to Le")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 1) { mockAppUserRepository.save(any()) }
        verify(exactly = 1) { mockUserInfoMapper.toDto(updatedUser) }
    }

    @Test
    fun testFetchInfo_ShouldReturnUserInfo() {
        //arrange
        val userInfoResponse = UserInfoResponse(
            firstName = "Tom",
            lastName = "Map",
            email = "tom@gmail.com",
            appUsername = "TomLearnKotlin"
        )

        every { mockClientSessionService.getCurrentLoggedInUser() } returns user
        every { mockUserInfoMapper.toDto(user) } returns userInfoResponse

        //act
        val actualResponse = appUserServiceImpl.fetchInfo()

        //assert
        assertNotNull(actualResponse, "Response should not be null")
        assertEquals("Tom", actualResponse.firstName, "First name should be Tom")
        assertEquals("Map", actualResponse.lastName, "Last name should be Map")
        assertEquals("tom@gmail.com", actualResponse.email)
        assertEquals("TomLearnKotlin", actualResponse.appUsername, "App username should be TomLearnKotlin")

        verify(exactly = 1) { mockClientSessionService.getCurrentLoggedInUser() }
        verify(exactly = 1) { mockUserInfoMapper.toDto(user) }
    }
}