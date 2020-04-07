package dubhacks404.dubhacks_mobile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_LONG;


public class MainFragment extends Fragment implements View.OnClickListener, KeyEvent.Callback{

    private final static String TAG = "MAIN_FRAGMENT";
    private final static int RESULT_LOAD_IMG = 1;
    private final static int SPEECH_REQUEST_CODE = 2;
    private final static int LOGO_DETECTION = 0;
    private final static int TEXT_DETECTION = 1;
    protected static String dataToSend;

    private Button scan_btn;
    private Button logo_btn;
    private Button voice_btn;
    private int currentState = 0;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        scan_btn = (Button) view.findViewById(R.id.getText_btn);
        logo_btn = (Button) view.findViewById(R.id.logo_btn);
        voice_btn = (Button) view.findViewById(R.id.voice_btn);


        scan_btn.setOnClickListener(this);
        logo_btn.setOnClickListener(this);
        voice_btn.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getText_btn:
                Log.e(TAG, "text detection");
                pickImage();
                currentState = TEXT_DETECTION;
                break;
            case R.id.voice_btn:
                Log.e(TAG, "voice recognition");
                displaySpeechRecognizer();
                break;
            case R.id.logo_btn:
                Log.e(TAG, "logo detection");
                pickImage();
                currentState = LOGO_DETECTION;
                break;
        }
    }

    private void pickImage() {
        // Selecting photo from Gallery

//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

        // Taking photo
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePhotoIntent, RESULT_LOAD_IMG);
        } else {
            Toast.makeText(getContext(), "Unable to use Camera", LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RESULT_LOAD_IMG:
                if (resultCode == RESULT_OK) {
                    Log.e(TAG, "Done with taking photo or logo detection");
                    try {
                        // this is for selecting image from Galley
//                        final Uri imageUri = data.getData();
//                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                        processImage(selectedImage);

                        // this is for taking image from Camera
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        processImage(imageBitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Something is wrong");
                    }

                } else {
                    Log.e(TAG, "Something wrong when taking photo");
                }
                break;
            case SPEECH_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String spokenText = results.get(0);
                    sendRequest(Arrays.asList(spokenText));
                    Log.e(TAG, "Got this input: " + spokenText);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processImage(Bitmap img) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);

        if (currentState == TEXT_DETECTION) {
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
        else if (currentState == LOGO_DETECTION) {

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
    }

    private void sendLabelsToServer(List<FirebaseVisionCloudLabel> labels) {
        Log.e(TAG, "Labeled the image");
        List<String> output = new ArrayList<>();
        for (FirebaseVisionCloudLabel label: labels) {
            String text = label.getLabel();
            float confidence = label.getConfidence();
            output.add(text);
            Log.e(TAG, "label: " + text + ", confidence: " + confidence);
        }
        sendRequest(output);
    }

    private void sendRequest(final List<String> res) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final StringBuilder url = new StringBuilder("http://35.199.144.87/app/blackrockTest?");
//        final String baseUrl = "http://35.199.144.87/app/blackrockTest";
        if (res.size() == 0 || res == null) {
            Toast.makeText(getContext(), "Cannot Detect Image", LENGTH_LONG).show();
            return;
        }
        else if (res.size() == 1) url.append("text=").append(res.get(0));
        else {
            for (int i = 0; i < res.size() - 1; i++) {
                url.append("text=").append(res.get(i)).append("&");
            }
            url.append("text=").append(res.get(res.size() - 1));
        }
        Log.e("URL:",url.toString());

        dataToSend = url.toString();
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        startActivity(intent);

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

        List<String> output = new ArrayList<>();
        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            for (FirebaseVisionText.Line line: block.getLines()) {
                String line_of_words = line.getText().trim().toLowerCase();

                if (line_of_words.split("\\s+").length == 1) {
                    Log.e("Words", line_of_words);
                    output.add(line_of_words);
                }
            }
        }
        sendRequest(output);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            Log.e(TAG, "VOICE: activating from long press");
            displaySpeechRecognizer();
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

}
