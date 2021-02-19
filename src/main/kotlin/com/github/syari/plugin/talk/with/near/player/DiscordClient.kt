package com.github.syari.plugin.talk.with.near.player

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import javax.security.auth.login.LoginException

object DiscordClient {
    private var jda: JDA? = null
    private var lastToken: String? = null

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
}
