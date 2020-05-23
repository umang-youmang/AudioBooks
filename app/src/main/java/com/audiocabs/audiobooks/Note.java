package com.audiocabs.audiobooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Note extends AppCompatActivity {

    EditText mEditText;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);




        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
    }

    public void save(View view)
    {
        mEditText=findViewById(R.id.textView);
        String str=mEditText.getText().toString();
        SharedPreferences.Editor editor1 = pref.edit();
        editor.putString("key_name", str);
        editor.commit();
    }

    public void load(View view)
    {
        mEditText.setText(pref.getString("key_name", ""));
    }

}
