package io.github.ghostbuster91.postponeit.job

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.TextView
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.list.JobListFragment
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : AppCompatActivity() {

    private val mDrawerToggle by lazy {
        object : ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.home_drawer_open,
                R.string.home_drawer_close
        ) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        showFragment(JobFilter.PENDING)
        leftMenuList.layoutManager = LinearLayoutManager(this)
        var selectedItem : JobFilter? =null
        leftMenuList.adapter = basicAdapterWithLayoutAndBinder(listOf(JobFilter.PENDING, JobFilter.ALL), R.layout.left_menu_item, { holder, item ->
            val textView = holder.itemView as TextView
            textView.text = item.name
            textView.setOnClickListener {
                selectedItem = item
                showFragment(item)
                title = item.name
                supportActionBar?.title = item.name
                drawerLayout.closeDrawer(leftMenuList)
                leftMenuList.adapter.notifyDataSetChanged()
            }
            textView.isSelected = selectedItem == item
        })
        drawerLayout.addDrawerListener(mDrawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun showFragment(jobFilter: JobFilter) {
        val fragment = JobListFragment.newInstance(jobFilter)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}