package com.young.game.roles

import java.util.*

class Player(val race: Race, val name: String = "Player") {

    var animProxy:RealPlayer? = null

    var level: Int = 0
        set(value) {
            field = value
            reset()
        }
    var hpPoint: Int = 0
        set(value) {
            field = value
            reset()
        }
    var mpPoint: Int = 0
        set(value) {
            field = value
            reset()
        }
    var apPoint: Int = 0
    var spPoint: Int = 0
    var maxHp: Long = 0
    var hp: Long = 0
    var maxMp: Long = 0
    var mp: Long = 0

    var magic: Magic? = null

    var equipmentCoat: Equipment? = null
        set(value) {
            field = value
            reset()
        }
    var equipmentTrousers: Equipment? = null
        set(value) {
            field = value
            reset()
        }
    var equipmentNecklace: Equipment? = null
        set(value) {
            field = value
            reset()
        }
    var equipmentShoes: Equipment? = null
        set(value) {
            field = value
            reset()
        }
    var equipmentWeapon: Equipment? = null
        set(value) {
            field = value
            reset()
        }

    init {
        maxHp = getHealthValue()
        hp = maxHp
        maxMp = getMagicValue()
        mp = maxMp
    }

    override fun toString(): String {
        return name.plus("\r\n")
                .plus("HP:").plus(getHealthValue()).plus("  Self:").plus(getSelfHealth()).plus("  Equipment:").plus(getEquipmentHealth()).plus("\r\n")
                .plus("MP:").plus(getMagicValue()).plus("  Self:").plus(getSelfMagic()).plus("  Equipment:").plus(getEquipmentMagicValue()).plus("\r\n")
                .plus("AP:").plus(getAttackValue()).plus("  Self:").plus(getSelfAttack()).plus("  Equipment:").plus(getEquipmentAttack()).plus("\r\n")
                .plus("SP:").plus(getSpeedValue()).plus("  Self:").plus(getSelfSpeed()).plus("  Equipment:").plus(getEquipmentSpeed()).plus("\r\n")
                .plus("DF:").plus(getDefenseValue())
    }

    fun attack(target: Player) {
        val attackValue = getAttackValue()
        val targetDefenseValue = target.getDefenseValue()
        var value = attackValue - targetDefenseValue
        val resistance = target.getResistance(Magic.Type.TYPE_NORMAL)
        value = (value * ((100-resistance) / 100.0)).toLong()
        if (value < 1) {
            value = 1
        }
        target.hp = target.hp - value
        if (target.hp < 0) {
            target.hp = 0
        }
        println("${name}攻击了${target.name}，扣血${value}点 -- ${name}攻击：${attackValue}，${target.name}防御：${targetDefenseValue}，抗物理：${resistance}% -- ${target.name}剩余${target.hp}点血")
    }

    fun attack(targets: List<Player>, magic: Magic) {
        val consume = magic.getConsume()
        if (consume > mp) {
            return
        }
        println("${name}使用了法术${magic.name}，消耗了MP${consume}点")
        for (target in targets){
            attack(target, magic)
        }
        mp -= consume
        println("${name}使用了法术${magic.name}，剩余MP${mp}点")
    }

    fun attack(target: Player, magic: Magic){
        val magicType = magic.type
        val attackValue = magic.getAttack(level)
        val resistance = target.getResistance(magicType)
        var value = (attackValue * ((100 - resistance) / 100.0)).toLong()
        if (value < 1) {
            value = 1
        }
        target.hp = target.hp - value
        if (target.hp < 0) {
            target.hp = 0
            target.animProxy?.run {
                dead()
            }
        }
        target.animProxy?.run {
            powerDown(value)
        }
        println("${name}用法术${magic.name}攻击了${target.name}，扣血${value}点 -- ${name}攻击：${attackValue}，抗性：${resistance}% -- ${target.name}剩余${target.hp}点血")
    }

    fun getAttackValue(): Long {
        var attack = getSelfAttack()
        attack += getEquipmentAttack()
        return attack
    }

    fun getDefenseValue(): Long = getEquipmentDefense()

    fun getSpeedValue(): Long {
        var speed = getSelfSpeed()
        speed += getEquipmentSpeed()
        return speed
    }

    fun getHealthValue(): Long {
        var health = getSelfHealth()
        health += getEquipmentHealth()
        return health
    }

    fun getMagicValue(): Long {
        var mgc = getSelfMagic()
        mgc += getEquipmentMagicValue()
        return mgc
    }

    private fun reset() {
        val preMaxHp = maxHp
        maxHp = getHealthValue()
        val diffHp = maxHp - preMaxHp
        if (diffHp > 0) {
            hp += diffHp
        }
        if (hp > maxHp) {
            hp = maxHp
        }

        val preMaxMp = maxMp
        maxMp = getMagicValue()
        val diffMp = maxMp - preMaxMp
        if (diffMp > 0) {
            mp += diffMp
        }
        if (mp > maxMp) {
            mp = maxMp
        }
    }

