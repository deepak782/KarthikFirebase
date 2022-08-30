package com.example.karthikfirebase.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karthikfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements OnItemClickListerner{

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView textView;
    String mail;
    EditText title,desc;
    String titleStr,descStr;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    RecyclerView recyclerView;
    NoteModel noteModel;
    NoteAdapter noteAdapter;
    List<NoteModel> noteModelList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        textView=findViewById(R.id.usermail);
        recyclerView=findViewById(R.id.noteRecycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences=getSharedPreferences("LOGIN",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        mail=sharedPreferences.getString("usermail","");
        textView.setText(""+mail);

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);

        noteAdapter=new NoteAdapter(this,noteModelList,this);
        recyclerView.setAdapter(noteAdapter);

        loadNoteList();

        findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences=getSharedPreferences("LOGIN",MODE_PRIVATE);
                editor=sharedPreferences.edit();
                editor.clear();
                editor.commit();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });

        findViewById(R.id.openDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences=getSharedPreferences("LOGIN",MODE_PRIVATE);
                editor=sharedPreferences.edit();

                mail=sharedPreferences.getString("usermail","");
                createDialog(mail,0,"", "");
            }
        });
    }

    private void loadNoteList() {
        noteModelList.clear();
        progressDialog.show();
        progressDialog.setMessage("Loading Notes");

        firebaseFirestore.collection("NoteApp").whereEqualTo("UserMail",""+mail).orderBy("date2", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                if(!task.isSuccessful())
                {
                    Toast.makeText(HomeActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(QueryDocumentSnapshot qb:task.getResult())
                    {
                        String title=qb.getString("Title");
                        String desc=qb.getString("Desc");
                        String mail=qb.getString("UserMail");
                        //Timestamp timestamp=qb.getTimestamp("date");
                        Long time=qb.getLong("date2");
                        String id=qb.getId();


                        Date timeD = new Date(time * 1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date = sdf.format(timeD);

                        noteModel=new NoteModel(title,desc,id, date);
                        noteModelList.add(noteModel);

                        noteAdapter.notifyDataSetChanged();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("vv",e.getLocalizedMessage());



            }
        });
    }

    private void createDialog(String mail, int val, String title1, String desc1){
        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        View root=getLayoutInflater().inflate(R.layout.note_dialog,null);
        builder.setView(root);
        builder.setCancelable(false);
        title =root.findViewById(R.id.title_dialog);
        desc =root.findViewById(R.id.description_dialog);

        title.setText(""+title1);
        desc.setText(""+desc1);


        if(val==0)
        {
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    titleStr=title.getText().toString();
                    descStr=desc.getText().toString();

                    createDataList(dialogInterface,titleStr,descStr,mail);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
        }
        else if(val==1)
        {
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    titleStr=title.getText().toString();
                    descStr=desc.getText().toString();

                    createUpdateList(dialogInterface,titleStr,descStr,mail);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }



        builder.create();
        builder.show();

    }

    private void createUpdateList(DialogInterface dialogInterface, String titleStr, String descStr, String uid) {
        progressDialog.show();
        progressDialog.setMessage("Updating Note");

        Long tsLong = System.currentTimeMillis()/1000;

        Map<String,Object> map=new HashMap<>();
        map.put("Title",titleStr);
        map.put("Desc",descStr);
        //Firebase TimeStamp
        map.put("date", new Timestamp(new Date()));
        //System timeStamp
        map.put("date2",tsLong);
        firebaseFirestore.collection("NoteApp").document(""+uid).set(map, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                dialogInterface.dismiss();
                loadNoteList();
                Toast.makeText(HomeActivity.this, "Note Updated Success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Failed to Create\n"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void createDataList(DialogInterface dialogInterface, String titleStr, String descStr, String mail) {
        progressDialog.show();
        progressDialog.setMessage("Creating Note");

        //System timeStamp
        Long tsLong = System.currentTimeMillis()/1000;

        Map<String,Object> map=new HashMap<>();
        map.put("Title",titleStr);
        map.put("Desc",descStr);
        map.put("UserMail",mail);
        //Firebase TimeStamp
        map.put("date", new Timestamp(new Date()));
        //System timeStamp
        map.put("date2",tsLong);


        firebaseFirestore.collection("NoteApp").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();
                dialogInterface.dismiss();
                loadNoteList();
                Toast.makeText(HomeActivity.this, "Note Created Success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Failed to Create\n"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    public void onItemClick(int i) {

        createDialog(noteModelList.get(i).getUid(),1,noteModelList.get(i).getTitle(),noteModelList.get(i).getDesc());
    }
}