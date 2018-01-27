package io.github.ghostbuster91.postponeit.job.create

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filterable
import io.github.ghostbuster91.postponeit.R
import kotlinx.android.synthetic.main.create_job_suggestion_contact_item.view.*

class ContactsAdapter(context: Context, list: List<Contact>) : ArrayAdapter<Contact>(context, R.layout.create_job_suggestion_contact_item, R.id.contactLabel, list.toMutableList()), Filterable {

    private val filter = ContactAdapterFilter(list)

    init {
        filter.subscribe {
            clear()
            addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: View.inflate(context, R.layout.create_job_suggestion_contact_item, null)
        val contact = getItem(position)
        return view.apply {
            if (contact.avatarUri != null) {
                contactAvatar.setImageURI(Uri.parse(contact.avatarUri))
            } else {
                contactAvatar.setImageDrawable(null)
            }
            contactLabel.text = contact.label
            contactNumber.text = contact.phoneNumber
        }
    }

    override fun getFilter() = filter
}

