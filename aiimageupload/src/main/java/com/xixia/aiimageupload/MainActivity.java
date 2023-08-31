package com.xixia.aiimageupload;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureSelectorUIStyle;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void upload(View view) {
        //initPhotoPickerSingle();
        uploadMeitu();
    }
    public void check(View view) {
        //4db2a50a-44b2-4628-b84a-6f115b8ded25
        //b7e42226-8117-4222-bea0-7f7eabfc0d43
//        9545299e-e678-4faa-8c30-faad9827bb0b
        uploadMeituCheck("e2c80321-1290-4e07-b9bf-8bee00a578d2");
    }

    //选择相册-单选
    public void initPhotoPickerSingle() {
        PictureSelector.create(MainActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .isEnablePreviewAudio(true) // 是否可播放音频
                .isCamera(false)// 是否显示拍照按钮
                .isEnableCrop(true)// 是否裁剪
                //.isCompress(true)// 是否压缩
                //.compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .withAspectRatio(3, 2)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        if (result.size() > 0) {
                            String filePath = result.get(0).getRealPath();
                            Log.e("AAA", "onResult: " + filePath);
                            //upload(filePath);
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }


    private void uploadMeitu() {
        String url = "https://openapi.mtlab.meitu.com/v1/image_expand_async?api_key=120175b4e9a74a6fb971114c5fe4136f&api_secret=5c0e4cab60dd4d1c8d56a3fde2098cdf";
        Request<String> mRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
        ImageBean imageBean = new ImageBean();


        ImageBean.MediaInfoListDTO mediaInfoListDTO=new ImageBean.MediaInfoListDTO();
        mediaInfoListDTO.media_data="https://yangsa123.oss-cn-beijing.aliyuncs.com/1.png";
        ImageBean.MediaInfoListDTO.MediaProfilesDTO mediaProfilesDTO=new ImageBean.MediaInfoListDTO.MediaProfilesDTO();
        mediaProfilesDTO.media_data_type="url";
        mediaInfoListDTO.media_profiles=mediaProfilesDTO;

        List<ImageBean.MediaInfoListDTO> media_info_list=new ArrayList<>();
        media_info_list.add(mediaInfoListDTO);
        imageBean.media_info_list=media_info_list;

        ImageBean.ParameterDTO parameterDTO=new ImageBean.ParameterDTO();
        parameterDTO.rsp_media_type="url";
        ImageBean.ParameterDTO.FreeExpandRatioDTO freeExpandRatioDTO=new ImageBean.ParameterDTO.FreeExpandRatioDTO();
        freeExpandRatioDTO.top=0.2;
        parameterDTO.free_expand_ratio=freeExpandRatioDTO;
        parameterDTO.seed=100;
        imageBean.parameter=parameterDTO;
        
        String jsonString = new Gson().toJson(imageBean);
        Log.e("AAA", "uploadMeitu: "+jsonString );
        mRequest.setDefineRequestBodyForJson(jsonString); // 传入json格式的字符串即可。

        // 添加到请求队列
        RequestQueue queue = NoHttp.newRequestQueue();

        queue.add(0, mRequest, new OnResponseListener<String>() {

            @Override
            public void onSucceed(int what, Response<String> response) {
                if (response.responseCode() == 200) {// 请求成功。

                }
                String result = response.get();
                Log.e("AAAAAA", "onSucceed: " + result);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e("AAAAAA", "onFailed: " + response.getException().getMessage());
            }

            @Override
            public void onStart(int what) {
                // 这里可以show()一个wait dialog。
                Log.e("AAAAAA", "onStart: " + what);
            }

            @Override
            public void onFinish(int what) {
                // 这里可以dismiss()上面show()的wait dialog。
                Log.e("AAAAAA", "onFinish: " + what);
            }
        });
    }

    private void uploadMeituCheck(String msg_id) {
        String url = "https://openapi.mtlab.meitu.com/v1/query?api_key=120175b4e9a74a6fb971114c5fe4136f&api_secret=5c0e4cab60dd4d1c8d56a3fde2098cdf&msg_id="+msg_id;
        Request<String> mRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
        // 添加到请求队列
        RequestQueue queue = NoHttp.newRequestQueue();

        queue.add(0, mRequest, new OnResponseListener<String>() {

            @Override
            public void onSucceed(int what, Response<String> response) {
                if (response.responseCode() == 200) {// 请求成功。

                }
                String result = response.get();
                Log.e("AAAAAA", "onSucceed: " + result);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                Log.e("AAAAAA", "onFailed: " + response.getException().getMessage());
            }

            @Override
            public void onStart(int what) {
                // 这里可以show()一个wait dialog。
                Log.e("AAAAAA", "onStart: " + what);
            }

            @Override
            public void onFinish(int what) {
                // 这里可以dismiss()上面show()的wait dialog。
                Log.e("AAAAAA", "onFinish: " + what);
            }
        });
    }

//    private void upload(String filePath) {
//        String url = "https://cv-api.bytedance.com/api/vc/image-edit/v1/image_outpaint";
//        Request<String> mRequest = NoHttp.createStringRequest(url, RequestMethod.POST);
//        mRequest.add("x_pref", "both");
//        mRequest.add("y_pref", "both");
//        mRequest.add("ratio", 2);
//
//        mRequest.add("image", new FileBinary(new File(filePath)));
//
//        // 添加到请求队列
//        RequestQueue queue = NoHttp.newRequestQueue();
//
//        queue.add(0, mRequest, new OnResponseListener<String>() {
//
//            @Override
//            public void onSucceed(int what, Response<String> response) {
//                if (response.responseCode() == 200) {// 请求成功。
//
//                }
//                String result = response.get();
//                Log.e("AAAAAA", "onSucceed: " + result);
//            }
//
//            @Override
//            public void onFailed(int what, Response<String> response) {
//                Log.e("AAAAAA", "onFailed: " + response.getException().getMessage());
//            }
//
//            @Override
//            public void onStart(int what) {
//                // 这里可以show()一个wait dialog。
//                Log.e("AAAAAA", "onStart: " + what);
//            }
//
//            @Override
//            public void onFinish(int what) {
//                // 这里可以dismiss()上面show()的wait dialog。
//                Log.e("AAAAAA", "onFinish: " + what);
//            }
//        });
//    }
}