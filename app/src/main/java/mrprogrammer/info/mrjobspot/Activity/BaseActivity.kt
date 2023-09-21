package mrprogrammer.info.mrjobspot.Activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import mrprogrammer.info.mrjobspot.Adapter.ViewPagerAdapter
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityBaseBinding


class BaseActivity : AppCompatActivity() {
    lateinit var root : ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityBaseBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        initViewPager()
        initNavigationClick()
    }

    private fun initViewPager() {
        val viewPager = root.viewPager
        viewPager.isUserInputEnabled = false;
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
    }

    private fun initNavigationClick() {
        root.navigation.home.setOnClickListener {
            removeSelectedNavigation()
            root.navigation.home.setTint(R.color.darkBlue)
            updateViewPagePosition(0)
        }

        root.navigation.share.setOnClickListener {
            removeSelectedNavigation()
            root.navigation.share.setTint(R.color.darkBlue)
            updateViewPagePosition(1)
        }

        root.navigation.add.setOnClickListener {
            val intent = Intent(this,AddJob::class.java)
            startActivity(intent)
            LocalFunctions.activityAnimation(this ,true)
        }

        root.navigation.chat.setOnClickListener {
            removeSelectedNavigation()
            root.navigation.chat.setTint(R.color.darkBlue)
            updateViewPagePosition(1)
        }

        root.navigation.save.setOnClickListener {
            removeSelectedNavigation()
            root.navigation.save.setTint(R.color.darkBlue)
            updateViewPagePosition(3)
        }
    }

    private fun updateViewPagePosition(position:Int) {
        root.viewPager.currentItem = position
    }

    private fun removeSelectedNavigation() {
        root.navigation.home.setTint(R.color.icon_default)
        root.navigation.share.setTint(R.color.icon_default)
        root.navigation.chat.setTint(R.color.icon_default)
        root.navigation.save.setTint(R.color.icon_default)
    }

    private fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context, colorRes)))
    }
}