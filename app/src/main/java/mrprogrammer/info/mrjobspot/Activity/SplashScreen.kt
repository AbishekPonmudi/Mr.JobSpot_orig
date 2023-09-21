package mrprogrammer.info.mrjobspot.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions
import com.mrprogrammer.Utils.CommonFunctions.LocalSharedPreferences
import com.permissionx.guolindev.PermissionX
import mrprogrammer.info.mrjobspot.Firebase.Sync.SyncAllJob
import mrprogrammer.info.mrjobspot.Firebase.Sync.SyncUserDetails
import mrprogrammer.info.mrjobspot.R
import mrprogrammer.info.mrjobspot.SingleTon.MrContext
import mrprogrammer.info.mrjobspot.Utils.Const
import mrprogrammer.info.mrjobspot.Utils.LocalFunctions

class SplashScreen : AppCompatActivity() {
    private val listOfPermissionX = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val mr = findViewById<TextView>(R.id.mr);
        mr.text = Html.fromHtml(Const.MADE_IN_LOVE)
        networkCheck()
    }

    fun syncJob() {
        SyncAllJob().syncAllJob()
    }
    private fun requestPermission() {
        PermissionX.init(this)
            .permissions(listOfPermissionX)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "Core fundamental are based on these permissions", "OK", "Cancel")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    MrContext.Debouncer().debounce {
                        checkForLocationEnabled()
                    }
                } else {
                    //  Toast.makeText(this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun checkForLocationEnabled() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val dialog = MaterialAlertDialogBuilder(this@SplashScreen)
                .setTitle(getString(R.string.app_name))
                .setCancelable(false)
                .setMessage("Please enable location services to use this feature.")
                .setNegativeButton("Ok") { dialog, which ->
                    dialog.dismiss()
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(settingsIntent)
                }.create()
            dialog.show()
        } else {
           initObjectAndLogin()
        }
    }

    private fun initObjectAndLogin() {
        initObject()
        login()
    }

    private fun networkCheck() {
        if(CommonFunctions.isConnected(this)){
            syncJob()
            val handler = Handler()
            val runnable = Runnable {
                listOfPermissionX.add(Manifest.permission.ACCESS_FINE_LOCATION)
                listOfPermissionX.add(Manifest.permission.CAMERA)
                listOfPermissionX.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOfPermissionX.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                requestPermission()
            }
            handler.postDelayed(runnable, Const.SplashScreenTimeOut)
            return
        }
        val dialog = MaterialAlertDialogBuilder(this@SplashScreen)
            .setTitle(getString(R.string.app_name))
            .setCancelable(false)
            .setMessage("Please Connect to Network.")
            .setNegativeButton("Ok") { dialog, which ->
                networkCheck()
            }.create()
        dialog.show()
    }

    private fun initObject() {
        MrContext.context = applicationContext
        MrContext.getLocation()
    }

    private fun syncData() {
        SyncUserDetails().syncProfile(this)
    }

    private fun login() {
        val user = FirebaseAuth.getInstance().currentUser
        val profile =  LocalFunctions.convertStringToBoolean(LocalSharedPreferences.getPreferences(this, Const.PROFILE))
        val intent = if (user == null) {
            Intent(this@SplashScreen, AppTour::class.java)
        }  else if(user != null && !profile) {
            Intent(this@SplashScreen, ProfileSetup::class.java)
        } else {
            syncData()
            Intent(this@SplashScreen, BaseActivity::class.java)
        }
        startActivity(intent)
        finish()
        LocalFunctions.activityAnimation(this,true)
    }

    override fun onRestart() {
        super.onRestart()
        requestPermission()
    }
}