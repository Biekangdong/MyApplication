package com.ruanmeng.lensunc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
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

/**
 * @ClassName GoogleLocationActivity
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/3/18 11:51
 * @Version 1.0
 * @UpdateDate 2023/3/18 11:51
 * @UpdateRemark 更新说明
 */
public class GoogleLocationActivity extends Activity {
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



    private LocationRequest locationRequest;

    private void initGoogleLocation() {
        try {
            locationRequest = LocationRequest.create();
//            locationRequest.setInterval(20000);
//            locationRequest.setFastestInterval(10000);
//            locationRequest.setNumUpdates(1);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.e(TAG, "onSuccess");
                    startLocation();

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
                            resolvable.startResolutionForResult(GoogleLocationActivity.this,
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


    private Location lastKnownLocation;
    private FusedLocationProviderClient locationProviderClient;

    private void startLocation() {
        // Construct a FusedLocationProviderClient.
        if (locationProviderClient == null) {
            locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location> locationResult = locationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if(lastKnownLocation!=null){
                            updateLocation();
                        }else {
                            startNewLocation();
                        }

                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }


    //开始google定位

    private void startNewLocation() {

        if (locationProviderClient == null) {
            locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Log.e(TAG, "startGoogleLocation11: " + "开始Google定位");
        locationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e(TAG, "onSuccess: ");
            }
        });
    }

    //定期接受位置信息
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            locationProviderClient.removeLocationUpdates(locationCallback);
            lastKnownLocation=locationResult.getLastLocation();
            updateLocation();
        }

        @Override
        public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.e(TAG, "数量3："+locationAvailability.isLocationAvailable());
        }
    };

    private void updateLocation(){
        if (lastKnownLocation != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("time : ");
            sb.append(lastKnownLocation.getTime());
            sb.append("\nlatitude : ");
            sb.append(lastKnownLocation.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(lastKnownLocation.getLongitude());
            Log.e(TAG, "google定位：\n"+sb.toString() + "\n\n");
            tvResult.setText("google定位：\n"+sb.toString() + "\n\n");
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
