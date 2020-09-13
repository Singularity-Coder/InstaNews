package com.singularitycoder.newstime.helper;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentFrescoImageViewerBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

// Change the java inheritance from AppCompatActivity to Activity for the activity translucency to work
public final class FrescoImageViewerFragment extends Fragment implements View.OnTouchListener {

    private static final String TAG = "FrescoImageViewerFragment";

    private Uri uri;

    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;

    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    GestureDetector.SimpleOnGestureListener swipeClose;

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private FragmentFrescoImageViewerBinding binding;

    public FrescoImageViewerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFrescoImageViewerBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        getBundleData();
        createFolderToSaveImages();
        setUpListeners();
        return viewRoot;
    }

    private void getBundleData() {
        if (null == getArguments()) return;
        if (null == getArguments().getString("IMAGE_URL")) return;

        uri = Uri.parse(getArguments().getString("IMAGE_URL"));
        binding.simpleDraweeView.setImageURI(uri);
    }

    private void createFolderToSaveImages() {
        File audioFileDirectory = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/images/");
        audioFileDirectory.mkdirs();
    }

    private void setUpListeners() {
        binding.ibRotate.setOnClickListener(view -> {
            // Rotation
            final ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setRotationOptions(RotationOptions.forceRotation(RotationOptions.ROTATE_90))
                    .build();
            binding.simpleDraweeView.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(imageRequest)
                            .build());
        });

        binding.ivBack.setOnClickListener(view -> getActivity().getSupportFragmentManager().popBackStackImmediate());
        binding.ibShareImage.setOnClickListener(v -> appUtils.shareData(getActivity(), getArguments().getString("IMAGE_URL"), binding.simpleDraweeView, getArguments().getString("IMAGE_TITLE"), ""));

//        binding.conLayFresco.setOnTouchListener(() -> onTouch(this));
    }


    private void resizeImage() {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(50, 50))
                .build();
        binding.simpleDraweeView.setController(
                Fresco.newDraweeControllerBuilder()
                        .setOldController(binding.simpleDraweeView.getController())
                        .setImageRequest(request)
                        .build());
    }


    public boolean onTouch(View view, MotionEvent event) {

        // Get finger position on screen
        final int Y = (int) event.getRawY();

        // Switch on motion event type
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // save default base layout height
                defaultViewHeight = binding.conLayFresco.getHeight();

                // Init finger and view position
                previousFingerPosition = Y;
                baseLayoutPosition = (int) binding.conLayFresco.getY();
                break;

            case MotionEvent.ACTION_UP:
                // If user was doing a scroll up
                if (isScrollingUp) {
                    // Reset binding.conLayFresco position
                    binding.conLayFresco.setY(0);
                    // We are not in scrolling up mode anymore
                    isScrollingUp = false;
                }

                // If user was doing a scroll down
                if (isScrollingDown) {
                    // Reset binding.conLayFresco position
                    binding.conLayFresco.setY(0);
                    // Reset base layout size
                    binding.conLayFresco.getLayoutParams().height = defaultViewHeight;
                    binding.conLayFresco.requestLayout();
                    // We are not in scrolling down mode anymore
                    isScrollingDown = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isClosing) {
                    int currentYPosition = (int) binding.conLayFresco.getY();

                    // If we scroll up
                    if (previousFingerPosition > Y) {
                        // First time android rise an event for "up" move
                        if (!isScrollingUp) {
                            isScrollingUp = true;
                        }

                        // Has user scrolled down before -> view is smaller than it's default size -> resize it instead of change it position
                        if (binding.conLayFresco.getHeight() < defaultViewHeight) {
                            binding.conLayFresco.getLayoutParams().height = binding.conLayFresco.getHeight() - (Y - previousFingerPosition);
                            binding.conLayFresco.requestLayout();
                        } else {
                            // Has user scrolled enough to "auto close" popup ?
                            if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 4) {
                                closeUpAndDismissDialog(currentYPosition);
                                return true;
                            }

                            //
                        }
                        binding.conLayFresco.setY(binding.conLayFresco.getY() + (Y - previousFingerPosition));

                    }
                    // If we scroll down
                    else {

                        // First time android rise an event for "down" movement
                        if (!isScrollingDown) {
                            isScrollingDown = true;
                        }

                        // Has user scroll enough to "auto close" popup ?
                        if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                            closeDownAndDismissDialog(currentYPosition);
                            return true;
                        }

                        // Change base layout size and position (must change position because view anchor is top left corner)
                        binding.conLayFresco.setY(binding.conLayFresco.getY() + (Y - previousFingerPosition));
                        binding.conLayFresco.getLayoutParams().height = binding.conLayFresco.getHeight() - (Y - previousFingerPosition);
                        binding.conLayFresco.requestLayout();
                    }

                    // Update position
                    previousFingerPosition = Y;
                }
                break;
        }
        return true;
    }

    public void closeUpAndDismissDialog(int currentPosition) {
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(binding.conLayFresco, "y", currentPosition, -binding.conLayFresco.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {

            }

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        isClosing = true;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(binding.conLayFresco, "y", currentPosition, screenHeight + binding.conLayFresco.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {

            }

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        positionAnimator.start();
    }
}
