package com.xixia.aiimageupload;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.xixia.aiimageupload.icc.ICCActivity;
import com.xixia.aiimageupload.meitu.GlideEngine;

import java.util.List;

/**
 * @ClassName PictureSelectorUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023/9/11 10:11
 * @Version 1.0
 * @UpdateDate 2023/9/11 10:11
 * @UpdateRemark 更新说明
 */
public class PictureSelectorUtils {
    public interface OnPictureSelectorListener {
        void selectResult(String filePath);
    }

    public OnPictureSelectorListener onPictureSelectorListener;

    public void setOnPictureSelectorListener(OnPictureSelectorListener onPictureSelectorListener) {
        this.onPictureSelectorListener = onPictureSelectorListener;
    }

    //选择相册-单选
    public static void initPhotoPickerSingle(Activity context, OnPictureSelectorListener onPictureSelectorListener) {
        PictureSelector.create(context)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .isEnablePreviewAudio(true) // 是否可播放音频
                .isCamera(false)// 是否显示拍照按钮
                .isEnableCrop(false)// 是否裁剪
                //.isCompress(true)// 是否压缩
                //.compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(false)//同步true或异步false 压缩 默认同步
                .withAspectRatio(3, 2)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        if (result.size() > 0) {
                            String filePath = result.get(0).getRealPath();
                            if (onPictureSelectorListener != null) {
                                onPictureSelectorListener.selectResult(filePath);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}
