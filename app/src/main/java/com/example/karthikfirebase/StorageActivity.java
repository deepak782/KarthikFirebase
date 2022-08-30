package com.example.karthikfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView pathofuri;
    Uri getUri;
    String myuri="content://media/external_primary/images/media/75259";
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    UploadTask uploadTask;
    ProgressDialog progressDialog;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        imageView=findViewById(R.id.selected_Image);
        pathofuri=findViewById(R.id.imagepath);
        //imageView.setImageURI(Uri.parse(myuri));

        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference("Faiz");
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);



        findViewById(R.id.chooseFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Implicit Intent Function
                // image/,video/,audio/,application/pdf/,doc/
/*
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,202);*/

              /*  Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent,202);*/

             /*  Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/");
                startActivityForResult(intent,202);*/


              /*  Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent,202);*/


            /*    Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("doc/");
                startActivityForResult(intent,202);
*/

                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,202);
            }
        });

        findViewById(R.id.uploadFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();

                StorageReference riversRef = storageReference.child(""+getUri.getLastPathSegment());
                uploadTask = riversRef.putFile(getUri);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        progressDialog.dismiss();
                       /* Log.d("TAG", "onSuccess:MetaData "+taskSnapshot.getMetadata());
                        Log.d("TAG", "onSuccess:sessionUri "+taskSnapshot.getUploadSessionUri());*/
                        getDownloadUri(riversRef,uploadTask);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        //The model pertaining to this file.
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");



                    }
                });

            }
        });
    }

    private void getDownloadUri(StorageReference riversRef, UploadTask uploadTask) {

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.d("TAG", "onComplete: "+downloadUri);
                    String url=downloadUri.toString();
                    uploadLoadFirestore(url);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void uploadLoadFirestore(String url) {
        progressDialog.show();
        progressDialog.setMessage("almost done!!");
        Map<String,Object> map=new HashMap<>();
        map.put("url",url);
        map.put("mail","deepak@gmail.com");

        firebaseFirestore.collection("Photos").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();

                Toast.makeText(StorageActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(StorageActivity.this, "Fail"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 202:
                if(resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
                {
                   getUri=data.getData();//Uri
                    pathofuri.setText(""+getUri);
                    imageView.setImageURI(getUri);

                   /* try {
                        Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),getUri);
                        //Set Image using Bitmap
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }                    Log.d("uri",""+getUri);
*/
                }
                else {
                    Toast.makeText(this, "No File Chosen", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}