package mrprogrammer.info.mrjobspot.Activity.Settings

import android.graphics.drawable.Drawable

class SettingsModel(private val ids: String, private val icons: Drawable?, private val titls: String) {
    var id: String = ids
    var icon: Drawable? = icons
    var title: String = titls
}