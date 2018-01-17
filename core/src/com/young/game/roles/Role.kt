package com.young.game.roles

abstract class Role {
    var maxHp:Long = 0
    var maxMp:Long = 0
    var hp:Long = 0
    var mp:Long = 0


    fun isDead():Boolean = hp==0L

}