package com.zwwl.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.zwwl.myapplication.service.ViewService;

/**
 * @Description: 类作用描述
 * @Author: ltt
 * @CreateDate: 2023/4/11 15:15
 */
public class App extends Application {
    public Intent intentService;
    public boolean showFloatingView = true;

    private int mCount=0;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunningForeground();
    }
    public void startViewServer() {

        intentService = new Intent(getApplicationContext(), ViewService.class);
        startService(intentService);
    }

    public void stopViewServer() {
        if (intentService != null) {
            stopService(intentService);
            intentService = null;
        }
    }
    /**
     * 2017.6.19
     * 判断应用在前后台  ym
     * 2017.6.30 新增判断议价详情页前后台标记  ym
     */
    public void isRunningForeground() {
        if (Build.VERSION.SDK_INT >= 14) {
            registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    mCount++;
                    //如果mCount==1，说明是从后台到前台
                    if (mCount == 1) {
                        //执行app跳转到前台的逻辑
                        Log.e("appcontext", "mCount前台" + mCount);
                        showFloatingView = true;

                        stopViewServer();
                    }
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    mCount--;
                    //如果mCount==0，说明是前台到后台
                    if (mCount == 0) {
                        //执行应用切换到后台的逻辑
                        Log.e("appcontext", "mCount后台" + mCount);
                        startViewServer();
                    }
                }

                @Override
                public void onActivitySaveInstanceState(
                        Activity activity, Bundle bundle) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });

        }
    }
}
