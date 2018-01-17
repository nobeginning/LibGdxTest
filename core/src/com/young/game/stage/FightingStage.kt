package com.young.game.stage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
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
                RealPlayer.Status.IN_FIGHTING, cp1)
        p1.setPosition(0f, 0f)
        addActor(p1)
        p1.fighting(Direction.Right)


        val cp2 = Player(Race.RACE_IMMORTAL, "P2")
        cp2.level = 60
        val p2 = RealPlayer(Texture("running.png"),
                Texture("standing.png"),
                null,
                null,
                Texture("attack-magic.png"),
                RealPlayer.Status.IN_FIGHTING, cp2)
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
                RealPlayer.Status.IN_FIGHTING, cp3)
        p3.setPosition(300f, 200f)
        addActor(p3)
        p3.fighting(Direction.Left)



        p1.addListener(object:InputListener(){
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                if (p1.status==RealPlayer.Status.FIGHTING_MAGIC){
                    p1.fighting(Direction.Right)
                } else {
                    p1.attackWithMagic(arrayOf(p2, p3))
                }
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        })
    }




}