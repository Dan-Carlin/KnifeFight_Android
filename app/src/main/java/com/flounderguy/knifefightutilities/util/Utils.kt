package com.flounderguy.knifefightutilities.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.flounderguy.knifefightutilities.BuildConfig
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang

val <T> T.exhaustive: T
    get() = this

fun generateGangName(): String {
    val gangPrefix = listOf(
        "Southside", "Northside", "Eastside", "Westside", "12th Street",
        "18th Street", "Barrio", "Chinatown", "Downtown", "Uptown",
        "10th Avenue", "22nd Avenue", "Crazy Town", "East Coast", "West Coast",
        "Main Street", "Underground", "Hood", "The", "Dark", "Savage", "Wild",
        "Latin", "Ghost Town", "Los", "Humpty", "Notorious", "Cowbell",
        "Crazy", "Crusty", "Vegan", "Curmudgeonly", "Iron", "Saucy", "Good ol'",
        "Band of", "Big City", "Small Town", "Bloodthirsty", "Twisted",
        "River City", "Lake City", "Beachside", "Back Alley", "Country", "Dogtown",
        "Mystic", "Aztec", "Shadow", "Killer", "Infamous", "Battle", "War", "Road"
    )

    val gangSuffix = listOf(
        "Locos", "Clan", "Brotherhood", "Syndicate", "Ryders", "Family",
        "Familia", "Fancymen", "Clowns", "Cartel", "Mafia", "Boyz",
        "Nation", "Dawgz", "Jokerz", "Horsemen", "Militia", "Mob", "Gangstaz",
        "Hoodlums", "Crew", "Rapscallions", "Gang", "Scallywags", "Lunatics",
        "Ticklers", "Fiends", "Bruisers", "Hooligans", "Vermin", "Pirates",
        "Scumbags", "Sisterhood", "Warthogs", "Squad", "Soldiers",
        "Funnymen", "Animals", "Ladies Men", "Maids", "Poppers", "Homeslices",
        "Metalheads", "Ex-Girlfriends", "Ex-Boyfriends", "Zombies", "Lads", "Lords",
        "Triads", "Lunatics", "Warriors", "Butlers", "Maniacs", "Folk", "Nuns", "Saints"
    )
    return gangPrefix.random() + " " + gangSuffix.random()
}

fun convertTraitToLabel(trait: CharacterTrait): Gang.Trait {
    return enumValueOf(trait.name.uppercase().replace('-', '_'))
}

fun getTraitDrawable(context: Context, image: ImageView, trait: CharacterTrait): Drawable? {
    val traitString = "ic_trait_symbol_" + trait.name.lowercase().replace('-', '_')
    val iconResourceId = image.context.resources.getIdentifier(
        traitString, "drawable",
        BuildConfig.APPLICATION_ID
    )

    return context.let { ContextCompat.getDrawable(it, iconResourceId) }
}

fun getTraitDrawable(context: Context, image: ImageView, trait: Gang.Trait): Drawable? {
    val traitString = "ic_trait_symbol_" + trait.asString.lowercase().replace('-', '_')
    val iconResourceId = image.context.resources.getIdentifier(
        traitString, "drawable",
        BuildConfig.APPLICATION_ID
    )

    return context.let { ContextCompat.getDrawable(it, iconResourceId) }
}

enum class DieType(val sides: Int) {
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D20(20)
}

fun rollDice(dieType: DieType): Int {
    return (1..dieType.sides).random()
}

enum class ResultRequest {
    ATTACK,
    DAMAGE,
    COUNTER,
    TIE,
    SECOND_WIND
}