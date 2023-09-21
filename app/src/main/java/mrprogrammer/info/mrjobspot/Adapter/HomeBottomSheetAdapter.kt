package mrprogrammer.info.mrjobspot.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.Dialog.FilterBottomSheet
import mrprogrammer.info.mrjobspot.Dialog.ImageViewer
import mrprogrammer.info.mrjobspot.Dialog.LoadingDialog
import mrprogrammer.info.mrjobspot.Firebase.FirebaseUploadJob
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.RealmModel.JobRealmModel
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions


class HomeBottomSheetAdapter(var ctx: Context, localData: List<JobRealmModel>, val fragmentManager: FragmentManager,val isCurrentUser:Boolean = false) :
    RecyclerView.Adapter<HomeBottomSheetAdapter.Holder>() {
    var data: List<JobRealmModel>
    var previousExpandedPosition = -1
    var mExpandedPosition = -1

    init {
        data = localData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.each_job_item, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.title.text = data[position].title
        holder.address.text = data[position].address
        holder.name.text = data[position].name
        holder.about.text = data[position].about
        holder.date.text = data[position].date
        holder.chipData.text = data[position].skill
        Glide.with(ctx).load(data[position].image).into(holder.image)

        val isExpanded = position == mExpandedPosition;
        holder.hidableLayout.visibility = if(isExpanded) View.VISIBLE else View.GONE
        holder.date.visibility = if(isExpanded) View.GONE else View.VISIBLE

        if(isExpanded) {
            holder.about.maxLines = 15
        }else{
            holder.about.maxLines = 3
        }

        if (isExpanded)
            previousExpandedPosition = position;

        holder.outerLayout.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(previousExpandedPosition)
            notifyItemChanged(position)
        }

        holder.share.setOnClickListener {
            val shareText = """
                Hey, here is a new job for you,
                
                Job Title - ${data[position].title}
                Job Description - ${data[position].about}
                Customer Mobile - ${data[position].mobile}
                Customer Address - ${data[position].address}
                Location - ${data[position].locality}
                
                
                For More Local Job's Download ${ctx.getString(R.string.app_name)}
                
                Thank you.
            """.trimIndent()
            LocalFunctions.shareText(ctx, shareText)
        }

        holder.call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${data[position].mobile}")
            ctx.startActivity(intent)
        }


        holder.image.setOnClickListener {
            val imageView = ImageViewer(data[position].image)
            imageView.show(fragmentManager,"")
        }

        holder.more.setOnClickListener {
            val listOfFilter = mutableListOf<String>()
            listOfFilter.add("What's App")
            listOfFilter.add("Google Map")
            val filterBottomSheet = FilterBottomSheet(ctx, -1, listOfFilter, object : CompleteHandler {
                override fun onFailure(e: String) {

                }

                override fun onSuccess(Message: Any) {
                    moreOptionAction(Message.toString(), data[position].mobile, data[position].lon,data[position].lat)
                }

            }, titleText = "Options", showCheckbox = false)
            filterBottomSheet.show()
        }

       if(isCurrentUser) {
           holder.outerLayout.setOnLongClickListener {
               val dialog = MaterialAlertDialogBuilder(ctx)
                   .setTitle(ctx.getString(R.string.app_name))
                   .setMessage("Do you remove this job")
                   .setPositiveButton("Yes") { dialog, which ->
                       dialog.dismiss()
                       removeCurrentDataFromServer(data[position].id, data[position].imagekey)
                   }.setNegativeButton("No") { dialog, which ->
                       dialog.dismiss()
                   }.create()
               dialog.show()
               true
           }
       }
    }

    private fun removeCurrentDataFromServer(id: String, imagekey: String) {
        if(!CommonFunctions.isConnected(ctx)) {
            MrContext.MrToast().warning(ctx as Activity,"No InterNet..")
            return
        }

        val  dialog = LoadingDialog(ctx)
        dialog.show()

        FirebaseUploadJob.removeJobDetails(id,object :CompleteHandler {
            override fun onFailure(e: String) {
                dialog.dismiss()
                MrContext.MrToast().error(ctx as Activity,"Unable to Delete")
            }

            override fun onSuccess(Message: Any) {
                MrContext.MrToast().success(ctx as Activity,"Deleted")
                dialog.dismiss()
                removeImage(imagekey)
            }

        })

    }

    private fun removeImage(id:String) {
        FirebaseUploadJob.removeJobImage(id,object :CompleteHandler {
            override fun onFailure(e: String) {
                print("----------> Image Not Deleted")

            }

            override fun onSuccess(Message: Any) {
               print("----------> Image Deleted")
            }

        })
    }

    fun moreOptionAction(text: String, mobile: String, lon: String, lat: String) {
        when(text) {
            "What's App" -> {
                CommonFunctions.openInWhatsApp(ctx,mobile)
            }

            "Google Map" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lon")
                ctx.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var name: TextView
        var address: TextView
        var about: TextView
        var chipData: TextView
        var image: ImageView
        var share: ImageView
        var call: ImageView
        var more: ImageView
        var date: TextView
        var hidableLayout: LinearLayout
        var outerLayout: MaterialCardView


        init {
            title = itemView.findViewById(R.id.title)
            name = itemView.findViewById(R.id.name)
            address = itemView.findViewById(R.id.address)
            about = itemView.findViewById(R.id.about)
            chipData = itemView.findViewById(R.id.chipData)
            image = itemView.findViewById(R.id.image)
            share = itemView.findViewById(R.id.share)
            call = itemView.findViewById(R.id.call)
            more = itemView.findViewById(R.id.more)
            date = itemView.findViewById(R.id.date)
            outerLayout = itemView.findViewById(R.id.each_job_layout)
            hidableLayout = itemView.findViewById(R.id.hidableLayout)
        }
    }

}