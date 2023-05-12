package com.example.autoclicker.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.example.autoclicker.MainActivity;

public class MyService extends AccessibilityService {
    static MyService service = null;
    private static final String TAG = "MyService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.i(TAG, "onAccessibilityEvent: ");
    }

    public boolean click(int x, int y, long duration, long startTime) {
        Log.i(TAG, String.format("click %d %d", x, y));
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, startTime, duration))
                .build();

        boolean isDispatched = dispatchGesture(gestureDescription, null, null);
        Log.i(TAG, "click: " + isDispatched);
        return isDispatched;
    }



    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt: ");
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        service = this;
        Log.i(TAG, "onServiceConnected: " + this.getServiceInfo());

    }

}