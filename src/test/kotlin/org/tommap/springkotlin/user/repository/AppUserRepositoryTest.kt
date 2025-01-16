package org.tommap.springkotlin.user.repository

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.tommap.springkotlin.user.Role
import org.tommap.springkotlin.user.entity.AppUser

/*
    - @DataJpaTest
        + create application context with JPA-related components only
        + test method by default is transactional and will roll back when completes
        + in-memory databased by default is used
 */
@DataJpaTest
class AppUserRepositoryTest @Autowired constructor(
    val testEntityManager: TestEntityManager,
    val appUserRepository: AppUserRepository
) {
    private val email = "tom@gmail.com"
    private val appUsername = "TomLearnKotlin"
    private val user = AppUser(
        firstName = "Tom",
        lastName = "Map",
        email = email,
        appUsername = appUsername,
        appPassword = "123456",
        role = Role.USER,
        isVerified = true,
    )

    @BeforeEach
    fun init() {
        testEntityManager.persist(user)
    }

    @AfterEach
    fun clear() {
        testEntityManager.clear()
    }

    @Test
    @DisplayName("user found when providing correct email")
    fun testFindByEmail_WhenCorrectEmailProvided_ShouldReturnAppUserDetails() {
        //arrange

        //act
        val actualUser: AppUser? = appUserRepository.findByEmail(email)

        //assert
        assertNotNull(actualUser, "Response should not be null")
        assertEquals(email, actualUser!!.email) {"Email should be $email"} //non-null assertion operator - !!
    }

    @Test
    @DisplayName("user not found when providing wrong email")
    fun testFindByEmail_WhenWrongEmailProvided_ShouldReturnNull() {
        //arrange
        val fakeEmail = "non-existing-user@gmail.com"

        //act
        val actualUser = appUserRepository.findByEmail(fakeEmail)

        //assert
        assertNull(actualUser, "Response should be null")
    }

    @Test
    @DisplayName("user found when providing correct username")
    fun testFindByAppUsername_WhenCorrectUsernameProvided_ShouldReturnAppUserDetails() {
        //arrange

        //act
        val actualUser = appUserRepository.findByAppUsername(appUsername)

        //assert
        assertNotNull(actualUser, "Response should not be null")
        assertEquals(appUsername, actualUser!!.username) {"Username should be $appUsername"}
    }

    @Test
    @DisplayName("user not found when providing wrong username")
    fun testFindByAppUsername_WhenWrongUsernameProvided_ShouldReturnNull() {
        //arrange
        val fakeUsername = "non-existing-username"

        //act
        val actualUser = appUserRepository.findByAppUsername(fakeUsername)

        //assert
        assertNull(actualUser, "Response should be null")
    }
}