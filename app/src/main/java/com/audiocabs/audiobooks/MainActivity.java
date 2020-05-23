package com.audiocabs.audiobooks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> arrayListSongName=new ArrayList<>();
    ArrayList<String> arrayListBookName=new ArrayList<>();
    ArrayList<String> arrayListSongUrl=new ArrayList<>();
    ArrayList<String> arrayListBookUrl=new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    JcPlayerView jcplayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    EditText search;
    InterstitialAd mInterstitialAd;
    int move=0;
    BottomNavigationView bottomNavigationView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.index:Toast.makeText(MainActivity.this,"Will be adding soon...",Toast.LENGTH_LONG).show();
                break;

            case R.id.contact:
                Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/you.man.g/"));
                startActivity(browserIntent);
                break;

            case R.id.request:
                Intent intent=new Intent(MainActivity.this,Request.class);
                startActivity(intent);
                break;

            case R.id.notes:
                Intent intent1=new Intent(MainActivity.this, Note.class);
                startActivity(intent1);
                break;




        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4018644777322474/1824900878");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });



        listView=findViewById(R.id.myListView);
        jcplayerView = findViewById(R.id.jcplayer);
        search=findViewById(R.id.search);

        bottomNavigationView=findViewById(R.id.bottom_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ebook:
                        move=1;
                        listView.setAdapter(null);
                        arrayListBookName.clear();
                        arrayListBookUrl.clear();
                        jcplayerView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,"E-Books",Toast.LENGTH_SHORT).show();
                        retriveBooks();

                        break;

                    case R.id.audiobook:
                        move=0;
                        listView.setAdapter(null);
                        arrayListSongName.clear();
                        arrayListSongUrl.clear();
                        jcplayerView.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,"AudioBooks",Toast.LENGTH_SHORT).show();
                        retriveSongs();

                        break;
                }
                return true;
            }
        });
        if(move==0)
            retriveSongs();




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                publishAd();
                if(move==0) {
                    jcplayerView.playAudio(jcAudios.get(position));
                    jcplayerView.setVisibility(View.VISIBLE);
                    jcplayerView.createNotification();
                }

                if(move==1){
                    Toast.makeText(MainActivity.this,"Download Started...",Toast.LENGTH_LONG).show();
                    Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(arrayListBookUrl.get(position)));
                    startActivity(browserIntent);
                }

            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (MainActivity.this).arrayAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void retriveSongs() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Songs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Song song=ds.getValue(Song.class);
                    arrayListSongName.add(song.getSongName());
                    arrayListSongUrl.add(song.getSongUrl());
                    jcAudios.add(JcAudio.createFromURL(song.getSongName(),song.getSongUrl()));
                }
                arrayAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,arrayListSongName);

                jcplayerView.initPlaylist(jcAudios, null);
                listView.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void retriveBooks(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Books");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Books books=ds.getValue(Books.class);
                    arrayListBookName.add(books.getBookName());
                    arrayListBookUrl.add(books.getBookUrl());
                }

                arrayAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,arrayListBookName);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void publishAd(){

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial  wasn't loaded yet.");
        }

    }



}
