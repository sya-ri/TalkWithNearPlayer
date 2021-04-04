package com.github.syari.plugin.talk.with.near.player.mode

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.discord.DiscordClient
import com.github.syari.plugin.talk.with.near.player.mainHand.MainHandItem.Companion.mainHandItem
import com.github.syari.plugin.talk.with.near.player.templateMessage
import com.github.syari.plugin.talk.with.near.player.toColor
import com.github.syari.spigot.api.event.events
import com.github.syari.spigot.api.scheduler.runTask
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object ToggleMuteUseItem {
    val defaultType = Material.SLIME_BALL
    const val defaultName = "&aミュート切り替え"

    var item = createItem(defaultType, defaultName)

    fun createItem(type: Material, name: String): ItemStack {
        return ItemStack(type).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(name.toColor())
            }
        }
    }

    fun registerEvent() {
        plugin.events {
            event<PlayerInteractEntityEvent> { e ->
                if (Mode.mode == Mode.Item) {
                    val player = e.player
                    val mainHandItem = mainHandItem.getFromInventory(player.inventory)
                    if (mainHandItem.isSimilar(item)) {
                        val targetPlayer = e.rightClicked as? Player ?: return@event
                        plugin.runTask(async = true) {
                            DiscordClient.mute(targetPlayer, false)?.let {
                                player.sendMessage(templateMessage("&c$it"))
                            }
                        }
                    }
                }
            }
            event<PlayerInteractEvent> { e ->
                if (Mode.mode == Mode.Item && e.action == Action.LEFT_CLICK_AIR) {
                    val player = e.player
                    val mainHandItem = mainHandItem.getFromInventory(player.inventory)
                    if (mainHandItem.isSimilar(item)) {
                        plugin.runTask(async = true) {
                            DiscordClient.muteAll().forEach { (name, message) ->
                                player.sendMessage(templateMessage("&c$name: $message"))
                            }
                        }
                    }
                }
            }
        }
    }
}
