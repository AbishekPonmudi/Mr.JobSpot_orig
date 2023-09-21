package mrprogrammer.info.mrjobspot.Activity.Settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rjtech.shop.Interface.ItemClickListener
import mrprogrammer.info.mrjobspot.R


class SettingsAdapters(var ctx: Context, datas: List<SettingsModel>, private  val completeHandler: ItemClickListener) :
    RecyclerView.Adapter<SettingsAdapters.Holder>() {
    var data: List<SettingsModel>

    init {
        data = datas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.each_settings, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(ctx).load(data[position].icon).into(holder.images)
        holder.textView.text = data[position].title
        holder.eachSettings.setOnClickListener {
            completeHandler.onItemClick(data[position].id)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var images: ImageView
        var textView: TextView
        var eachSettings: LinearLayout

        init {
            images = itemView.findViewById(R.id.icon)
            textView = itemView.findViewById(R.id.text)
            eachSettings = itemView.findViewById(R.id.each_Settings)
        }
    }

}