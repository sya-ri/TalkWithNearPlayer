package com.github.syari.plugin.talk.with.near.player

import com.github.syari.spigot.api.event.register.EventRegister
import com.github.syari.spigot.api.event.register.Events
import org.bukkit.event.player.PlayerMoveEvent

object AutoGroup : EventRegister {
    override fun Events.register() {
        event<PlayerMoveEvent> {
            if (Mode.mode == Mode.Auto) {
            }
        }
    }
}
