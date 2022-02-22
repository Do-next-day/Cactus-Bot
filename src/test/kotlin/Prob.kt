package org.laolittle.plugin

import kotlin.system.measureNanoTime

fun main() {
    fun chance(bar: Int): Double{
        if (bar == 70) return 0.006
        val foo = (0.994 / 210) * (bar - 70)
        return foo + chance(bar - 1)
    }

    val start = 1
    val end = 114514.toLong()
    println(measureNanoTime {
        println((start..end).sum())
    })

    println(measureNanoTime {
        println((end * (end + 1)) shr 1)
    })

    for (i in 71..90) println(chance(i))

   // for (i in 70..71) println(g(i))
    repeat(90) {
        when (val i = it) {
            in 1..50 -> 6.0
            in 50..70 -> 6 + (i-50) *0.02
            in 71..90 -> {
                val foo = 90 - i
                6 + 0.994 / ((foo*(1 + foo)) shr 1)
            }
            else -> 100.0
        }.also {
            println(it)
        }
    }
}