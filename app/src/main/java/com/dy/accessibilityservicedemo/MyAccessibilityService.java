package com.dy.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLICK;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_LONG_CLICK;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD;

/**
 * @author DaiYao
 */
public class MyAccessibilityService extends AccessibilityService {

    private String TAG = "Accessibility";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "服务连接了,开始初始化工作...");

    }

    /**
     * 页面变化回调事件
     *
     * @param event event.getEventType() 当前事件的类型;
     *              event.getClassName() 当前类的名称;
     *              event.getSource() 当前页面中的节点信息；
     *              event.getPackageName() 事件源所在的包名
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
//        switch (eventType) {
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                //界面点击
//                break;
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
//                //界面文字改动
//                break;
//        }
        Log.v(TAG, "eventType" + eventType);

        AccessibilityNodeInfo nodeClientView = getRootInActiveWindow();
        findAllClientView(nodeClientView);
    }

    private int xjCount = 0;

    private void findAllClientView(AccessibilityNodeInfo nodeClientView) {
        AccessibilityNodeInfoCompat wrap = AccessibilityNodeInfoCompat.wrap(nodeClientView);
        if (wrap.getChildCount() == 0) {
            String viewIdResourceName = wrap.getViewIdResourceName();
            CharSequence text = wrap.getText();

            if (!TextUtils.isEmpty(text) || !TextUtils.isEmpty(viewIdResourceName)) {
                StringBuilder sBuffer = new StringBuilder();
                sBuffer.append("控件名称:").append(wrap.getClassName())
                        .append("   ")
                        .append("控件中的值：")
                        .append(text)
                        .append("   ")
                        .append("控件的ID:")
                        .append(viewIdResourceName)
                        .append("   ")
                        .append("点击是否出现弹窗:")
                        .append(wrap.canOpenPopup())
                        .append("   ");

                try {
                    Rect bounds = new Rect();
                    wrap.getBoundsInScreen(bounds);
                    sBuffer.append("控件在父视图位置:")
                            .append(bounds);
                } catch (Exception ignored) {

                }
                Log.e(TAG, sBuffer.toString());


                if ("日历".equals(text)) {
                    xjCount++;
                    sBuffer.append("日历次数:").append(xjCount);
                    if (xjCount > 5) {
                        AccessibilityNodeInfoCompat wrapParent = wrap.getParent();
//                        wrap.performAction(ACTION_CLICK); //模拟点击

                        //粒子滑动
//                        Bundle arguments = new Bundle();
//                        arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
//                                AccessibilityNodeInfo.MOVEMENT_GRANULARITY_PAGE);
//                        arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
//                                false);
//
//                        wrapParent.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
//                                arguments);

//                        performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT);

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            GestureDescription.Builder builder = new GestureDescription.Builder();

                            Path path = new Path();
                            path.moveTo(100, 500);
                            path.lineTo(1000, 500);
                            GestureDescription gesture = builder.addStroke(
                                    new GestureDescription.StrokeDescription(
                                            path,
                                            10L,
                                            200L))
                                    .build();

                            dispatchGesture(gesture, new GestureResultCallback() {
                                @Override
                                public void onCompleted(GestureDescription gestureDescription) {
                                    super.onCompleted(gestureDescription);
                                }

                                @Override
                                public void onCancelled(GestureDescription gestureDescription) {
                                    super.onCancelled(gestureDescription);
                                }
                            }, null);
                        }
                    }
                }
                wrap.recycle();
            }
        } else {
            for (int i = 0; i < nodeClientView.getChildCount(); i++) {
                if (nodeClientView.getChild(i) != null) {
                    findAllClientView(nodeClientView.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "服务关闭了...");

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "服务解绑了,释放资源操作...");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "服务停止了...");
    }
}
