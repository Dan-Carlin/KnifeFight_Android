package com.flounderguy.knifefightutilities.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "gang_table")
@Parcelize
data class Gang(
    val name: String,
    val color: GangColor,
    val trait: Trait,
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0
) : Parcelable {

    enum class GangColor {
        RED, BLUE, GREEN,
        ORANGE, PURPLE, CYAN,
        WHITE, BLACK, BROWN,
        YELLOW, PINK, DARK_GREEN,
        NONE
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

