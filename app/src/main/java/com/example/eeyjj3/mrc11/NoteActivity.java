package com.example.eeyjj3.mrc11;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.eeyjj3.mrc11.Adapter.ListItemAdapter;
import com.example.eeyjj3.mrc11.Models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

/**
 * Created by eeyjj3 on 29/04/2018.
 * FireBase setting:
 * use package name and the debug signing debug certificate to Add Firebase
 * signing debug certificate in this APP is: SHA1: xxxxxx
 * Then download google-services.json, add it in to app/src
 * Then edit build.gradle according to the instruction, the Firebase can be added into the APP
 */

public class NoteActivity extends AppCompatActivity {
    //an array of Note, each note have an id, a title and a description.
    List<Note> NoteList = new ArrayList<>();
    //declare FileStore
    FirebaseFirestore db;

    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    public MaterialEditText title,description;//access by ListAdapter, should be set as public.
    public boolean isUpdate = false;//check if the action is update the old note or add a new note.
    public String idUpdate = "";//Id of the object tobe updated.

    ListItemAdapter adapter;
    AlertDialog dialog;// use 'dmax.dialog.SpotsDialog;' to show the animation.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //init FileStore
        db = FirebaseFirestore.getInstance();

        //View
        dialog = new SpotsDialog(this);
        title = (MaterialEditText)findViewById(R.id.title);
        description = (MaterialEditText)findViewById(R.id.description);
        fab = (FloatingActionButton)findViewById(R.id.fab_note);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(!isUpdate)//if the user create a new note
                {
                    setData(title.getText().toString(),description.getText().toString());
                }else{
                    updateData(title.getText().toString(),description.getText().toString());
                    isUpdate = !isUpdate;//clear the flag
                }
            }

        });

        listItem = (RecyclerView)findViewById(R.id.listNote);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);

        loadData();//load data from FireStore
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, NoteActivity.class);
    }//link the main menu

//---------------------------------------------------------------------
//-----------------delete the note-------------------------------------
    @Override//if the user want to delete the note
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("DELETE"))
            deleteItem(item.getOrder());
        return super.onContextItemSelected(item);
    }
    //delete the item
    private void deleteItem(int index) {
        db.collection("NoteList").document(NoteList.get(index).getId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();
            }
        });
    }

//---------------------------------------------------------------------
//-----------------set/update/load data--------------------------------
    //update the data in the old note
    private void updateData(String title, String description) {
        db.collection("NoteList").document(idUpdate)
                .update("title",title,"description",description)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NoteActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                    }
                });
        //keep refreshing data
        db.collection("NoteList").document(idUpdate).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                loadData();
            }
        });
    }
    //set te data in the new note
    private void setData(String title, String description) {
        //set a random ID to the note
        String id = UUID.randomUUID().toString();
        Map<String,Object> note = new HashMap<>();
        note.put("id",id);
        note.put("title",title);
        note.put("description",description);

        db.collection("NoteList").document(id).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //refresh data
                loadData();
            }
        });
    }
    //load the data into Firebase
    private void loadData() {
        dialog.show();//show the dialog animation
        if(NoteList.size()>0)
            NoteList.clear();//remove the old value when the note is occupied.
        db.collection("NoteList")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc:task.getResult())
                        {
                            Note note = new Note(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"));
                            NoteList.add(note);//add note into the list
                        }
                        adapter = new ListItemAdapter(NoteActivity.this,NoteList);
                        listItem.setAdapter(adapter);
                        dialog.dismiss();//close the dialog animation
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //use exception to toast the error
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NoteActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
