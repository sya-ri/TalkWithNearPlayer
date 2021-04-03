package com.github.syari.plugin.talk.with.near.player.mainHand

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

object MainHandItem9 : MainHandItem {
    override fun getFromInventory(inventory: PlayerInventory): ItemStack {
        return inventory.itemInMainHand
    }
}
