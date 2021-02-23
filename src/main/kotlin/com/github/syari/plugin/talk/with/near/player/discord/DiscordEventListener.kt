package com.github.syari.plugin.talk.with.near.player.discord

import com.github.syari.plugin.talk.with.near.player.templateMessage
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object DiscordEventListener : ListenerAdapter() {
    private val alreadyFailureMessage = mutableSetOf<Long>()

    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.isBot) return
        val authorId = event.author.idLong
        val authPlayer = DiscordMember.auth(event.message.contentRaw, authorId)
        if (authPlayer != null) {
            authPlayer.player?.sendMessage(templateMessage("&6${event.author.asTag} &fと連携しました"))
            event.channel.sendMessage("認証に成功しました")
        } else {
            if (alreadyFailureMessage.contains(authorId)) return
            alreadyFailureMessage.add(authorId)
            event.channel.sendMessage("`/twnp connect` というコマンドをサーバー内で実行して認証コードを取得してください")
        }.complete()
    }
}
