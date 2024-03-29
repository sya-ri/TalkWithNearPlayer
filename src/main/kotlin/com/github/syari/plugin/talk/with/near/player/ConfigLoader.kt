package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.discord.DiscordClient
import com.github.syari.plugin.talk.with.near.player.discord.DiscordMember
import com.github.syari.plugin.talk.with.near.player.mode.AutoGroupOnMove
import com.github.syari.plugin.talk.with.near.player.mode.Mode
import com.github.syari.plugin.talk.with.near.player.mode.ToggleMuteUseItem
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.def.DefaultConfigMap
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.uuid.UUIDPlayer
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
        const val auto_category = "auto.category"
    }

    fun load(sender: CommandSender) {
        plugin.config(sender, "config.yml", DefaultConfigMap(default)) {
            val token = get(Key.discord_token, ConfigDataType.String)
            DiscordClient.login(token)
            if (DiscordClient.isLogin.not()) {
                sendError(Key.discord_token, "ログインに失敗しました")
            } else {
                DiscordClient.guildId = get(Key.discord_guild, ConfigDataType.Long)
            }
            Mode.mode = get(Key.mode, ConfigDataType.String)?.let(Mode.Companion::get) ?: Mode.Item
            val toggleItemType = get(Key.item_type, ConfigDataType.Material, ToggleMuteUseItem.defaultType, false)
            val toggleItemName = get(Key.item_name, ConfigDataType.String, ToggleMuteUseItem.defaultName, false)
            ToggleMuteUseItem.item = ToggleMuteUseItem.createItem(toggleItemType, toggleItemName)
            AutoGroupOnMove.radius = get(Key.auto_radius, ConfigDataType.Double, AutoGroupOnMove.defaultRadius)
            AutoGroupOnMove.owners = get(Key.auto_player, ConfigDataType.StringList)?.mapNotNull(UUIDPlayer.Companion::from).orEmpty().toMutableList()
            AutoGroupOnMove.categoryId = get(Key.auto_category, ConfigDataType.Long, false)
        }
        DiscordMember.ConfigLoader.load(sender)
        Mode.applyMode()
    }

    fun setMode(sender: CommandSender, mode: Mode) {
        plugin.config(sender, "config.yml", DefaultConfigMap(default)) {
            set(Key.mode, ConfigDataType.String, mode.toString(), true)
        }
    }

    fun setAutoRadius(sender: CommandSender, radius: Double) {
        plugin.config(sender, "config.yml", DefaultConfigMap(default)) {
            set(Key.auto_radius, ConfigDataType.Double, radius, true)
        }
    }

    fun setAutoPlayer(sender: CommandSender, owners: List<UUIDPlayer>) {
        plugin.config(sender, "config.yml", DefaultConfigMap(default)) {
            set(Key.auto_player, ConfigDataType.UUIDList, owners.map(UUIDPlayer::uniqueId), true)
        }
    }

    private val default = mapOf(
        Key.discord_token to "",
        Key.discord_guild to 0L,
        Key.item_type to ToggleMuteUseItem.defaultType.name,
        Key.item_name to ToggleMuteUseItem.defaultName,
        Key.mode to Mode.Item.key,
        Key.auto_radius to 5,
        Key.auto_player to listOf<UUIDPlayer>()
    )
}
