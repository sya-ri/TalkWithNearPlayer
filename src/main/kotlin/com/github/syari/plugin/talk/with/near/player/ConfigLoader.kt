package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.type.ConfigDataType
import org.bukkit.command.CommandSender

object ConfigLoader {
    object Key {
        const val discord_token = "discord.token"
        const val discord_guild = "discord.guild"
        const val item_type = "item.type"
        const val item_name = "item.name"
    }

    fun load(sender: CommandSender) {
        plugin.config(sender, "config.yml", default) {
            val token = get(Key.discord_token, ConfigDataType.String)
            DiscordClient.login(token)
            if (DiscordClient.isLogin.not()) {
                sendError(Key.discord_token, "ログインに失敗しました")
            } else {
                DiscordClient.guildId = get(Key.discord_guild, ConfigDataType.Long)
            }
            val toggleItemType = get(Key.item_type, ConfigDataType.Material, ToggleSpeak.defaultType, false)
            val toggleItemName = get(Key.item_name, ConfigDataType.String, ToggleSpeak.defaultName, false)
            ToggleSpeak.item = ToggleSpeak.createItem(toggleItemType, toggleItemName)
        }
        DiscordMember.ConfigLoader.load(sender)
    }

    private val default = mapOf(
        Key.discord_token to "",
        Key.discord_guild to 0L,
        Key.item_type to ToggleSpeak.defaultType.name,
        Key.item_name to ToggleSpeak.defaultName
    )
}