    /**
     * 抗性
     * @return 百分比
     */
    fun getResistance(magicType: Magic.Type): Int {
        var resistance = getSelfResistance(magicType)
        resistance += getEquipmentResistance(magicType)
        return resistance
    }

    private fun getSelfResistance(magicType: Magic.Type): Int {
        return when (magicType) {
            Magic.Type.TYPE_IMMORTAL_WIND -> {
                return when {
                    this.race == Race.RACE_IMMORTAL -> level / 4
                    this.race == Race.RACE_GHOST -> -20
                    this.race == Race.RACE_DEMON -> level / 8
                    else -> 0
                }
            }
            else -> 0
        }
    }

    private fun getEquipmentResistance(magicType: Magic.Type): Int {
        var resistance = 0
        equipmentCoat?.apply {
            resistance += getResistance(magicType)
        }
        equipmentTrousers?.apply {
            resistance += getResistance(magicType)
        }
        equipmentShoes?.apply {
            resistance += getResistance(magicType)
        }
        equipmentNecklace?.apply {
            resistance += getResistance(magicType)
        }
        equipmentWeapon?.apply {
            resistance += getResistance(magicType)
        }
        return resistance
    }

    private fun getSelfSpeed(): Long {
        return ((10 + spPoint + level) * race.spRate).toLong()
    }

    private fun getSelfAttack(): Long {
        return ((level + (100 - level) / 5) * (apPoint + level) * race.apRate / 5 + race.apInit).toLong()
    }

    private fun getSelfHealth(): Long {
        return ((level + (100 - level) / 5) * (hpPoint + level) * race.hpRate + race.hpInit).toLong()
    }


    private fun getSelfMagic(): Long {
        return ((level + (100 - level) / 5) * (mpPoint + level) * race.mpRate + race.mpInit).toLong()
    }

    private fun getEquipmentAttack(): Long {
        var attack: Long = 0
        equipmentCoat?.apply {
            attack += getAttackValue()
        }
        equipmentTrousers?.apply {
            attack += getAttackValue()
        }
        equipmentShoes?.apply {
            attack += getAttackValue()
        }
        equipmentNecklace?.apply {
            attack += getAttackValue()
        }
        equipmentWeapon?.apply {
            attack += getAttackValue()
        }
        return attack
    }


    private fun getEquipmentMagicValue(): Long {
        var magicValue: Long = 0
        equipmentCoat?.apply {
            magicValue += getMagicValue()
        }
        equipmentTrousers?.apply {
            magicValue += getMagicValue()
        }
        equipmentShoes?.apply {
            magicValue += getMagicValue()
        }
        equipmentNecklace?.apply {
            magicValue += getMagicValue()
        }
        equipmentWeapon?.apply {
            magicValue += getMagicValue()
        }
        return magicValue
    }

    private fun getEquipmentSpeed(): Long {
        var speed: Long = 0
        equipmentCoat?.apply {
            speed += getSpeedValue()
        }
        equipmentTrousers?.apply {
            speed += getSpeedValue()
        }
        equipmentShoes?.apply {
            speed += getSpeedValue()
        }
        equipmentNecklace?.apply {
            speed += getSpeedValue()
        }
        equipmentWeapon?.apply {
            speed += getSpeedValue()
        }
        return speed
    }

    private fun getEquipmentHealth(): Long {
        var health: Long = 0
        equipmentCoat?.apply {
            health += getHealthValue()
        }
        equipmentTrousers?.apply {
            health += getHealthValue()
        }
        equipmentShoes?.apply {
            health += getHealthValue()
        }
        equipmentNecklace?.apply {
            health += getHealthValue()
        }
        equipmentWeapon?.apply {
            health += getHealthValue()
        }
        return health
    }


    private fun getEquipmentDefense(): Long {
        var defense: Long = 0
        equipmentCoat?.apply {
            defense += getDefenseValue()
        }
        equipmentTrousers?.apply {
            defense += getDefenseValue()
        }
        equipmentShoes?.apply {
            defense += getDefenseValue()
        }
        equipmentNecklace?.apply {
            defense += getDefenseValue()
        }
        equipmentWeapon?.apply {
            defense += getDefenseValue()
        }
        return defense
    }

}

