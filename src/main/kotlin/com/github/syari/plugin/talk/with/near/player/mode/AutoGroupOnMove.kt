package com.github.syari.plugin.talk.with.near.player.mode

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.discord.DiscordClient
import com.github.syari.plugin.talk.with.near.player.discord.DiscordMember
import com.github.syari.spigot.api.event.register.EventRegister
import com.github.syari.spigot.api.event.register.Events
import com.github.syari.spigot.api.scheduler.runTaskLater
import com.github.syari.spigot.api.util.uuid.UUIDPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent

object AutoGroupOnMove : EventRegister {
    const val defaultRadius = 5.0

    var radius = defaultRadius
    var owners = mutableListOf<UUIDPlayer>()

    fun createAllVoiceChannel() {
        plugin.runTaskLater(20, true) {
            (owners + null).forEach(::createVoiceChannel)
            ownerToChannel[null]?.let(DiscordClient::addMutePermission)
        }
    }

    fun createVoiceChannel(uuidPlayer: UUIDPlayer?) {
        if (ownerToChannel.contains(uuidPlayer).not()) {
            val result = DiscordClient.crate("twnp-${uuidPlayer?.uniqueId ?: "wait"}")
            if (result is DiscordClient.CreateResult.Success) {
                ownerToChannel[uuidPlayer] = result.channel.idLong
            } else if (result is DiscordClient.CreateResult.Failure) {
                plugin.logger.warning(result.message)
            }
        }
    }

    fun removeVoiceChannel(uuidPlayer: UUIDPlayer?) {
        ownerToChannel[uuidPlayer]?.let(DiscordClient::removeChannel)
    }

    fun clearVoiceChannels() {
        ownerToChannel.values.forEach(DiscordClient::removeChannel)
        ownerToChannel.clear()
    }

    private val ownerToChannel = mutableMapOf<UUIDPlayer?, Long>()

    private val coolTime = mutableSetOf<UUIDPlayer>()

    override fun Events.register() {
        event<PlayerMoveEvent> {
            if (Mode.mode == Mode.Auto) {
                val uuidPlayer = UUIDPlayer.from(it.player)
                if (coolTime.contains(uuidPlayer)) return@event
                updateConnectChannel()
                coolTime.add(uuidPlayer)
                plugin.runTaskLater(5, true) {
                    coolTime.remove(uuidPlayer)
                }
            }
        }
    }

    private var lastConnectChannel = mapOf<UUIDPlayer, Long>()

    private fun updateConnectChannel() {
        val voiceMemberList = DiscordMember.playerList.toMutableSet()

        /** オーナー同士の干渉 */
        val mergeGroups = mutableMapOf<UUIDPlayer, UUIDPlayer>()

        /** key: それぞれのプレイヤー, value: 所属先のオーナー */
        val playerToOwner = mutableMapOf<UUIDPlayer, UUIDPlayer>().apply {
            owners.forEach { owner ->
                val ownerPlayer = owner.player ?: return@forEach
                ownerPlayer.getNearbyEntities(radius, radius, radius).forEach { entity ->
                    if (entity is Player) {
                        val player = UUIDPlayer.from(entity)
                        if (player in owners) {
                            if (player < owner) {
                                mergeGroups[owner] = player
                            } else {
                                mergeGroups[player] = owner
                            }
                        } else {
                            put(player, owner)
                        }
                        voiceMemberList.remove(player)
                    }
                }
                put(owner, owner)
                voiceMemberList.remove(owner)
            }
        }

        fun solveMerge(player: UUIDPlayer): UUIDPlayer {
            return mergeGroups[player]?.let(::solveMerge) ?: player
        }

        val solvedMergerGroups = mergeGroups.map { it.key to solveMerge(it.value) }.associate { it.first to it.second }

        val connectChannel = mutableMapOf<UUIDPlayer, Long>().apply {
            playerToOwner.forEach { (player, owner) ->
                val channel = ownerToChannel[solvedMergerGroups[owner] ?: owner] ?: return@forEach
                put(player, channel)
            }
        }

        if (lastConnectChannel != connectChannel) {
            connectChannel.forEach { (uuidPlayer, channel) ->
                val player = uuidPlayer.player ?: return@forEach
                DiscordClient.move(player, channel)
            }
            voiceMemberList.forEach {
                val player = it.player ?: return@forEach
                val channel = ownerToChannel[null]
                if (channel != null) {
                    DiscordClient.move(player, channel)
                }
            }
            lastConnectChannel = connectChannel
        }
    }
}
