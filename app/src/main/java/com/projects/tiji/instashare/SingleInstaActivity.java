package com.projects.tiji.instashare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SingleInstaActivity extends AppCompatActivity {

    private String post_key=null;
    private DatabaseReference  mDatabase;
    private ImageView singlePostImage;
    private TextView singlePostDesc;
    private TextView singlePostTitle;
    private Button deleteButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_insta);

        post_key=getIntent().getExtras().getString("PostId");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("InstaShare");

        singlePostDesc=(TextView)findViewById(R.id.SingleDesc);
        singlePostImage=(ImageView)findViewById(R.id.singleImageView);
        singlePostTitle=(TextView)findViewById(R.id.SingleTitle);

        mAuth=FirebaseAuth.getInstance();
        deleteButton=(Button)findViewById(R.id.deleetButton);
        deleteButton.setVisibility(View.INVISIBLE);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title=(String)dataSnapshot.child("title").getValue();
                String post_desc=(String)dataSnapshot.child("desc").getValue();
                String post_image=(String)dataSnapshot.child("image").getValue();
                String post_uid=(String)dataSnapshot.child("uid").getValue();

                singlePostTitle.setText(post_title);
                singlePostDesc.setText(post_desc);
                Picasso.with(SingleInstaActivity.this).load(post_image).into(singlePostImage);

                if (mAuth.getCurrentUser().getUid().equals(post_uid)){
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void deleteButtonClicked(View view)
    {
        mDatabase.child(post_key).removeValue();
        Intent mainIntent=new Intent(SingleInstaActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.shareicon)

        {
            Uri bmpUri = getLocalBitmapUri(singlePostImage);
            if (bmpUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else {
            }

        }
        return super.onOptionsItemSelected(item);

    }

    private Uri getLocalBitmapUri(ImageView imageview) {
        Drawable drawable = imageview.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {

            bmp = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
        } else {
            return null;

        }
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Share_image_" + System.currentTimeMillis() + ".png");
            bmpUri = Uri.fromFile(file);
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


}

