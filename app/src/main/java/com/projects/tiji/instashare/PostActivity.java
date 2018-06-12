package com.projects.tiji.instashare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Tg on 10-11-2017.
 */

public class PostActivity   extends AppCompatActivity {
    private EditText editName;
    private EditText editDesc;
    private ImageView imageButton;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri uri = null;
    private FirebaseDatabase database;
    private static final int GALLERY_REQUEST = 2;
    private FirebaseAuth mAuth;
    private DatabaseReference  mDatsbaseUsers;
    private FirebaseUser  mCurrentUser;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        editName = (EditText) findViewById(R.id.editName);
        editDesc = (EditText) findViewById(R.id.editDesc);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference=database.getInstance().getReference().child("InstaShare");
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mDatsbaseUsers=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        progressDialog=new ProgressDialog(PostActivity.this);
    }

    public void imageButtonClicked  (View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GALLERY_REQUEST) && (resultCode == RESULT_OK)) {
            uri = data.getData();
            if (null != uri) {
                imageButton = (ImageView) findViewById(R.id.imageButton);
                imageButton.setImageURI(uri);
                imageButton.setAdjustViewBounds(true);
                imageButton.setScaleType(ImageView.ScaleType.FIT_XY);

            }
        }
    }

    public void uploadButtonClick(View view) {
        final String titleValue = editName.getText().toString().trim();
        final String titleDesc = editDesc.getText().toString().trim();

        if (!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(titleDesc)) {
            progressDialog.setTitle("Image is Uploading..... ");
            progressDialog.show();
            StorageReference filepath = storageReference.child("PostImage").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri download = taskSnapshot.getDownloadUrl();
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Upload Complete", Toast.LENGTH_LONG).show();
                    final DatabaseReference newPost = databaseReference.push();

                    mDatsbaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(titleValue);
                            newPost.child("desc").setValue(titleDesc);
                            newPost.child("image").setValue(download.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent mainActivityIntent =new Intent(PostActivity.this,MainActivity.class);
                                        startActivity(mainActivityIntent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });
        }
    }
}
