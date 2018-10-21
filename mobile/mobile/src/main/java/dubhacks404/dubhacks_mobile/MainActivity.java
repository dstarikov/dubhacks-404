package dubhacks404.dubhacks_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = "MAIN_ACTIVITY";
    private final static int RESULT_LOAD_IMG = 1;

    private Button scan_btn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        scan_btn = (Button) findViewById(R.id.getText_btn);
        imageView = (ImageView) findViewById(R.id.imageView);


        scan_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getText_btn:
                Log.e(TAG, "Open the Album");
                pickImage();
        }
    }

    private void pickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            Log.e(TAG, "Done with selecting");
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                processImage(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Something is wrong");
            }

        }else {
            Log.e(TAG, "Did not select any image");
        }
    }

    private void processImage(Bitmap img) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);

        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.e(TAG, "Success on processing image");
                        sendTextToServer(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Fail on processing image");
                    }
        });
    }

    private void sendTextToServer(FirebaseVisionText result) {
        String resultText = result.getText();
        Log.e(TAG, "H:" + resultText);

//        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
//
//        }

    }
}
