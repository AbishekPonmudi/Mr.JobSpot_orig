package mrprogrammer.info.mrjobspot.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import mrprogrammer.info.mrjobspot.Activity.Ui.WorkList
import mrprogrammer.info.mrjobspot.Activity.Ui.Home
import mrprogrammer.info.mrjobspot.Activity.Ui.Save
import mrprogrammer.info.mrjobspot.Activity.Ui.Share

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return WorkList()
            2 -> return Share()
            3 -> return Save()
        }
        return Home()
    }

    override fun getItemCount(): Int {
        return 4
    }
}
