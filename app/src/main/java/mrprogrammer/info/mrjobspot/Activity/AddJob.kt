package mrprogrammer.info.mrjobspot.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.Dialog.LoadingDialog
import mrprogrammer.info.mrjobspot.Firebase.FirebaseUploadJob
import mrprogrammer.info.mrjobspot.Interface.UploadImageEvent
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.Repository.UserRepository
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.BadWords
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.Utils.UploadImages
import mrprogrammer.info.mrjobspot.databinding.ActivityPostWorkBinding
import java.io.ByteArrayOutputStream


class AddJob : AppCompatActivity() {
    lateinit var root:ActivityPostWorkBinding
    var lat = "0.0"
    var lon = "0.0"
    var address = ""
    var isLocationChoosen = false
    var isImageChoosen = false
    var skillList:MutableList<String> = mutableListOf()
    var locality=""
    lateinit var selectedImageUri:Uri
    private lateinit var dialog:LoadingDialog
    var currentUserEmail = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root  = ActivityPostWorkBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        dialog = LoadingDialog(this)
        initValue()
        initClick()
    }

    private fun initValue() {
        val number = UserRepository.getUserDetails()?.mobile
        currentUserEmail = UserValue.getUserEmail(this)
        root.mobile.text = number
    }

    private fun initClick(){
        root.appBar.back.setOnClickListener {
            onBackPressed()
        }

        root.postJob.setOnClickListener {
            if(!CommonFunctions.isConnected(this)) {
                MrContext.MrToast().error(this@AddJob,"Please connect to Internet")
                return@setOnClickListener
            }

            if(validUserData()) {
                if(validInputText()) {
                    return@setOnClickListener
                }
                dialog.show()
                val dbCompleteHandler = object : CompleteHandler{
                    override fun onFailure(e: String) {
                        dialog.dismiss()
                        MrContext.MrToast().error(this@AddJob, "Posting Failed")
                    }

                    override fun onSuccess(Message: Any) {
                        dialog.dismiss()
                        MrContext.MrToast().success(this@AddJob, "Job Posted")
                        finishActivity()
                    }
                }

                UploadImages.uploadImage(this,selectedImageUri, object : UploadImageEvent {
                    override fun onUploadCompleted(downloadUrl: String, imageKey: String) {
                        FirebaseUploadJob.saveJobToFirebase(
                            this@AddJob,
                            root.jobTitle.text.toString(),
                            root.aboutText.text.toString(),
                            root.mobile.text.toString(),
                            skillList[0],
                            root.locationText.text.toString(),
                            downloadUrl,
                            locality,
                            imageKey,
                            dbCompleteHandler,
                            currentUserEmail,
                            lat,
                            lon
                        )
                        isImageChoosen = false
                        isLocationChoosen = false
                    }

                    override fun onUploadFailed(cause: String) {
                        dialog.dismiss()
                        MrContext.MrToast().error(this@AddJob, "Posting Failed")
                    }

                    override fun onUploadProgress(level: Double) {
                    }
                })
            }
        }

        root.skill.setOnClickListener {
            skillList.clear()
            root.skillChipGroup.removeAllViews()
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("title","Choose your skill")
            intent.putExtra("type",0)
            intent.putExtra("choose",1)
            startActivityForResult(intent, 1134)
            LocalFunctions.activityAnimation(this, fromFront = true)
        }

        root.location.setOnClickListener {
            val intent = Intent(this, LocationChooser::class.java)
            startActivityForResult(intent, 7011)
            LocalFunctions.activityAnimation(this, fromFront = true)
        }

        root.gallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 7077)
        }

        root.camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 1133)
            } else {
                MrContext.MrToast().error(this,"No camera app found")
            }
        }
    }

    private fun validInputText(): Boolean {
        val title = root.jobTitle.text.toString()
        val description = root.aboutText.text.toString()
        val isTitleBad = containsBadWords(title)
        val isDescriptionBad = containsBadWords(description)

        if(isTitleBad || isDescriptionBad) {
            return true
        }
        return false
    }

    private fun containsBadWords(sentence: String): Boolean {
        val listOfBad = LocalFunctions.splitStringToList(BadWords.badWords,",")
        val lowerCaseSentence = sentence.toLowerCase()
        for (badWord in listOfBad) {
            if (lowerCaseSentence.contains(badWord)) {
                MrContext.MrToast().information(this, "$badWord is an offensive word. ")
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === 7011 && resultCode === RESULT_OK) {
            val lat = data?.extras?.getString("lat")
            val lon = data?.extras?.getString("lon")
            val add = data?.extras?.getString("add")
            locality = data?.extras?.getString("add1").toString()

            this.lat = lat.toString()
            this.lon = lon.toString()
            this.address = add.toString()
            root.locationText.visibility = View.VISIBLE
            root.locationText.text = add
            isLocationChoosen = true
        }else if (requestCode == 7077 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            isImageChoosen = true
            loadImageUri(selectedImageUri)
        }else if (requestCode == 1133 && resultCode == RESULT_OK) {
            val capturedImage = data?.extras?.get("data") as Bitmap
            selectedImageUri = getImageUri(this,capturedImage)
            isImageChoosen = true
            loadImageUri(selectedImageUri)
        } else if(requestCode === 1134 && resultCode === RESULT_OK) {
            val data = data?.extras?.getStringArrayList("finalList") ?: return
            buildSkillList(data)
        }
    }


    private fun validUserData(): Boolean {
        if(root.jobTitle.text.toString().isEmpty()) {
            MrContext.MrToast().warning(this, "Please enter Job Title ..")
            return false
        }

        if(root.jobTitle.text.toString().length < 5) {
            MrContext.MrToast().warning(this, "Please enter valid Job Title ..")
            return false
        }

        if(root.aboutText.text.toString().isEmpty()) {
            MrContext.MrToast().warning(this, "Please enter Job Description ..")
            return false
        }

        if(root.aboutText.text.toString().length < 20) {
            MrContext.MrToast().warning(this, "Please enter valid Description ..")
            return false
        }

        if(root.mobile.text.toString().isEmpty()) {
            MrContext.MrToast().warning(this, "Please enter Job Mobile Number..")
            return false
        }


        if(root.mobile.text.toString().length != 10) {
            MrContext.MrToast().warning(this, "Please enter Valid Mobile Number..")
            return false
        }

        if(skillList.isEmpty()) {
            MrContext.MrToast().warning(this, "Please select required skill")
            return false
        }

        if(!isImageChoosen) {
            MrContext.MrToast().warning(this, "Please add Job Image")
            return false
        }

        if(!isLocationChoosen) {
            MrContext.MrToast().warning(this, "Please add Job Location")
            return false
        }

        return true
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    private fun loadImageUri(imageUri: Uri) {
        Glide.with(this).load(imageUri).into(root.image)
        root.ImageCard.visibility = View.VISIBLE
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
    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this ,false)
    }

    private fun finishActivity(){
        finish()
        LocalFunctions.activityAnimation(this,false)
    }
}