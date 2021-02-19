package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
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
    }

    companion object {
        private var list = mapOf<UUIDPlayer, DiscordMember>()

        fun get(player: Player) = UUIDPlayer.from(player).let(list::get)
    }
}
