package com.example.karthikfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SetDocument extends AppCompatActivity {

    EditText name,mobile;
    Spinner course;
    String[] courses={"-- Select Course --","Android","IOS","Web","Others"};
    ArrayAdapter<String> arrayAdapter;
    String nameStr,mobileStr,courseStr;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_document);
        name=findViewById(R.id.name);
        mobile=findViewById(R.id.mobile);
        course=findViewById(R.id.course);
        arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,courses);
        course.setAdapter(arrayAdapter);

        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

    }

    public void addDoc(View view) {
        progressDialog.show();
        progressDialog.setMessage("Checking Document");
        nameStr=name.getText().toString();
        mobileStr=mobile.getText().toString();
        courseStr=course.getSelectedItem().toString();
        checkDoc();

    }


    private void checkDoc()
    {

        DocumentReference documentReference=db.collection("CreateDoc").document(""+mobileStr);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();

                if(!task.isSuccessful())
                {
                    Log.d("TAG", "onComplete: "+task.getException());
                }
                else {

                    DocumentSnapshot snapshot=task.getResult();
                    if(snapshot.exists())
                    {
                        Log.d("TAG", "onComplete: "+"Doc Found");
                    }
                    else
                    {
                        Log.d("TAG", "onComplete: "+"Doc Not Found");
                        setDoc();
                    }
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d("TAG", "onFailure: "+e.getLocalizedMessage());


            }
        });


    }

    private void setDoc()
    {
        progressDialog.show();
        progressDialog.setMessage("Creating Doc");

       /* Map<String,Object> map=new HashMap<>();
        map.put("Name", nameStr);
        map.put("Mobile", mobileStr);
        map.put("Course", courseStr);
        map.put("State","TS");*/

        UserModel userModel=new UserModel(nameStr,mobileStr,courseStr,"TS");

        db.collection("CreateDoc").document(""+ this.mobileStr).set(userModel, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Log.d("TAG", "onSuccess: "+"success");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d("TAG", "onFailure: "+e.getLocalizedMessage());

            }
        });
    }
}