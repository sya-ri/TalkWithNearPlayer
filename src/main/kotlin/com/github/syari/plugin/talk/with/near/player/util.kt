package com.github.syari.plugin.talk.with.near.player

import com.github.syari.spigot.api.uuid.UUIDPlayer
import org.bukkit.ChatColor

fun String.toColor() = ChatColor.translateAlternateColorCodes('&', this)

fun templateMessage(message: String) = "&b[TalkWithNearPlayer] &f$message".toColor()

inline val UUIDPlayer.displayName
    get() = offlinePlayer.name ?: uniqueId.toString()
