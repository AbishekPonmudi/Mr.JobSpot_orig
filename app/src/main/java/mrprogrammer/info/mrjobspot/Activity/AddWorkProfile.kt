package mrprogrammer.info.mrjobspot.Activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import mrprogrammer.info.mrjobspot.Model.LocalWorkModel
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions.Companion.hasConsecutiveSpacesOrSymbols
import mrprogrammer.info.mrjobspot.databinding.ActivityAddWorkBinding
import java.text.SimpleDateFormat
import java.util.*


class AddWorkProfile : AppCompatActivity() {
    lateinit var root: ActivityAddWorkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityAddWorkBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        initDefaultDate()
        initClick()
    }

    private fun initClick() {
        root.startDate.setOnClickListener {
            datePicker(root.startDate)
        }
        root.endDate.setOnClickListener {
            datePicker(root.endDate)
        }

        root.save.setOnClickListener {
            getAndSentBack()
        }

        root.skip.setOnClickListener {
            skipAndMoveToNextStep()
        }

        root.back.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this, fromFront = false)
    }

    private fun initDefaultDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val formattedDate = dateFormat.format(currentDate)
        root.startDate.setText(formattedDate)
        root.endDate.setText(formattedDate)
    }

    private fun datePicker(textView: TextView) {
        val datePickerDialog = DatePickerDialog(
            this, R.style.CustomDatePickerDialogTheme, { _, year, month, dayOfMonth ->
                var localMonth = ""
                if (month.toString().length == 1) {
                    localMonth = "0${month + 1}"
                } else {
                    localMonth = (month + 1).toString()
                }
                val selectedDate = "$dayOfMonth/$localMonth/$year"
                textView.text = selectedDate
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun getAndSentBack() {
        if (root.job.text.isEmpty() || root.job.text.length < 4 || hasConsecutiveSpacesOrSymbols(
                root.job.text.toString()
            )
        ) {
            MrContext.MrToast().error(this, "Please enter a valid job title")
            return
        }

        if (root.company.text.isEmpty() || root.company.text.length < 5 || hasConsecutiveSpacesOrSymbols(
                root.company.text.toString()
            )
        ) {
            MrContext.MrToast().error(this, "Please enter a valid company")
            return
        }

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = format.parse(root.startDate.text.toString())
        val endDate = format.parse(root.endDate.text.toString())
        val result: Int = startDate.compareTo(endDate)
        if (result > 0 || endDate.time < startDate.time) {
            MrContext.MrToast().error(this, "Please enter a valid date")
            return
        }

        if (root.aboutText.text.isEmpty() || root.aboutText.text.length < 15 || hasConsecutiveSpacesOrSymbols(
                root.aboutText.text.toString()
            )
        ) {
            MrContext.MrToast().error(this, "Please enter a valid description")
            return
        }

        val workModel = LocalWorkModel()
        workModel.id = CommonFunctions.createUniqueKey().toString()
        workModel.end = root.endDate.text.toString()
        workModel.job = root.job.text.toString()
        workModel.company = root.company.text.toString()
        workModel.start = root.startDate.text.toString()
        workModel.description = root.aboutText.text.toString()

        val intent = Intent()
        intent.putExtra("model", workModel)
        setResult(RESULT_OK, intent)
        finish()
        LocalFunctions.activityAnimation(this, false)

    }

    private fun skipAndMoveToNextStep() {
        val defaultJobTitle = ""
        val defaultCompany = "NA"
        val defaultStartDate = ""
        val defaultEndDate = ""
        val defaultDescription = ""

        root.job.setText(defaultJobTitle)
        root.company.setText(defaultCompany)
        root.startDate.text = defaultStartDate
        root.endDate.text = defaultEndDate
        root.aboutText.setText(defaultDescription)

        val workModel = LocalWorkModel()
        workModel.id = CommonFunctions.createUniqueKey().toString()
        workModel.end = defaultEndDate
        workModel.job = defaultJobTitle
        workModel.company = defaultCompany
        workModel.start = defaultStartDate
        workModel.description = defaultDescription

        val intent = Intent()
        intent.putExtra("model", workModel)
        setResult(RESULT_OK, intent)
        finish()
        LocalFunctions.activityAnimation(this, false)
    }
}
