package com.ruanmeng.lensunc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;
import java.util.Locale;

public class AndroidLocationActivity extends Activity {
    private static final String TAG = "MainActivity";
    private Button btnLocation;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        btnLocation = (Button) findViewById(R.id.btn_location);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationServiceOpen();
            }
        });
    }

    //检查定位服务是否开启
    private void checkLocationServiceOpen() {
        if (isLocServiceEnable(this) && isLocServiceEnable(this, 1)) {
            initLocation();
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, LOCATION_SERVICCE);
        }
    }


    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    private final static int LOCATION_SERVICCE = 1;// 定位
    private final int REQUEST_CHECK_SETTINGS = 2;//google权限设置

    public boolean isLocServiceEnable(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public boolean isLocServiceEnable(Context context, int type) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }


    public void initLocation() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.e(TAG, "onAction: " + "开始定位");
                        startLocation();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        Log.e(TAG, "onAction: " + "定位失败");
                        startLocation();
//                        // 判断用户是不是不再显示权限弹窗了，若不再显示的话进入权限设置页
//                        if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
//                            // 打开权限设置页
//                            AndPermission.permissionSetting(mContext).start();
//                            return;
//                        }
                    }
                })
                .start();
    }


    private LocationManager locationManager;
    public void startLocation() {
        try {
            if (locationManager == null) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            }
            Criteria criteria = new Criteria();
            criteria.setAltitudeRequired(true);
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Log.e(TAG, "最佳的定位方式:" + bestProvider);
            //最佳定位方式LocationManager.GPS_PROVIDER LocationManager.NETWORK_PROVIDER
            locationManager.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    private LocationListener myLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            locationManager.removeUpdates(myLocationListener);
            setLocationResult(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void setLocationResult(Location location) {
        if (location == null) {
            tvResult.setText("定位失败");
            return;
        }
        long time = location.getTime();
        float accuracy = location.getAccuracy();//获取精确位置
        double altitude = location.getAltitude();//获取海拔
        double latitude = location.getLatitude();//获取纬度，平行
        double longitude = location.getLongitude();//获取经度，垂直
        StringBuffer sb = new StringBuffer();
        sb.append("time : ");
        sb.append(time);
        sb.append("\nlatitude : ");
        sb.append(latitude);
        sb.append("\nlontitude : ");
        sb.append(longitude);
        Log.e(TAG, "Android定位：\n"+sb.toString() + "\n\n");
        tvResult.setText("Android定位：\n"+sb.toString() + "\n\n");
        geocoderAddress(location);

    }

    //获取地址信息:城市、街道等信息
    public void geocoderAddress(Location location) {
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                List<Address> result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                Log.e(TAG, "获取地址信息：" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == LOCATION_SERVICCE) {//开启定位服务
            if (isLocServiceEnable(this) && isLocServiceEnable(this, 1)) {
                checkLocationServiceOpen();
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    Log.e("locationSettingCallback", "RESULT_OK");
                    startLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e("locationSettingCallback", "RESULT_CANCELED");
                    // The user was asked to change settings, but chose not to
//                    googleLocationStar();
//                    checkLocationPermission();
                    break;
                default:
                    break;
            }
        }
    }
}
