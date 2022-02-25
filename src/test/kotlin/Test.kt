package org.laolittle.plugin.genshin

fun main(args: Array<String>) {
    repeat(100) {
        println(Math.random())
    }

    val cssxsh = Cssxsh() // old Cssxsh()
    cssxsh.调教()
}

interface BT {
    fun 调教()
}

interface 滑稽 {
    fun PR()
}

class Cssxsh: BT, 滑稽 {
    override fun PR() {
        TODO("Not yet implemented")
    }

    override fun 调教() {
        TODO("Not yet implemented")
    }
}