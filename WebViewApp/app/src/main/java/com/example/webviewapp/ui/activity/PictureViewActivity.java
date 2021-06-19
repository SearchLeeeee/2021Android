package com.example.webviewapp.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.webviewapp.R;
import com.example.webviewapp.common.utils.DownloadUtils;
import com.example.webviewapp.databinding.ActivityPictureViewBinding;

import java.util.Arrays;

public class PictureViewActivity extends AppCompatActivity {
    private static final String TAG = "PictureViewActivity";
    ActivityPictureViewBinding viewBinding;

    private String curImageUrl = "";
    private String[] imageUrls = new String[]{};

    private int curPosition = -1;
    private int[] initialedPositions = null;
    private ObjectAnimator objectAnimator;
    private View curPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityPictureViewBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        getSupportActionBar().hide();

        initValue();
        initButton();
        initViewPager();
    }

    private void initValue() {
//        curImageUrl = getIntent().getStringExtra("curImageUrl");
//        imageUrls = getIntent().getStringArrayExtra("imageUrls");
        curImageUrl = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F004%2F531%2F381%2F4339f96900344574a0c8ca272a7b8f27.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1626491577&t=919ff00912ce95d69b1072acaf51d5ed";
        imageUrls = new String[]{"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fyouimg1.c-ctrip.com%2Ftarget%2Ftg%2F004%2F531%2F381%2F4339f96900344574a0c8ca272a7b8f27.jpg&refer=http%3A%2F%2Fyouimg1.c-ctrip.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1626491577&t=919ff00912ce95d69b1072acaf51d5ed",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201409%2F08%2F20140908130732_kVXzh.jpeg&refer=http%3A%2F%2Fcdn.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1626491577&t=5434f6258c1e89a728a37f84bdc8153c"};
        Log.d(TAG, "initValue: curImageUrls" + curImageUrl);
        Log.d(TAG, "initValue: imageUrls" + imageUrls[0]);

        initialedPositions = new int[imageUrls.length];
        Arrays.fill(initialedPositions, -1);
    }

    private void initButton() {
        viewBinding.backButton.setOnClickListener(v -> finish());
        viewBinding.save.setOnClickListener(v -> savePicture2Local());
    }

    @SuppressLint("SetTextI18n")
    private void initViewPager() {
        viewBinding.viewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        viewBinding.viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageUrls.length;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                if (imageUrls[position] != null && !"".equals(imageUrls[position])) {
                    final PhotoView view = new PhotoView(PictureViewActivity.this);
                    view.enable();
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    RequestListener<String, GlideDrawable> listener = new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if (position == curPosition) {
                                hideLoadingAnimation();
                            }
                            showErrorLoading();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            initialedPositions[position] = position;
                            if (position == curPosition) {
                                hideLoadingAnimation();
                            }
                            return false;
                        }
                    };
                    Glide.with(PictureViewActivity.this)
                            .load(imageUrls[position])
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .fitCenter()
                            .crossFade()
                            .listener(listener)
                            .into(view);
                    container.addView(view);
                }
                return null;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                initialedPositions[position] = -1;
                container.removeView((View) object);
            }

            @Override
            public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                curPage = (View) object;
            }
        });

        curPosition = returnClickedPosition() == -1 ? 0 : returnClickedPosition();
        viewBinding.viewPager.setCurrentItem(curPosition);
        viewBinding.viewPager.setTag(curPosition);
        if (initialedPositions[curPosition] != curPosition) {
            showLoadingAnimation();
        }
        viewBinding.order.setText((curPosition + 1) + "/" + imageUrls.length);
        viewBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (initialedPositions[position] != position) {
                    showLoadingAnimation();
                } else {
                    hideLoadingAnimation();
                }
                curPosition = position;
                viewBinding.order.setText((curPosition + 1) + "/" + imageUrls.length);
                viewBinding.order.setTag(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void savePicture2Local() {
        PhotoView photoView = (PhotoView) curPage;
        if (photoView != null) {
            GlideBitmapDrawable glideBitmapDrawable = (GlideBitmapDrawable) photoView.getDrawable();
            if (glideBitmapDrawable == null) {
                return;
            }
            Bitmap bitmap = glideBitmapDrawable.getBitmap();
            if (bitmap == null) {
                return;
            }
            DownloadUtils.savePicture(this, bitmap, new DownloadUtils.SaveResultCallback() {
                @Override
                public void onSavedSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PictureViewActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSavedFailed() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PictureViewActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            Log.d(TAG, "savePicture2Local: photoView null");
        }
    }

    private void hideLoadingAnimation() {
        releaseResource();
        viewBinding.picture.setVisibility(View.GONE);
    }

    private int returnClickedPosition() {
        if (imageUrls == null || curImageUrl == null) {
            return -1;
        }
        for (int i = 0; i < imageUrls.length; i++) {
            if (curImageUrl.equals(imageUrls[i])) {
                return i;
            }
        }
        return -1;
    }

    private void showLoadingAnimation() {
        viewBinding.picture.setVisibility(View.VISIBLE);
        viewBinding.picture.setImageResource(R.drawable.loading);
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(viewBinding.picture, "rotation", 0f, 360f);
            objectAnimator.setDuration(2000);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                objectAnimator.setAutoCancel(true);
            }
        }
        objectAnimator.start();
    }

    private void showErrorLoading() {
        viewBinding.picture.setVisibility(View.VISIBLE);
        releaseResource();
        viewBinding.picture.setImageResource(R.drawable.load_error);
    }

    private void releaseResource() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (viewBinding.picture.getAnimation() != null) {
            viewBinding.picture.getAnimation().cancel();
        }
    }
}