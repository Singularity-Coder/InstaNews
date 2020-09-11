package com.singularitycoder.newstime.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.transition.Slide;
import android.util.Log;
import android.util.TimingLogger;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ActivityIntroBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public final class AppUtils extends AppCompatActivity {

    @NonNull
    private final String TAG = "AppUtils";

    @Nullable
    private static AppUtils _instance;

    @NonNull
    public static synchronized AppUtils getInstance() {
        if (null == _instance) _instance = new AppUtils();
        return _instance;
    }

    public final void checkFunctionExecutionTimings() {
        TimingLogger timingLogger = new TimingLogger(TAG, "hasValidInput");
        timingLogger.addSplit("");
        timingLogger.dumpToLog();
    }

    public final void setupWindowAnimations(Activity activity) {
        Slide slide = new Slide();
        slide.setDuration(1000);
        activity.getWindow().setExitTransition(slide);
    }

    public final void setFadeAnimation(View view) {
        int FADE_DURATION = 550;
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public final boolean hasInternet(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    public final void showFragment(@NonNull final Activity activity, @Nullable final Bundle bundle, final int parentLayout, @NonNull final Fragment fragment) {
        fragment.setArguments(bundle);
        ((AppCompatActivity) activity).getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(parentLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    public final void hideActivityKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public final void hideFragmentKeyboard(Context context, View view) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public final String getFileExtension(Uri uri, Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        final MimeTypeMap mimeType = MimeTypeMap.getSingleton();
        return mimeType.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public final void showSnack(View view, String message, int snackTextColor, String actionBtnText, Callable<Void> voidFunction) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        final View snackbarView = snackbar.getView();
        final TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(snackTextColor);
        snackbar.setAction(actionBtnText, view1 -> {
            try {
                voidFunction.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        snackbar.show();
    }

    public final void showSnackBar(View view, String message, int snackTextColor, String actionBtnText, View.OnClickListener actionClickListener) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        final View snackbarView = snackbar.getView();
        final TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(snackTextColor);
        snackbar.setAction(actionBtnText, view1 -> actionClickListener.onClick(view));
        snackbar.show();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public final void setStatusBarColor(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
            window.requestFeature(window.FEATURE_NO_TITLE);
            window.requestFeature(Window.FEATURE_PROGRESS);
            window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public final void checkPermissions(Activity activity, Callable<Void> permissionsGrantedFunction, Callable<Void> permissionsDeniedFunction, String... permissionsVarArgsArray) {
        Dexter.withActivity(activity)
                .withPermissions(permissionsVarArgsArray)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            try {
                                permissionsGrantedFunction.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                permissionsDeniedFunction.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(activity);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public final void showSettingsDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Give Permissions!");
        builder.setMessage("We need you to grant the permissions for the camera feature to work!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.cancel();
            openSettings(context);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Open device app settings to allow user to enable permissions
    public final void openSettings(Context context) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public final void dialogActionMessage(Activity activity, String title, String message, String positiveActionWord, String negativeActionWord, Callable<Void> positiveAction, Callable<Void> negativeAction, boolean cancelableDialog) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveActionWord, (dialog, which) -> {
                    try {
                        positiveAction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(negativeActionWord, (dialog, which) -> {
                    try {
                        negativeAction.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setCancelable(cancelableDialog)
                .show();
    }

    public final Uri getLocalBitmapUri(Activity activity, ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        final Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage. This way, you don't need to request external read/write permission.
            // activity.getExternalFilesDir is
            File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // Warning: This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public final long getCurrentEpochTime() {
        return System.currentTimeMillis();
    }

    @SuppressLint("SimpleDateFormat")
    public final String currentDateTime() {
        final String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // split date and time for event created date
        final String[] arrOfStr = dateTime.split(" ", 2);
        final ArrayList<String> dateAndTime = new ArrayList<>(Arrays.asList(arrOfStr));

        // convert date to dd/mm/yyyy
        Date dateObj = null;
        try {
            dateObj = new SimpleDateFormat("yyyy-MM-dd").parse(dateAndTime.get(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final String outputDate = new SimpleDateFormat("dd MMM yyyy").format(dateObj);
        Log.d(TAG, "date: " + outputDate);

        // convert time to 12 hr format
        Date timeObj = null;
        try {
            timeObj = new SimpleDateFormat("H:mm:ss").parse(dateAndTime.get(1));
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        final String outputTime = new SimpleDateFormat("hh:mm a").format(timeObj);
        Log.d(TAG, "time: " + outputTime);

        return outputDate + " at " + outputTime;
    }

    public final String formatDate(String inputDate) {
        String outputDate = "";
        try {
            final DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            final DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US);
            final Date date = inputFormat.parse(inputDate);

            if (null != date) outputDate = outputFormat.format(date);
        } catch (Exception ex) {
            Log.e("log", ex.toString());
        }
        return outputDate;
    }

    public final boolean hasValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z]).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public final boolean hasValidEmail(final String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public final void glideImage(Context context, String imgUrl, ImageView imageView) {
        final RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.color.colorAccent)
                .error(android.R.color.holo_red_light)
                .encodeQuality(40)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context).load(imgUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    public final void glideImageWithErrHandle(Context context, String imgUrl, ImageView imageView, String empty1) {
        Glide.with(context)
                .load(imgUrl)
                .apply(
                        new RequestOptions()
                                .error(android.R.color.holo_red_light)
                                .placeholder(R.color.colorAccent)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .centerCrop()
                )
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //on load failed
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onLoadFailed: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //on load success
                        return false;
                    }
                })
                .transition(withCrossFade())
                .into(imageView);
    }

    public Void shareData(Activity activity, Object imageDrawableOrUrl, ImageView imageView, String title, String subtitle) {
        if (null != imageDrawableOrUrl && null != imageView) {
            checkPermissions(activity, () -> shareImageAndText(activity, imageDrawableOrUrl, imageView, title, subtitle), null, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            shareOnlyText(activity, title, subtitle);
        }
        return null;
    }

    private Void shareImageAndText(Activity activity, Object imageDrawableOrUrl, ImageView imageView, String title, String subtitle) {
        Glide.with(activity)
                .asBitmap()
                .load(imageDrawableOrUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        Uri bmpUri = getLocalBitmapUri(activity, imageView);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/.*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, subtitle);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
        return null;
    }


    private void shareOnlyText(Activity activity, String title, String subtitle) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, subtitle);
//                share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivity(Intent.createChooser(share, "Share to"));
    }
}
