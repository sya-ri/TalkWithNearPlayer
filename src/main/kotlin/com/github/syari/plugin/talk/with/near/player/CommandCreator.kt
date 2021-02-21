package com.github.syari.plugin.talk.with.near.player

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.plugin.talk.with.near.player.mode.AutoGroupOnMove
import com.github.syari.plugin.talk.with.near.player.mode.Mode
import com.github.syari.plugin.talk.with.near.player.mode.ToggleMuteUseItem
import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.util.uuid.UUIDPlayer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object CommandCreator {
    fun create() {
        plugin.command("talk-with-near-player") {
            aliases = listOf("twnp")
            permission = "twnp.player"
            tab {
            }
            execute {
                sender.sendMessage("Talk With Near Player")
            }
        }
        plugin.command("talk-with-near-player-admin") {
            aliases = listOf("twnpa")
            permission = "twnp.admin"
            tab {
                argument { add("mode", "item", "auto", "reload") }
                argument("mode") { addAll(Mode.values().map(Mode::name)) }
                argument("auto") { add("radius", "player") }
                argument("auto player") { add("add", "remove", "list") }
                argument("auto player add") { addAll(plugin.server.offlinePlayers.mapNotNull(OfflinePlayer::getName)) }
                argument("auto player remove") { addAll(AutoGroupOnMove.owners.map(UUIDPlayer::displayName)) }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "mode" -> {
                        val mode = args.lowerOrNull(1)?.let(Mode::get)
                        if (mode != null) {
                            sender.sendMessage(templateMessage("モードを &6${Mode.mode} &fから &6$mode &fに変更しました"))
                            Mode.mode = mode
                            Mode.applyMode()
                            ConfigLoader.setMode(sender, mode)
                        } else {
                            sender.sendMessage(templateMessage("現在のモードは &6${Mode.mode} &fです"))
                        }
                    }
                    "item" -> {
                        val player = sender as? Player ?: return@execute run {
                            sender.sendMessage(templateMessage("&cプレイヤーからのみ実行出来るコマンドです"))
                        }
                        player.inventory.addItem(ToggleMuteUseItem.item)
                    }
                    "auto" -> {
                        when (args.lowerOrNull(1)) {
                            "radius" -> {
                                val radius = args.lowerOrNull(2)?.toDoubleOrNull()
                                if (radius != null) {
                                    sender.sendMessage(templateMessage("声が聞こえる範囲を &6${AutoGroupOnMove.radius} &fから &6$radius &fに変更しました"))
                                    AutoGroupOnMove.radius = radius
                                    ConfigLoader.setAutoRadius(sender, radius)
                                } else {
                                    sender.sendMessage(templateMessage("声が聞こえる範囲は &6${AutoGroupOnMove.radius} &fになっています"))
                                }
                            }
                            "player" -> {
                                @Suppress("DEPRECATION")
                                when (args.lowerOrNull(2)) {
                                    "add" -> {
                                        val arg = args.getOrNull(3) ?: return@execute run {
                                            sender.sendMessage(templateMessage("&a/$label auto remove <UUID/PlayerName> &7自動モードでのオーナープレイヤーを削除します"))
                                        }
                                        val uuidPlayer = UUIDPlayer.from(arg) ?: UUIDPlayer.Companion.from(Bukkit.getOfflinePlayer(arg))
                                        if (AutoGroupOnMove.owners.contains(uuidPlayer)) {
                                            sender.sendMessage(templateMessage("&6${uuidPlayer.displayName} &cはオーナープレイヤーです"))
                                        } else {
                                            AutoGroupOnMove.owners.add(uuidPlayer)
                                            ConfigLoader.setAutoPlayer(sender, AutoGroupOnMove.owners)
                                            AutoGroupOnMove.createVoiceChannel(uuidPlayer)
                                            sender.sendMessage(templateMessage("&6${uuidPlayer.displayName} &fをオーナープレイヤーに追加しました"))
                                        }
                                    }
                                    "remove" -> {
                                        val arg = args.getOrNull(3) ?: return@execute run {
                                            sender.sendMessage(templateMessage("&a/$label auto add <UUID/PlayerName> &7自動モードでのオーナープレイヤーを追加します"))
                                        }
                                        val uuidPlayer = UUIDPlayer.from(arg) ?: UUIDPlayer.Companion.from(Bukkit.getOfflinePlayer(arg))
                                        if (AutoGroupOnMove.owners.contains(uuidPlayer)) {
                                            AutoGroupOnMove.owners.remove(uuidPlayer)
                                            ConfigLoader.setAutoPlayer(sender, AutoGroupOnMove.owners)
                                            AutoGroupOnMove.removeVoiceChannel(uuidPlayer)
                                            sender.sendMessage(templateMessage("&6${uuidPlayer.displayName} &fをオーナープレイヤーから削除しました"))
                                        } else {
                                            sender.sendMessage(templateMessage("&6${uuidPlayer.displayName} &cはオーナープレイヤーではありません"))
                                        }
                                    }
                                    "list" -> {
                                        val nameList = AutoGroupOnMove.owners.joinToString { it.displayName }
                                        sender.sendMessage(templateMessage("会話部屋のオーナー: &7$nameList"))
                                    }
                                    else -> {
                                        sender.sendMessage(
                                            templateMessage(
                                                """
                                                    コマンド一覧
                                                    &7- &a/$label auto player add <UUID/PlayerName> &7自動モードでのオーナープレイヤーを追加します
                                                    &7- &a/$label auto player remove <UUID/PlayerName> &7自動モードでのオーナープレイヤーを削除します
                                                    &7- &a/$label auto player list &7自動モードでのオーナープレイヤーの一覧を確認します
                                                """.trimIndent()
                                            )
                                        )
                                    }
                                }
                            }
                            else -> {
                                sender.sendMessage(
                                    templateMessage(
                                        """
                                            コマンド一覧
                                            &7- &a/$label auto radius [Radius] &7声が聞こえる範囲を設定します
                                            &7- &a/$label auto player add <UUID/PlayerName> &7自動モードでのオーナープレイヤーの一覧を確認します
                                            &7- &a/$label auto player remove <UUID/PlayerName> &7自動モードでのオーナープレイヤーの一覧を確認します
                                            &7- &a/$label auto player list &7自動モードでのオーナープレイヤーの一覧を確認します
                                        """.trimIndent()
                                    )
                                )
                            }
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
                                    &7- &a/$label mode [Auto/Item] &7モードの切り替えをします
                                    &7- &a/$label item &7アイテムモードでのミュート切り替え用のアイテムを入手します
                                    &7- &a/$label auto radius [Radius] &7声が聞こえる範囲を設定します
                                    &7- &a/$label auto player add <UUID/PlayerName> &7自動モードでのオーナープレイヤーの一覧を確認します
                                    &7- &a/$label auto player remove <UUID/PlayerName> &7自動モードでのオーナープレイヤーの一覧を確認します
                                    &7- &a/$label auto player list &7自動モードでのオーナープレイヤーの一覧を確認します
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
