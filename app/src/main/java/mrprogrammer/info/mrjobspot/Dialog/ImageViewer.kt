package mrprogrammer.info.mrjobspot.Dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import mrprogrammer.info.mrjobspot.R


class ImageViewer(val imageUrl:String): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.dialog_fragment_image_viewer, container, false)
        val image = view.findViewById<ImageView>(R.id.image)
        context?.let { Glide.with(it).load(imageUrl).into(image) }
        return view
    }
}