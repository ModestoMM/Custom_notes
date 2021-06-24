package com.modesto.customnotes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.modesto.customnotes.Activities.ImageViewActivity;
import com.modesto.customnotes.Models.ImageR;
import com.modesto.customnotes.Providers.UploadImageProvider;
import com.modesto.customnotes.R;
import com.squareup.picasso.Picasso;


public class ImageAdapters extends FirestoreRecyclerAdapter<ImageR,ImageAdapters.ViewHolder> {

    Context context;
    String title;
    String idorganizer;
    UploadImageProvider muploadImageProvider;

    public ImageAdapters(@NonNull FirestoreRecyclerOptions<ImageR> options, Context context, String titulo,String idorganizer ) {
        super(options);
        this.context= context;
        this.title= titulo;
        this.idorganizer= idorganizer;
        muploadImageProvider = new UploadImageProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ImageAdapters.ViewHolder holder, int position, @NonNull final ImageR imageR) {

        DocumentSnapshot documentSnapshot= getSnapshots().getSnapshot(position);

        final String ImagesId = documentSnapshot.getId();

        if(imageR.getUri() != null){
            if(!imageR.getUri().isEmpty()){
                Picasso.with(context).load(imageR.getUri()).into(holder.ImageViewImageUp);
            }
        }

        holder.ViewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("id_notes", imageR.getId_Note());
                intent.putExtra("id_organizer",idorganizer);
                intent.putExtra("title", title);
                intent.putExtra("id_imageR", ImagesId);
                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public ImageAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewimages,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ImageViewImageUp;
        View ViewHolder;

        public ViewHolder(@NonNull View view) {
            super(view);

            ImageViewImageUp = view.findViewById(R.id.ImageViewImageUp);
            ViewHolder =view;

        }

    }

}
