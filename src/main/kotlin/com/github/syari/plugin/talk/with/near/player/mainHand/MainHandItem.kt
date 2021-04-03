package com.github.syari.plugin.talk.with.near.player.mainHand

import com.github.syari.spigot.api.nms.SERVER_VERSION
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

interface MainHandItem {
    fun getFromInventory(inventory: PlayerInventory): ItemStack

    companion object {
        val mainHandItem = if (9 <= SERVER_VERSION) {
            MainHandItem9
        } else {
            MainHandItem8
        }
    }
}
