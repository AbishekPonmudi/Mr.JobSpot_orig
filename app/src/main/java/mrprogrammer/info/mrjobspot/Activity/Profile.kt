package mrprogrammer.info.mrjobspot.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import mrprogrammer.info.mrjobspot.Activity.Settings.Settings
import mrprogrammer.info.mrjobspot.Firebase.Sync.SyncUserDetails
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.RealmModel.UserProfileInformation
import mrprogrammer.info.mrjobspot.Repository.UserRepository
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityProfileBinding

class Profile : AppCompatActivity() {
    lateinit var root:ActivityProfileBinding
    lateinit var userProfileInformation: UserProfileInformation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)

        if(intent.extras != null) {
            hideViewBasedOnValue(false)
            val userProfile = UserRepository.getUserDetailsBasesOnId("123")
            if (userProfile != null) {
                userProfileInformation = userProfile
            }
        } else {
            UserRepository.getUserDetails()?.let { userProfileInformation = (it) }
            hideViewBasedOnValue(true)
        }
        initUserData()
        initClick()
        setUpWorkDetails()
        setUpSkillDetails()
        setUpLanDetails()
    }


    private fun hideViewBasedOnValue(show:Boolean) {
        root.editProfile.visibility = if(show) View.VISIBLE else View.GONE
        root.viewProfile.visibility = if(show) View.VISIBLE else View.GONE
        root.settings.visibility = if(show) View.VISIBLE else View.GONE
    }

    private fun initClick() {
        root.settings.setOnClickListener {
            val activity = Intent(this, Settings::class.java)
            startActivity(activity)
            LocalFunctions.activityAnimation(this,fromFront = true)
        }

        root.viewProfile.setOnClickListener {
           val intent = Intent(this,ViewProfile::class.java)
            startActivity(intent)
            LocalFunctions.activityAnimation(this,fromFront = true)
            finish()
        }



        root.editProfile.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Do you want to remove existing profile ?")
                .setPositiveButton("Remove") { dialog, which ->
                    val intent = Intent(this,ProfileSetup::class.java)
                    startActivity(intent)
                    finish()
                    LocalFunctions.activityAnimation(this,true)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
            dialog.show()

        }
    }

    private fun initUserData() {
        val profileImage = userProfileInformation.imageUrl
        val profileName = userProfileInformation.username
        Glide.with(this).load(profileImage).into(root.userProfileImage)
        root.userProfileName.text = profileName
        root.about.text = userProfileInformation.about
        root.userLocation.text = userProfileInformation.address
    }

    private fun setUpSkillDetails(){
        root.skillChipGroup.visibility = View.VISIBLE
        val data = LocalFunctions.splitStringToList(userProfileInformation.skills)
        data.forEach {text ->
            val chip = Chip(this)
            chip.text = text
            chip.isCloseIconVisible = false
            chip.setChipBackgroundColorResource(R.color.chip_color);
            chip.typeface = ResourcesCompat.getFont(this, R.font.dm_regular)
            root.skillChipGroup.addView(chip)
        }
    }

    private fun setUpLanDetails() {
        root.lanChipGroup.visibility = View.VISIBLE
        val data = LocalFunctions.splitStringToList(userProfileInformation.languages)
        data.forEach {text ->
            val chip = Chip(this)
            chip.text = text
            chip.isCloseIconVisible = false
            chip.setChipBackgroundColorResource(R.color.chip_color);
            chip.typeface = ResourcesCompat.getFont(this, R.font.dm_regular)
            root.lanChipGroup.addView(chip)
        }
    }
    private fun setUpWorkDetails() {
        userProfileInformation.work?.forEach {
            val layout = LayoutInflater.from(this@Profile).inflate(R.layout.wrk_dynamic, null) as LinearLayout
            val wrkTextView = layout.findViewById<TextView>(R.id.wrk_name)
            val companyTextView = layout.findViewById<TextView>(R.id.company)
            val dateTextView = layout.findViewById<TextView>(R.id.date)
            wrkTextView.text = it.job
            companyTextView.text = it.office
            dateTextView.text = it?.start + " - " + it?.end
            root.workExpLayout.addView(layout)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this,fromFront = false)
    }
}