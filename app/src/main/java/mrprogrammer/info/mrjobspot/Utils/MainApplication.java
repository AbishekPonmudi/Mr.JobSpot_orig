package mrprogrammer.info.mrjobspot.Utils;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import mrprogrammer.info.mrjobspot.Firebase.Sync.SyncUserDetails;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        RealmConfiguration DB = new RealmConfiguration.Builder()
                .name(Const.LocalRealmDb)
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(DB);
        FirebaseApp.initializeApp(this);
        new Thread(() -> Glide.get(getApplicationContext()).clearDiskCache()).start();
    }
}
