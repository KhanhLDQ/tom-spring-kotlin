package org.tommap.springkotlin.auth.entity

import jakarta.persistence.*
import org.tommap.springkotlin.user.entity.AppUser
import java.time.Instant

@Entity
@Table(name = "verification_token")
class VerificationToken (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_token_id_seq")
    @SequenceGenerator(name = "verification_token_id_seq", sequenceName = "verification_token_id_seq", allocationSize = 1)
    val id: Long = 0,

    @Column(name = "token")
    var token: String,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "app_user_id")
    val appUser: AppUser,

    @Column(name = "expiry_date")
    var expiryDate: Instant
) {
    fun isExpired(): Boolean = expiryDate.isBefore(Instant.now())
}