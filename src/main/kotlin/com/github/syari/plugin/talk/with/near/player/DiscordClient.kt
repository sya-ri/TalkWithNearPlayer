package com.github.syari.plugin.talk.with.near.player

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.exceptions.RateLimitedException
import net.dv8tion.jda.api.requests.GatewayIntent
import org.bukkit.entity.Player
import javax.security.auth.login.LoginException

object DiscordClient {
    private var jda: JDA? = null
    private var lastToken: String? = null

    var guildId: Long? = null
    var waitRoomId: Long? = null
    var talkRoomId: Long? = null

    val isLogin
        get() = jda != null

    fun login(token: String?) {
        if (token != lastToken) {
            jda?.shutdownNow()
            jda = token?.let {
                try {
                    JDABuilder.create(GatewayIntent.GUILD_VOICE_STATES).apply {
                        setToken(it)
                    }.build()
                } catch (ex: LoginException) {
                    ex.printStackTrace()
                    null
                }
            }
            lastToken = token
        }
    }

    fun move(player: Player, room: Long): String? {
        val guild = guildId?.let { jda?.getGuildById(it) } ?: return "ギルドが見つかりませんでした"
        val userId = DiscordMember.get(player)?.discordUserId ?: return "アカウントの紐付けがされていません"
        val member = guild.getMemberById(userId) ?: return "ユーザーが見つかりませんでした"
        val channel = jda?.getVoiceChannelById(room) ?: return "チャンネルが見つかりませんでした"
        try {
            guild.moveVoiceMember(member, channel).complete()
        } catch (ex: RateLimitedException) {
            ex.printStackTrace()
        }
        return null
    }
}
