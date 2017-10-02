package io.github.ghostbuster91.postponeit.job

import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
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
        leftMenuList.adapter = ArrayAdapter<JobFilter>(this, R.layout.left_menu_item, listOf(JobFilter.PENDING, JobFilter.ALL))
        leftMenuList.setOnItemClickListener { parent, _, position, _ ->
            val jobFilter = parent.getItemAtPosition(position) as JobFilter
            showFragment(jobFilter)

            leftMenuList.setItemChecked(position, true)
            title = jobFilter.name
            supportActionBar?.title = jobFilter.name
            drawerLayout.closeDrawer(leftMenuList)
        }
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