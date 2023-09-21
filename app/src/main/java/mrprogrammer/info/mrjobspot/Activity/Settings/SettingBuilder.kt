package com.mrprogrammer.mrshop.Activity.Settings

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import mrprogrammer.info.mrjobspot.R

class SettingBuilder {

    companion object {
        fun buildTextWithSwitch(context: Context, text: String, hint:String,preferenceKey: String, defaultState:String = "-NONE-"): LinearLayout {
            val outerLayout = LinearLayout(context)
            outerLayout.orientation = LinearLayout.VERTICAL
            outerLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val innerLayout = LinearLayout(context)
            innerLayout.orientation = LinearLayout.HORIZONTAL
            innerLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            innerLayout.addView(buildTextWithHint(context, text, hint))
            innerLayout.addView(buildSwitch(context, preferenceKey, defaultState))

            outerLayout.addView(innerLayout)
            outerLayout.addView(buildEmptyView(context, 52))
            return outerLayout
        }

        private fun buildSwitch(context: Context, preferenceKey: String, defaultState: String): SwitchMaterial {
            val switch = SwitchMaterial(context)
            val state = convertStringIntoBoolean(getSettingsState(context,preferenceKey, defaultState))
            switch.gravity = Gravity.END or Gravity.CENTER_VERTICAL  or Gravity.CENTER

            val switchLayoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,0.5f)
            switch.layoutParams = switchLayoutParams
            switch.isChecked = state

            switch.changeSwitchColorBasedOnValue(state)
            switch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    saveSettingsState(context,preferenceKey,"true")
                } else {
                    saveSettingsState(context,preferenceKey,"false")
                }
                switch.changeSwitchColorBasedOnValue(isChecked)
            }
            return switch
        }


        private fun SwitchMaterial.changeSwitchColorBasedOnValue(state:Boolean) {
            if(state){
                val thumbColor: ColorStateList = ColorStateList.valueOf(context.resources.getColor(R.color.darkBlue))
                this.thumbTintList = thumbColor
                val trackColor: ColorStateList = ColorStateList.valueOf(context.resources.getColor(R.color.darkBlue))
                this.trackTintList = trackColor
            } else {
                val thumbColor: ColorStateList = ColorStateList.valueOf(context.resources.getColor(R.color.darkBlue))
                this.thumbTintList = thumbColor
                val trackColor: ColorStateList = ColorStateList.valueOf(context.resources.getColor(R.color.darkBlue))
                this.trackTintList = trackColor
            }
        }

        private fun buildText(context: Context, text: String, size: Float = 16f,marginTop:Int = 16,marginBottom:Int = 8):TextView {
            val textView = TextView(context)
            textView.text = text
            val textViewLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textViewLayoutParams.setMargins(16, 8, marginTop, marginBottom)
            textView.layoutParams = textViewLayoutParams
            textView.textSize = size
            textView.setPadding(8, 8, 8, marginBottom)
            return textView
        }

        private fun buildTextWithHint(context: Context, text: String, hint: String):LinearLayout {
            val outerLayout = LinearLayout(context)
            val outerLayoutParams =LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f)
            outerLayout.layoutParams = outerLayoutParams
            val innerLayout = LinearLayout(context)
            innerLayout.orientation = LinearLayout.VERTICAL
            innerLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            innerLayout.addView(buildText(context, text, marginBottom = 0))
            innerLayout.addView(buildText(context, hint, size = 12f, marginTop = -10))
            outerLayout.addView(innerLayout)
            return outerLayout
        }

        private fun saveSettingsState(context: Context,key:String,value: String){
            val sharedPreferences = context.getSharedPreferences("Settings",Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getSettingsState(context: Context, key: String, defaultState:String): String {
            val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, defaultState).toString()
        }

        fun convertStringIntoBoolean(text:String):Boolean{
            if(text == "true") {
                return true
            }
            return false
        }

        private fun buildEmptyView(context: Context,height : Int): View {
            val outerLayout = View(context)
            val outerLayoutParams =LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
            outerLayout.layoutParams = outerLayoutParams
            return outerLayout
        }
    }
}