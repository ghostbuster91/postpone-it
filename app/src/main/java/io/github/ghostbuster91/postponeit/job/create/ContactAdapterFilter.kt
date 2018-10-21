package io.github.ghostbuster91.postponeit.job.create

import android.widget.Filter
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class ContactAdapterFilter(private val contacts: List<Contact>,
                           private val selectedContactsFetcher: () -> List<Contact>) : Filter() {
    private val filteringResults = PublishSubject.create<List<Contact>>()

    override fun convertResultToString(resultValue: Any) = (resultValue as Contact).label

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        if (constraint != null) {
            val selectedContacts = selectedContactsFetcher()
            contacts
                    .asSequence()
                    .filter { it.label.startsWith(constraint.toString(), true) }
                    .filterNot { it in selectedContacts }
                    .toList()
                    .let {
                        results.count = it.size
                        results.values = it
                    }
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        if (results != null && results.count > 0) {
            @Suppress("UNCHECKED_CAST")
            filteringResults.onNext(results.values as List<Contact>)
        } else {
            filteringResults.onNext(emptyList())
        }
    }

    fun subscribe(observer: (List<Contact>) -> Unit): Disposable {
        return filteringResults.subscribe(observer)
    }
}