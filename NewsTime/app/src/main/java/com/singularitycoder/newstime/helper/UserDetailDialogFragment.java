package com.singularitycoder.newstime.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.singularitycoder.newstime.R;

public final class UserDetailDialogFragment extends DialogFragment {

    private static final String TAG = "UserDetailDialogFragment";

    @Nullable
    ImageView ivCall;
    @Nullable
    ImageView ivSms;
    @Nullable
    ImageView ivWhatsApp;
    @Nullable
    ImageView ivLocate;
    @Nullable
    ImageView ivUserImage;
    @Nullable
    TextView tvName;
    @Nullable
    TextView tvPhone;
    @Nullable
    TextView tvNameDesc;
    @Nullable
    TextView tvCategoryDesc;
    @Nullable
    TextView tvAddressDesc;
    @Nullable
    TextView tvDescriptionDesc;
    @Nullable
    TextView tvContactDesc;
    @Nullable
    TextView tvEmpCodeDesc;

    @NonNull
    private final AppUtils myHelpers = AppUtils.getInstance();

    public UserDetailDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getArguments() && ("fullScreen").equals(getArguments().getString("DIALOG_TYPE"))) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.8f), dialog.getWindow().getAttributes().height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // for rounded corners

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_user_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != getArguments()) {

            if (!("").equals(getArguments().getString("USER_PROFILE_IMAGE"))) {
                String image = getArguments().getString("USER_PROFILE_IMAGE");
                myHelpers.glideImage(getContext(), image, ivUserImage);
            }

            if (!("").equals(getArguments().getString("USER_NAME"))) {
                String name = getArguments().getString("USER_NAME");
                tvName.setText(name);
                tvNameDesc.setText(name);
            }

            if (!("").equals(getArguments().getString("USER_CATEGORY"))) {
                String category = getArguments().getString("USER_CATEGORY");
                tvCategoryDesc.setText(category);
            }

            if (!("").equals(getArguments().getString("USER_ADDRESS"))) {
                String address = getArguments().getString("USER_ADDRESS");
                tvAddressDesc.setText(address);
//                ivLocate.setOnClickListener(view1 -> myHelpers.showMap(getActivity(), address));
            }

            if (!("").equals(getArguments().getString("USER_DESCRIPTION"))) {
                String description = getArguments().getString("USER_DESCRIPTION");
                tvDescriptionDesc.setText(description);
            }

            if (!("").equals(getArguments().getString("USER_CONTACT"))) {
                String phone = getArguments().getString("USER_CONTACT");
                tvPhone.setText(phone);
                tvContactDesc.setText(phone);

//                ivCall.setOnClickListener(view1 -> myHelpers.call(getActivity(), phone));
//                ivSms.setOnClickListener(view1 -> myHelpers.sendSms(getActivity(), phone));
//                ivWhatsApp.setOnClickListener(view1 -> myHelpers.sendToWhatsApp(getActivity(), phone));
            }

            if (!("").equals(getArguments().getString("USER_EMPCODE"))) {
                String empCode = getArguments().getString("USER_EMPCODE");
                tvEmpCodeDesc.setText(empCode);
            }
        }
    }
}
