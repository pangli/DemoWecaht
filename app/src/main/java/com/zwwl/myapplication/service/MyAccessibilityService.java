package com.zwwl.myapplication.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: AccessibilityService
 * @Description: 类作用描述
 * @Author: ltt
 * @CreateDate: 2021/8/16 15:10
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/16 15:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class MyAccessibilityService extends AccessibilityService {
    public static MyAccessibilityService mService;
    private String TAG = MyAccessibilityService.class.getSimpleName();
    private List<AccessibilityNodeInfo> ids = new ArrayList<>();

    public String currentPackage = "";

    public String currentClass = "";

    public static boolean isServerStarted() {
        return mService != null;
    }

    private String currentActivity = "";
    private int i = 0;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (event != null) {
            int eventType = event.getEventType();
            currentActivity = String.valueOf(event.getClassName());
            Log.i(TAG, currentActivity);
            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && "com.tencent.wework.launch.WwMainActivity"
                    .equals(currentActivity)) {
                i = 0;
                openNext("群发助手");
            } else if ("com.tencent.wework.msg.controller.MessageListActivity".equals(currentActivity)
                    && eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                extracted();
            } else if ("com.tencent.wework.customerservice.controller.EnterpriseCustomerEnterpriseMassMessageDetailActivity".equals(currentActivity)
                    && eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null) {
                    Log.d(TAG, "rootWindow为空");
                    return;
                }
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.wework:id/bya");
                if (list != null && !list.isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        performClick(list.get(0), true, new GestureResultCallback() {
                            @Override
                            public void onCompleted(GestureDescription gestureDescription) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                i++;
                            }
                        });
                    } else {
                        performClick(list.get(0));
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        i++;
                    }
                } else {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    i++;
                }
            } else if ("com.tencent.wework.customerservice.controller.AllCombineMassMessageListActivity".equals(currentActivity)
                    && eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                i++;
            }
        }
    }

    private void extracted() {
        final AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.wework:id/ma1");
            if (list != null && !list.isEmpty()) {
                if (i < list.size()) {
                    performClick(list.get(i).getParent(), true, null);
                } else {
                    //添加滚动逻辑
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dispatchGestureScroll(new GestureResultCallback() {
                            @Override
                            public void onCompleted(GestureDescription gestureDescription) {
                                i = 0;
                                extracted();
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    private void openNext(String str) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Toast.makeText(this, "rootWindow为空", Toast.LENGTH_SHORT).show();
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        for (AccessibilityNodeInfo node : list) {
            performClick(node, true, null);
        }
    }


    public void dispatchGestureScroll(GestureResultCallback callback) {
        if (mService == null) return;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Toast.makeText(this, "rootWindow为空", Toast.LENGTH_SHORT).show();
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.wework:id/aig");
        if (list != null && !list.isEmpty()) {
            Rect rect = new Rect();
            list.get(0).getBoundsInScreen(rect);
            float x = rect.left + (rect.right - rect.left) / 2.0f;
            float y = rect.bottom - rect.top;
            Log.e(TAG, "Scroll point:x->" + x + " y->" + y);
            //发送一个点击事件
            Path mPath = new Path();//线性的path代表手势路径,点代表按下,封闭的没用
            mPath.moveTo(x, rect.top);
            mPath.lineTo(x, rect.bottom);//滑动终点
            @SuppressLint({"NewApi", "LocalSuppress"})
            GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(mPath, 0, 500, true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).build(), callback, null);
            }
        }
    }


    public List<AccessibilityNodeInfo> findByClassName(String className) {
        if (mService == null) return null;
        ids.clear();
        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
                return null;
            }
            //开始找目标节点，这里拎出来细讲，直接往下看正文
            if (rootInfo.getChildCount() > 0) {
                findByClassName(rootInfo, className);

            }
        } catch (Exception e) {

        }
        return ids;
    }

    public void findByClassName(AccessibilityNodeInfo rootInfo, String className) {
        //com.ss.android.ugc.aweme:id/a2r

        for (int i = 0; i < rootInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootInfo.getChild(i);

            try {
//                String id=child.getViewIdResourceName();
                if (className.equals(child.getClassName().toString())) {

                    ids.add(child);
                }
//                if(text.equals(id)){
//                    return child;
//                }
            } catch (NullPointerException e) {
            }
            findByClassName(child, className);//递归一直找一层层的全部遍历
        }
    }

    public List<AccessibilityNodeInfo> getAllNodes() {
        ids.clear();
        if (mService == null) return ids;

        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
                return ids;
            }
            if (rootInfo.getClassName() != null)
                ids.add(rootInfo);
            if (rootInfo.getChildCount() > 0) {

                getAllNodes(rootInfo);
            }
        } catch (Exception e) {

        }
        return ids;
    }

    private void getAllNodes(AccessibilityNodeInfo rootInfo) {

        int count = rootInfo.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = rootInfo.getChild(i);
            if (child.getClassName() != null)
                ids.add(child);
            if (child.getChildCount() > 0) {
                getAllNodes(child);
            }
        }
    }

    public AccessibilityNodeInfo findByContent(String text) {
        if (mService == null) return null;
        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
                return null;
            }
            //开始找目标节点
            if (rootInfo.getChildCount() > 0) {
                return findByContent(rootInfo, text);

            }
        } catch (Exception e) {

        }
        return null;
    }

    public AccessibilityNodeInfo findByContent(AccessibilityNodeInfo rootInfo, String text) {
        //com.ss.android.ugc.aweme:id/a2r
        if (rootInfo == null) return null;
        for (int i = 0; i < rootInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootInfo.getChild(i);

            try {
//                String id=child.getViewIdResourceName();
                if (child.findAccessibilityNodeInfosByText(text).size() > 0) {
                    for (AccessibilityNodeInfo view : child.findAccessibilityNodeInfosByText(text)) {

                        return view;
                    }
                    return child;
                }
//                if(text.equals(id)){
//                    return child;
//                }
            } catch (NullPointerException e) {
            }
            findByContent(child, text);//递归一直找一层层的全部遍历
        }
        return null;
    }

    public void performClick(AccessibilityNodeInfo clickable) {
        performClick(clickable, false, null);
    }

    public void performClick(AccessibilityNodeInfo clickable, boolean useGusture, GestureResultCallback callback) {
        if (clickable == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && useGusture) {
            Rect rect = new Rect();
            clickable.getBoundsInScreen(rect);
            float x = rect.left + (rect.right - rect.left) / 2.0f;
            float y = rect.top + (rect.bottom - rect.top) / 2.0f;
            Log.e(TAG, "click point:x->" + x + " y->" + y);
//            if(!ZipFilesUtil.gueture(x,y)){
            Path mPath = new Path();//线性的path代表手势路径,点代表按下,封闭的没用
            mPath.moveTo(x, y);
            mService.dispatchGesture(new GestureDescription.Builder().addStroke(new GestureDescription.StrokeDescription
                    (mPath, 10, 50L)).build(), callback, null);
            return;
        }
        clickable.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        clickable.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }

    public AccessibilityNodeInfo findByID(String id) {
        if (mService == null) return null;
        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
                return null;
            }
            //开始找目标节点，这里拎出来细讲，直接往下看正文
            if (rootInfo.getChildCount() > 0) {

                return findByID(rootInfo, id);

            }
        } catch (Exception e) {

        }
        return null;
    }

    public AccessibilityNodeInfo findByID(AccessibilityNodeInfo rootInfo, String text) {
        //com.ss.android.ugc.aweme:id/a2r
        if (rootInfo == null) return null;
        for (int i = 0; i < rootInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = rootInfo.getChild(i);

            try {
//                String id=child.getViewIdResourceName();
                if (child.findAccessibilityNodeInfosByViewId(text).size() > 0) {
                    for (AccessibilityNodeInfo view : child.findAccessibilityNodeInfosByViewId(text)) {

                        return view;
                    }
                    return child;
                }
//                if(text.equals(id)){
//                    return child;
//                }
            } catch (NullPointerException e) {
            }
            findByID(child, text);//递归一直找一层层的全部遍历
        }
        return null;
    }

    public AccessibilityNodeInfo getWindow() {
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        return rootInfo;
    }

    public void printAllTrees() {
        if (mService == null) return;
        try {
            //拿到根节点
            AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
            if (rootInfo == null || TextUtils.isEmpty(rootInfo.getClassName())) {
                return;
            }
            Rect rect = new Rect();
            rootInfo.getBoundsInScreen(rect);
            String id = rootInfo.getViewIdResourceName();
            if (TextUtils.isEmpty(id)) id = "no id";
            Log.e(TAG, rootInfo.getClassName().toString() + "----" + id/*+"----"+text*/ + "  " + rect);
            //开始找目标节点，这里拎出来细讲，直接往下看正文
            if (rootInfo.getChildCount() > 0) {
                printTree(rootInfo);
            }
//            }
        } catch (Exception e) {

        }
    }

    private void printTree(AccessibilityNodeInfo rootInfo) {
        int count = rootInfo.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo child = rootInfo.getChild(i);
            Rect rect = new Rect();
            child.getBoundsInScreen(rect);
            String id = child.getViewIdResourceName();
            if (TextUtils.isEmpty(id)) id = "no id";
            String text = "no text";
            if (child.getText() != null)
                text = child.getText().toString();
//            }
            Log.e(TAG, child.getClassName().toString() + "----" + id/*+"----"+text*/ + "  " + rect + "  " + text);
            if (child.getChildCount() > 0) {

                printTree(child);
            }
        }
    }

}
