package com.github.syari.plugin.talk.with.near.player.mode

enum class Mode(val key: String) {
    Item("item"),
    Auto("auto");

    companion object {
        var mode = Item

        fun get(key: String) = values().firstOrNull { it.key.equals(key, true) }

        fun applyMode() {
            if (mode == Auto) {
                AutoGroupOnMove.createAllVoiceChannel()
                AutoGroupOnMove.registerTask()
            } else {
                AutoGroupOnMove.clearVoiceChannels()
                AutoGroupOnMove.unregisterTask()
            }
        }
    }
}
