package com.example.kotlininvaders

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF

class PlayerShip(context: Context, private var screenX: Int, screenY: Int) {

    //    the player ship will be represented by a bitmap
    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.playership)

    //    how high and wide our ship will be
    val width = screenX / 20f
    private val height = screenY / 20f

    //    this keeps track of the ships position
    val position = RectF(screenX / 2f, screenY - height, screenX / 2 + width, screenY.toFloat())

    //    this will hold the pixels per second that the ship will move
    val speed = 450f

    //    this data will be accessed using ClassName.propertyName
    companion object {
        //        which ways can the ship move
        const val stopped = 0
        const val left = 1
        const val right = 2
    }

    //    is the shi moving and in which direction
//starts stopped
    var moving = stopped

    init {
//        stretch the bitmap to a size appropriate to the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
    }

    // This update method will be called from update in
    // KotlinInvadersView It determines if the player's
    // ship needs to move and changes the coordinates
    fun update(fps: Long) {
//        move as long as it isn't outside the screen
        if (moving == left && position.left > 0) {
            position.left -= speed / fps
        } else if (moving == right && position.left < screenX - width) {
            position.left += speed / fps
        }
        position.right = position.left + width
    }
}