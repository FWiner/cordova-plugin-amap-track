package com.maycur.plugin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import com.maycur.plugin.SimpleOnTrackListener;

import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.amap.api.track.ErrorCode;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;


import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * This class echoes a string called from JavaScript.
 */
public class AmapTrack extends CordovaPlugin{

  private AMapTrackClient aMapTrackClient;

  private long serviceId;
  private long terminalId;
  private long trackId;
  private boolean isServiceRunning;
  private boolean isGatherRunning;

  private boolean uploadToTrack = false;

  /**
   * 需要进行检测的权限数组
   */
  protected String[] needPermissions = {
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_PHONE_STATE
  };

  CallbackContext callbackContext;

  private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";


  private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {
    @Override
    public void onBindServiceCallback(int status, String msg) {
      Log.w(TAG, "onBindServiceCallback, status: " + status + ", msg: " + msg);
    }

    @Override
    public void onStartTrackCallback(int status, String msg) {
      if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
        // 成功启动
        Toast.makeText(cordova.getActivity(), "启动服务成功", Toast.LENGTH_SHORT).show();
        isServiceRunning = true;
        callbackContext.success("init success");
      } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
        // 已经启动
        Toast.makeText(cordova.getActivity(), "服务已经启动", Toast.LENGTH_SHORT).show();
        isServiceRunning = true;
        callbackContext.success("init success");
      } else {
        Log.w(TAG, "error onStartTrackCallback, status: " + status + ", msg: " + msg);
        Toast.makeText(cordova.getActivity(),
          "error onStartTrackCallback, status: " + status + ", msg: " + msg,
          Toast.LENGTH_LONG).show();
        callbackContext.error("init error");
      }
    }

    @Override
    public void onStopTrackCallback(int status, String msg) {
      if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
        // 成功停止
        Toast.makeText(cordova.getActivity(), "停止服务成功", Toast.LENGTH_SHORT).show();
        isServiceRunning = false;
        isGatherRunning = false;
        callbackContext.success("stop track success");
      } else {
        Log.w(TAG, "error onStopTrackCallback, status: " + status + ", msg: " + msg);
        Toast.makeText(cordova.getActivity(),
          "error onStopTrackCallback, status: " + status + ", msg: " + msg,
          Toast.LENGTH_LONG).show();
        callbackContext.error("error onStopTrackCallback, status: " + status + ", msg: " + msg);

      }
    }

    @Override
    public void onStartGatherCallback(int status, String msg) {
      if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) {
        Toast.makeText(cordova.getActivity(), "定位采集开启成功", Toast.LENGTH_SHORT).show();
        isGatherRunning = true;
        callbackContext.success("startTrck success");
      } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {
        Toast.makeText(cordova.getActivity(), "定位采集已经开启", Toast.LENGTH_SHORT).show();
        isGatherRunning = true;
        callbackContext.success("startTrck success");
      } else {
        Log.w(TAG, "error onStartGatherCallback, status: " + status + ", msg: " + msg);
        Toast.makeText(cordova.getActivity(),
          "error onStartGatherCallback, status: " + status + ", msg: " + msg,
          Toast.LENGTH_LONG).show();
        callbackContext.success("startTrck error");
      }
    }

    @Override
    public void onStopGatherCallback(int status, String msg) {
      if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
        Toast.makeText(cordova.getActivity(), "定位采集停止成功", Toast.LENGTH_SHORT).show();
        isGatherRunning = false;
        callbackContext.success("stop track success");
      } else {
        Log.w(TAG, "error onStopGatherCallback, status: " + status + ", msg: " + msg);
        Toast.makeText(cordova.getActivity(),
          "error onStopGatherCallback, status: " + status + ", msg: " + msg,
          Toast.LENGTH_LONG).show();
      }
    }
  };


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext c) throws JSONException {
        callbackContext = c;
        if (action.equals("getCurrentPosition")) {
            //单次定位
            String message = args.getString(0);
            this.getCurrentPosition(message, callbackContext);
            return true;
        }else if (action.equals("init")) {

            aMapTrackClient = new AMapTrackClient(cordova.getActivity().getApplicationContext());
            aMapTrackClient.setInterval(10, 60);
            aMapTrackClient.setCacheSize(50);
            //初始化猎鹰service
            JSONObject params = (JSONObject) args.get(0);
            serviceId = params.getLong("serviceId");
            terminalId = params.getLong("terminalId");
            trackId = params.getLong("traceId");
            this.initTrack(callbackContext);
            return true;
        }else if(action.equals("startTrack")) {
            //开启轨迹上报
            String message = args.getString(0);
            this.startTrack(message, callbackContext);
            return true;
        }
        else if (action.equals("stopTrack")){
            //停止猎鹰打点服务
            String message = args.getString(0);
            this.stopTrack(message, callbackContext);
            return true;
        }else if (action.equals("stopService")){
            //停止猎鹰打点服务
            String message = args.getString(0);
            this.stopService(message, callbackContext);
            return true;
          }
        return false;
    }

    private void getCurrentPosition(String message, CallbackContext callbackContext) {
        AmapTrackSingle a =  new AmapTrackSingle();

        if(hasPermisssion()){
          a.getCurrentPosition(cordova.getActivity(), callbackContext);
        }else{
          PermissionHelper.requestPermissions(this, 0, needPermissions);
        }
    }
    //开始轨迹上传
    public void startTrack(String message, CallbackContext callbackContext){
        aMapTrackClient.setTrackId(trackId);
        aMapTrackClient.startGather(onTrackListener);
    }

    //停止轨迹上传，不停止服务
    public void stopTrack(String message, CallbackContext callbackContext){
      if (isGatherRunning) {
        aMapTrackClient.stopGather(onTrackListener);
      }
    }
    //停止服务以及停止轨迹上传
    public void stopService(String message, CallbackContext callbackContext){
      if (isServiceRunning) {
        aMapTrackClient.stopTrack(new TrackParam(serviceId, terminalId), onTrackListener);
      }
    }

    //开启猎鹰服务
    public void initTrack(CallbackContext callbackContext){

      if(hasPermisssion()){
        TrackParam trackParam = new TrackParam(serviceId, terminalId);
        aMapTrackClient.startTrack(trackParam, onTrackListener);
      }else{
        PermissionHelper.requestPermissions(this, 0, needPermissions);
      }
    }

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException
  {
    PluginResult result;
    //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
    if(callbackContext != null) {
      for (int r : grantResults) {
        if (r == PackageManager.PERMISSION_DENIED) {
          result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
          callbackContext.sendPluginResult(result);
          return;
        }

      }
      result = new PluginResult(PluginResult.Status.OK);
      callbackContext.sendPluginResult(result);
    }
  }

  public boolean hasPermisssion() {
    for(String p : needPermissions)
    {
      if(!PermissionHelper.hasPermission(this, p))
      {
        return false;
      }
    }
    return true;
  }

}
