package mrprogrammer.info.mrjobspot.Utils;




import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mrprogrammer.Utils.CommonFunctions.CommonFunctions;
import com.mrprogrammer.Utils.CommonFunctions.UserValue;

import org.jetbrains.annotations.NotNull;

import mrprogrammer.info.mrjobspot.Interface.UploadImageEvent;
import mrprogrammer.info.mrjobspot.SingleTon.MrContext;

public class UploadImages {
    public static void uploadImage(Context context, Uri Image, UploadImageEvent completeHandler) {
        String key = CommonFunctions.Companion.firebaseClearString(UserValue.Companion.getUserEmail(context));
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if (Image != null) {
            String storageKey = CommonFunctions.Companion.createUniqueKey()+"."+getfiletype(Image,context);
            StorageReference ref = storageReference.child(Const.JOB_IMAGE+"/" + key + "/" + storageKey);
            ref.putFile(Image).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            completeHandler.onUploadCompleted(downloadUri.toString(),storageKey);
                        }
                    }
                });

            }).addOnFailureListener(e -> {
                completeHandler.onUploadFailed(e.getMessage());
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                completeHandler.onUploadProgress(progress);
            });
        }

    }
    static String getfiletype(Uri videouri,Context context) {
        ContentResolver r = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    public static void delete(Context context, String keys) {
        String key = CommonFunctions.Companion.firebaseClearString(UserValue.Companion.getUserEmail(context));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference = databaseReference.child(Const.JOB_IMAGE + "/" + key + "/" + keys);
        databaseReference.removeValue();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child("images/" + key + "/" + keys);
        ref.delete().addOnCompleteListener(task -> System.out.println("Image Deleted"));
    }

    public static void removeImageFromDatabase(@NotNull Activity ctx,  String key) {
        String path = CommonFunctions.Companion.firebaseClearString(key);
        DatabaseReference databaseReference = LocalFirebase.Companion.getDatabaseReferences().child("Demo");
        databaseReference.child(path).removeValue((error, ref) -> {
            MrContext.MrToast().success(ctx,"Image Url Deleted . . .", Toast.LENGTH_SHORT);
        });
    }
}