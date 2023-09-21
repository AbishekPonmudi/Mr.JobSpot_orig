package mrprogrammer.info.mrjobspot.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.LocalSharedPreferences.Companion.saveUserLocally
import com.mrprogrammer.Utils.Interface.LoginCompleteHandler
import com.mrprogrammer.Utils.Login.GoogleLogin
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityAppTourBinding

class AppTour : AppCompatActivity() {
    lateinit var root : ActivityAppTourBinding
    lateinit var googleLogin: GoogleLogin
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityAppTourBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        initObject()
        onclick()
    }

    private fun initObject() {
        firebaseAuth = FirebaseAuth.getInstance()
        googleLogin = GoogleLogin(this, firebaseAuth, getString(R.string.default_web_client_id))
    }
    private fun onclick() {
        root.login.setOnClickListener { view ->
            root.login.setLoaderStatus(true)
            googleLogin.LoginSafely(object : LoginCompleteHandler {
                override fun onSuccess(firebaseUser: FirebaseUser) {
                    saveUserLocallyAndChangeActivity(firebaseUser)
                }

                override fun onFailure(s: String) {
                    root.login.setLoaderStatus(false)
                    MrContext.MrToast().error(this@AppTour, s, Toast.LENGTH_SHORT)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        googleLogin.postActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveUserLocallyAndChangeActivity(user: FirebaseUser) {
        val db = FirebaseDatabase.getInstance().reference
        val email = CommonFunctions.firebaseClearString(user.email).toString()
        db.child("Userdata").child(email).child("work").removeValue()
        saveUserLocally(this, user.displayName, user.email, user.photoUrl.toString())
        startActivity(Intent(this,ProfileSetup::class.java))
        LocalFunctions.activityAnimation(this, true)
        finish()
    }
}