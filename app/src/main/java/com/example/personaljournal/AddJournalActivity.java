package com.example.personaljournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personaljournal.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import util.JournalUser;


public class AddJournalActivity extends AppCompatActivity {


    boolean isUpdateMode=false;
    private static final int GALLERY_CODE = 1;
    Button savePostBtn;
    EditText titleAdd;
    EditText thoughtsAdd;
    TextView usernameAdd;
    ProgressBar progressBarAdd;
    ImageView imageViewAdd;
    ImageView addImg;

    String currUsername;
    String currUserId;

 FirebaseAuth firebaseAuth;
 FirebaseAuth.AuthStateListener authStateListener;
 FirebaseUser currUser;

 FirebaseFirestore firestore = FirebaseFirestore.getInstance();

private StorageReference storageReference;

private Uri imageUri;

CollectionReference collectionReference = firestore.collection("Journal");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        imageViewAdd= findViewById(R.id.imgAdd);
        addImg = findViewById(R.id.icon);
        titleAdd=findViewById(R.id.titleAdd);
        thoughtsAdd=findViewById(R.id.thoughtsAdd);
        progressBarAdd= findViewById(R.id.progressBarAdd);
        usernameAdd= findViewById(R.id.usernameAdd);
        savePostBtn=findViewById(R.id.savePostBtn);

        progressBarAdd.setVisibility(View.INVISIBLE);

        if(JournalUser.getInstance()!=null){

            currUsername = JournalUser.getInstance().getUsername();
           currUserId= JournalUser.getInstance().getUserId();

            usernameAdd.setText(currUsername);

        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currUser = firebaseAuth.getCurrentUser();

                if(currUser!=null){

                }else {

                }
            }
        };



        if(getIntent().hasExtra("documentId")){

            isUpdateMode=true;
            savePostBtn.setText("Update");

            String documentId = getIntent().getStringExtra("documentId");
            String currImgUri = getIntent().getStringExtra("currImgUri");
            retrieveExistingData(documentId);
        }

        savePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isUpdateMode){

                    String docId = getIntent().getStringExtra("documentId");
                    UpdateJournal(docId);
                }else{
                    SaveJournal();
                }




            }
        });


;
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");


                startActivityForResult(galleryIntent,GALLERY_CODE);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_CODE && resultCode==RESULT_OK){
            if(data!=null){
                imageUri=data.getData();
                imageViewAdd.setImageURI(imageUri);
            }

        }

    }


    private void retrieveExistingData(String documentId) {

        collectionReference.document(documentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Journal journal = documentSnapshot.toObject(Journal.class);
                titleAdd.setText(journal.getTitle());
                thoughtsAdd.setText(journal.getThoughts());

                String imgUrl = journal.getImageUrl();

                Glide.with(AddJournalActivity.this).load(imgUrl).fitCenter().into(imageViewAdd);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddJournalActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void UpdateJournal(String documentId) {

        progressBarAdd.setVisibility(View.VISIBLE);


        String updatedTitle = titleAdd.getText().toString().trim();
        String updatedThoughts =thoughtsAdd.getText().toString().trim();

        if(!TextUtils.isEmpty(updatedThoughts)  && !TextUtils.isEmpty(updatedTitle)){

            DocumentReference documentReference = collectionReference.document(documentId);

            documentReference.update("title",updatedTitle,"thoughts",updatedThoughts)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            if(imageUri!=null){

                                StorageReference filePath = storageReference.child("Image_folder").child("img_" + Timestamp.now().getSeconds());

                                // we need img url which we can get from documentRef

                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                                        //this will fetch the curr img url that is stored in firebase storage console
                                        String imgUrl = documentSnapshot.getString("imageUrl");

                                        if(imgUrl!=null){

                                            // // Get a reference to the existing image in the storage

                                            StorageReference existingImgRef = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);

//                                            In the context of Firebase Storage, the getReferenceFromUrl() method is used to
//                                            obtain a reference to a file in the storage using its URL.
//                                            By providing the URL of the file, getReferenceFromUrl() returns a StorageReference object
//                                            that represents the file in the Firebase Storage. This reference can be used to perform
//                                            various operations on the file, such as downloading, deleting, or updating it.

                                            existingImgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            // Get the updated image URL
                                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {

                                                                    String currImgUrl = uri.toString();

                                                                    documentReference.update("imageUrl",currImgUrl)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                    Toast.makeText(AddJournalActivity.this, "Journal Updated successfully", Toast.LENGTH_SHORT).show();
                                                                                    progressBarAdd.setVisibility(View.INVISIBLE);
                                                                                    Intent intent = new Intent(AddJournalActivity.this, JournalListActivity.class );
                                                                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                    startActivity(intent);
                                                                                    finish();

                                                                                }
                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(AddJournalActivity.this, "failed to update url in document", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(AddJournalActivity.this, "failed to get download url", Toast.LENGTH_SHORT).show();

                                                                }
                                                            });


                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(AddJournalActivity.this, "Failed to add new img", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddJournalActivity.this, "Failed to delete current image", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }else {

                                            Toast.makeText(AddJournalActivity.this, "url is null", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddJournalActivity.this, "Failed to get the document", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{

                              progressBarAdd.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(AddJournalActivity.this, JournalListActivity.class );
                                intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddJournalActivity.this, "failed to update the data", Toast.LENGTH_SHORT).show();
                        }
                    });


        }
        else {
            Toast.makeText(this, "fields cannot be empty", Toast.LENGTH_SHORT).show();
        }



    }



    private void SaveJournal() {


        progressBarAdd.setVisibility(View.VISIBLE);

        String title = titleAdd.getText().toString().trim();
        String thoughts = thoughtsAdd.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri!= null){


            final StorageReference filePath = storageReference
                    .child("Image_folder").child("img_"+ Timestamp.now().getSeconds());


            // uploading image
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUri = uri.toString();

                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thoughts);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setImageUrl(imageUri);
                            journal.setUsername(currUsername);
                            journal.setUserId(currUserId);


                            // invoking collection ref

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    progressBarAdd.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(AddJournalActivity.this, JournalListActivity.class );
                                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddJournalActivity.this, "Failed ->"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddJournalActivity.this, "file Upload failed for some reason", Toast.LENGTH_SHORT).show();
                    progressBarAdd.setVisibility(View.INVISIBLE);
                }
            });

        }else {
            progressBarAdd.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Fields cant be empty try again", Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    protected void onStart() {
        super.onStart();

       currUser=firebaseAuth.getCurrentUser();
       firebaseAuth.addAuthStateListener(authStateListener);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(currUser!=null){

            currUser=firebaseAuth.getCurrentUser();
            firebaseAuth.addAuthStateListener(authStateListener);

        }

    }
}