package com.flounderguy.knifefightutilities.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "trait_table")
@Parcelize
data class CharacterTrait(
    val name: String,
    val weapon: String,
    val hp: Int,
    val guts: Int,
    val attack: Int,
    val damage: Int,
    val luck: Int,
    val utility: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) : Parcelable