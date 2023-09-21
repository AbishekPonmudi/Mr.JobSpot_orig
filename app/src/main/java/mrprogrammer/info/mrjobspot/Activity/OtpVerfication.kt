package mrprogrammer.info.mrjobspot.Activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.LocalSharedPreferences
import com.mrprogrammer.Utils.CommonFunctions.UserValue
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions
import mrprogrammer.info.mrjobspot.databinding.ActivityUserDetailBinding
import java.util.concurrent.TimeUnit

class OtpVerfication : AppCompatActivity() {
    lateinit var root:ActivityUserDetailBinding
    lateinit var verificationId:String
    val mAuth = FirebaseAuth.getInstance()
    lateinit var mobileNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = ActivityUserDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(root.root)
        LocalFunctions.hideKeyboard(this@OtpVerfication,root.otp)
        initClick()
    }
    private fun initClick() {
        root.sentOtp.setOnClickListener {
            if(root.sentOtp.isLoading()){
                return@setOnClickListener
            }
            if(root.sentOtp.getText() == "Sent Otp") {
                mobileNumber = root.number.text.toString()
                if(mobileNumber.length != 10) {
                    MrContext.MrToast().error(this,"Invalid Mobile Number")
                    return@setOnClickListener
                }
                root.sentOtp.setLoaderStatus(show = true)
                sendVerificationCode("+91$mobileNumber")
            } else {
                val verificationCode = root.otp.text.toString()
                root.sentOtp.setLoaderStatus(show = true)
                verifyCode(verificationCode)

            }
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val   mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                removeNumberEditText()
                MrContext.MrToast().success(this@OtpVerfication,"OTP Sent Successfully.")

            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {
                root.sentOtp.setLoaderStatus(show = false)
                MrContext.MrToast().error(this@OtpVerfication,e.message.toString())
            }
        }

    private fun removeNumberEditText() {
        val originalX: Float = root.numberLayout.x
        val distance: Float = -root.numberLayout.width.toFloat()
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(root.numberLayout, "translationX", 0f, distance)
        animator.duration = 500
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                root.numberLayout.visibility = View.GONE
                root.numberLayout.x = originalX
                showOtpEditText()
            }
        })
        animator.start()
    }


    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    root.sentOtp.setLoaderStatus(show = true)
                    saveMobileToDatabase()
                } else {
                    root.sentOtp.setLoaderStatus(show = false)
                    MrContext.MrToast().error(this, task.exception?.message.toString())
                }
            }
    }

    private fun showOtpEditText() {
        val myLinearLayout = root.otpLayout
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        myLinearLayout.apply {
            translationX = screenWidth
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(500)
                .withEndAction {
                    root.sentOtp.setLoaderStatus(false)
                    root.sentOtp.setText("Verify OTP")
                    LocalFunctions.hideKeyboard(this@OtpVerfication,root.otp)
                }
                .start()
        }
    }

    private fun saveMobileToDatabase(){
        MrContext.MrToast().success(this@OtpVerfication,"Please wait")
        val db = FirebaseDatabase.getInstance().reference
        val email = CommonFunctions.firebaseClearString(UserValue.getUserEmail(this)).toString()
        val result = db.child("Userdata").child(email).child("Mobile").setValue(mobileNumber)
        result.addOnCompleteListener {
            if(it.isSuccessful) {
                LocalSharedPreferences.savePreferences(this, Const.OTPAUTH,"true")
                LocalSharedPreferences.savePreferences(this,Const.MOBILENUMBER,mobileNumber)
                startActivity(Intent(this,ProfileSetup::class.java))
                LocalFunctions.activityAnimation(this, true)
                finish()
            } else {
                LocalSharedPreferences.savePreferences(this,Const.OTPAUTH,"false")
            }

        }

    }
}