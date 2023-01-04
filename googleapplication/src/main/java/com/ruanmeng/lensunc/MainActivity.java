package com.ruanmeng.lensunc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnLocation;
    private TextView tvResult;

    private final static int LOCATION_SERVICCE = 1;// 定位
    private final int REQUEST_CHECK_SETTINGS = 2;//google权限设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLocation = (Button) findViewById(R.id.btn_location);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationServiceOpen();
            }
        });
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么所有app将不能使用定位功能
     */
    public  boolean isLocServiceEnable(Context context) {
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
    public  boolean isLocServiceEnable(Context context, int type) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
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

    public void initLocation() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.e(TAG, "onAction: " + "开始定位");
                        initGooglePlayService();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        Log.e(TAG, "onAction: " + "定位失败");
                        initGooglePlayService();
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
    //连接google服务
    private void initGooglePlayService() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.e(TAG, "onConnected");
                        initGoogleLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e(TAG, "onConnectionSuspended" + "---i");
                        //startBaiduLocation();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "onConnectionFailed---" + connectionResult.getErrorCode() + "---" + connectionResult.getErrorMessage());
                        //startBaiduLocation();
                    }
                })
                .build();
        mGoogleApiClient.connect();
    }



    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;

    private void initGoogleLocation() {
        try {
            locationRequest = LocationRequest.create();
//            locationRequest.setInterval(20000);
//            locationRequest.setFastestInterval(10000);
            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.e(TAG, "onSuccess");
                    startGoogleLocation();

                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure");
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
    }

    //开始google定位
    @SuppressLint("MissingPermission")
    private void startGoogleLocation() {
        Log.e(TAG, "startGoogleLocation: " + "开始Google定位");
        if (locationProviderClient == null) {
            locationProviderClient = new FusedLocationProviderClient(this);
        }

        locationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    //定期接受位置信息
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.e(TAG, "数量："+locationResult.getLocations().size());
            if (locationResult != null && locationResult.getLocations().size() > 0) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    StringBuffer sb = new StringBuffer();
                    sb.append("time : ");
                    sb.append(location.getTime());
                    sb.append("\nlatitude : ");
                    sb.append(location.getLatitude());
                    sb.append("\nlontitude : ");
                    sb.append(location.getLongitude());
                    Log.e(TAG, sb.toString() + "\n\n");
                    tvResult.setText(location.getLatitude() + "," + location.getLongitude());
                }

//                Location location = locationResult.getLocations().get(0);
//                lat = String.valueOf(location.getLatitude());
//                lng = String.valueOf(location.getLongitude());

            }

        }
    };


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
                    startGoogleLocation();
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