package com.github.syari.plugin.talk.with.near.player.mainHand

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

object MainHandItem8 : MainHandItem {
    @Suppress("Deprecation")
    override fun getFromInventory(inventory: PlayerInventory): ItemStack {
        return inventory.itemInHand
    }
}
