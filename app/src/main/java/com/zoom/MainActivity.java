package com.zoom;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int ZOOM_WIDTH = 200;
    private static final int ZOOM_HEIGHT = 200;

    private View mRootView;
    private PopupWindow mZoomView;
    private LinearLayout mContainerLy;
    private ImageView mDestIv;

    private Bitmap mCacheBitmap;
    private Bitmap mDisplayBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mRootView = getWindow().getDecorView();
        mContainerLy = (LinearLayout) findViewById(R.id.main_container);
        mZoomView = new PopupWindow(this);
        mDestIv = new ImageView(this);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ZOOM_WIDTH + ZOOM_WIDTH / 2,
                ZOOM_HEIGHT + ZOOM_HEIGHT / 2);
        mDestIv.setLayoutParams(params);
        mZoomView.setContentView(mDestIv);
        mZoomView.setWidth(ZOOM_WIDTH + ZOOM_WIDTH / 2);
        mZoomView.setHeight(ZOOM_HEIGHT + ZOOM_HEIGHT / 2);
    }

    private void showZoom(int x, int y) {
        int startX = x;
        int startY = y;

        //处理边界问题。
        if (x + ZOOM_WIDTH > mCacheBitmap.getWidth()) {
            startX = mCacheBitmap.getWidth() - ZOOM_WIDTH;
        }

        if (y + ZOOM_HEIGHT > mCacheBitmap.getHeight()) {
            startY = mCacheBitmap.getHeight() - ZOOM_HEIGHT;
        }

        if (startX - ZOOM_WIDTH / 5 <= 0) {
            startX = 0;
        } else {
            startX = startX - ZOOM_WIDTH / 5;
        }

        if (startY - ZOOM_HEIGHT / 2 <= 0) {
            startY = 0;
        } else {
            startY = startY - ZOOM_HEIGHT / 2;
        }

        // TODO:这里涉及到在短时间内产生大量的bitmap对象，会引起GC event，有一定几率造成界面的卡顿。
        //获取最终要显示的部分，这部分也是个bitmap。
        mDisplayBitmap = Bitmap.createBitmap(mCacheBitmap, startX, startY, ZOOM_WIDTH, ZOOM_HEIGHT);
        mDestIv.setImageBitmap(mDisplayBitmap);

        int showX = x - ZOOM_WIDTH / 2;
        int showY = (int) (y - ZOOM_HEIGHT * 1.5);
        mZoomView.showAtLocation(mContainerLy,
                Gravity.NO_GRAVITY, showX, showY);
        mZoomView.update(showX, showY, mZoomView.getWidth(), mZoomView.getHeight(), false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent: ");
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //在这里获取到root view 的Cache，而不是每次在showRoom的时候去获取，避免了卡顿问题。
                mRootView.setDrawingCacheEnabled(true);//先开启drawingCache.
                mRootView.buildDrawingCache();//建立drawingCache
                mCacheBitmap = mRootView.getDrawingCache();//得到drawingCache,实际上就是个bitmap。
                showZoom(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                showZoom(x, y);
                break;
            case MotionEvent.ACTION_UP:
                mZoomView.dismiss();
                mRootView.setDrawingCacheEnabled(false);//关闭drawingCache。
                mCacheBitmap.recycle();
                mCacheBitmap = null;
                mDisplayBitmap.recycle();
                mDisplayBitmap = null;
                mRootView.setDrawingCacheEnabled(false);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
