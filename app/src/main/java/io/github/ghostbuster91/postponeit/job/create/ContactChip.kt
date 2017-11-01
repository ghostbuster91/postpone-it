package io.github.ghostbuster91.postponeit.job.create

import android.net.Uri
import com.pchmn.materialchips.model.ChipInterface

data class ContactChip(private val phoneNumber: String,
                       private val avatarUri: Uri?,
                       private val name: String,
                       private val id: String) : ChipInterface {
    override fun getInfo() = phoneNumber

    override fun getAvatarDrawable() = null

    override fun getLabel() = name

    override fun getId(): Any = id

    override fun getAvatarUri() = avatarUri
}