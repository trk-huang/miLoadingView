package sunhdj.com.mivideoloadingview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WaveLoadingView v1i = (WaveLoadingView) this.findViewById(R.id.v1i);
        v1i.startLinesAnimation();
    }
}
