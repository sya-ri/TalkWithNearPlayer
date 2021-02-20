package com.github.syari.plugin.talk.with.near.player

enum class Mode(val key: String) {
    Item("item"),
    Auto("auto");

    companion object {
        var mode = Item

        fun get(key: String) = values().firstOrNull { it.key.equals(key, true) }
    }
}
