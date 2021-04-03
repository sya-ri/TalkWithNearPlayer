package com.github.syari.plugin.talk.with.near.player.mode

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.discord.DiscordClient
import com.github.syari.plugin.talk.with.near.player.templateMessage
import com.github.syari.plugin.talk.with.near.player.toColor
import com.github.syari.spigot.api.event.events
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
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
                    val mainHandItem = player.inventory.itemInMainHand
                    if (mainHandItem.isSimilar(item)) {
                        val targetPlayer = e.rightClicked as? Player ?: return@event
                        DiscordClient.mute(targetPlayer, false)?.let {
                            player.sendMessage(templateMessage("&c$it"))
                        }
                    }
                }
            }
            event<EntityDamageByEntityEvent> { e ->
                if (Mode.mode == Mode.Item) {
                    val player = e.damager as? Player ?: return@event
                    val mainHandItem = player.inventory.itemInMainHand
                    if (mainHandItem.isSimilar(item)) {
                        val targetPlayer = e.entity as? Player ?: return@event
                        e.isCancelled = true
                        DiscordClient.mute(targetPlayer, true)?.let {
                            player.sendMessage(templateMessage("&c$it"))
                        }
                    }
                }
            }
        }
    }
}
