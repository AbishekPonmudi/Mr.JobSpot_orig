package mrprogrammer.info.mrjobspot.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import mrprogrammer.info.mrjobspot.R

class LoadingDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(0))
        setCancelable(false)
        setContentView(R.layout.loading_dialog)
    }
}