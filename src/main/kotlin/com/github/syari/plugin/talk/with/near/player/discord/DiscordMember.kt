package com.github.syari.plugin.talk.with.near.player.discord

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.displayName
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.util.uuid.UUIDPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DiscordMember(val discordUserId: Long) {
    object ConfigLoader {
        fun load(sender: CommandSender) {
            plugin.config(sender, "player.yml") {
                list = mutableMapOf<UUIDPlayer, DiscordMember>().apply {
                    section("")?.forEach {
                        val uuidPlayer = get("$it.uuid", ConfigDataType.String)?.let(UUIDPlayer::from) ?: return@forEach
                        val discordUserId = get("$it.discord", ConfigDataType.Long) ?: return@forEach
                        put(uuidPlayer, DiscordMember(discordUserId))
                    }
                }
            }
        }

        fun addPlayer(uuidPlayer: UUIDPlayer, discordUserId: Long) {
            plugin.config(plugin.server.consoleSender, "player.yml") {
                val name = uuidPlayer.displayName
                set("$name.uuid", ConfigDataType.UUID, uuidPlayer.uniqueId)
                set("$name.discord", ConfigDataType.Long, discordUserId)
                save()
            }
        }

        fun removePlayer(uuidPlayer: UUIDPlayer) {
            plugin.config(plugin.server.consoleSender, "player.yml") {
                section("")?.forEach {
                    val uuid = get("$it.uuid", ConfigDataType.UUID, false)
                    if (uuid == uuidPlayer.uniqueId) {
                        setUnsafe(it, null)
                        return@config
                    }
                }
            }
        }
    }

    private val user
        get() = DiscordClient.getUser(discordUserId)

    val asTag
        get() = user?.asTag

    val displayName
        get() = asTag ?: discordUserId.toString()

    companion object {
        var list = mutableMapOf<UUIDPlayer, DiscordMember>()

        fun get(player: Player) = UUIDPlayer.from(player).let(list::get)

        val playerList
            get() = list.keys

        private val playerToAuthCode = mutableMapOf<UUIDPlayer, String>()

        fun generateAuthCode(uuidPlayer: UUIDPlayer): String {
            return playerToAuthCode.getOrPut(uuidPlayer) { "%04d".format((0..9999).random()) }
        }

        fun auth(code: String, userId: Long): UUIDPlayer? {
            if (code.length != 4 || code.toIntOrNull() == null) return null
            return playerToAuthCode.entries.firstOrNull { it.value == code }?.key?.let {
                playerToAuthCode.remove(it)
                list[it] = DiscordMember(userId)
                ConfigLoader.addPlayer(it, userId)
                it
            }
        }

        fun removePlayer(uuidPlayer: UUIDPlayer) {
            ConfigLoader.removePlayer(uuidPlayer)
            list.remove(uuidPlayer)
        }
    }
}
