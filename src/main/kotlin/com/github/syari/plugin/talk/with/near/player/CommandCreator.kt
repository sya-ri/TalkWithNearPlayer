package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import org.bukkit.entity.Player

object CommandCreator {
    fun create() {
        plugin.command("talk-with-near-player") {
            aliases = listOf("twnp")
            tab {
                argument { add("item", "reload") }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "item" -> {
                        val player = sender as? Player ?: return@execute run {
                            sender.sendMessage(templateMessage("&cプレイヤーからのみ実行出来るコマンドです"))
                        }
                        player.inventory.addItem(ToggleSpeak.item)
                    }
                    "reload" -> {
                        sender.sendMessage(templateMessage("コンフィグをリロードします"))
                        ConfigLoader.load(sender)
                        sender.sendMessage(templateMessage("コンフィグをリロードしました"))
                    }
                    else -> {
                        sender.sendMessage(
                            templateMessage(
                                """
                                    コマンド一覧
                                    &7- &a/$label item &7ミュート切り替え用のアイテムを入手します
                                    &7- &a/$label reload &7コンフィグをリロードします
                                """.trimIndent()
                            )
                        )
                    }
                }
            }
        }
    }
}
