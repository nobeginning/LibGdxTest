package com.young.game.roles

abstract class Magic(val name:String, val type:Type) {

    enum class Type{

        TYPE_NORMAL,

        TYPE_IMMORTAL_WIND,
        TYPE_IMMORTAL_THUNDER,
        TYPE_IMMORTAL_WATER,
        TYPE_IMMORTAL_FIRE,

        TYPE_HUMAN_CHAOS,
        TYPE_HUMAN_SEAL,
        TYPE_HUMAN_SLEEP,
        TYPE_HUMAN_POISON,

        TYPE_DEMON_DETERRENT,
        TYPE_DEMON_DEFENSE_UP,
        TYPE_DEMON_POWER_UP,
        TYPE_DEMON_SPEED_UP,

        TYPE_GHOST_HEALTH_UP,
        TYPE_GHOST_COLD_FIRE,
        TYPE_GHOST_DEFENSE_UP,
        TYPE_GHOST_FORGOTTEN,

    }
    val maxProficiency:Int = 25000

    open fun getTargetCount():Int = 0
    open fun getConsume():Long = 0
    open fun getAttack(level:Int):Long = 0

}

class MagicImmortalV5: Magic("MagicImmortalV5", Type.TYPE_IMMORTAL_WIND) {
    val baseAttack = 5000
    val baseConsume = 5000
    val maxConsume = 18000
    var proficiency:Int = 0

    override fun getTargetCount(): Int  = when {
        proficiency>=5621 -> 5
        proficiency>=558 -> 4
        else -> 3
    }

    override fun getConsume(): Long {
        return baseConsume+((maxConsume-baseConsume)*proficiency/maxProficiency).toLong()
    }

    override fun getAttack(level:Int): Long {
        return (baseAttack*(level/160.0)).toLong() + (proficiency*0.8).toLong()
    }
}

class MagicImmortalV4: Magic("MagicImmortalV4", Type.TYPE_IMMORTAL_WIND) {
    val baseAttack = 7500
    val baseConsume = 3000
    val maxConsume = 8000
    var proficiency:Int = 0

    override fun getTargetCount(): Int = 1

    override fun getConsume(): Long {
        return baseConsume+((maxConsume-baseConsume)*proficiency/maxProficiency).toLong()
    }

    override fun getAttack(level:Int): Long {
        return (baseAttack*(level/160.0)).toLong() + (proficiency*1.2).toLong()
    }
}

class MagicImmortalV3: Magic("MagicImmortalV3", Type.TYPE_IMMORTAL_WIND) {
    val baseAttack = 2000
    val baseConsume = 1500
    val maxConsume = 5000
    var proficiency:Int = 0

    override fun getTargetCount(): Int = when {
        proficiency>=16610 -> 5
        proficiency>=5215 -> 4
        proficiency>=720 -> 3
        else -> 2
    }

    override fun getConsume(): Long {
        return baseConsume+((maxConsume-baseConsume)*proficiency/maxProficiency).toLong()
    }

    override fun getAttack(level:Int): Long {
        return (baseAttack*(level/160.0)).toLong() + (proficiency*0.5).toLong()
    }
}

class MagicImmortalV2: Magic("MagicImmortalV2", Type.TYPE_IMMORTAL_WIND) {
    val baseAttack = 2500
    val baseConsume = 600
    val maxConsume = 1500
    var proficiency:Int = 0

    override fun getTargetCount(): Int = 1

    override fun getConsume(): Long {
        return baseConsume+((maxConsume-baseConsume)*proficiency/maxProficiency).toLong()
    }

    override fun getAttack(level:Int): Long {
        return (baseAttack*(level/160.0)).toLong() + (proficiency*0.6).toLong()
    }
}

class MagicImmortalV1: Magic("MagicImmortalV1", Type.TYPE_IMMORTAL_WIND) {
    val baseAttack = 1000
    val baseConsume = 100
    val maxConsume = 600
    var proficiency:Int = 0

    override fun getTargetCount(): Int = 1

    override fun getConsume(): Long {
        return baseConsume+((maxConsume-baseConsume)*proficiency/maxProficiency).toLong()
    }

    override fun getAttack(level:Int): Long {
        return (baseAttack*(level/160.0)).toLong() + (proficiency*0.5).toLong()
    }
}

fun main(args: Array<String>) {
    val magic = MagicImmortalV4()
    magic.proficiency = 1
    println(magic.getConsume())
    magic.proficiency = 10
    println(magic.getConsume())
    magic.proficiency = 100
    println(magic.getConsume())
    magic.proficiency = 1000
    println(magic.getConsume())
    magic.proficiency = 3000
    println(magic.getConsume())
    magic.proficiency = 6000
    println(magic.getConsume())
    magic.proficiency = 10000
    println(magic.getConsume())
}