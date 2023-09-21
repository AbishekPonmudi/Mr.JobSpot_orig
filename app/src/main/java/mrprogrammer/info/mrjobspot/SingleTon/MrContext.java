package mrprogrammer.info.mrjobspot.SingleTon;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mrprogrammer.Utils.Toast.MrToast;

import mrprogrammer.info.mrjobspot.Dialog.LoadingDialog;
import mrprogrammer.info.mrjobspot.R;
import mrprogrammer.info.mrjobspot.Utils.Const;
import mrprogrammer.info.mrjobspot.Utils.Debouncer;
import mrprogrammer.info.mrjobspot.Utils.LocationManger;

public class MrContext {

    public static  Context context = null;
    private static Debouncer debouncer = null;


    private static MrToast mrToast = null;
    private static DatabaseReference database = null;
    private static LocationManger locationManger = null;

    public static MrToast MrToast() {
        if (mrToast == null) {
            mrToast = new MrToast();
        }
        return mrToast;
    }

    public static Debouncer Debouncer() {
        if(debouncer == null) {
            debouncer = new  Debouncer(500);
        }
        return debouncer;
    }

    public static DatabaseReference database() {
        if (database == null) {
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(context, new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyCqsC-tJFm7EkWY-8fvw3GDCMqrWAxa9xE")
                    .setApplicationId("1:837184126840:android:7bf360640b3bc7e41a928d")
                    .setDatabaseUrl("https://mrjob-5c1d8-default-rtdb.firebaseio.com")
                    .setGcmSenderId("837184126840")
                    .setStorageBucket("mrjob-5c1d8.appspot.com").build(),context.getString(R.string.app_name));


            database = FirebaseDatabase.getInstance(firebaseApp).getReference();
        }
        return database;
    }

    public static LocationManger getLocation(){
        if(locationManger == null) {
            locationManger = new LocationManger(context);
        }
        return locationManger;
    }
}
