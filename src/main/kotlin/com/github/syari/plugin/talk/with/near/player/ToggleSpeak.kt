package com.github.syari.plugin.talk.with.near.player

import com.github.syari.spigot.api.event.register.EventRegister
import com.github.syari.spigot.api.event.register.Events
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack

object ToggleSpeak : EventRegister {
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

    private fun movePlayer(trigger: Player, player: Player, channelId: Long) {
        DiscordClient.move(player, channelId)?.let {
            trigger.sendMessage(templateMessage("&c$it"))
        }
    }

    override fun Events.register() {
        event<PlayerInteractEntityEvent> { e ->
            val player = e.player
            val mainHandItem = player.inventory.itemInMainHand
            if (mainHandItem.isSimilar(item)) {
                val targetPlayer = e.rightClicked as? Player ?: return@event
                DiscordClient.talkRoomId?.let {
                    movePlayer(player, targetPlayer, it)
                } ?: return@event run {
                    player.sendMessage(templateMessage("&c会話部屋が設定されていません"))
                }
            }
        }
        event<EntityDamageByEntityEvent> { e ->
            val player = e.damager as? Player ?: return@event
            val mainHandItem = player.inventory.itemInMainHand
            if (mainHandItem.isSimilar(item)) {
                val targetPlayer = e.entity as? Player ?: return@event
                e.isCancelled = true
                DiscordClient.waitRoomId?.let {
                    movePlayer(player, targetPlayer, it)
                } ?: return@event run {
                    player.sendMessage(templateMessage("&c待機部屋が設定されていません"))
                }
            }
        }
    }
}
