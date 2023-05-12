package com.example.autoclicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.autoclicker.service.ForegroundService;
import com.example.autoclicker.service.MyService;
import com.example.autoclicker.service.SettingsDialogFragment;
import com.example.autoclicker.service.Window;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button start;
    private Button close;
    private Button settings;
    private TextView mainText;
    private Button access_perm;
    private Window window;
    private ActivityResultLauncher<Intent> overlayPermissionLauncher;
    private ActivityResultLauncher<Intent> accessibilityPermissionLauncher;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        String id = MyService.class.getName();
        Log.i(TAG, "onCreate: " + id);

        window = new Window(this);

        overlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Settings.canDrawOverlays(this)) {
                        if (!checkAccessibilityPermission()) {
                            accessibilityPermissionAlert();
                        }
                    } else {
                        Log.i(TAG, "Result else");
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.need_a_permission)
                                .setMessage(R.string.this_app_needs_an_overlay_permission)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                }
        );

        accessibilityPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (!checkAccessibilityPermission()) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.need_a_permission)
                                .setMessage(R.string.this_app_needs_an_accessibility_permission)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                }
        );

        if (!Settings.canDrawOverlays(MainActivity.this)) {
            overlayPermissionAlert();
        } else if (!checkAccessibilityPermission()) {
            accessibilityPermissionAlert();
        }

        access_perm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    overlayPermissionAlert();
                } else if (!checkAccessibilityPermission()) {
                    accessibilityPermissionAlert();
                } else {
                    startService();
                    window.openControlPanel();
                    window.openCircle();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.closeCircle();
                window.closeControlPanel();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                SettingsDialogFragment myDialogFragment = new SettingsDialogFragment();
//                myDialogFragment.setTargetFragment();
                myDialogFragment.show(manager, "settings");
//                manager.setFragmentResultListener("interval",
//                        (LifecycleOwner) getLifecycle(), new FragmentResultListener() {
//                    @Override
//                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
//                        if (requestKey.equals("interval")) {
//                            Window.period = result.getInt("interval");
//                        }
//                    }
//                });

            }
        });
//        mainText.setOnTouchListener(this);

    }

    private void init() {
        start = findViewById(R.id.start);
        close = findViewById(R.id.close);
        mainText = findViewById(R.id.text);
        settings = findViewById(R.id.settings);
        access_perm = findViewById(R.id.acces_perm);
    }

    private boolean checkAccessibilityPermission() {
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = accessibilityManager
                        .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        for (AccessibilityServiceInfo service : runningServices) {
            if(getString(R.string.accessibility_service_id).equals(service.getId())) {
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

    private void overlayPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.need_an_overlay_permission)
                .setMessage(R.string.go_to_settings)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        overlayPermissionLauncher.launch(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void accessibilityPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.need_an_accessibility_permission)
                .setMessage(R.string.go_to_settings)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        accessibilityPermissionLauncher.launch(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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

//    private float downX;
//    private float downY;
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouch(View v, MotionEvent mv) {
//        switch (mv.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                downX = mv.getX();
//                downY = mv.getY();
////                Log.i(TAG, "Action Down: " + mv.getRawX() + "," + mv.getRawY());
//                Log.i(TAG, "Action Move V: " + v.getX() + "," + mv.getY());
//
//            case MotionEvent.ACTION_MOVE:
////                Log.i(TAG, "Action Move: " + mv.getRawX() + "," + mv.getRawY());
//                Log.i(TAG, "Action Move V: " + v.getX() + "," + mv.getY());
//                float dx, dy;
//                dx = mv.getX() - downX;
//                dy = mv.getY() - downY;
//
//                v.setX(v.getX() + dx);
//                v.setY(v.getY() + dy);
//        }
//        return true;
//    }

}
