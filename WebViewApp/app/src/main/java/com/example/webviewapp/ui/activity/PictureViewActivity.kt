package com.example.webviewapp.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bm.library.PhotoView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.webviewapp.R
import com.example.webviewapp.common.base.BaseActivity
import com.example.webviewapp.common.utils.DownloadUtils
import com.example.webviewapp.common.utils.DownloadUtils.SaveResultCallback
import com.example.webviewapp.databinding.ActivityPictureViewBinding
import com.example.webviewapp.ui.activity.PictureViewActivity
import java.util.*

class PictureViewActivity : BaseActivity() {
    var viewBinding: ActivityPictureViewBinding? = null
    private var curImageUrl: String? = ""
    private var imageUrls: Array<String?>? = arrayOf()
    private var curPosition = -1
    private var initialedPositions: IntArray? = null
    private var objectAnimator: ObjectAnimator? = null
    private var curPage: View? = null
    private var pagerAdapter: PagerAdapter? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar().hide()
        initValue()
        initButton()
        initViewPager()
    }

    private fun initValue() {
        curImageUrl = getIntent().getStringExtra("curImageUrl")
        imageUrls = getIntent().getStringArrayExtra("imageUrls")
        initialedPositions = IntArray(imageUrls!!.size)
        Arrays.fill(initialedPositions, -1)
    }

    private fun initButton() {
        viewBinding!!.backButton.setOnClickListener { v: View? -> finish() }
        viewBinding!!.save.setOnClickListener { v: View? -> savePicture2Local() }
    }

    @SuppressLint("SetTextI18n")
    private fun initViewPager() {
        viewBinding!!.viewPager.setPageMargin((getResources().getDisplayMetrics().density * 15) as Int)
        pagerAdapter = object : PagerAdapter() {
            val count: Int
                get() = imageUrls!!.size

            fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            fun instantiateItem(container: ViewGroup, position: Int): Any {
                if (imageUrls!![position] != null && "" != imageUrls!![position]) {
                    val view = PhotoView(this@PictureViewActivity)
                    view.enable()
                    view.setScaleType(ImageView.ScaleType.FIT_CENTER)
                    val listener: RequestListener<String, GlideDrawable> = object : RequestListener<String?, GlideDrawable?> {
                        override fun onException(e: Exception, model: String?, target: Target<GlideDrawable?>, isFirstResource: Boolean): Boolean {
                            if (position == curPosition) {
                                hideLoadingAnimation()
                            }
                            showErrorLoading()
                            return false
                        }

                        override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable?>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                            initialedPositions!![position] = position
                            if (position == curPosition) {
                                hideLoadingAnimation()
                            }
                            return false
                        }
                    }
                    Glide.with(this@PictureViewActivity)
                            .load(imageUrls!![position])
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .fitCenter()
                            .crossFade()
                            .listener(listener)
                            .into(view)
                    container.addView(view)
                    return view
                }
                return null
            }

            fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                initialedPositions!![position] = -1
                container.removeView(`object` as View)
            }

            fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
                curPage = `object` as View
            }
        }
        viewBinding!!.viewPager.setAdapter(pagerAdapter)
        curPosition = if (returnClickedPosition() == -1) 0 else returnClickedPosition()
        viewBinding!!.viewPager.setCurrentItem(curPosition)
        viewBinding!!.viewPager.setTag(curPosition)
        if (initialedPositions!![curPosition] != curPosition) {
            showLoadingAnimation()
        }
        viewBinding!!.order.text = (curPosition + 1).toString() + "/" + imageUrls!!.size
        viewBinding!!.viewPager.addOnPageChangeListener(object : OnPageChangeListener() {
            fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            fun onPageSelected(position: Int) {
                if (initialedPositions!![position] != position) {
                    showLoadingAnimation()
                } else {
                    hideLoadingAnimation()
                }
                curPosition = position
                viewBinding!!.order.text = (curPosition + 1).toString() + "/" + imageUrls!!.size
                viewBinding!!.order.tag = position
            }

            fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun savePicture2Local() {
        val photoView: PhotoView? = curPage as PhotoView?
        if (photoView != null) {
            val glideBitmapDrawable = photoView.getDrawable() as GlideBitmapDrawable
                    ?: return
            val bitmap = glideBitmapDrawable.bitmap ?: return
            DownloadUtils.savePicture(this, bitmap, object : SaveResultCallback {
                override fun onSavedSuccess() {
                    runOnUiThread(Runnable { Toast.makeText(this@PictureViewActivity, "保存成功", Toast.LENGTH_SHORT).show() })
                }

                override fun onSavedFailed() {
                    runOnUiThread(Runnable { Toast.makeText(this@PictureViewActivity, "保存失败", Toast.LENGTH_SHORT).show() })
                }
            })
        } else {
            Log.d(TAG, "savePicture2Local: photoView null")
        }
    }

    private fun hideLoadingAnimation() {
        releaseResource()
        viewBinding!!.picture.visibility = View.GONE
    }

    private fun returnClickedPosition(): Int {
        if (imageUrls == null || curImageUrl == null) {
            return -1
        }
        for (i in imageUrls!!.indices) {
            if (curImageUrl == imageUrls!![i]) {
                return i
            }
        }
        return -1
    }

    private fun showLoadingAnimation() {
        viewBinding!!.picture.visibility = View.VISIBLE
        viewBinding!!.picture.setImageResource(R.drawable.loading)
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(viewBinding!!.picture, "rotation", 0f, 360f)
            objectAnimator.setDuration(2000)
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                objectAnimator.setAutoCancel(true)
            }
        }
        objectAnimator!!.start()
    }

    private fun showErrorLoading() {
        viewBinding!!.picture.visibility = View.VISIBLE
        releaseResource()
        viewBinding!!.picture.setImageResource(R.drawable.load_error)
    }

    private fun releaseResource() {
        if (objectAnimator != null) {
            objectAnimator!!.cancel()
        }
        if (viewBinding!!.picture.animation != null) {
            viewBinding!!.picture.animation.cancel()
        }
    }

    companion object {
        private const val TAG = "PictureViewActivity"
    }
}