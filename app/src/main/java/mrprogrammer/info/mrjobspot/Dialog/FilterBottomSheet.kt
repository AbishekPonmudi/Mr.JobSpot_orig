package mrprogrammer.info.mrjobspot.Dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mrprogrammer.Utils.Interface.CompleteHandler
import mrprogrammer.info.mrjobspot.R


class FilterBottomSheet (context: Context,val currentSelectedPosition:Int,val data:List<String>,val completeHandler: CompleteHandler,val showCheckbox:Boolean = true,val titleText:String) : BottomSheetDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter_bottom_sheet)
        val listView = findViewById<ListView>(R.id.filterData)
        val clear = findViewById<TextView>(R.id.clear)
        val title = findViewById<TextView>(R.id.title)
        val adapter = ListCustomAdapter(context,data)
        listView?.adapter = adapter
        listView?.divider = null

        title?.text = titleText

        if(showCheckbox) {
            clear?.visibility = View.VISIBLE
        }else {
            clear?.visibility = View.GONE
        }

        clear?.setOnClickListener {
            completeHandler.onFailure("")
            dismiss()
        }
    }

    inner class ListCustomAdapter(private val context: Context, private val itemList: List<String>) : BaseAdapter() {

        override fun getCount(): Int {
            return this.itemList.size
        }

        override fun getItem(position: Int): Any {
            return this.itemList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = LayoutInflater.from(context)
            val view = convertView ?: inflater.inflate(R.layout.list_item_layout, parent, false)

            val itemTextView = view.findViewById<TextView>(R.id.text)
            val outerLayout = view.findViewById<LinearLayout>(R.id.outer_layout)
            val checkBox = view.findViewById<CheckBox>(R.id.checkbox)


            if(showCheckbox) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.GONE
            }
            checkBox.isChecked = position == currentSelectedPosition

            outerLayout.setOnClickListener {
                completeHandler.onSuccess(this.itemList[position])
                dismiss()
            }

            val currentItem = this.itemList[position]
            itemTextView.text = currentItem

            return view
        }
    }

}
