package com.modesto.customnotes.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.modesto.customnotes.R;

public class ZoomActivity extends AppCompatActivity {

    TextView mTextViewContentZoom;
    ImageView mImageViewBackZoom;
    TextView mTextViewTittleNoteZoom;

    String contentZoom;
    String idnotes;
    String idorganizer;
    String title;

    //ZOOM
    final static float STEP = 200;
    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        mImageViewBackZoom = findViewById(R.id.ImageViewBackZoom);
        mTextViewContentZoom = findViewById(R.id.TextViewContentZoom);
        mTextViewTittleNoteZoom = findViewById(R.id.TextViewTittleNotaZoom);

        mTextViewContentZoom.setMovementMethod(new ScrollingMovementMethod());

        idnotes= getIntent().getStringExtra("id_notes");
        title= getIntent().getStringExtra("title");
        idorganizer= getIntent().getStringExtra("id_organizer");
        contentZoom = getIntent().getStringExtra("contentNote");

        mTextViewContentZoom.setText(contentZoom);
        mTextViewTittleNoteZoom.setText(title);

        mImageViewBackZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoomActivity.this,EditNoteActivity.class);
                intent.putExtra("id_notes",idnotes);
                intent.putExtra("title",title);
                intent.putExtra("id_organizer",idorganizer);
                startActivity(intent);
                finish();
            }
        });

        mTextViewContentZoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() == 2) {
                    int action = event.getAction();
                    int pureaction = action & MotionEvent.ACTION_MASK;
                    if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                        mBaseDist = getDistance(event);
                        mBaseRatio = mRatio;
                    } else {
                        float delta = (getDistance(event) - mBaseDist) / STEP;
                        float multi = (float) Math.pow(2, delta);
                        mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi));
                        mTextViewContentZoom.setTextSize(mRatio + 20);
                    }
                }
                return true;
            }
        });

    }

    private int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public void onBackPressed() {
    }
}
