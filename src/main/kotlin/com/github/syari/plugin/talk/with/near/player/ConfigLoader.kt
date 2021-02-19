package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.type.ConfigDataType
import org.bukkit.command.CommandSender

object ConfigLoader {
    object Key {
        const val discord_token = "discord.token"
        const val discord_guild = "discord.guild"
        const val discord_wait_room = "discord.room.wait"
        const val discord_talk_room = "discord.room.talk"
    }

    fun load(sender: CommandSender) {
        plugin.config(sender, "config.yml", default) {
            val token = get(Key.discord_token, ConfigDataType.String)
            DiscordClient.login(token)
            if (DiscordClient.isLogin.not()) {
                sendError(Key.discord_token, "ログインに失敗しました")
            } else {
                DiscordClient.guildId = get(Key.discord_guild, ConfigDataType.Long)
                DiscordClient.waitRoomId = get(Key.discord_wait_room, ConfigDataType.Long)
                DiscordClient.talkRoomId = get(Key.discord_talk_room, ConfigDataType.Long)
            }
        }
        DiscordMember.ConfigLoader.load(sender)
    }

    private val default = mapOf(
        Key.discord_token to "",
        Key.discord_guild to 0L,
        Key.discord_wait_room to 0L,
        Key.discord_talk_room to 0L
    )
}
