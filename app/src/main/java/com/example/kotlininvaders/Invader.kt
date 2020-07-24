package com.example.kotlininvaders

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import java.util.*

class Invader(context: Context, row: Int, column: Int, screenX: Int, screenY: Int) {
    // How wide, high and spaced out are the invader will be
    val width = screenX / 35f
    private var height = screenY / 35f
    private val padding = screenX / 45

    var position = RectF(
        column * (width + padding),
        100 + row * (width + padding / 4),
        column * (width + padding) + width,
        100 + row * (width + padding / 4) + height
    )

    //    this will hold the pixels per second speed
    private var speed = 40f

    private val left = 1
    private val right = 2

    //    is the ship moving and in which direction
    private var shipMoving = right

    var isVisible = true

    companion object {
        //        the aline ship will be represented by a bitmap
        var bitmap1: Bitmap? = null
        var bitmap2: Bitmap? = null

        //        keep track of the number of instances
        //        active
        var numberOfInvaders = 0
    }

    init {
//        initialize the bitmaps
        bitmap1 = BitmapFactory.decodeResource(context.resources, R.drawable.invader1)
        bitmap2 = BitmapFactory.decodeResource(context.resources, R.drawable.invader2)

//        stretch the ship to size appropriate for the screen
        bitmap1 = Bitmap.createScaledBitmap(bitmap1!!, (width.toInt()), (height.toInt()), false)
        bitmap2 = Bitmap.createScaledBitmap(bitmap2!!, (width.toInt()), (height.toInt()), false)

        numberOfInvaders++
    }

    fun update(fps: Long) {
        if (shipMoving == left) {
            position.left -= speed / fps
        }
        if (shipMoving == right) {
            position.left += speed / fps
        }
        position.right = position.left + width
    }

    fun dropDownAndReverse(waveNumber: Int) {
        shipMoving = if (shipMoving == left) {
            //change moving position when reaching screen's edge
            right
        } else {
            left
        }
        //lower the ship
        position.top += height
        position.bottom += height

//        the later the wave the faster the ship
        speed = 1.1f + (waveNumber.toFloat() / 20)
    }

    fun takeAim(playerShipX: Int, playerWidth: Int, waves: Int): Boolean {

        val generator = Random()
        var randomNumber: Int

//        if near the player consider taking a shot
        if (playerShipX + playerWidth > position.left && playerShipX + playerWidth < position.left + width
            || playerShipX > position.left && playerShipX < position.left + width) {

//            the fewer ships the more each ship shoots
//            the higher the wave the more the ship shoots
            randomNumber = generator.nextInt(100 * numberOfInvaders) / waves
            if (randomNumber == 0) {
                return true
            }
        }
//        if firing randomly (not near the player)
        randomNumber = generator.nextInt(15 * numberOfInvaders)
        return randomNumber == 0
    }
}