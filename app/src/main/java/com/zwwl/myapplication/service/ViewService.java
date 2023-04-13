package com.zwwl.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zwwl.myapplication.App;
import com.zwwl.myapplication.R;
import com.zwwl.myapplication.utils.DisplayUtil;
import com.zwwl.myapplication.utils.NodeInfo;

public class ViewService extends Service {
    private WindowManager windowManager;
    private View view;
    private TextView tv_start;
    WindowManager.LayoutParams wmParams = null;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private static final String TAG = "FloatService";
    boolean initViewPlace = false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    App myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ViewService","ViewService.onCreate");
         myApplication=(App) getApplicationContext();
//        showOver();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ViewService","ViewService.onDestroy");
//        windowManager.removeView(view);
    }

    private void showOver() {
        App myApplication=(App)getApplicationContext();
        view= LayoutInflater.from(this).inflate(R.layout.view_float,null);
        tv_start=view.findViewById(R.id.tv_start);

        tv_start.setOnClickListener(v -> {

            NodeInfo.assistantInfo();

        });

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            wmParams.type =  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        wmParams.width = -1;
        wmParams.height = -2;
        wmParams.x = 0 ;
        wmParams.y = DisplayUtil.dp2px(130) ;
        wmParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP ;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.format = PixelFormat.RGBA_8888 | PixelFormat.TRANSLUCENT;

//        view.setOnTouchListener(new CustomTouchListener(view,MainActivity.screenWidth,MainActivity.screenHeight));
        windowManager.addView(view, wmParams);
//        view.setVisibility(myApplication.showFloatingView?View.VISIBLE:View.GONE);
        initViewPlace = false;
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!initViewPlace) {
                            initViewPlace = true;
                            //获取初始位置
                            mTouchStartX = event.getRawX();
                            mTouchStartY = event.getRawY();
                            x = event.getRawX();
                            y = event.getRawY();
                            Log.i(TAG, "startX:" + mTouchStartX + "=>startY:" + mTouchStartY);
                        }else {
                            //根据上次手指离开的位置与此次点击的位置进行初始位置微调
                            mTouchStartX += (event.getRawX() -x);
                            mTouchStartY += (event.getRawY() - y);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取相对屏幕的坐标，以屏幕左上角为原点
                        x = event.getRawX();
                        y = event.getRawY();
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

    }


    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        windowManager.updateViewLayout(view, wmParams);

    }


}
