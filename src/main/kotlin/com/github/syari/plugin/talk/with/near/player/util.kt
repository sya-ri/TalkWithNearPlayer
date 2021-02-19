package com.github.syari.plugin.talk.with.near.player

import org.bukkit.ChatColor

fun String.toColor() = ChatColor.translateAlternateColorCodes('&', this)

fun templateMessage(message: String) = "&b[TalkWithNearPlayer] &f$message".toColor()
