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
                argument { add("mode", "item", "auto", "reload") }
                argument("mode") { addAll(Mode.values().map(Mode::name)) }
                argument("auto") { add("list") }
                argument { add("item", "reload") }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "mode" -> {
                        val mode = args.lowerOrNull(1)?.let(Mode::get)
                        if (mode != null) {
                            sender.sendMessage(templateMessage("モードを ${Mode.mode} から $mode に変更しました"))
                            Mode.mode = mode
                            ConfigLoader.setMode(sender, mode)
                        } else {
                            sender.sendMessage(templateMessage("現在のモードは ${Mode.mode} &fです"))
                        }
                    }
                    "item" -> {
                        val player = sender as? Player ?: return@execute run {
                            sender.sendMessage(templateMessage("&cプレイヤーからのみ実行出来るコマンドです"))
                        }
                        player.inventory.addItem(ToggleSpeak.item)
                    }
                    "auto" -> {
                        sender.sendMessage(templateMessage("会話部屋のオーナー: &7${AutoGroup.owners.joinToString { it.offlinePlayer.name ?: it.uniqueId.toString() }}"))
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
                                    &7- &a/$label mode [Auto/Item] &7モードの切り替えをします
                                    &7- &a/$label item &7アイテムモードでのミュート切り替え用のアイテムを入手します
                                    &7- &a/$label auto &7自動モードでのオーナープレイヤーの一覧を確認します
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
