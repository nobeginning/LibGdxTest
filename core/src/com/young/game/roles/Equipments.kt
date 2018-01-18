package com.young.game.roles

class CoatV1 : Equipment("CoatV1") {
    override fun getDefenseValue(): Long = 70
    override fun getHealthValue(): Long = 100
    override fun getMagicValue(): Long = 20
    override fun getResistance(magicType:Magic.Type): Int = when(magicType){
        Magic.Type.TYPE_IMMORTAL_WIND->70
        Magic.Type.TYPE_NORMAL->30
        else->0
    }
}

class SwordV1 : Equipment("SwordV1") {
    override fun getAttackValue(): Long = 120
}

class NecklaceV1 : Equipment("NecklaceV1") {
    override fun getHealthValue(): Long = 100
    override fun getMagicValue(): Long = 100
}

class ShoesV1 : Equipment("ShoesV1") {
    override fun getSpeedValue(): Long = 35
}

class TrousersV1 : Equipment("TrousersV1") {
    override fun getDefenseValue(): Long = 50
}