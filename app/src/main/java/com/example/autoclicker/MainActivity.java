package com.example.autoclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.autoclicker.service.ForegroundService;
import com.example.autoclicker.service.MyService;
import com.example.autoclicker.service.Window;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    private static final String TAG = "MainActivity";
    private Button start;
    private Button close;
    private TextView mainText;
    private Button access_perm;
    private Window window;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        String id = MyService.class.getName();
        Log.i(TAG, "onCreate: ff " + id);

        Log.d(TAG, "onCreate: ");
        window = new Window(this);
//        controlPanel = new ControlPanel(this);

        checkOverlayPermission();

        access_perm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));

                boolean isAccessibilityEnabled = isAccessibilityEnabled(MainActivity.this);
                Log.i("AAA", String.valueOf(isAccessibilityEnabled));
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
                window.openControlPanel();
                window.openCircle();

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.closeCircle();
                window.closeControlPanel();
            }
        });
        mainText.setOnTouchListener(this);

    }

    private void init() {
        start = findViewById(R.id.start);
        close = findViewById(R.id.close);
        mainText = findViewById(R.id.text);
        access_perm = findViewById(R.id.acces_perm);
    }

    private boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        String serviceId = getString(R.string.accessibility_service_id);
        for (AccessibilityServiceInfo service : runningServices) {

            if(serviceId.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(this, ForegroundService.class));
                } else
                    startService(new Intent(this, ForegroundService.class));
            }
        } else
            startService(new Intent(this, ForegroundService.class));
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }
    @Override
    protected void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        startService();
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        window.closeCircle();
        window.closeControlPanel();
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart: ");
        super.onRestart();
    }

    private float downX;
    private float downY;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent mv) {
        switch (mv.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = mv.getX();
                downY = mv.getY();
//                Log.i(TAG, "Action Down: " + mv.getRawX() + "," + mv.getRawY());
                Log.i(TAG, "Action Move V: " + v.getX() + "," + mv.getY());

            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "Action Move: " + mv.getRawX() + "," + mv.getRawY());
                Log.i(TAG, "Action Move V: " + v.getX() + "," + mv.getY());
                float dx, dy;
                dx = mv.getX() - downX;
                dy = mv.getY() - downY;

                v.setX(v.getX() + dx);
                v.setY(v.getY() + dy);
        }
        return true;
    }

}
