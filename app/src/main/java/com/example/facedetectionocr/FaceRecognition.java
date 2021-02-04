package com.example.facedetectionocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FaceRecognition extends AppCompatActivity {
    Boolean justChooseImage=false;
    CameraView cameraView;
    Button faceDetectButton,chooseImage;
    GraphicOverlay graphicOver;
    ImageView imageView;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        imageView=findViewById(R.id.imageView);
        cameraView = findViewById(R.id.camera);
        faceDetectButton = findViewById(R.id.detect);
        graphicOver = findViewById(R.id.graphicOver);
        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (justChooseImage){
                    graphicOver.clear();
                    imageView.setVisibility(View.INVISIBLE);
                    justChooseImage=false;
                }else {
                    cameraView.start();
                    cameraView.captureImage();
                    graphicOver.clear();
                }
            }
        });
        chooseImage=findViewById(R.id.chooseImage);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                justChooseImage=true;
                imageView.setVisibility(View.VISIBLE);
                graphicOver.clear();
                setChooseImage();
            }
        });
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();
                detectFace(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraView.stop();
    }

    private void detectFace(Bitmap bitmap){
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(List<Face> faces) {
                                drawFace(faces);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FaceRecognition.this, "ERROR"+e,Toast.LENGTH_SHORT).show();
                            }
                        });
    }
    private void drawFace(List<Face> faces) {
        Toast.makeText(this,"Number of faces: "+faces.size(), Toast.LENGTH_LONG).show();
        for (Face face: faces){
            graphicOver.add(face.getBoundingBox());
        }
    }
    public void setChooseImage(){
        justChooseImage=true;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                }
                detectFace(bitmap);
            }
        }
    }
}