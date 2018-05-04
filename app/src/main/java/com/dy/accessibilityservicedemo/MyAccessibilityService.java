package com.dy.accessibilityservicedemo;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_CLICK;
import static android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD;

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
        Log.v(TAG, eventType + "");
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
                } catch (Exception e) {

                }
                Log.e(TAG, sBuffer.toString());


                if ("相机".equals(text)) {
                    xjCount++;
                    sBuffer.append("相机次数:").append(xjCount);
                    if (xjCount > 5) {
                        AccessibilityNodeInfoCompat wrapParent = wrap.getParent();
                        if (wrapParent.isScrollable()) {
                            wrap.performAction(ACTION_SCROLL_BACKWARD);
                        }
//                            performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT);
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
