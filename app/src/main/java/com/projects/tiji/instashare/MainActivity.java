package com.projects.tiji.instashare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mInstaList;
    private DatabaseReference mDataBase;
    private FirebaseAuth mAuth;

    private LinearLayoutManager mLayoutmanager;


    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        mInstaList = (RecyclerView) findViewById(R.id.insta_list);
        mInstaList.setHasFixedSize(true);
        //To display reverse order oflist items
       mLayoutmanager=new LinearLayoutManager(MainActivity.this);
       mLayoutmanager.setReverseLayout(true);
        mLayoutmanager.setStackFromEnd(true);
        mDataBase = FirebaseDatabase.getInstance().getReference().child("InstaShare");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                   Intent loginIntent=new Intent(MainActivity.this,RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter <Insta,InstaViewHolder> FBRA=new FirebaseRecyclerAdapter<Insta, InstaViewHolder>(
                Insta.class,
                R.layout.insta_row,
                InstaViewHolder.class,mDataBase
        ) {
            @Override
            protected void populateViewHolder(InstaViewHolder viewHolder, Insta model, int position) {
                final String post_key=getRef(position).getKey().toString();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){

                        Intent singleInstaActivity=new Intent(MainActivity.this,SingleInstaActivity.class);
                        singleInstaActivity.putExtra("PostId",post_key);
                        startActivity(singleInstaActivity);

                    }
                });
            }
        };
        mInstaList.setLayoutManager(mLayoutmanager);
        mInstaList.setAdapter(FBRA);
    }
    public static class InstaViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public InstaViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public  void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.textTitle);
            post_title.setText(title);
        }

        public void setDesc(String Desc)
        {
            TextView post_desc=(TextView)mView.findViewById(R.id.textDescription);
            post_desc.setText(Desc);
        }
        public void setImage(Context ctx,String image){
            ImageView post_image=(ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

        }
        public void setUsername(String username){
            TextView Post_username=(TextView) mView.findViewById(R.id.textUsername);
            Post_username.setText(username);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if (id == R.id.addicon)
        {
            Intent intent=new Intent(MainActivity.this,PostActivity.class);
            startActivity(intent);
        }
       else if (id == R.id.Logout)
        {
        mAuth.signOut();
        }
return super.onOptionsItemSelected(item);
    }

}
