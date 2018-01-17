package com.young.game.roles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.young.game.stage.AnimActor
import com.young.game.stage.Direction

class RealPlayer(val corePlayer:Player) : Actor() {

    init {
        corePlayer.animProxy = this
    }

    enum class Status {
        RUNNING, STANDING, IN_FIGHTING, FIGHTING_RUN, FIGHTING_ATTACK, FIGHTING_MAGIC
    }

    val col = 8
    val row = 8

    var stateTime = 0f

    var status: Status = Status.STANDING
    var direction: Direction = Direction.Down
    lateinit var runningAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var standingAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingGoRunningAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingBackRunningAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingAttackMagicAnimations: ArrayList<Animation<TextureRegion>>

    constructor(runningTexture: Texture,
                standingTexture: Texture, corePlayer: Player) :
            this(runningTexture,
                    standingTexture,
                    null, null, null,
                    Status.STANDING, corePlayer)

    constructor(runningTexture: Texture,
                standingTexture: Texture,
                fightingRunningTexture: Texture?,
                fightingAttackNormalTexture: Texture?,
                fightingAttackMagicTexture: Texture?,
                status: Status, corePlayer: Player) : this(corePlayer) {
        setSize(200f, 200f)
        this.status = status
        runningAnimations = ArrayList(row)
        standingAnimations = ArrayList(row)

        val runningTextureRegion = TextureRegion(runningTexture)
        val trsRunning: Array<Array<TextureRegion>> = runningTextureRegion.split(200, 200)
        for (i in 0 until row) {
            val array: com.badlogic.gdx.utils.Array<TextureRegion> = com.badlogic.gdx.utils.Array(8)
            for (j in 0 until col) {
                array.add(trsRunning[i][j])
            }
            val anim: Animation<TextureRegion> = Animation(0.1f, array, Animation.PlayMode.LOOP)
            runningAnimations.add(anim)
        }
        val standingTextureRegion = TextureRegion(standingTexture)
        val trsStanding: Array<Array<TextureRegion>> = standingTextureRegion.split(200, 200)
        for (i in 0 until row) {
            val array: com.badlogic.gdx.utils.Array<TextureRegion> = com.badlogic.gdx.utils.Array(16)
            for (j in 0 until col) {
                array.add(trsStanding[i][j])
            }
            for (j in (col - 1) downTo 0) {
                array.add(trsStanding[i][j])
            }
            val anim: Animation<TextureRegion> = Animation(0.22f, array, Animation.PlayMode.LOOP)
            standingAnimations.add(anim)
        }

        fightingAttackMagicAnimations = ArrayList(2)
        val fightingAttackMagicRegion = TextureRegion(fightingAttackMagicTexture)
        val trsAttackMagic: Array<Array<TextureRegion>> = fightingAttackMagicRegion.split(192, 192)
        for (i in 0..1) {
            val array: com.badlogic.gdx.utils.Array<TextureRegion> = com.badlogic.gdx.utils.Array(20)
            for (j in 20 * i until 20 * (i + 1)) {
                array.add(trsAttackMagic[j][0])
            }
            val anim: Animation<TextureRegion> = Animation(0.08f, array)
            fightingAttackMagicAnimations.add(anim)
        }
    }

    public fun stand() {
        status = Status.STANDING
        stateTime = 0f
    }

    public fun run(direction: Direction) {
        status = Status.RUNNING
        stateTime = 0f
        this.direction = direction
    }

    public fun fighting(direction: Direction) {
        status = Status.IN_FIGHTING
        stateTime = 0f
        this.direction = direction
    }

    public fun attackWithMagic(targets:Array<RealPlayer>) {
        status = Status.FIGHTING_MAGIC
        stateTime = 0f
        direction = Direction.FightingRight
        val magic = MagicImmortalV5()
        magic.proficiency = 3000
        val arr = mutableListOf<Player>()
        for (rp in targets){
            rp.beAttacked(magic)
            arr.add(rp.corePlayer)
        }
        corePlayer.attack(arr, magic)
    }

    public fun beAttacked(magic: Magic){
        val anim = magic.getAnimation()
        if (parent!=null && anim!=null){
            val animActor = AnimActor(anim)
            animActor.setPosition(x-19, y-110)
            parent.addActor(animActor)
        }
    }

    public fun powerDown(num:Long){
        val lb = Label(num.toString(), Skin())
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        stateTime += Gdx.graphics.deltaTime
        var animations = when (status) {
            Status.STANDING -> standingAnimations
            Status.RUNNING -> runningAnimations
            Status.IN_FIGHTING -> standingAnimations
            Status.FIGHTING_MAGIC -> {
                val anim = fightingAttackMagicAnimations[direction.index]
                if (anim.isAnimationFinished(stateTime)){
                    fighting(Direction.Right)
                    standingAnimations
                } else {
                    fightingAttackMagicAnimations
                }
            }
            else -> standingAnimations
        }
        val anim = animations[direction.index]
//        println("Draw: $originX - $originY")
        batch?.draw(anim.getKeyFrame(stateTime, true), x, y)
    }

}