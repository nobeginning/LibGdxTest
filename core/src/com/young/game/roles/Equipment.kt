package com.young.game.roles

abstract class Equipment(val name:String) {

    open fun getAttackValue():Long = 0

    open fun getDefenseValue():Long = 0

    open fun getSpeedValue():Long = 0

    open fun getHealthValue():Long = 0

    open fun getMagicValue():Long = 0

    open fun getResistance(magicType:Magic.Type):Int = 0

}