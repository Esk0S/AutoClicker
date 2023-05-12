package com.example.autoclicker.service;

import static android.view.Display.DEFAULT_DISPLAY;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static com.example.autoclicker.service.MyService.service;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.autoclicker.MainActivity;
import com.example.autoclicker.R;
import java.util.Timer;
import java.util.TimerTask;

public class Window extends AppCompatActivity {
    private static final String TAG = "Window";
    private Context context;
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mParamControlPanel;
    private LayoutInflater layoutInflater;
    private View mView;
    private View controlPanel;
    private ImageView conPanStartPause;
    private ImageView conPanSettings;
    private int[] coords = new int[2];
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    public static long period = 100;

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    public Window(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
            mParamControlPanel = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
        }

        final DisplayManager dm = context.getSystemService(DisplayManager.class);
        final Display primaryDisplay = dm.getDisplay(DEFAULT_DISPLAY);


        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = layoutInflater.inflate(R.layout.pop_up, null);
        controlPanel = layoutInflater.inflate(R.layout.control_panel_pop_up, null);
        mParamControlPanel.gravity = Gravity.START;
        mParams.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        conPanStartPause = controlPanel.findViewById(R.id.control_panel_start_pause);
        conPanSettings = controlPanel.findViewById(R.id.control_panel_settings);

        conPanStartPause.setOnTouchListener(new View.OnTouchListener() {
            Timer timer = null;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    Log.i(TAG, "onClick: ");
                    long clickDuration = 10;
                    long startTime = 60;
//                    period = 100;
                    mView.getLocationOnScreen(coords);
                    Log.i(TAG, "Window: " + coords[0] + " " + coords[1]);


                    if (timer != null) {
                        ((ImageView) view).setImageResource(R.drawable.play2);
                        timer.cancel();
                        timer = null;
                        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
                        mWindowManager.updateViewLayout(mView, mParams);
                        mView.getLayoutParams();
                    } else {
                        ((ImageView) view).setImageResource(R.drawable.pause2);
                        timer = new Timer();
                        mView.getLocationOnScreen(coords);
                        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
                        mWindowManager.updateViewLayout(mView, mParams);
                        mView.getLayoutParams();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                service.click(coords[0] + mView.getMeasuredWidth() / 2,
                                        coords[1] + mView.getMeasuredHeight() / 2, clickDuration, startTime);
                            }
                        }, startTime, period);
                    }
                }
                return true;
            }
        });

        conPanSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {

//                    FragmentManager manager =  ((FragmentActivity) cc).getSupportFragmentManager();
//                    SettingsDialogFragment myDialogFragment = new SettingsDialogFragment(Window.this);
//                    myDialogFragment.show(manager, "settings");

                }
                return false;
            }
        });

        mView.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams mParamsUpdated = mParams;
            double x;
            double y;
            double px;
            double py;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = mParamsUpdated.x;
                        y = mParamsUpdated.y;

                        px = event.getRawX();

                        py = event.getRawY();
//                        Log.i(TAG, "getRawX: " + event.getRawX() + ", getRawY: " + event.getRawY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mView.getLocationOnScreen(coords);
                        Log.i(TAG, "x: " +  coords[0] + ", y: " + coords[1]);

                        mParamsUpdated.x = (int) (x + (event.getRawX() - px));
                        mParamsUpdated.y = (int) (y + (event.getRawY() - py));
//                        Log.i(TAG, "getRawX: " + event.getRawX() + ", getRawY: " + event.getRawY());
                        mWindowManager.updateViewLayout(v, mParamsUpdated);
                        break;
                }
                return false;
            }
            });

        controlPanel.setOnTouchListener(new View.OnTouchListener() {
            final WindowManager.LayoutParams mParamsUpdated = mParamControlPanel;
            double y;
            double x;
            double py;
            double px;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = mParamsUpdated.y;
                        x = mParamsUpdated.x;
                        py = event.getRawY();
                        px = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mParamsUpdated.y = (int) (y + (event.getRawY() - py));
                        mParamsUpdated.x = (int) (x + (event.getRawX() - px));
                        mWindowManager.updateViewLayout(v, mParamsUpdated);
                        break;
                }
                return false;
            }
        });

    }

    public void openCircle() {
        try {
            if (mView.getWindowToken() == null) {
                mWindowManager.addView(mView, mParams);
            }

        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }
    }

    public void closeCircle() {
        try {
            if (mView.getWindowToken() != null) {
                mWindowManager.removeView(mView);
                mView.invalidate();
                context.stopService(new Intent(context, ForegroundService.class));
            }
        } catch (Exception e) {
            Log.d("Close conPan ",e.toString());
        }
    }

    public void openControlPanel() {
        try {
            if (controlPanel.getWindowToken() == null) {
                mWindowManager.addView(controlPanel, mParamControlPanel);
            }

        } catch (Exception e) {
            Log.d("open conPan ",e.toString());
        }
    }

    public void closeControlPanel() {
        try {
            if (controlPanel.getWindowToken() != null) {
                mWindowManager.removeView(controlPanel);
                mView.invalidate();
                context.stopService(new Intent(context, ForegroundService.class));
            }
        } catch (Exception e) {
            Log.d("Close conPan ",e.toString());
        }
    }

}
