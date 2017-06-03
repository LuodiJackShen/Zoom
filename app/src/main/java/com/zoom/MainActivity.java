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
    private static final int ZOOM_WIDTH = 200;
    private static final int ZOOM_HEIGHT = 200;
    private View mRootView;
    private PopupWindow mZoomView;
    private LinearLayout mContainerLy;
    private ImageView mDestIv;

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

        Bitmap src = mRootView.getDrawingCache();//得到drawingCache,实际上就是个bitmap。

        //处理边界问题。
        if (x + ZOOM_WIDTH > src.getWidth()) {
            startX = src.getWidth() - ZOOM_WIDTH;
        }

        if (y + ZOOM_HEIGHT > src.getHeight()) {
            startY = src.getHeight() - ZOOM_HEIGHT;
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

        //获取最终要显示的部分，这部分也是个bitmap。
        Bitmap bitmap = Bitmap.createBitmap(src, startX, startY, ZOOM_WIDTH, ZOOM_HEIGHT);

        mDestIv.setImageBitmap(bitmap);
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
                mRootView.setDrawingCacheEnabled(true);//先开启drawingCache.
                mRootView.buildDrawingCache();//建立drawingCache
                showZoom(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                showZoom(x, y);
                break;
            case MotionEvent.ACTION_UP:
                mZoomView.dismiss();
                mRootView.setDrawingCacheEnabled(false);//关闭drawingCache。
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
