package dubhacks404.dubhacks_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, KeyEvent.Callback {
    private final static String TAG = "MAIN_ACTIVITY";
    private final static int RESULT_LOAD_IMG = 1;
    private static final int SPEECH_REQUEST_CODE = 2;

    private Button scan_btn;
    private ImageView imageView;
    private WebView dataView;
    private Button request_btn;
    private Button voice_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        scan_btn = (Button) findViewById(R.id.getText_btn);
        imageView = (ImageView) findViewById(R.id.imageView);
        dataView = (WebView) findViewById(R.id.dataView_webView);
        request_btn = (Button) findViewById(R.id.sendRequest_btn);
        voice_btn = (Button) findViewById(R.id.voice_btn);

        scan_btn.setOnClickListener(this);
        request_btn.setOnClickListener(this);
        voice_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getText_btn:
                Log.e(TAG, "Open the Album");
                pickImage();
                break;
            case R.id.sendRequest_btn:
                Log.e(TAG, "test sendRequest");
                sendRequest();
                break;
            case R.id.voice_btn:
                Log.e(TAG, "voice recognition");
                displaySpeechRecognizer();
                break;
        }
    }

    private void pickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RESULT_LOAD_IMG:
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

                } else {
                    Log.e(TAG, "Did not select any image");
                }
                break;
            case SPEECH_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    Log.e(TAG, "Got this input: " + spokenText);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processImage(Bitmap img) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);

//        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
//                .getOnDeviceTextRecognizer();
//
//        textRecognizer.processImage(image)
//                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                    @Override
//                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                        Log.e(TAG, "Success on processing image");
//                        sendTextToServer(firebaseVisionText);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Fail on processing image");
//                    }
//        });

        Log.e(TAG, "setting up detector for image");
        // Image recognition
        FirebaseVisionCloudLabelDetector detector = FirebaseVision.getInstance()
                .getVisionCloudLabelDetector();
        Log.e(TAG, "trying to run detector on image");

        if (detector == null) {
            Log.e(TAG, "detector is null");
        } else {
            Task<List<FirebaseVisionCloudLabel>> result =
                    detector.detectInImage(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                                            Log.e(TAG, "Image cloud recognition succeeded");
                                            sendLabelsToServer(labels);
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Image cloud recognition failed: " + e.getMessage());
                                        }
                                    });
        }
    }

    private void sendLabelsToServer(List<FirebaseVisionCloudLabel> labels) {
       Log.e(TAG, "Labeled the image");
        for (FirebaseVisionCloudLabel label: labels) {
            String text = label.getLabel();
            float confidence = label.getConfidence();
            Log.e(TAG, "label: " + text + ", confidence: " + confidence);
        }
    }

    private void sendRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url ="http://10.19.4.45:8000/app/blackrockTest";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dataView.loadDataWithBaseURL(url, response, null, null, null);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "VOLLEY: error: " + error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void sendTextToServer(FirebaseVisionText result) {
        String resultText = result.getText();
        Log.e(TAG, "H:" + resultText);

//        for (FirebaseVisionText.TextBlock block : result.getTextBlocks()) {
//
//        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            Log.e(TAG, "VOICE: activating from long press");
            displaySpeechRecognizer();
        }
        return true;
    }
}
