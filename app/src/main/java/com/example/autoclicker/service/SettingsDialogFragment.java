package com.example.autoclicker.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.example.autoclicker.R;

import java.util.Locale;

public class SettingsDialogFragment extends DialogFragment {
    public static Lifecycle context;
    private long interval = Window.period;
    private static final String TAG = "SettingsDialogFragment";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getLifecycle();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.settings_title);
        Log.e(TAG, interval + "");
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.settings, null);
        builder.setView(view);
        ((TextView) view.findViewById(R.id.interval_field)).setText(String.format(Locale.getDefault(), "%d", interval));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                interval = Long.parseLong(
                        ((TextView) view.findViewById(R.id.interval_field)).getText().toString());

                Window.period = interval;

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        return builder.create();
    }


}
