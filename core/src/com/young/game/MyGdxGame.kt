package com.young.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.young.game.stage.FightingStage
import com.young.game.stage.PracticeStage

class MyGdxGame : ApplicationAdapter() {
    internal lateinit var stage: Stage

    override fun create() {
//        stage = PracticeStage(ScreenViewport())
        stage = FightingStage(FillViewport(800f, 480f))
        Gdx.input.inputProcessor = stage
//        practiceStage.addListener(InputEventListener())
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}
