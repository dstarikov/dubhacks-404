package dubhacks404.dubhacks_mobile;



import android.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        FragmentManager fm = getFragmentManager();

        fm.beginTransaction()
                .add(R.id.main_container, new MainFragment())
                .commit();


    }

}
