package com.flounderguy.knifefightutilities.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemCharacterTraitBinding

class CharacterTraitAdapter (private val listener: OnItemClickListener) :
    ListAdapter<CharacterTrait, CharacterTraitAdapter.TraitsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraitsViewHolder {
        val adapterBinding = ItemCharacterTraitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TraitsViewHolder(adapterBinding)
    }

    override fun onBindViewHolder(holder: TraitsViewHolder, position: Int) {
        val currentItem = getItem(position)
        val isUser = listener.isUserTrait(currentItem)
        holder.bind(currentItem, isUser)
    }

    inner class TraitsViewHolder(private val binding: ItemCharacterTraitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                buttonTraitSelect.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val trait = getItem(position)
                        listener.onCheckBoxClicked(trait, buttonTraitSelect.isChecked)
                    }
                }
            }
        }

        fun bind(trait: CharacterTrait, isUserTrait: Boolean) {
            binding.apply {
                textTraitLabel.text = trait.name

                imageTraitThumb.contentDescription = trait.name

                when (trait.name) {
                    Gang.Trait.BRASH.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.BURLY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.FIERCE.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.HEAVY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.LUCKY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.QUICK.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.PRACTICAL.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.UNSTABLE.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.SAVAGE.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.SMART.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.STRONG.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.SLICK.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.TENACIOUS.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.TOUGH.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.EAGER.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.WELL_ROUNDED.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.SKETCHY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.WILY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.COCKY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.AGGRESSIVE.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.ADVENTUROUS.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                    Gang.Trait.LOW_KEY.asString -> imageTraitThumb.setImageResource(R.drawable.gang_image_placeholder)
                }

                buttonTraitSelect.setButtonDrawable(R.drawable.trait_checkbox)

                buttonDisabledOverlay.isVisible = isUserTrait
            }
        }
    }

    interface OnItemClickListener {
        fun onCheckBoxClicked(trait: CharacterTrait, isChecked: Boolean)
        fun isUserTrait(trait: CharacterTrait): Boolean
    }

    class DiffCallback : DiffUtil.ItemCallback<CharacterTrait>() {
        override fun areItemsTheSame(oldItem: CharacterTrait, newItem: CharacterTrait) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CharacterTrait, newItem: CharacterTrait) =
            oldItem == newItem
    }
}