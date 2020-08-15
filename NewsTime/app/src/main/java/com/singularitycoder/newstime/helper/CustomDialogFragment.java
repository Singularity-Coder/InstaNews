package com.singularitycoder.newstime.helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.singularitycoder.newstime.view.MainFragment;

import java.util.Map;

public final class CustomDialogFragment extends DialogFragment {

    @NonNull
    private final String TAG = "CustomDialogFragment";

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private SimpleAlertDialogListener simpleAlertDialogListener;

    @Nullable
    private ListDialogListener listDialogListener;

    public CustomDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (null != getArguments()) {
            if (("simpleAlert").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (("activity").equals(getArguments().getString("KEY_CONTEXT_TYPE"))) {
                    try {
                        this.simpleAlertDialogListener = (SimpleAlertDialogListener) context;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(getActivity().toString() + " must implement SimpleAlertDialogListener");
                    }
                }
            }

            if (("list").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (("activity").equals(getArguments().getString("KEY_CONTEXT_TYPE"))) {
                    try {
                        this.listDialogListener = (ListDialogListener) context;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(getActivity().toString() + " must implement ListDialogViewListener");
                    }
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        if (null != getArguments()) {
            if (("simpleAlert").equals(getArguments().getString("DIALOG_TYPE"))) {
                simpleAlertDialog(builder);
            }

            if (("list").equals(getArguments().getString("DIALOG_TYPE"))) {
                if (null != getArguments().getStringArray("KEY_LIST") && null != getArguments().getString("KEY_TITLE")) {
                    String[] list = getArguments().getStringArray("KEY_LIST");
                    String title = getArguments().getString("KEY_TITLE");
                    String contextType = getArguments().getString("KEY_CONTEXT_TYPE");
                    String contextObject = getArguments().getString("KEY_CONTEXT_OBJECT");
                    String listDialogType = getArguments().getString("KEY_LIST_DIALOG_TYPE");
                    listDialog(builder, list, title, contextType, contextObject, listDialogType);
                }
            }
        }

        return builder.create();
    }

    @UiThread
    public final void simpleAlertDialog(AlertDialog.Builder builder) {
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete getContext() message?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(true);
        builder
                .setPositiveButton("OK", (dialog1, id) -> {
                    simpleAlertDialogListener.onAlertDialogPositiveClick("DIALOG_TYPE_SIMPLE_ALERT", CustomDialogFragment.this, null);
                })
                .setNegativeButton("CANCEL", (dialog12, id) -> {
                    simpleAlertDialogListener.onAlertDialogNegativeClick("DIALOG_TYPE_SIMPLE_ALERT", CustomDialogFragment.this);
                })
                .setNeutralButton("LATER", (dialogInterface, id) -> {
                    simpleAlertDialogListener.onAlertDialogNeutralClick("DIALOG_TYPE_SIMPLE_ALERT", CustomDialogFragment.this);
                });
    }

    @UiThread
    public void listDialog(AlertDialog.Builder builder, String[] list, String title, String contextType, String contextObject, String listDialogType) {

        if (("fragment").equals(contextType) && ("MainFragment").equals(contextObject)) {
//            this.listDialogListener = (MainFragment) getTargetFragment();
        }

        builder.setTitle(title);
        builder.setCancelable(true);
        String[] selectArray = list;
        builder.setItems(selectArray, (dialog, which) -> {
            for (int i = 0; i < list.length; i++) {
                if (which == i) {
                    if (null != listDialogListener)
                        this.listDialogListener.onListDialogItemClick(selectArray[i], listDialogType);
                }
            }
        });
    }

    public final void setSimpleAlertDialogListener(SimpleAlertDialogListener simpleAlertDialogListener) {
        this.simpleAlertDialogListener = simpleAlertDialogListener;
    }

    public interface SimpleAlertDialogListener {
        void onAlertDialogPositiveClick(String dialogType, DialogFragment dialog, Map<Object, Object> map);

        void onAlertDialogNegativeClick(String dialogType, DialogFragment dialog);

        void onAlertDialogNeutralClick(String dialogType, DialogFragment dialog);
    }

    public interface ListDialogListener {
        void onListDialogItemClick(String listItemText, String listDialogType);
    }
}