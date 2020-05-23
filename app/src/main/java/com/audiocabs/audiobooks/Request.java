package com.audiocabs.audiobooks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Request extends AppCompatActivity {

    TextView textView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        textView=findViewById(R.id.textView);
        button=findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);


                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"umang.gupta@mnnit.ac.in"});
                intent.putExtra(Intent.EXTRA_SUBJECT,"REQUESTING THE AUDIOBOOK");
                intent.putExtra(Intent.EXTRA_TEXT,textView.getText().toString());

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,"Choose the Email Client"));
            }
        });


    }


}
