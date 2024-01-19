package com.example.personaljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personaljournal.Service.MusicService;
import com.example.personaljournal.adapter.JournalAdapter;
import com.example.personaljournal.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import util.JournalUser;

public class JournalListActivity extends AppCompatActivity {

final int ACTION_ADD_ID = R.id.actionAdd;


// firebaseAuth
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser  ;

    // Firestore
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //Storage
    private StorageReference storageReference;


    List<Journal> journalList;

    RecyclerView recyclerView;
    JournalAdapter journalAdapter;


//    MediaPlayer music;
    boolean isPlaying = true;
    MenuItem playMenuItem;

private final CollectionReference collectionReference = firestore.collection("Journal");

TextView noPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);


        noPost = findViewById(R.id.noPost);

        // firebase Auth
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        journalList = new ArrayList<>();
        // RecycleView
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        // toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        // making a service class, this is  more plausible as u can play music in background even when ur switching activities
        Intent musicIntent= new Intent(this, MusicService.class);
        startService(musicIntent);

    }


    // adding menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        playMenuItem= menu.findItem(R.id.play);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        int itemId = item.getItemId();


        if(itemId==R.id.actionAdd){

            if(firebaseUser!=null && firebaseAuth!=null) {
                startActivity(new Intent(JournalListActivity.this, AddJournalActivity.class));
            }
        } else if (itemId==R.id.SignOut) {

            if(firebaseUser!=null && firebaseAuth!=null) {
                firebaseAuth.signOut();
                Intent i = new Intent(JournalListActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);  // // makes sure all activity is removed from stack
                startActivity(i);
            }
        }else if (itemId==R.id.play) {

            isPlaying= !isPlaying;  // changes the boolean value when clicked
            updatePlayIcon();


        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePlayIcon() {

        if(isPlaying){
            playMenuItem.setIcon(R.drawable.pause);
            Intent musicIntent= new Intent(this, MusicService.class);
            startService(musicIntent);

        }else {
            playMenuItem.setIcon(R.drawable.play);
            Intent musicIntent= new Intent(this, MusicService.class);
            stopService(musicIntent);

        }

    }


    // Getting all the post

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", JournalUser.getInstance().getUserId())
                .orderBy("timeAdded", Query.Direction.DESCENDING)  // Query.Direction.DESCENDING --> this is setting latest item in recycleView
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshots) {

                        noPost.setVisibility(View.INVISIBLE);

                        if(!querySnapshots.isEmpty()){

                            for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshots){


                                // adding document id
                                String documentId = queryDocumentSnapshot.getId();

                                Journal journal = queryDocumentSnapshot.toObject(Journal.class);

                                // setting document id
                                journal.setDocumentId(documentId);

                                journalList.add(journal);
                            }

                            journalAdapter = new JournalAdapter(JournalListActivity.this,journalList);
                            recyclerView.setAdapter(journalAdapter);
                            journalAdapter.notifyDataSetChanged();
                        }else {

                            noPost.setVisibility(View.VISIBLE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JournalListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent musicIntent= new Intent(this, MusicService.class);
        stopService(musicIntent);
    }
}