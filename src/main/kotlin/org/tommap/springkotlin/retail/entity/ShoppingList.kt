package org.tommap.springkotlin.retail.entity

import jakarta.persistence.*
import org.tommap.springkotlin.user.entity.AppUser

@Entity
@Table(name = "shopping_list")
class ShoppingList (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shopping_list_id_seq")
    @SequenceGenerator(name = "shopping_list_id_seq", sequenceName = "shopping_list_id_seq", allocationSize = 1)
    val id: Long = 0,

    @Column(name = "is_done")
    var isDone: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    val appUser: AppUser? = null
) {
}