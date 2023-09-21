package mrprogrammer.info.mrjobspot.Activity.Ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mrprogrammer.info.mrjobspot.databinding.FragmentShareBinding

class Share : Fragment() {
    lateinit var root: FragmentShareBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = FragmentShareBinding.inflate(inflater,container,false)
        return root.root
    }
}