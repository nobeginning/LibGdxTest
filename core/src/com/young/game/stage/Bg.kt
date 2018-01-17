package com.young.game.stage

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class Bg(val texture: Texture) : Actor() {

    init {
        setSize(texture.width.toFloat(), texture.height.toFloat())
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.draw(texture, 0f, 0f)
    }
}