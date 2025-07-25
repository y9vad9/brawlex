package com.y9vad9.brawlex.user.domain.value

import com.y9vad9.brawlex.user.domain.User
import com.y9vad9.brawlex.user.domain.entity.BrawlStarsPlayer
import com.y9vad9.valdi.domain.ValueObject

/**
 * Represents a list of [BrawlStarsPlayer]s associated with a [User].
 *
 * This value object captures the state of player linking, allowing the system
 * to reason about whether a user has no linked players, one, or multiple.
 */
@ValueObject
public class LinkedBrawlStarsPlayers(
    public val list: List<BrawlStarsPlayer>,
) {
    /** True when the user has linked more than one player. */
    public val isLinkedToMultiple: Boolean get() = list.size > 1

    /** True when the user has linked exactly one player. */
    public val isLinkedToSingle: Boolean get() = list.size == 1

    /** True when the user hasn't linked any players. */
    public val isNotLinked: Boolean get() = list.isEmpty()

    /** True if player with specified [tag] is already linked */
    public fun has(tag: BrawlStarsPlayerTag): Boolean =
        list.any { it.tag == tag }

    /** Returns the linked player with the given [tag], or `null` if not found. */
    public operator fun get(tag: BrawlStarsPlayerTag): BrawlStarsPlayer? =
        list.firstOrNull { it.tag == tag }

    /**
     * Adds a new player. Throws [IllegalArgumentException] if player is already linked.
     */
    public fun withNew(player: BrawlStarsPlayer): LinkedBrawlStarsPlayers {
        require(!has(player.tag)) { "Player with tag ${player.tag} is already linked." }
        return LinkedBrawlStarsPlayers(list + player)
    }

    /**
     * Refreshes (updates) an existing player. Throws [IllegalArgumentException] if player is not linked.
     */
    @Throws(IllegalArgumentException::class)
    public fun withRefreshed(player: BrawlStarsPlayer): LinkedBrawlStarsPlayers {
        require(has(player.tag)) { "Player with tag ${player.tag} is not linked." }
        return LinkedBrawlStarsPlayers(list.map { if (it.tag == player.tag) player else it })
    }

    /** Removes a linked player by [tag]. Returns same instance if not found. */
    public fun without(tag: BrawlStarsPlayerTag): LinkedBrawlStarsPlayers {
        if (!has(tag)) return this
        return LinkedBrawlStarsPlayers(list.filterNot { it.tag == tag })
    }

    /** Replaces all players with [newList]. */
    public fun replacedWith(newList: List<BrawlStarsPlayer>): LinkedBrawlStarsPlayers =
        LinkedBrawlStarsPlayers(newList)

    /** Removes all linked players. */
    public fun withoutAll(): LinkedBrawlStarsPlayers = LinkedBrawlStarsPlayers(emptyList())

    override fun toString(): String {
        val playersInfo = list.joinToString(separator = ", ") { "${it.name} (${it.tag})" }
        return "LinkedPlayers(count=${list.size}, players=[$playersInfo])"
    }
}
