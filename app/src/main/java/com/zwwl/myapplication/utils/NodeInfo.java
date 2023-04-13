package com.zwwl.myapplication.utils;

import java.util.Timer;


/**
 * @Description: 类作用描述
 * @Author: ltt
 * @CreateDate: 2023/4/7 17:11
 */
public class NodeInfo {
    private static Timer mTimer;
    private static final String TAG = NodeInfo.class.getSimpleName();






    /**
     * 群发助手
     */
    public static void assistantInfo() {

//        AccessibilityNodeInfo assistantInfo = MyAccessibilityService.mService.findByContent("群发助手");
//        if (assistantInfo != null) {
//            MyAccessibilityService.mService.performClick(assistantInfo.getParent(),true);
//            sleep(1000);
//            if (mTimer != null) {
//                mTimer.cancel();
//                mTimer = null;
//            }
//            mTimer = new Timer();
//            mTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    getWechatListView();
//                }
//            }, 1000, 1000);
//
//        }


    }
    public static void getWechatListView() {
//        if (MyAccessibilityService.mService.getRootInActiveWindow() != null) {
//            List<AccessibilityNodeInfo> list = MyAccessibilityService.mService.getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.wework:id/ma1");
//            Log.e(TAG,"assistantInfo"+list.size());
//            if (list != null && list.size() > 0) {
//                for (AccessibilityNodeInfo child : list) {
//                    MyAccessibilityService.mService.performClick(child,true);
////                    for (int i = 0; i <child.getChildCount() ; i++) {
////                        Log.e(TAG,"assistantInfo"+child.getClassName().toString());
////                        AccessibilityNodeInfo info=child.getChild(i);
////                        if (info!=null){
////                            if (info.isClickable()) {
////                                sleep(2000);
////                                Log.e(TAG, "listview child click"+i);
////                                MyAccessibilityService.mService.performClick(info.getParent(),true);
////                                sleep(2000);
////                                MyAccessibilityService.mService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
////                            }
////                        }
////                    }
//
//                }
//
//            }
//        }
    }



}
