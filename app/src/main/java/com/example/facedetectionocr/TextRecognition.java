package com.example.facedetectionocr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

public class TextRecognition extends AppCompatActivity {
    CameraView cameraView;
    Button textDetectButton;
    Button chooseImage;
    ImageView viewImage;
    TextView textFound;
    Boolean justChooseImage=false;

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
        setContentView(R.layout.activity_text_recognition);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        cameraView = findViewById(R.id.camera);
        textDetectButton=findViewById(R.id.btn_textRecognition);
        chooseImage = findViewById(R.id.btnChooseImage);
        viewImage = findViewById(R.id.viewImage);
        textFound = findViewById(R.id.tvTextFound);
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImage.setVisibility(View.VISIBLE);
                setChooseImage();
            }
        });
        textDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (justChooseImage){
                    justChooseImage=false;
                    viewImage.setVisibility(View.INVISIBLE);
                } else {
                    cameraView.start();
                    cameraView.captureImage();
                    textFound.setText("");
                }
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
                runTextRecognition(bitmap);
            }
            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        });
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
                    viewImage.setImageURI(selectedImageUri);
                }
                runTextRecognition(bitmap);
            }
        }
    }
    private void runTextRecognition(Bitmap mSelectedImage) {
        InputImage image = InputImage.fromBitmap(mSelectedImage,0);
        TextRecognizer detector = com.google.mlkit.vision.text.TextRecognition.getClient();
        detector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextRecognition.this, e+"", Toast.LENGTH_SHORT).show();
                            }
                        });
    }
    @SuppressLint("SetTextI18n")
    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            textFound.setText("No text found!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) sb.append(elements.get(k).getText()).append(" ");
                sb.append("\n");
            }
            sb.append("\n");
        }
        textFound.setText(sb.toString());
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
}