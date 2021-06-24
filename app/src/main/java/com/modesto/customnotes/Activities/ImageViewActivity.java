package com.modesto.customnotes.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.modesto.customnotes.Providers.UploadImageProvider;
import com.modesto.customnotes.R;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    String idnotes;
    String idorganizer;
    String title;
    String id_imageR;

    TouchImageView mImageViewSelectImage;
    UploadImageProvider mUploadImageProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        mImageViewSelectImage = findViewById(R.id.ImageViewSelectImage);

        mUploadImageProvider = new UploadImageProvider();

        idnotes= getIntent().getStringExtra("id_notes");
        title= getIntent().getStringExtra("title");
        idorganizer= getIntent().getStringExtra("id_organizer");
        id_imageR= getIntent().getStringExtra("id_imageR");

        ViewImageSelect();

    }

    private void ViewImageSelect() {

        mUploadImageProvider.getImage(id_imageR).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("uri")){
                        String uri = documentSnapshot.getString("uri");
                        if(!uri.isEmpty() && !uri.equals(null)){
                            Picasso.with(ImageViewActivity.this).load(uri).into(mImageViewSelectImage);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ImageViewActivity.this, EditNoteActivity.class);
        intent.putExtra("id_notes",idnotes);
        intent.putExtra("title",title);
        intent.putExtra("id_organizer",idorganizer);
    }
}