package  mrprogrammer.info.mrjobspot.Activity.Settings

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.LocalSharedPreferences
import com.mrprogrammer.mrshop.Activity.Settings.SettingBuilder
import com.rjtech.shop.Interface.ItemClickListener
import mrprogrammer.info.mrjobspot.Activity.ProfileSetup
import mrprogrammer.info.mrjobspot.BuildConfig
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivitySettingsBinding


class Settings : AppCompatActivity() {
    lateinit var root: ActivitySettingsBinding
    lateinit var adapter: SettingsAdapters
    var adaptersData = mutableListOf<SettingsModel>()
    lateinit var completeHandler: ItemClickListener
    lateinit var reviewManager: ReviewManager
    var recycleViewState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivitySettingsBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        reviewManager = ReviewManagerFactory.create(this);
        initHandler()
        initData()
        initViews()
        setUpAppBar("Settings")
    }

    private fun initHandler() {
        completeHandler = object : ItemClickListener {
            override fun onItemClick(id: String) {
                when (id) {
                    "1" -> {
                        CommonFunctions.openInWhatsApp(this@Settings, Const.whatsappNumber)
                    }
                    "2" -> {
                        inAppReview()
                    }
                    "3" -> {
                        CommonFunctions.openUrlInBrowser(
                            this@Settings,
                           "https://mrprogrammer.info"
                        )
                    }
                    "4" -> {
                        val dialog = MaterialAlertDialogBuilder(this@Settings)
                            .setTitle(getString(R.string.app_name))
                            .setMessage("Do you want to Logout ?")
                            .setPositiveButton("Yes") { dialog, which ->
                                CommonFunctions.logout(this@Settings)
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, which ->
                                dialog.dismiss()
                            }
                        dialog.show()
                    }
                }

            }
        }
    }

    private fun inAppReview() {
        if(LocalSharedPreferences.isAppReviewed(this@Settings)) {
            MrContext.MrToast().message(this@Settings,"Already Reviewed")
            return
        }
        val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    LocalSharedPreferences.setAppReviewed(this@Settings)
                }
            } else {
                MrContext.MrToast().information(this,"Already Reviewed")
            }
        }
    }

    private fun changeStateForRecyclerView() {
        recycleViewState = true
        root.dynamic.removeAllViews()
        root.dynamic.visibility = View.VISIBLE
        root.settingsRecycler.visibility = View.GONE
    }

    private fun initData() {
        root.version.text = BuildConfig.VERSION_NAME
        adaptersData.add(SettingsModel("1", getDrawableFromInt(R.drawable.headset), "Help Center"))
        adaptersData.add(SettingsModel("2", getDrawableFromInt(R.drawable.star), "Reviews"))
     //   adaptersData.add(SettingsModel("3", getDrawableFromInt(R.drawable.document), "Terms and conditions"))
        adaptersData.add(SettingsModel("4", getDrawableFromInt(R.drawable.logout), "Logout"))
    }


    private fun getDrawableFromInt(icon: Int): Drawable? {
        return ContextCompat.getDrawable(this@Settings, icon)
    }

    private fun initViews() {
        adapter = SettingsAdapters(this@Settings, adaptersData, completeHandler)
        root.settingsRecycler.layoutManager = LinearLayoutManager(this)
        root.settingsRecycler.adapter = adapter
    }

    private fun setUpAppBar(title: String) {
        root.appBar.title.text = title
        root.appBar.back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (recycleViewState) {
            setUpAppBar("Settings")
            recycleViewState = false
            root.settingsRecycler.visibility = View.VISIBLE
            root.dynamic.visibility = View.GONE
            return
        }
        super.onBackPressed()
        LocalFunctions.activityAnimation(this, fromFront = false)
    }

    private fun buildSoundLater() {
        root.dynamic.addView(
            SettingBuilder.buildTextWithSwitch(
                this,
                "Cart Click Sound",
                "On click add to cart a beep sound",
                "cardSound",
                defaultState = "true"
            )
        )
    }

    private fun buildItemForNotification() {
        root.dynamic.addView(
            SettingBuilder.buildTextWithSwitch(
                this,
                "Cart Notification",
                "If the cart has item, then it will be notified.",
                "cardNotification",
                defaultState = "true"
            )
        )
        root.dynamic.addView(
            SettingBuilder.buildTextWithSwitch(
                this,
                "Push Notification",
                "If the any notification was sent by company, then it will be notified.",
                "pushNotification",
                defaultState = "true"
            )
        )
    }
}