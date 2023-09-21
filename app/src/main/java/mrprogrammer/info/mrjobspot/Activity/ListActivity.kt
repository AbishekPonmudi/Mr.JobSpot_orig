package mrprogrammer.info.mrjobspot.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import mrprogrammer.info.mrjobspot.Model.ItemListModel
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {
    lateinit var root:ActivityListBinding
    var listOfString:MutableList<ItemListModel> = mutableListOf()
    lateinit var adapter:ListCustomAdapter
    var finalList :MutableList<String> = mutableListOf()
    var chooseCount = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityListBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        initClick()

        val title = intent.getStringExtra("title")
        val type = intent.getIntExtra("type", -1)
        chooseCount = intent.getIntExtra("choose", -1)
        listOfString.addAll(LocalFunctions.getProfileListValue(type))
        root.title.text = title

        adapter = ListCustomAdapter(this,listOfString)
        root.listItem.adapter = adapter
        root.listItem.divider = null
    }

    private fun initClick() {
        root.back.setOnClickListener {
            onBackPressed()
        }

        root.save.setOnClickListener {
            val intent = Intent()
            intent.putExtra("finalList", ArrayList(finalList))
            setResult(RESULT_OK, intent)
            finish()
            LocalFunctions.activityAnimation(this,false)
        }

        root.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                adapter.filter(inputText)
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this,fromFront = false)
    }

    inner class ListCustomAdapter(private val context: Context, private val itemList: List<ItemListModel>) : BaseAdapter() {
            var localItemList = itemList.toMutableList()

        fun filter(string: String) {
            localItemList.clear()

            localItemList.addAll(itemList.filter {
                it.title.contains(string)
            })
            notifyDataSetChanged()
        }
        override fun getCount(): Int {
            return localItemList.size
        }

        override fun getItem(position: Int): Any {
            return localItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = LayoutInflater.from(context)
            val view = convertView ?: inflater.inflate(R.layout.list_item_layout, parent, false)

            val itemTextView = view.findViewById<TextView>(R.id.text)
            val outerLayout = view.findViewById<LinearLayout>(R.id.outer_layout)
            val checkbox = view.findViewById<CheckBox>(R.id.checkbox)

            checkbox.isChecked = localItemList[position].checkState

            outerLayout.setOnClickListener {
                if(checkbox.isChecked) {
                    localItemList[position].checkState = false
                    val index = finalList.indexOf(localItemList[position].title)
                    finalList.removeAt(index)
                }else {
                    localItemList[position].checkState = true
                    if(chooseCount == -1){
                        finalList.add(localItemList[position].title)
                    } else {
                        if(finalList.isEmpty()) {
                            finalList.add(localItemList[position].title)
                        } else {
                            localItemList[position].checkState = false
                            MrContext.MrToast().information(this@ListActivity,"Choose only one skill")
                            notifyDataSetChanged()
                        }
                    }
                }
                notifyDataSetChanged()
            }

            val currentItem = localItemList[position].title
            itemTextView.text = currentItem

            return view
        }
    }

}