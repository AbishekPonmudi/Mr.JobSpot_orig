package mrprogrammer.info.mrjobspot.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.realm.RealmList
import mrprogrammer.info.mrjobspot.FirebaseModel.UserInfo
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.RealmModel.UserProfileInformation
import mrprogrammer.info.mrjobspot.RealmModel.WorkRealm
import mrprogrammer.info.mrjobspot.Repository.UserRepository
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityViewProfileBinding

class ViewProfile :AppCompatActivity() {
    val userProfileDataFilter = mutableListOf<UserProfileInformation>()
    val userProfileData = mutableListOf<UserProfileInformation>()
    lateinit var adapter:ListCustomAdapter
    lateinit var root:ActivityViewProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityViewProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)

        init()
        initOnClick()


    }

    private fun initOnClick() {
        root.appBar.back.setOnClickListener {
            onBackPressed()
        }
        root.appBar.title.text = "View Profile"

        root.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                filterTheJobList(editable.toString())
            }

        })
    }

    private fun filterTheJobList(string: String) {
        userProfileDataFilter.clear()
        userProfileData.forEach {
            if(it.email.toString().contains(string,true) || it.username.toString().contains(string,true)) {
                userProfileDataFilter.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun init() {
        val listView = findViewById<ListView>(R.id.profileData)

        adapter = ListCustomAdapter(this,userProfileDataFilter)
        listView?.adapter = adapter
        listView?.divider = null

        val db = FirebaseDatabase.getInstance().reference
        val result = db.child("Userdata")

        result.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userProfileDataFilter.clear()
                userProfileData.clear()
                snapshot.children.forEach {
                    val data = it.getValue(UserInfo::class.java)
                    val userProfileInformation = UserProfileInformation()
                    userProfileInformation.id = "123"
                    userProfileInformation.about = data?.about.toString()
                    userProfileInformation.skills = data?.skill.toString()
                    userProfileInformation.languages = data?.lan.toString()
                    userProfileInformation.mobile = data?.Mobile.toString()
                    userProfileInformation.username = data?.Username.toString()
                    userProfileInformation.imageUrl = data?.Imageurl.toString()
                    userProfileInformation.email = data?.Email.toString()
                    userProfileInformation.lat = data?.lat.toString()
                    userProfileInformation.lon = data?.lon.toString()
                    userProfileInformation.address = data?.address.toString()
                    userProfileInformation.locality = data?.locality.toString()
                    val listOfWork = mutableListOf<WorkRealm>()
                    data?.work?.forEach {
                        val localWork = WorkRealm()
                        localWork.id = it.key
                        localWork.dis = it.value.dis.toString()
                        localWork.end = it.value.end.toString()
                        localWork.start = it.value.start.toString()
                        localWork.job = it.value.job.toString()
                        localWork.office = it.value.office.toString()
                        listOfWork.add(localWork)
                    }
                    if (userProfileInformation.work == null) {
                        userProfileInformation.work = RealmList<WorkRealm>()
                    }
                    userProfileInformation.work?.addAll(listOfWork)
                    if (data != null) {
                        userProfileDataFilter.add(userProfileInformation)
                        userProfileData.add(userProfileInformation)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    inner class ListCustomAdapter(private val context: Context, private val itemList: MutableList<UserProfileInformation>) : BaseAdapter() {

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
            val view = convertView ?: inflater.inflate(R.layout.list_view_layout, parent, false)

            val name = view.findViewById<TextView>(R.id.name)
            val email = view.findViewById<TextView>(R.id.email)
            val profile = view.findViewById<ImageView>(R.id.profile)
            val outerLayout = view.findViewById<LinearLayout>(R.id.outer_layout)

            email.text = itemList[position].email
            name.text = itemList[position].username

            Glide.with(context).load(itemList[position].imageUrl).into(profile)

            outerLayout.setOnClickListener {
                if(itemList[position].work?.size == 0) {
                    MrContext.MrToast().information(this@ViewProfile,"Invalid User")
                    return@setOnClickListener
                }

                UserRepository.saveProfile(itemList[position])
                val intent = Intent(this@ViewProfile,Profile::class.java)
                intent.putExtra("userProfileId", 123)
                startActivity(intent)
                LocalFunctions.activityAnimation(this@ViewProfile,true)

            }


            return view
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LocalFunctions.activityAnimation(this,false)
    }

}