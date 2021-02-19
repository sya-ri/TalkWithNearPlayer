package com.github.syari.plugin.talk.with.near.player

import com.github.syari.spigot.api.event.register.EventRegister.Companion.registerEvents
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class Main : JavaPlugin() {
    companion object {
        internal lateinit var plugin: JavaPlugin
    }

    init {
        plugin = this
    }

    override fun onEnable() {
        ConfigLoader.load(server.consoleSender)
        CommandCreator.create()
        registerEvents(ToggleSpeak)
    }
}
