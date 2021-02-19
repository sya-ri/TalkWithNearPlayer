package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CommandCreator {
    fun create() {
        plugin.command("talk-with-near-player") {
            aliases = listOf("twnp")
            tab {
                argument { add("item", "move", "reload") }
                argument("move") { addAll(plugin.server.onlinePlayers.map(Player::getName)) }
                argument("move *") { add("wait", "talk") }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "item" -> {
                        val player = sender as? Player ?: return@execute run {
                            sender.sendMessage(templateMessage("&cプレイヤーからのみ実行出来るコマンドです"))
                        }
                        player.inventory.addItem(ToggleSpeak.item)
                    }
                    "move" -> {
                        val player = args.getOrNull(1)?.let(Bukkit::getPlayer) ?: return@execute run {
                            sender.sendMessage(templateMessage("&cプレイヤーが見つかりませんでした"))
                        }
                        val (channelId, noConfigMessage) = when (args.lowerOrNull(2)) {
                            "wait" -> {
                                DiscordClient.waitRoomId to "待機部屋が設定されていません"
                            }
                            "talk" -> {
                                DiscordClient.talkRoomId to "会話部屋が設定されていません"
                            }
                            else -> {
                                return@execute sender.sendMessage(
                                    templateMessage(
                                        """
                                            コマンド一覧
                                            &7- &a/$label move <Player> wait &7待機部屋にプレイヤーを移動させます
                                            &7- &a/$label move <Player> talk &7会話部屋にプレイヤーを移動させます
                                        """.trimIndent()
                                    )
                                )
                            }
                        }
                        if (channelId != null) {
                            DiscordClient.move(player, channelId)?.let {
                                sender.sendMessage(templateMessage("&c$it"))
                            }
                        } else {
                            sender.sendMessage(templateMessage("&c$noConfigMessage"))
                        }
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
                                    &7- &a/$label move <Player> wait &7待機部屋にプレイヤーを移動させます
                                    &7- &a/$label move <Player> talk &7会話部屋にプレイヤーを移動させます
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
