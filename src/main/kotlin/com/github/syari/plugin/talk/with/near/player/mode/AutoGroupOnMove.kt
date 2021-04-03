package com.github.syari.plugin.talk.with.near.player.mode

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.discord.DiscordClient
import com.github.syari.plugin.talk.with.near.player.discord.DiscordMember
import com.github.syari.spigot.api.scheduler.runTaskLater
import com.github.syari.spigot.api.scheduler.runTaskTimer
import com.github.syari.spigot.api.uuid.UUIDPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object AutoGroupOnMove {
    const val defaultRadius = 5.0

    var radius = defaultRadius
    var owners = mutableListOf<UUIDPlayer>()
    var categoryId: Long? = null

    fun createAllVoiceChannel() {
        plugin.runTaskLater(20, true) {
            createVoiceChannel(null)
            owners.forEach(::createVoiceChannel)
            ownerToChannel[null]?.let(DiscordClient::addMutePermission)
        }
    }

    private var createChannelIndex = 0

    fun createVoiceChannel(uuidPlayer: UUIDPlayer?) {
        if (ownerToChannel.contains(uuidPlayer).not()) {
            val result = DiscordClient.crate("twnp-auto-$createChannelIndex", categoryId)
            createChannelIndex ++
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

    private var task: BukkitTask? = null

    fun registerTask() {
        if (task == null) {
            task = plugin.runTaskTimer(15) {
                updateConnectChannel()
            }
        }
    }

    fun unregisterTask() {
        task?.cancel()
        task = null
    }

    private var lastConnectOwner = mapOf<UUIDPlayer, UUIDPlayer?>()

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
                            voiceMemberList.remove(player)
                        }
                    }
                }
                put(owner, owner)
                voiceMemberList.remove(owner)
            }
        }

        tailrec fun solveMerge(player: UUIDPlayer): UUIDPlayer {
            val group = mergeGroups[player]
            return if (group != null) {
                solveMerge(group)
            } else {
                player
            }
        }

        val solvedMergerGroups = mergeGroups.map { it.key to solveMerge(it.value) }.associate { it.first to it.second }

        val connectOwner = mutableMapOf<UUIDPlayer, UUIDPlayer?>().apply {
            playerToOwner.forEach { (player, owner) ->
                put(player, solvedMergerGroups[owner] ?: owner)
            }
            voiceMemberList.forEach {
                put(it, null)
            }
        }

        val moveChannelSize = mutableMapOf<Pair<UUIDPlayer?, UUIDPlayer?>, Int>()

        connectOwner.forEach { (uuidPlayer, owner) ->
            val lastOwner = if (lastConnectOwner.contains(uuidPlayer)) {
                lastConnectOwner[uuidPlayer] ?: owner
            } else {
                null
            }
            val moveChannel = lastOwner to owner
            val lastSize = moveChannelSize.getOrDefault(moveChannel, 0)
            moveChannelSize[moveChannel] = lastSize + 1
        }

        val ownerToChannelList = ownerToChannel.toList()
        val iterator1 = ownerToChannelList.listIterator()
        while (iterator1.hasNext()) {
            val (owner1, channel1) = iterator1.next()
            val iterator2 = ownerToChannelList.listIterator(iterator1.nextIndex())
            while (iterator2.hasNext()) {
                val (owner2, channel2) = iterator2.next()
                val m1t1 = moveChannelSize.getOrDefault(owner1 to owner1, 0)
                val m1t2 = moveChannelSize.getOrDefault(owner1 to owner2, 0)
                val m2t1 = moveChannelSize.getOrDefault(owner2 to owner1, 0)
                val m2t2 = moveChannelSize.getOrDefault(owner2 to owner2, 0)
                when {
                    (m1t1 + m2t2) < (m1t2 + m2t1) -> {
                        ownerToChannel[owner1] = channel2
                        ownerToChannel[owner2] = channel1
                        if (owner1 == null) {
                            DiscordClient.removeMutePermission(channel1)
                            DiscordClient.addMutePermission(channel2)
                        } else if (owner2 == null) {
                            DiscordClient.removeMutePermission(channel2)
                            DiscordClient.addMutePermission(channel1)
                        }
                    }
                }
            }
        }

        connectOwner.forEach { (uuidPlayer, owner) ->
            val player = uuidPlayer.player ?: return@forEach
            val channel = ownerToChannel[owner] ?: return@forEach
            DiscordClient.move(player, channel)
        }

        lastConnectOwner = connectOwner
    }
}
