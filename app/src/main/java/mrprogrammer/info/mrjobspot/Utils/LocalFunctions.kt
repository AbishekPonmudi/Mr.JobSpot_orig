package mrprogrammer.info.mrjobspot.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mrprogrammer.Utils.CommonFunctions.LocalSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mrprogrammer.info.mrjobspot.Model.ItemListModel
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.Utils.Const.Companion.MOBILENUMBER
import java.util.*

class LocalFunctions {
    companion object{
        fun activityAnimation(context: Context, fromFront: Boolean) {
            if (context is Activity && fromFront) {
                context.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
                return
            }
            (context as Activity).overridePendingTransition(
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }

        fun hideKeyboard(context: Context,view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun convertStringToBoolean(string: String): Boolean {
            if(string == "true" || string == "True"){
                return true
            }
            return false
        }

        fun convertDimensionToPixels(context: Context, dimension: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimension, context.resources.displayMetrics).toInt()
        }

        fun hasConsecutiveSpacesOrSymbols(input: String): Boolean {
            var consecutiveCount = 0
            var previousChar = '\u0000'
            for (currentChar in input.toCharArray()) {
                if (currentChar == ' ' || !Character.isLetterOrDigit(currentChar)) {
                    if (currentChar == previousChar) {
                        consecutiveCount++
                        if (consecutiveCount > 2) {
                            return true
                        }
                    } else {
                        consecutiveCount = 1
                        previousChar = currentChar
                    }
                } else {
                    consecutiveCount = 0
                    previousChar = '\u0000'
                }
            }
            return false
        }

        fun buildConsecutiveString(data:MutableList<String>): String {
            var finalString = ""
            data.forEach {
                finalString = "$finalString~*-*~$it"
            }
            return finalString
        }

        fun splitStringToList(input: String,delimiter:String  = "~*-*~"): List<String> {
            val data =  input.split(delimiter) as MutableList
            data.removeAt(0)
            return data
        }

        suspend fun getAddress(context: Context, lat: Double, lon: Double): List<Address>? {
            return withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    return@withContext geocoder.getFromLocation(lat, lon, 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext null
                }
            }
        }

        fun getProfileListValue(id:Int): MutableList<ItemListModel> {
            return when(id){
                0 -> {
                    val skillList: MutableList<ItemListModel> = mutableListOf()
                    Const.skills.forEach {
                        val itemListModel = ItemListModel()
                        itemListModel.title = it
                        itemListModel.checkState = false
                        skillList.add(itemListModel)
                    }
                    return skillList
                }
                1 -> {
                    val lanList:MutableList<ItemListModel> = mutableListOf()
                    Const.indianLanguages.forEach {
                        val itemListModel = ItemListModel()
                        itemListModel.title = it
                        itemListModel.checkState = false
                        lanList.add(itemListModel)
                    }
                    return lanList
                }
                else -> {
                    mutableListOf()
                }
            }
        }

        fun shareText(context: Context,text:String) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(Intent.createChooser(intent, "Share via"))
        }


        fun getUserMobileNumber(context: Context): String {
            return LocalSharedPreferences.getPreferences(context, MOBILENUMBER)
        }
    }
}