package org.tommap.springkotlin.error

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

/*
    - val (immutable): final in Java
    - var (mutable)

    - data class: record in Java (quite similar) - automatically generates equals, hashCode, toString, copy
 */
data class ApiError(
    val message: String? = "API encountered an unexpected error",
    val status: HttpStatus,
    val code: Int = status.value(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)
