package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.util.uuid.UUIDPlayer
import org.bukkit.command.CommandSender

object ConfigLoader {
    object Key {
        const val discord_token = "discord.token"
        const val discord_guild = "discord.guild"
        const val mode = "mode"
        const val item_type = "item.type"
        const val item_name = "item.name"
        const val auto_radius = "auto.radius"
        const val auto_player = "auto.player"
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
            Mode.mode = get(Key.mode, ConfigDataType.String)?.let(Mode.Companion::get) ?: Mode.Item
            when (Mode.mode) {
                Mode.Item -> {
                    val toggleItemType = get(Key.item_type, ConfigDataType.Material, ToggleSpeak.defaultType, false)
                    val toggleItemName = get(Key.item_name, ConfigDataType.String, ToggleSpeak.defaultName, false)
                    ToggleSpeak.item = ToggleSpeak.createItem(toggleItemType, toggleItemName)
                }
                Mode.Auto -> {
                    AutoGroup.radius = get(Key.auto_radius, ConfigDataType.Double, 5.0)
                    AutoGroup.owners = get(Key.auto_player, ConfigDataType.StringList)?.mapNotNull(UUIDPlayer.Companion::from).orEmpty()
                }
            }
        }
        DiscordMember.ConfigLoader.load(sender)
    }

    fun setMode(sender: CommandSender, mode: Mode) {
        plugin.config(sender, "config.yml", default) {
            set(Key.mode, ConfigDataType.String, mode.toString(), true)
        }
    }

    private val default = mapOf(
        Key.discord_token to "",
        Key.discord_guild to 0L,
        Key.item_type to ToggleSpeak.defaultType.name,
        Key.item_name to ToggleSpeak.defaultName,
        Key.mode to Mode.Item.key,
        Key.auto_radius to 5,
        Key.auto_player to listOf<UUIDPlayer>()
    )
}
