package com.flounderguy.knifefightutilities.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flounderguy.knifefightutilities.R
import kotlinx.parcelize.Parcelize

@Entity(tableName = "gang_table")
@Parcelize
data class Gang(
    val name: String,
    val color: Color?,
    val trait: Trait?,
    val isUser: Boolean,
    val isDefeated: Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : Parcelable {

    enum class Color(
        val normalColorValue: Int,
        val darkColorValue: Int,
        val innerStrokeValue: Int,
        val outerStrokeValue: Int
    ) {
        RED(R.color.gang_red, R.color.gang_dark_red, R.color.gang_light_red, R.color.black),
        BLUE(R.color.gang_blue, R.color.gang_dark_blue, R.color.gang_light_blue, R.color.black),
        GREEN(R.color.gang_green, R.color.gang_dark_green, R.color.gang_light_green, R.color.black),
        ORANGE(R.color.gang_orange, R.color.gang_dark_orange, R.color.gang_light_orange, R.color.black),
        PURPLE(R.color.gang_purple, R.color.gang_dark_purple, R.color.gang_light_purple, R.color.black),
        CYAN(R.color.gang_cyan, R.color.gang_dark_cyan, R.color.gang_light_cyan, R.color.black),
        WHITE(R.color.gang_white, R.color.gang_dark_white, R.color.black, R.color.gang_dark_white),
        BLACK(R.color.gang_black, R.color.gang_dark_black, R.color.gang_light_black, R.color.black),
        BROWN(R.color.gang_brown, R.color.gang_dark_brown, R.color.gang_light_brown, R.color.black),
        YELLOW(R.color.gang_yellow, R.color.gang_dark_yellow, R.color.black, R.color.gang_dark_yellow),
        PINK(R.color.gang_pink, R.color.gang_dark_pink, R.color.gang_light_pink, R.color.black),
        GOLD(R.color.gang_gold, R.color.gang_dark_gold, R.color.gang_light_gold, R.color.black),
        NONE(
            R.color.design_default_color_primary,
            R.color.design_default_color_primary_dark,
            R.color.white,
            R.color.black
        )
    }

    enum class Trait(val asString: String) {
        BRASH("Brash"),
        BURLY("Burly"),
        FIERCE("Fierce"),
        HEAVY("Heavy"),
        LUCKY("Lucky"),
        QUICK("Quick"),
        PRACTICAL("Practical"),
        UNSTABLE("Unstable"),
        SAVAGE("Savage"),
        SMART("Smart"),
        STRONG("Strong"),
        SLICK("Slick"),
        TENACIOUS("Tenacious"),
        TOUGH("Tough"),
        EAGER("Eager"),
        WELL_ROUNDED("Well-Rounded"),
        SKETCHY("Sketchy"),
        WILY("Wily"),
        COCKY("Cocky"),
        AGGRESSIVE("Aggressive"),
        ADVENTUROUS("Adventurous"),
        LOW_KEY("Low-Key"),
        NONE("None")
    }
}

