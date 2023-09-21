package mrprogrammer.info.mrjobspot.Activity.Ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.databinding.FragmentHomeBinding
import mrprogrammer.info.mrjobspot.databinding.FragmentSaveBinding


class Save : Fragment() {
    lateinit var root: FragmentSaveBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = FragmentSaveBinding.inflate(inflater,container,false)
        return root.root
    }
}