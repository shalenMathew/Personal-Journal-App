package com.example.personaljournal.adapter;



import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.personaljournal.AddJournalActivity;
import com.example.personaljournal.R;
import com.example.personaljournal.model.Journal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {

    Context context;
    List<Journal> journalList;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    CollectionReference collectionReference= firestore.collection("Journal");

    public JournalAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_view,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalAdapter.ViewHolder holder, int position) {

        Journal journal = journalList.get(position);

        String imageUrl;

        holder.cardViewTitle.setText(journal.getTitle());
        holder.cardViewThoughts.setText(journal.getThoughts());
        imageUrl = journal.getImageUrl();


        Glide.with(context).load(imageUrl).fitCenter().into(holder.imageView);

        String Date = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()* 1000);
         holder.cardViewTime.setText(Date);


         // deleting the view, document and image
         holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {

                 alertDialogBox(position);

                 return true;
             }
         });

         // updating

        Uri currImgUri = Uri.parse(imageUrl);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(context, AddJournalActivity.class);
                i.putExtra("documentId",journal.getDocumentId());
                i.putExtra("currImgUri",currImgUri.toString());

                context.startActivity(i);

            }
        });

    }

    private void alertDialogBox(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Delete Post");
        builder.setMessage("Are u sure u want to delete the post");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
            }
        });

        builder.setNegativeButton("No",null);
        builder.create().show();

    }

    private void deleteItem(int position) {

        Journal journal = journalList.get(position);

        String documentId = journal.getDocumentId();

        String imageUrl = journal.getImageUrl();


        // Check if the image URL is empty or null
        if(imageUrl==null || imageUrl.isEmpty()){
            deleteJournalItem(position,documentId);
        }else{

            // Delete the journal item only if the image exists

            StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

            imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    deleteJournalItem(position,documentId);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    collectionReference.document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            journalList.remove(position);
                            notifyItemRangeChanged(position, journalList.size());
                            notifyItemRemoved(position);
                            Toast.makeText(context, "failed to delete img", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "failed to delete th doc", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            });

        }


    }

    private void deleteJournalItem(int position, String documentId) {

        collectionReference.document(documentId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(context, "Deleted post successfully", Toast.LENGTH_SHORT).show();

                journalList.remove(position);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, journalList.size());
                notifyItemRemoved(position);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed to delete the document", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView cardViewTitle,cardViewThoughts,cardViewTime;
        ImageView imageView;



        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);

            context=ctx;
            cardViewTitle=itemView.findViewById(R.id.cardviewTitle);
            cardViewThoughts=itemView.findViewById(R.id.cardviewThoughts);
            cardViewTime=itemView.findViewById(R.id.cardviewTime);
            imageView = itemView.findViewById(R.id.cardViewImg);

        }
    }
}
