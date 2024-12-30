package org.tommap.springkotlin.user.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.tommap.springkotlin.auth.entity.VerificationToken
import org.tommap.springkotlin.retail.entity.ShoppingList
import org.tommap.springkotlin.user.Role

@Entity
@Table(name = "app_user")
class AppUser (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_seq")
    @SequenceGenerator(name = "app_user_id_seq", sequenceName = "app_user_id_seq", allocationSize = 1)
    val id: Long = 0,

    @NotBlank
    @Column(name = "first_name")
    var firstName: String = "",

    @NotBlank
    @Column(name = "last_name")
    var lastName: String = "",

    @NotBlank
    @Column(name = "email", unique = true, nullable = false)
    var email: String = "",

    @NotBlank
    @Column(name = "app_username", unique = true, nullable = false)
    var appUsername: String = "",

    @NotBlank
    @Column(name = "app_password")
    var appPassword: String = "",

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var role: Role = Role.USER,

    @Column(name = "is_verified")
    var isVerified: Boolean = false,

    @OneToOne(
        mappedBy = "appUser",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        optional = true
    )
    private val verificationToken: VerificationToken? = null,

    @OneToMany(
        mappedBy = "appUser",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY
    )
    private val shoppingLists: List<ShoppingList>? = null
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String = appPassword

    override fun getUsername(): String = appUsername

    override fun isEnabled(): Boolean = isVerified
}