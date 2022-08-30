package com.example.karthikfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ProgressDialog progressDialog;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=findViewById(R.id.MyList);

        db=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        arrayAdapter=new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);

    }

    public void addData(View view) {
        progressDialog.show();
        progressDialog.setMessage("Adding Data to Cloud FireStore");

        Map<String,Object> map=new HashMap<>();
        map.put("Username","Fiaz");
        map.put("Usermail","FiazMohammad@gamil.com");
        map.put("Usermobile","1234567890");
        map.put("Userdob",1994);

        db.collection("MyUsers").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Log.d("doc",""+documentReference.getId());
                loadList();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });




    }

    private void loadList() {
        progressDialog.show();
        progressDialog.setMessage("loading the data");

        arrayList.clear();
        db.collection("MyUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();

                if(!task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String name=document.getString("Username");
                        String mobile=document.getString("Usermobile");
                        String mail=document.getString("Usermail");
                        //String dob=document.getString("Userdob");
                        Long dob=document.getLong("Userdob");
                        String docID=document.getId();

                        arrayList.add("ID:-"+docID+"\n"+"Name:-"+name+"\n"+"Mobile:-"+mobile+"\n"+"Mail:-"+mail+"Dob:-"+dob);
                        arrayAdapter.notifyDataSetChanged();
                    }

              }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }
}