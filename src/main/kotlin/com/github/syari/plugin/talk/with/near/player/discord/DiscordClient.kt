package com.github.syari.plugin.talk.with.near.player.discord

import com.github.syari.plugin.talk.with.near.player.Main.Companion.plugin
import com.github.syari.spigot.api.scheduler.runTask
import com.github.syari.spigot.api.scheduler.runTaskLater
import com.github.syari.spigot.api.util.uuid.UUIDPlayer
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.exceptions.RateLimitedException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.bukkit.entity.Player
import java.util.concurrent.RejectedExecutionException
import javax.security.auth.login.LoginException

object DiscordClient {
    private var jda: JDA? = null
    private var lastToken: String? = null

    var guildId: Long? = null

    private val guild
        get() = guildId?.let { jda?.getGuildById(it) }

    val isLogin
        get() = jda != null

    val botName
        get() = jda?.selfUser?.name

    fun checkBot(): String {
        if (lastToken == null) return "トークンが設定されていません。 (config.yml >> discord.token)"
        val botName = botName ?: return "ボットとの接続が出来ていません。トークンを見直してみてください。"
        if (guildId == null) return "Discordサーバーが設定されていません。 (config.yml >> discord.guild)"
        val guild = guild ?: return "Discordサーバーが見つかりませんでした。ギルドIDを見直してみてください。"
        return "$botName と接続中です。${guild.name} 内のボイスチャンネルに参加してください。"
    }

    fun login(token: String?) {
        if (token != lastToken) {
            jda?.shutdownNow()
            jda = token?.let {
                try {
                    val gatewayIntents = listOf(GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGES)
                    JDABuilder.create(gatewayIntents).apply {
                        setToken(it)
                        disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                        addEventListeners(DiscordEventListener)
                    }.build()
                } catch (ex: LoginException) {
                    ex.printStackTrace()
                    null
                }
            }
            lastToken = token
        }
    }

    private val lastMute = mutableMapOf<Long, Boolean>()
    private val muteCoolTime = mutableSetOf<UUIDPlayer>()

    fun mute(player: Player, mute: Boolean): String? {
        val uuidPlayer = UUIDPlayer.from(player)
        if (muteCoolTime.contains(uuidPlayer)) return null
        muteCoolTime.add(uuidPlayer)
        plugin.runTaskLater(10) {
            muteCoolTime.remove(uuidPlayer)
        }
        val guild = guild ?: return "ギルドが見つかりませんでした"
        val userId = DiscordMember.get(player)?.discordUserId ?: return "アカウントの紐付けがされていません"
        if (lastMute[userId] == mute) return null
        lastMute[userId] = mute
        val member = guild.getMemberById(userId) ?: return "ユーザーが見つかりませんでした"
        return try {
            member.mute(mute).submit().join()
            null
        } catch (ex: InsufficientPermissionException) {
            "ボットの権限がありません"
        } catch (ex: IllegalArgumentException) {
            "存在しないメンバーです"
        } catch (ex: IllegalStateException) {
            null
        }
    }

    private val lastRoom = mutableMapOf<Long, Long>()
    private val moveCoolTime = mutableSetOf<UUIDPlayer>()

    fun move(player: Player, room: Long): String? {
        val uuidPlayer = UUIDPlayer.from(player)
        if (moveCoolTime.contains(uuidPlayer)) return null
        moveCoolTime.add(uuidPlayer)
        plugin.runTaskLater(15) {
            moveCoolTime.remove(uuidPlayer)
        }
        val guild = guild ?: return "ギルドが見つかりませんでした"
        val userId = DiscordMember.get(player)?.discordUserId ?: return "アカウントの紐付けがされていません"
        if (lastRoom[userId] == room) return null
        lastRoom[userId] = room
        val member = guild.getMemberById(userId) ?: return "ユーザーが見つかりませんでした"
        val channel = jda?.getVoiceChannelById(room) ?: return "チャンネルが見つかりませんでした"
        plugin.runTask(true) {
            try {
                guild.moveVoiceMember(member, channel).complete()
            } catch (ex: RateLimitedException) {
                ex.printStackTrace()
            }
        }
        return null
    }

    sealed class CreateResult {
        class Success(val channel: VoiceChannel) : CreateResult()
        class Failure(val message: String) : CreateResult()
    }

    fun crate(name: String): CreateResult {
        val guild = guild ?: return CreateResult.Failure("ギルドが見つかりませんでした")
        val channel = try {
            guild.createVoiceChannel(name).complete()
        } catch (ex: RejectedExecutionException) {
            return CreateResult.Failure("チャンネルの作成に失敗しました")
        }
        return CreateResult.Success(channel)
    }

    fun removeChannel(id: Long) {
        jda?.getVoiceChannelById(id)?.delete()?.complete()
    }

    fun addMutePermission(id: Long): String? {
        val guild = guild ?: return "ギルドが見つかりませんでした"
        val channel = jda?.getVoiceChannelById(id) ?: return "チャンネルが見つかりませんでした"
        return try {
            val overrideAction = channel.getPermissionOverride(guild.publicRole)?.manager ?: channel.createPermissionOverride(guild.publicRole)
            overrideAction.setDeny(Permission.VOICE_SPEAK).complete()
            null
        } catch (ex: InsufficientPermissionException) {
            "ボットの権限がありません"
        } catch (ex: IllegalArgumentException) {
            "存在しないメンバーです"
        }
    }

    fun getUser(id: Long): User? {
        return jda?.getUserById(id)
    }
}
