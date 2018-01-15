package com.young.game.roles

enum class Race constructor(val hpRate:Double, val mpRate:Double, val apRate:Double, val spRate:Double,
                            val hpInit:Int, val mpInit:Int, val apInit:Int, val spInit:Int){
    RACE_HUMAN(1.2, 1.0, 0.95, 0.8, 360, 300, 70, 8),
    RACE_IMMORTAL(1.0, 1.3, 0.7, 1.0, 300, 390, 60, 10),
    RACE_DEMON(1.1, 0.6, 1.3, 1.0, 270, 350, 80, 9),
    RACE_GHOST(1.2, 1.0, 0.95, 0.85, 330, 210, 80, 10)
}