package mrprogrammer.info.mrjobspot.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.LocalSharedPreferences
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.Dialog.LoadingDialog
import mrprogrammer.info.mrjobspot.Firebase.Sync.SyncUserDetails
import mrprogrammer.info.mrjobspot.Firebase.FirebaseUploadUserProfile.Companion.saveUserDataToFirebase
import mrprogrammer.info.mrjobspot.Model.LocalWorkModel
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityProfieSetupBinding
import java.util.ArrayList

class ProfileSetup : AppCompatActivity() {
    lateinit var root:ActivityProfieSetupBinding
    var listOfWork:MutableList<LocalWorkModel> = mutableListOf()
    var skillList:MutableList<String> = mutableListOf()
    var lanList:MutableList<String> = mutableListOf()
    var isLocationChoosen = false
    var lat = "0.0"
    var lon = "0.0"
    var address = ""
    var isUpdate = false
    var locality = ""
    lateinit var dialog:LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root =  ActivityProfieSetupBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)

        dialog = LoadingDialog(this)
        initClick()
    }

    private fun initClick(){
        root.save.setOnClickListener {
            val email = UserValue.getUserEmail(this)

            if(root.aboutText.text.isEmpty() || root.aboutText.text.length < 10 || LocalFunctions.hasConsecutiveSpacesOrSymbols(root.aboutText.text.toString())) {
                MrContext.MrToast().error(this,"InValid About")
                return@setOnClickListener
            }

            if(root.number.text.toString().isEmpty()) {
                MrContext.MrToast().error(this,"Enter Mobile Number")
                return@setOnClickListener
            }


            if(root.number.text.toString().length != 10) {
                MrContext.MrToast().error(this,"Enter valid Mobile Number")
                return@setOnClickListener
            }

            if(listOfWork.isEmpty()) {
                MrContext.MrToast().error(this,"Add Job's")
                return@setOnClickListener
            }

            if(skillList.isEmpty()) {
                MrContext.MrToast().error(this,"Add Skill")
                return@setOnClickListener
            }

            if(lanList.isEmpty()) {
                MrContext.MrToast().error(this,"Add Languages")
                return@setOnClickListener
            }


            if(!isLocationChoosen) {
                MrContext.MrToast().error(this,"Choose Location")
                return@setOnClickListener
            }

            if(!CommonFunctions.isConnected(this)) {
                MrContext.MrToast().error(this,"Connect to Network")
                return@setOnClickListener
            }

            val completeHandler = object : CompleteHandler{
                override fun onFailure(e: String) {
                    MrContext.MrToast().error(this@ProfileSetup, e)
                }

                override fun onSuccess(Message: Any) {
                    LocalSharedPreferences.savePreferences(this@ProfileSetup,Const.PROFILE, "true")
                    LocalFunctions.activityAnimation(this@ProfileSetup,true)
                    startActivity(Intent(this@ProfileSetup, BaseActivity::class.java))
                    SyncUserDetails().syncProfile(this@ProfileSetup)
                    dialog.dismiss()
                    finish()

                }

            }

            isLocationChoosen = false
            dialog.show()
            saveUserDataToFirebase(email, root.aboutText.text.toString(), skillList, lanList, listOfWork, completeHandler, lat, lon, address,locality, root.number.text.toString())
        }

        root.wrkClick.setOnClickListener {
            val intent = Intent(this, AddWorkProfile::class.java)
            startActivityForResult(intent, 7077)
            LocalFunctions.activityAnimation(this, fromFront = true)
        }

        root.skill.setOnClickListener {
            skillList.clear()
            root.skillChipGroup.removeAllViews()
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("title","Choose your skill")
            intent.putExtra("type",0)
            startActivityForResult(intent, 1134)
            LocalFunctions.activityAnimation(this, fromFront = true)
        }

        root.lanadd.setOnClickListener {
            lanList.clear()
            root.lanChipGroup.removeAllViews()
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("title","Choose your Languages")
            intent.putExtra("type",1)
            startActivityForResult(intent, 70771134)
            LocalFunctions.activityAnimation(this, fromFront = true)
        }

        root.location.setOnClickListener {
            val intent = Intent(this, LocationChooser::class.java)
            startActivityForResult(intent, 7011)
            LocalFunctions.activityAnimation(this, fromFront = true)
        }
    }


    private fun buildWorkLayout(localWorkModel:LocalWorkModel){
        val layout = LayoutInflater.from(this@ProfileSetup).inflate(R.layout.wrk_dynamic, null) as LinearLayout
        listOfWork.add(localWorkModel)
        layout.setOnLongClickListener {
            root.workExpLayout.removeView(layout)
            removedWork(localWorkModel.id)
            true
        }
        val wrkTextView = layout.findViewById<TextView>(R.id.wrk_name)
        val companyTextView = layout.findViewById<TextView>(R.id.company)
        val dateTextView = layout.findViewById<TextView>(R.id.date)
        wrkTextView.text = localWorkModel.job
        companyTextView.text = localWorkModel.company
        dateTextView.text = localWorkModel?.start +" - "+localWorkModel?.end
        root.workExpLayout.addView(layout)
        root.workExpLayout.visibility = View.VISIBLE
    }


    private fun buildSkillList(data: ArrayList<String>) {
        skillList.addAll(data)
        root.skillChipGroup.visibility = View.VISIBLE
        data.forEach {text ->
            val chip = Chip(this)
            chip.text = text
            chip.isCloseIconVisible = true
            chip.setChipBackgroundColorResource(R.color.chip_color);
            chip.typeface = ResourcesCompat.getFont(this, R.font.dm_regular)
            chip.setOnCloseIconClickListener{
                root.skillChipGroup.removeView(chip)
                val index = skillList.indexOf(text)
                skillList.removeAt(index)
                if(skillList.isEmpty()) {
                    root.skillChipGroup.visibility =View.GONE
                }
            }
            root.skillChipGroup.addView(chip)
        }
    }

    fun buildLanList(data: ArrayList<String>) {
        lanList.addAll(data)
        root.lanChipGroup.visibility = View.VISIBLE
        data.forEach {text ->
            val chip = Chip(this)
            chip.text = text
            chip.isCloseIconVisible = true
            chip.setChipBackgroundColorResource(R.color.chip_color);
            chip.typeface = ResourcesCompat.getFont(this, R.font.dm_regular)
            chip.setOnCloseIconClickListener{
                root.lanChipGroup.removeView(chip)
                val index = lanList.indexOf(text)
                lanList.removeAt(index)
                if(skillList.isEmpty()) {
                    root.lanChipGroup.visibility =View.GONE
                }
            }
            root.lanChipGroup.addView(chip)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === 7077 && resultCode === RESULT_OK) {
            if (data != null && data.hasExtra("model")) {
                val localWorkModel = data.extras?.getSerializable("model") as LocalWorkModel? ?: return
                buildWorkLayout(localWorkModel)
            }
        } else if(requestCode === 1134 && resultCode === RESULT_OK) {
            val data = data?.extras?.getStringArrayList("finalList") ?: return
            buildSkillList(data)
        }
        else if(requestCode === 70771134 && resultCode === RESULT_OK) {
            val data = data?.extras?.getStringArrayList("finalList") ?: return
            buildLanList(data)

        } else if(requestCode === 7011 && resultCode === RESULT_OK) {
            val lat = data?.extras?.getString("lat")
            val lon = data?.extras?.getString("lon")
            val add = data?.extras?.getString("add")
            locality= data?.extras?.getString("add1").toString()

            this.lat = lat.toString()
            this.lon = lon.toString()
            this.address = add.toString()
            root.locationText.visibility = View.VISIBLE
            root.locationText.text = add
            isLocationChoosen = true
        }
    }

    private fun removedWork(id:String) {
        var index = 0
        listOfWork.forEach {
            if( it.id == id) {
                index =listOfWork.indexOf(it)
            }
        }
        listOfWork.removeAt(index)
        if(listOfWork.size == 0) {
            root.workExpLayout.visibility=View.GONE
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this,false)
    }
}

