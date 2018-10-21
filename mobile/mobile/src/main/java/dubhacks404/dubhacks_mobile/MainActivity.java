package dubhacks404.dubhacks_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final static String TAG = "MAIN_ACTIVITY";
    private final static int REQUEST_IMAGE_CAPTURE = 1;

    private Button scan_btn;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_btn = (Button) findViewById(R.id.getText_btn);
        imageView = (ImageView) findViewById(R.id.imageView);


        scan_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getText_btn:
                Log.e(TAG, "Open the Album");
                activateCamera();
        }
    }

    private void activateCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle img = data.getExtras();
            Bitmap imageBitmap = (Bitmap) img.get("data");
            imageView.setImageBitmap(imageBitmap);
            processImage(imageBitmap);

        }
    }

    private void processImage(Bitmap img) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(img);
    }
}
