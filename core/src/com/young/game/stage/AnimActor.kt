package com.young.game.stage

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor

class AnimActor(val anim:Animation<TextureRegion>) :Actor() {

    var stateTime = 0f

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        stateTime+=Gdx.graphics.deltaTime
        if (anim.isAnimationFinished(stateTime)){
            remove()
        } else {
            batch?.draw(anim.getKeyFrame(stateTime, true), x, y)
        }
    }

}