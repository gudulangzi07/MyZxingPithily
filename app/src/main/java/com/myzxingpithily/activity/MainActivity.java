package com.myzxingpithily.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.myzxingpithily.R;
import com.myzxingpithily.decode.Intents;

/**
 * @ClassName: MainActivity
 * @author: 张京伟
 * @date: 2016/11/4 17:15
 * @Description:
 * @version: 1.0
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateQRActivity.class);
                intent.setAction(Intents.Scan.ACTION);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_scanning).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                intent.setAction(Intents.Scan.ACTION);
                startActivity(intent);
            }
        });

    }
}
