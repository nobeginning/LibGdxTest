package com.young.game.stage

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.young.game.roles.Player
import com.young.game.roles.Race
import com.young.game.roles.RealPlayer
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit


public enum class Direction(val index: Int) {
    Down(0),
    Left(1),
    Right(2),
    Up(3),
    LeftDown(4),
    RightDown(5),
    LeftUp(6),
    RightUp(7),
    FightingLeft(1),
    FightingRight(0)
}


class PracticeStage(viewport: Viewport) : Stage(viewport) {

    val camera: OrthographicCamera = OrthographicCamera(viewport.worldWidth, viewport.worldHeight)
    var tiledMapRenderer: OrthogonalTiledMapRenderer
    var player: RealPlayer

    var cameraX = 0f
    var cameraY = 0f
    var playerX = 400f
    var playerY = 500f

    val mapWidth: Int
    val mapHeight: Int

    val btnLeft: ImageButton = ImageButton(SpriteDrawable(Sprite(Texture("ic.png"))))
    val btnDown: ImageButton = ImageButton(SpriteDrawable(Sprite(Texture("ic.png"))))
    val btnRight: ImageButton = ImageButton(SpriteDrawable(Sprite(Texture("ic.png"))))
    val btnUp: ImageButton = ImageButton(SpriteDrawable(Sprite(Texture("ic.png"))))

    var isMoving = false

    init {
        val cp1 = Player(Race.RACE_IMMORTAL, "P1")
        cp1.level = 80
        cp1.mpPoint = 320
        player = RealPlayer(Texture("running.png"),
                Texture("standing.png"),
                null,
                null,
                Texture("attack-magic.png"),
                RealPlayer.Status.STANDING, RealPlayer.FaceTo.RIGHT, cp1)
        addActor(player)
        addActor(btnLeft)
        addActor(btnDown)
        addActor(btnRight)
        addActor(btnUp)

        btnLeft.setPosition(100f, 200f)
        btnDown.setPosition(200f, 100f)
        btnRight.setPosition(300f, 200f)
        btnUp.setPosition(200f, 300f)

        player.setPosition(playerX, playerY)
        val tiledMap = TmxMapLoader().load("des2.tmx")
        val w: Int = tiledMap.properties["width"] as Int
        val tileW: Int = tiledMap.properties["tilewidth"] as Int
        mapWidth = w * tileW
        val h: Int = tiledMap.properties["height"] as Int
        val tileH: Int = tiledMap.properties["tileheight"] as Int
        mapHeight = h * tileH
        tiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap)
        camera.setToOrtho(false)
//        camera.position.x = player.x
//        camera.position.y = player.y
        camera.update()
        cameraX = camera.position.x
        cameraY = camera.position.y
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
        println("${tiledMapRenderer.viewBounds.width} ******** ${tiledMapRenderer.viewBounds.height}")

