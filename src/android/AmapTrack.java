package com.maycur.plugin;

import android.Manifest;
import android.content.pm.PackageManager;

import com.maycur.plugin.AmapTrackSingle;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;


import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class AmapTrack extends CordovaPlugin{

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

  CallbackContext callback;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("getCurrentPosition")) {
            String message = args.getString(0);
            this.getCurrentPosition(message, callbackContext);
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

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException
  {
    PluginResult result;
    //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
    if(callback != null) {
      for (int r : grantResults) {
        if (r == PackageManager.PERMISSION_DENIED) {
          result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
          callback.sendPluginResult(result);
          return;
        }

      }
      result = new PluginResult(PluginResult.Status.OK);
      callback.sendPluginResult(result);
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
