package com.flounderguy.knifefightutilities.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flounderguy.knifefightutilities.R
import kotlinx.parcelize.Parcelize

@Entity(tableName = "gang_table")
@Parcelize
data class Gang(
    val name: String?,
    val color: Color?,
    val trait: Trait,
    val isUser: Boolean,
    val isDefeated: Boolean,
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0
) : Parcelable {

    enum class Color(val resValue: Int) {
        RED(R.color.gang_red),
        BLUE(R.color.gang_blue),
        GREEN(R.color.gang_green),
        ORANGE(R.color.gang_orange),
        PURPLE(R.color.gang_purple),
        CYAN(R.color.gang_cyan),
        WHITE(R.color.gang_white),
        BLACK(R.color.gang_black),
        BROWN(R.color.gang_brown),
        YELLOW(R.color.gang_yellow),
        PINK(R.color.gang_pink),
        DARK_GREEN(R.color.gang_dark_green),
        NONE(R.color.design_default_color_primary)
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
        NONE("")
    }
}

