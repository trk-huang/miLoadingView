package sunhdj.com.mivideoloadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MiVideoLoadingView v1i = (MiVideoLoadingView) this.findViewById(R.id.v1i);
        v1i.startTranglesAnimation();
    }
}
