package io.github.ghostbuster91.postponeit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.asBasicList
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*

class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jobList.layoutManager = LinearLayoutManager(this)
        val items = listOf("kasper").asBasicList()
        jobList.adapter = basicAdapterWithLayoutAndBinder(items, R.layout.job_layout) { holder, item ->
            holder.itemView.jobName.text = item
        }
        createDelayedSmsButton.setOnClickListener {
            CreateDelayedActivity.start(this@MainActivity)
        }
    }
}