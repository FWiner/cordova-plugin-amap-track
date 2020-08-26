package com.maycur.plugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AmapNotification {


  private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";


  public Notification creatBackgroundMode(Context context, Activity activity){

    return createNotification(context, activity);
  }
  /**
   * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
   * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  private Notification createNotification(Context context, Activity activity) {
    Notification.Builder builder;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager nm = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE_RUNNING, "app service", NotificationManager.IMPORTANCE_LOW);
      nm.createNotificationChannel(channel);
      builder = new Notification.Builder(context, CHANNEL_ID_SERVICE_RUNNING);
    } else {
      builder = new Notification.Builder(context);
    }
    Intent nfIntent = new Intent(context, activity.getClass());
    nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0))
      .setContentTitle("实时定位运行中")
      .setContentText("实时定位运行中");
    Notification notification = builder.build();
    return notification;
  }
}