fun main(args: Array<String>) {
//    val player1 = Player(Race.RACE_DEMON, "魔族")
//    player1.level = 100
//    player1.equipmentCoat = CoatV1()
//    player1.equipmentNecklace = NecklaceV1()
//    player1.equipmentShoes = ShoesV1()
//    player1.equipmentTrousers = TrousersV1()
//    player1.equipmentWeapon = SwordV1()
//
//    println(player1)
//
//    val player2 = Player(Race.RACE_IMMORTAL, "仙族")
//    player2.level = 100
//    val magic = MagicImmortalV4()
//    magic.proficiency = 1000
//    player2.magic = magic
//    println(player2)
//
//    if (true){
//        player2.attack(player1, magic)
//        return
//    }

    val players = mutableListOf<Player>()

    val p1 = Player(Race.RACE_IMMORTAL, "P1")
    val p2 = Player(Race.RACE_IMMORTAL, "P2")
    val p3 = Player(Race.RACE_IMMORTAL, "P3")
    val p4 = Player(Race.RACE_IMMORTAL, "P4")
    val p5 = Player(Race.RACE_IMMORTAL, "P5")
    p1.level = 48
    p2.level = 51
    p3.level = 50
    p4.level = 53
    p5.level = 49
    p5.mpPoint = 190
    val magic5 = MagicImmortalV5()
    magic5.proficiency = 1
    val magic4 = MagicImmortalV3()
    magic4.proficiency = 3000
    val magic3 = MagicImmortalV3()
    magic3.proficiency = 6000
    val magic2 = MagicImmortalV3()
    magic2.proficiency = 3000
    val magic1 = MagicImmortalV3()
    magic1.proficiency = 10000
    p1.magic = magic1
    p2.magic = magic2
    p3.magic = magic3
    p4.magic = magic4
    p5.magic = magic5

    players.add(p1)
    players.add(p2)
    players.add(p3)
    players.add(p4)
    players.add(p5)

    Collections.sort(players, { o1, o2 -> (o2.getSpeedValue() - o1.getSpeedValue()).toInt() })

    val targets = mutableListOf<Player>()
    val t1 = Player(Race.RACE_DEMON, "T1")
    val t2 = Player(Race.RACE_DEMON, "T2")
    val t3 = Player(Race.RACE_DEMON, "T3")
    val t4 = Player(Race.RACE_DEMON, "T4")
    val t5 = Player(Race.RACE_DEMON, "T5")
    val t6 = Player(Race.RACE_DEMON, "T6")
    val t7 = Player(Race.RACE_DEMON, "T7")
    val t8 = Player(Race.RACE_DEMON, "T8")
    t1.level = 50
    t2.level = 61
    t3.level = 54
    t4.level = 40
    t5.level = 65
    t6.level = 67
    t7.level = 45
    t8.level = 30
    targets.add(t1)
    targets.add(t2)
    targets.add(t3)
    targets.add(t4)
    targets.add(t5)
    targets.add(t6)
    targets.add(t7)
    targets.add(t8)

    Collections.sort(targets, { o1, o2 -> (o2.getSpeedValue() - o1.getSpeedValue()).toInt() })

    val all = mutableListOf<Player>()
    all.addAll(players)
    all.addAll(targets)
    Collections.sort(all, { o1, o2 -> (o2.getSpeedValue() - o1.getSpeedValue()).toInt() })

    var isContinue = true
    while (isContinue) {
        for (item in all){
            if (item.hp==0L){
                println("${item.name}已死亡")
                continue
            }
            var targetList:List<Player>
            if (players.contains(item)){
                targetList = targets
            } else {
                targetList = players
            }

            var ts:List<Player>
            var mgc = item.magic
            var mgcToUse:Magic?
            if (mgc!=null){
                val consume = mgc.getConsume()
                if (item.mp>=consume){
                    mgcToUse = mgc
                    ts = getTarget(mgc, targetList)
                } else {
                    mgcToUse = null
                    ts = getTarget(null, targetList)
                }
            }else {
                mgcToUse = null
                ts = getTarget(null, targetList)
            }
            if (mgcToUse!=null){
                item.attack(ts, mgcToUse)
            } else {
                if (ts.isNotEmpty()) {
                    item.attack(ts[0])
                }
            }
            var stillAlive = false
            for (t in targetList){
                if (t.hp>0){
                    stillAlive = true
                }
            }
            if (!stillAlive){
                isContinue = false
                println("结束")
                break
            }
            Thread.sleep(1000)
        }
    }
}

fun getTarget(magic:Magic?, targets:List<Player>):List<Player>{
    val result = mutableListOf<Player>()
    var count = 1
    if (magic!=null){
        count = magic.getTargetCount()
    }
    if (count>targets.size){
        count = targets.size
    }
    var added = 0
    var log = ""
    for (t in targets){
        if (t.hp==0L){
            continue
        }
        log = log.plus("目标${added+1}：${t.name}")
        result.add(t)
        added++
        if (added==count){
            break
        }
        log = log.plus(", ")
    }
    println("找到了${result.size}个目标：$log")
    return result
}