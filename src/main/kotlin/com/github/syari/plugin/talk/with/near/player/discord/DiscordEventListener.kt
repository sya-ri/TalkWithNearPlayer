package com.github.syari.plugin.talk.with.near.player.discord

import com.github.syari.plugin.talk.with.near.player.templateMessage
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

object DiscordEventListener : ListenerAdapter() {
    override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.isBot) return
        val authPlayer = DiscordMember.auth(event.message.contentRaw, event.author.idLong)
        if (authPlayer != null) {
            authPlayer.player?.sendMessage(templateMessage("&6${event.author.asTag} &fと連携しました"))
            event.channel.sendMessage("認証に成功しました")
        } else {
            event.channel.sendMessage("認証に失敗しました")
        }.complete()
    }
}
