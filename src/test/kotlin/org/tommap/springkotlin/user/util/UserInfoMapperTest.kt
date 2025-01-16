package org.tommap.springkotlin.user.util

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.tommap.springkotlin.dto.UserInfoResponse
import org.tommap.springkotlin.user.entity.AppUser

class UserInfoMapperTest {
    private val userInfoMapper = UserInfoMapper()

    @Test
    @DisplayName("user info response when valid user provided")
    fun testToDto_WhenValidUserProvided_ShouldReturnUserInfoResponse() {
        //arrange
        val mockUser = mockk<AppUser>()
        every { mockUser.firstName } returns "Tom"
        every { mockUser.lastName } returns "Map"
        every { mockUser.email } returns "tom@gmail.com"
        every { mockUser.appUsername } returns "TomLearnKotlin"

        //act
        val actualResponse = userInfoMapper.toDto(mockUser)

        //assert
        assertInstanceOf(UserInfoResponse::class.java, actualResponse)

        assertEquals("Tom", actualResponse.firstName, "First name should be Tom")
        assertEquals("Map", actualResponse.lastName, "Last name should be Map")
        assertEquals("tom@gmail.com", actualResponse.email, "Email should be tom@gmail.com")
        assertEquals("TomLearnKotlin", actualResponse.appUsername, "Username should be TomLearnKotlin")
    }
}