package com.example.kotlininvaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView

class KotlinInvadersView(context: Context, private val size: Point) : SurfaceView(context), Runnable {

    //    this is our thread
    private val gameThread = Thread(this)

    //    boolean to set or unset
    private var playing = false

    //    game is paused at start
    private var paused = true

    //    a Canvas and a Paint object
    private var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()

    //    the player ship
    val playerShip: PlayerShip = PlayerShip(context, size.x, size.y)

    //    some invaders
    private val invaders = ArrayList<Invader>()
    private var numInvaders = 0

    //    the score
    private var score = 0

    //    the wave number
    private var waves = 1

    //    lives
    private var lives = 3

    //    high score
    private var highScore = 0

    //    how menacing should the sound be?
    private var menaceInterval: Long = 1000

    //    which menace sound should be played next
    private var uhOrOh: Boolean = false

    //    when did we last played a menacing sound
    private var lastMenaceTime = System.currentTimeMillis()

    private fun prepareLevel() {
//        initialise game objects
//        build an army of invaders
        Invader.numberOfInvaders = 0
        numInvaders = 0

        for (column in 0..10) {
            for (row in 0..5) {
                invaders.add(Invader(context, row, column, size.x, size.y))

                numInvaders++
            }
        }
    }

    override fun run() {
//        tracks the frame rate
        var fps: Long = 0
        while (playing) {
//            capture the current time
            val startFrameTime = System.currentTimeMillis()

//            update the frame
            if (!paused) {
                update(fps)
            }
//            draw the frame
            draw()

//            calculate the fps rate this frame
            val timeThisFrame = System.currentTimeMillis() - startFrameTime
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame
            }
        }
    }

    private fun update(fps: Long) {
//        update the state of all game objects

//        move the players ship
        playerShip.update(fps)

//        did an invader bumped to the edge
        var bumped = false

//        did the player lost
        var lost = false

        for (invader in invaders) {

            if (invader.isVisible) {
//            move the next invader
                invader.update(fps)

//            if that move cause them to bump
//            the screen bumped changes to true
                if (invader.position.left > size.x - invader.width
                    || invader.position.left < 0
                ) {
                    bumped = true
                }
            }
        }
//        did an invader touch the edge
        if (bumped) {
//            move all invaders down and change direction
            for (invader in invaders) {
                invader.dropDownAndReverse(waves)
//                have the invaders landed
                if (invader.position.bottom >= size.y && invader.isVisible) {
                    lost = true
                }
            }
        }
    }

    private fun draw() {
//    Make sure or drawing surface is valid otherwise the game will crash
        if (holder.surface.isValid) {
//       lock the canvas ready to draw
            canvas = holder.lockCanvas()

//            draw the background color
            canvas.drawColor(Color.argb(255, 0, 0, 0))
//            choose the brush color for drawing
            paint.color = Color.argb(255, 0, 255, 0)

//            draw all the game objects here
//            this will draw the player spaceship
            canvas.drawBitmap(playerShip.bitmap, playerShip.position.left, playerShip.position.top, paint)

//            draw the invaders
            for (invader in invaders) {
                if (invader.isVisible) {
                    if (uhOrOh) {
                        canvas.drawBitmap(Invader.bitmap1!!, invader.position.left, invader.position.top, paint)
                    } else {
                        canvas.drawBitmap(Invader.bitmap2!!, invader.position.left, invader.position.top, paint)
                    }
                }
            }
//            draw the score and remaining lives

//            change the brush color for the text
            paint.color = Color.argb(255, 255, 255, 255)
            paint.textSize = 70f
            canvas.drawText("Score: $score   Lives: $lives  Wave: $waves  HI: $highScore", 20f, 75f, paint)

//            draw everything to the screen
            holder.unlockCanvasAndPost(canvas)
        }
    }

    // If SpaceInvadersActivity is paused/stopped
    // then shut down our thread.
    fun pause() {
        playing = false
        try {
            gameThread.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }
    }

    // If SpaceInvadersActivity is started then
    // start our thread.
    fun resume() {
        playing = true
        prepareLevel()
        gameThread.start()
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}