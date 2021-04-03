package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.mode.AutoGroupOnMove
import com.github.syari.plugin.talk.with.near.player.mode.ToggleMuteUseItem
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
        ToggleMuteUseItem.registerEvent()
    }

    override fun onDisable() {
        AutoGroupOnMove.clearVoiceChannels()
    }
}
