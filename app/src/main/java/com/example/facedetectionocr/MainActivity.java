package com.example.facedetectionocr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.mlkit.vision.face.FaceDetection;

public class MainActivity extends AppCompatActivity {
    Button btn_faceRecognition,btn_textRecognition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_faceRecognition=findViewById(R.id.btn_faceRecognition);
        btn_textRecognition=findViewById(R.id.btn_textRecognition);

        btn_textRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TextRecognition.class));
            }
        });
        btn_faceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FaceRecognition.class));
            }
        });
    }
}