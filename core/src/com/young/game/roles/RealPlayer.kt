package com.young.game.roles

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.young.game.stage.AnimActor
import com.young.game.stage.Direction
import com.young.game.tools.GifDecoder

class RealPlayer(val corePlayer:Player) : Actor() {

    init {
        corePlayer.animProxy = this
    }

    enum class Status {
        RUNNING, STANDING, IN_FIGHTING, FIGHTING_BE_ATTACK, FIGHTING_RUN, FIGHTING_ATTACK, FIGHTING_MAGIC, FIGHTING_DEAD
    }

    enum class FaceTo{
        LEFT, RIGHT
    }

    val col = 8
    val row = 8

    var stateTime = 0f

    var status: Status = Status.STANDING
    var faceTo: FaceTo = FaceTo.RIGHT
    var direction: Direction = Direction.Down
    lateinit var runningAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var standingAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingGoRunningAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingBackRunningAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingAttackMagicAnimations: ArrayList<Animation<TextureRegion>>
    lateinit var fightingStandingRightAnimation: Animation<TextureRegion>
    lateinit var fightingStandingLeftAnimation: Animation<TextureRegion>
    lateinit var fightingDeadAnimation: Animation<TextureRegion>
    lateinit var fightingBeAttackLeftAnimation: Animation<TextureRegion>
    lateinit var fightingBeAttackRightAnimation: Animation<TextureRegion>

    constructor(runningTexture: Texture,
                standingTexture: Texture, corePlayer: Player) :
            this(runningTexture,
                    standingTexture,
                    null, null, null,
                    Status.STANDING, FaceTo.RIGHT, corePlayer)

    constructor(runningTexture: Texture,
                standingTexture: Texture,
                fightingRunningTexture: Texture?,
                fightingAttackNormalTexture: Texture?,
                fightingAttackMagicTexture: Texture?,
                status: Status, faceTo:FaceTo, corePlayer: Player) : this(corePlayer) {
        setSize(200f, 200f)
        this.status = status
        this.faceTo = faceTo
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

        fightingStandingRightAnimation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, "attack-standing.gif")
        fightingStandingLeftAnimation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, "attack-standing-2.gif")
        fightingDeadAnimation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, "fighting-dead.gif")
        fightingBeAttackLeftAnimation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, "fighting-be-attack-left.gif")
        fightingBeAttackRightAnimation = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, "fighting-be-attack-right.gif")
    }

    public fun isDead():Boolean = corePlayer.hp<=0

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

    public fun attackWithMagic(targets:List<RealPlayer>) {
        status = Status.FIGHTING_MAGIC
        stateTime = 0f
        direction = Direction.FightingRight
        val magic = MagicImmortalV5()
        magic.proficiency = 10000
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
        stateTime = 0f
        status = Status.FIGHTING_BE_ATTACK
    }

    public fun dead(){
        stateTime = 0f
        status = Status.FIGHTING_DEAD
    }

    public fun powerDown(num:Long){
        parent?.apply {
            val bitmapFont = BitmapFont(Gdx.files.internal("font/default.fnt"))
            bitmapFont.data.scale(0.6f)
            val lStyle = Label.LabelStyle(bitmapFont, Color.RED)
            val lb = Label("-$num", lStyle)
            lb.setPosition(this@RealPlayer.x+this@RealPlayer.width/2, this@RealPlayer.y+this@RealPlayer.height/2, Align.center)
            addActor(lb)

            val action1 = Actions.moveBy(0f, 100f, 2f, Interpolation.pow2Out)
            val action2 = Actions.removeActor(lb)
            lb.addAction(Actions.sequence(action1, action2))
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        stateTime += Gdx.graphics.deltaTime
        var anim:Animation<TextureRegion>? = null
        anim = when (status) {
            Status.STANDING -> {standingAnimations[direction.index]}
            Status.RUNNING -> {runningAnimations[direction.index]}
            Status.IN_FIGHTING -> {
                when(faceTo){
                    FaceTo.LEFT->fightingStandingLeftAnimation
                    FaceTo.RIGHT->fightingStandingRightAnimation
                }
            }
            Status.FIGHTING_BE_ATTACK->{
                val anim = when(faceTo){
                    FaceTo.RIGHT->fightingBeAttackRightAnimation
                    FaceTo.LEFT->fightingBeAttackLeftAnimation
                }
                if (anim.isAnimationFinished(stateTime)){
                    fighting(Direction.Right)
                    fightingStandingRightAnimation
                } else {
                    anim
                }
            }
            Status.FIGHTING_MAGIC -> {
                val anim = fightingAttackMagicAnimations[direction.index]
                if (anim.isAnimationFinished(stateTime)){
                    fighting(Direction.Right)
                    fightingStandingRightAnimation
                } else {
                    fightingAttackMagicAnimations[direction.index]
                }
            }
            Status.FIGHTING_DEAD->fightingDeadAnimation
            else -> fightingStandingRightAnimation
        }
//        val anim = animations[direction.index]
//        println("Draw: $originX - $originY")
        batch?.draw(anim.getKeyFrame(stateTime, true), x, y)
    }

}