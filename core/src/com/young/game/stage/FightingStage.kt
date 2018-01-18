package com.young.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.young.game.roles.CoatV1
import com.young.game.roles.Player
import com.young.game.roles.Race
import com.young.game.roles.RealPlayer

class FightingStage(viewport:Viewport) : Stage(viewport) {
    init {
        val bg = Bg(Texture("bg-fighting.jpg"))
        bg.setPosition(0f, 0f)
        addActor(bg)

        val cp1 = Player(Race.RACE_IMMORTAL, "P1")
        cp1.level = 80
        cp1.mpPoint = 320

        val p1 = RealPlayer(Texture("running.png"),
                Texture("standing.png"),
                null,
                null,
                Texture("attack-magic.png"),
                RealPlayer.Status.IN_FIGHTING, RealPlayer.FaceTo.RIGHT, cp1)
        p1.setPosition(0f, 0f)
        addActor(p1)
        p1.fighting(Direction.Right)


        val cp2 = Player(Race.RACE_IMMORTAL, "P2")
        cp2.level = 60
        val coat = CoatV1()
        cp2.equipmentCoat = coat
        val p2 = RealPlayer(Texture("running.png"),
                Texture("standing.png"),
                null,
                null,
                Texture("attack-magic.png"),
                RealPlayer.Status.IN_FIGHTING, RealPlayer.FaceTo.LEFT, cp2)
        p2.setPosition(300f, 100f)
        addActor(p2)
        p2.fighting(Direction.Left)


        val cp3 = Player(Race.RACE_IMMORTAL, "P3")
        cp3.level = 70
        val p3 = RealPlayer(Texture("running.png"),
                Texture("standing.png"),
                null,
                null,
                Texture("attack-magic.png"),
                RealPlayer.Status.IN_FIGHTING, RealPlayer.FaceTo.LEFT, cp3)
        p3.setPosition(300f, 200f)
        addActor(p3)
        p3.fighting(Direction.Left)

        p1.addListener(object:InputListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (p1.status==RealPlayer.Status.FIGHTING_MAGIC){
                    p1.fighting(Direction.Right)
                } else {
                    val arr = mutableListOf<RealPlayer>()
                    if (!p2.isDead()){
                        arr.add(p2)
                    }
                    if (!p3.isDead()){
                        arr.add(p3)
                    }
                    if (arr.isNotEmpty()) {
                        p1.attackWithMagic(arr)
                    } else {
                        val bitmapFont = BitmapFont(Gdx.files.internal("font/default.fnt"))
                        bitmapFont.data.scale(2.0f)
                        val lStyle = Label.LabelStyle(bitmapFont, Color.BLACK)
                        val act1 = Actions.fadeIn(0.6f)
                        val act2 = Actions.delay(3f)
                        val act3 = Actions.fadeOut(0.6f)
//                        val act3 = Actions.delay(0.6f)
                        val act4 = Actions.removeActor()
                        val seq = Actions.sequence(act1, act2, act3, act4)
                        val lb = Label("All Dead", lStyle)
                        lb.setPosition(width/2, height/2, Align.center)
                        addActor(lb)
                        lb.addAction(seq)
                    }
                }
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
    }




}