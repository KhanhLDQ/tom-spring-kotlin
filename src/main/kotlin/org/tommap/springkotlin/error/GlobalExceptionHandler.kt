package org.tommap.springkotlin.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/*
    - ':' (inheritance syntax in Kotlin) - extends in Java
    - public: default visibility in Kotlin
 */
@ControllerAdvice
class GlobalExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(
        UserNotFoundException::class,
        ShoppingListNotFoundException::class,
        ShoppingListItemNotFoundException::class
    )
    fun handleNotFoundException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildErrorResponse(HttpStatus.NOT_FOUND, exception.message)

    @ExceptionHandler(
        SignUpException::class,
        PasswordMismatchException::class
    )
    fun handleConflictException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildErrorResponse(HttpStatus.CONFLICT, exception.message)

    @ExceptionHandler(
        JwtAuthenticationException::class,
        UsernamePasswordMismatchException::class,
        AccountVerificationException::class,
        TokenExpiredException::class
    )
    fun handleUnauthorizedException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.message)

    @ExceptionHandler(
        BadRequestException::class,
        SupermarketException::class
    )
    fun handleBadRequestException(exception: RuntimeException): ResponseEntity<ApiError> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, exception.message)

    private fun buildErrorResponse(status: HttpStatus, message: String?): ResponseEntity<ApiError> {
        val error = ApiError(message = message, status = status);
        return ResponseEntity(error, status);
    }
}

class BadRequestException(message: String): RuntimeException(message)
class SignUpException(message: String): RuntimeException(message)
class JwtAuthenticationException(message: String, cause: Throwable? = null): RuntimeException(message, cause)
class UserNotFoundException(message: String): RuntimeException(message)
class PasswordMismatchException(message: String): RuntimeException(message)
class UsernamePasswordMismatchException(message: String): RuntimeException(message)
class AccountVerificationException(message: String): RuntimeException(message)
class TokenExpiredException(message: String): RuntimeException(message)
class JwtKeyException(message: String): IllegalStateException(message)
class ShoppingListNotFoundException(message: String): RuntimeException(message)
class ShoppingListItemNotFoundException(message: String): RuntimeException(message)
class SupermarketException(message: String): RuntimeException(message)