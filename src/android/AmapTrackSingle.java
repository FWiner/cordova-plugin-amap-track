package com.maycur.plugin;

import android.Manifest;
import android.app.Activity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;


public class AmapTrackSingle{

  //声明AMapLocationClient类对象
  private AMapLocationClient locationClient = null;

  private AMapLocationClientOption locationOption = null;

  CallbackContext callbackContext;


  public void getCurrentPosition(Activity activity, CallbackContext c) {
    callbackContext = c;
    initLocation(activity);
    locationClient.startLocation();
  }

  private void initLocation(Activity activity){
    //初始化定位
    locationClient = new AMapLocationClient(activity);
    //设置定位参数
    locationOption = getDefaultOption();
    //设置定位参数
    locationClient.setLocationOption(locationOption);
    //设置定位回调监听
    locationClient.setLocationListener(mAMapLocationListener);
  }


  /**
   * 默认的定位参数
   * @since 2.8.0
   * @author hongming.wang
   *
   */
  private AMapLocationClientOption getDefaultOption(){
    AMapLocationClientOption mOption = new AMapLocationClientOption();
    mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
    mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
    mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
    mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
    mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
    mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
    mOption.setLocationCacheEnable(false); //关闭缓存模式
    return mOption;
  }


  AMapLocationListener mAMapLocationListener = new AMapLocationListener(){
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
      if (amapLocation != null) {
        if (amapLocation.getErrorCode() == 0) {
          JSONObject locationInfo = new JSONObject();
          //可在其中解析amapLocation获取相应内容。
          try {
            locationInfo.put("latitude", amapLocation.getLatitude()); //获取纬度
            locationInfo.put("longitude", amapLocation.getLongitude()); //获取经度
            locationInfo.put("address", amapLocation.getAddress());
            locationInfo.put("poiname", amapLocation.getPoiName());
            locationInfo.put("province", amapLocation.getProvince());
            locationInfo.put("city", amapLocation.getCity());
            locationInfo.put("district", amapLocation.getDistrict());

            Log.e("locationInfo",locationInfo.toString());
            callbackContext.success(locationInfo);
            locationClient.stopLocation();
          } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error("解析错误");
          }

        }else {
          //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
          Log.e("AmapError","location Error, ErrCode:"
            + amapLocation.getErrorCode() + ", errInfo:"
            + amapLocation.getErrorInfo());
          callbackContext.error("location Error, ErrCode:"
            + amapLocation.getErrorCode() + ", errInfo:"
            + amapLocation.getErrorInfo());
        }
      }
    }
  };

}