        btnLeft.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isMoving = true
                run(Direction.Left)
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMoving = false
                stand()
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                isMoving = false
                stand()
            }
        })
        btnDown.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isMoving = true
                run(Direction.Down)
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMoving = false
                stand()
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                isMoving = false
                stand()
            }
        })
        btnRight.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isMoving = true
                run(Direction.Right)
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMoving = false
                stand()
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                isMoving = false
                stand()
            }
        })
        btnUp.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                isMoving = true
                run(Direction.Up)
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                isMoving = false
                stand()
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                isMoving = false
                stand()
            }
        })
    }

    override fun act() {
        camera.update()
        tiledMapRenderer.setView(camera)
        tiledMapRenderer.render()
        super.act()
    }

    fun run(direction: Direction) {
        player.run(direction)
        getRunObservable(direction)
                .repeatUntil { !isPlayerMoving() }
                .subscribe(
                        { },
                        { println(it.message) }
                )
    }

    fun stand() {
        player.stand()
    }

    var offsetX: Float = 0f
    var offsetY: Float = 0f
    var playerOffsetX: Float = 0f
    var playerOffsetY: Float = 0f
    val step = 4f
    fun getRunObservable(direction: Direction): Observable<Vector3> {
        if (true) {
            val test = Observable.create<Vector3> {
                var isPlayerMove: Boolean = when (direction) {
                    Direction.Left -> player.x > viewport.worldWidth / 2 - player.width/2 || (camera.position.x <= viewport.worldWidth / 2 && player.x>0)
                    Direction.Right -> player.x < viewport.worldWidth / 2 - player.width/2 || (camera.position.x + viewport.worldWidth / 2 >= mapWidth && player.x<viewport.worldWidth-player.width)
                    Direction.Down -> player.y > viewport.worldHeight / 2 - player.height/2 || (camera.position.y <= viewport.worldHeight / 2 && player.y>0)
                    Direction.Up -> player.y < viewport.worldHeight / 2 - player.height/2 || (camera.position.y + viewport.worldHeight / 2 >= mapHeight && player.y<viewport.worldHeight-player.height)
                    else -> false
                }

                var outOfMap: Boolean = when (direction) {
                    Direction.Left -> camera.position.x <= viewport.worldWidth / 2
                    Direction.Right -> camera.position.x + viewport.worldWidth / 2 >= mapWidth
                    Direction.Down -> camera.position.y <= viewport.worldHeight / 2
                    Direction.Up -> camera.position.y + viewport.worldHeight / 2 >= mapHeight
                    else -> false
                }
                if (!isPlayerMove) {
                    playerOffsetX = 0f
                    playerOffsetY = 0f
                    if (!outOfMap){
                        offsetX = when (direction) {
                            Direction.Left -> -step
                            Direction.Right -> step
                            else -> 0f
                        }
                        offsetY = when (direction) {
                            Direction.Down -> -step
                            Direction.Up -> step
                            else -> 0f
                        }
                    }
                } else {
                    offsetX = 0f
                    offsetY = 0f
                    playerOffsetX = when (direction) {
                        Direction.Left -> -step
                        Direction.Right -> step
                        else -> 0f
                    }
                    playerOffsetY = when (direction) {
                        Direction.Down -> -step
                        Direction.Up -> step
                        else -> 0f
                    }
                }
                player.moveBy(playerOffsetX, playerOffsetY)
                camera.position.add(offsetX, offsetY, 0f)
                it.onNext(camera.position)
                it.onComplete()
            }
            return Observable.zip(test, Observable.timer(20, TimeUnit.MILLISECONDS),
                    BiFunction { t1: Vector3, t2: Long -> t1 })
        }


        val run: Observable<Vector3> = when (direction) {
            Direction.Left -> Observable.create({
                if (player.x > viewport.worldWidth / 2) {
                    playerOffsetX = -step
                    playerOffsetY = 0f
                    offsetX = 0f
                    offsetY = 0f
                } else {
                    offsetX = -step
                    offsetY = 0f
                }
                if (camera.position.x <= viewport.worldWidth / 2) {
                    offsetX = 0f
                }
                if (offsetX == 0f) {
                    if (player.x > 0) {
                        playerOffsetX = -step
                    } else {
                        playerOffsetX = 0f
                    }
                    playerOffsetY = 0f
                } else {
                    playerOffsetX = 0f
                    playerOffsetY = 0f
                }
                println("Player : ${player.x}--${player.y} ===> $playerOffsetX -- $playerOffsetY")
                player.moveBy(playerOffsetX, playerOffsetY)
                camera.position.add(offsetX, offsetY, 0f)
                it.onNext(camera.position)
                it.onComplete()
            })
            Direction.Down -> Observable.create({
                offsetY = -step
                offsetX = 0f
                if (camera.position.y <= viewport.worldHeight / 2) {
                    offsetY = 0f
                }
                println("${camera.position.x}--${camera.position.y}")
                camera.position.add(offsetX, offsetY, 0f)
                it.onNext(camera.position)
                it.onComplete()
            })
            Direction.Right -> Observable.create({
                offsetX = step
                offsetY = 0f
                if (camera.position.x + viewport.worldWidth / 2 >= mapWidth) {
                    offsetX = 0f
                }
                println("${camera.position.x}--${camera.position.y}")
                camera.position.add(offsetX, offsetY, 0f)
                it.onNext(camera.position)
                it.onComplete()
            })
            Direction.Up -> Observable.create({
                offsetY = step
                offsetX = 0f
                if (camera.position.y + viewport.worldHeight / 2 >= mapHeight) {
                    offsetY = 0f
                }
                println("${camera.position.x}--${camera.position.y}")
                camera.position.add(offsetX, offsetY, 0f)
                it.onNext(camera.position)
                it.onComplete()
            })
            else -> Observable.create({
                camera.position.add(0f, 0f, 0f)
                it.onNext(camera.position)
                it.onComplete()
            })
        }
        return Observable.zip(run, Observable.timer(20, TimeUnit.MILLISECONDS),
                BiFunction { t1: Vector3, t2: Long -> t1 })
    }

    fun isPlayerMoving(): Boolean {
//        println("isPlayerMoving? $isMoving")
        return isMoving
    }
}