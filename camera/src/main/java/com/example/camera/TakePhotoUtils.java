package com.example.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @ClassName TakePhotoUtils
 * @Description TODO 内容
 * @Author biekangdong
 * @CreateDate 2023-10-28 11:53
 * @Version 1.0
 * @UpdateDate 2023-10-28 11:53
 * @UpdateRemark 更新说明
 */
public class TakePhotoUtils {
     public static final int REQUEST_CAMERA=101;
     public static String cameraPath="";
    /**
     * start to camera、preview、crop
     */
    public static void startOpenCameraImage(Activity context) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            Uri imageUri;
            String cameraFileName = System.currentTimeMillis()+".png";
            String imageFormat = "image/jpeg";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageUri = createImageUri(context, cameraFileName, imageFormat);
                if (imageUri != null) {
                    cameraPath = imageUri.toString();
                }
            } else {
                File cameraFile =createOutFile(context, cameraFileName);
                cameraPath = cameraFile.getAbsolutePath();
                imageUri = parUri(context, cameraFile);
            }
            if (imageUri == null) {
                Toast.makeText(context,"open is camera error，the uri is empty",Toast.LENGTH_SHORT).show();
                return;
            }
//            if (config.isCameraAroundState) {
//                cameraIntent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
//            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            context.startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
    }
    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param ctx
     * @param cameraFileName
     * @return 图片的uri
     */
    public static Uri createImageUri(final Context ctx, String cameraFileName, String mimeType) {
        Context context = ctx.getApplicationContext();
        Uri[] imageFilePath = {null};
        String status = Environment.getExternalStorageState();
        String time = String.valueOf(System.currentTimeMillis());
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, getCreateFileName("IMG_"));
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, getCreateFileName("IMG_"));
            } else {
                String suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."));
                String fileName = cameraFileName.replaceAll(suffix, "");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        }
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera");
            }
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            imageFilePath[0] = context.getContentResolver()
                    .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
        }
        return imageFilePath[0];
    }
    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    public static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(millis);
    }




    /**
     * 创建文件
     *
     * @param ctx                上下文
     * @param fileName           文件名
     * @return
     */
    private static File createOutFile(Context ctx, String fileName) {
        Context context = ctx.getApplicationContext();
        File folderDir;
        // 外部没有自定义拍照存储路径使用默认
        File rootDir;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            folderDir = new File(rootDir.getAbsolutePath() + File.separator + "Camera" + File.separator);
        } else {
            rootDir = getRootDirFile(context);
            folderDir = new File(rootDir.getAbsolutePath() + File.separator);
        }
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }

        boolean isOutFileNameEmpty = TextUtils.isEmpty(fileName);
        String suffix = ".jpeg";
        String newFileImageName = isOutFileNameEmpty ? getCreateFileName("IMG_") + suffix : fileName;
        return new File(folderDir, newFileImageName);
    }

    /**
     * 文件根目录
     *
     * @param context
     * @return
     */
    private static File getRootDirFile(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    /**
     * 生成uri
     *
     * @param context
     * @param cameraFile
     * @return
     */
    public static Uri parUri(Context context, File cameraFile) {
        Uri imageUri;
        String authority = context.getPackageName() + ".luckProvider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(context, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Camera Handle
     */
    public static String  dispatchHandleCamera(Activity activity) {
        try {
            // If PictureSelectionConfig is not empty, synchronize it
            String cameraPath= TakePhotoUtils.cameraPath;
            String realPath="";
            Log.e("EEE", "cameraPath: "+cameraPath);
            if (isContent(cameraPath)) {
                // content: Processing rules
                String path = getPath(activity, Uri.parse(cameraPath));
                realPath=path;
            } else {
                File cameraFile = new File(cameraPath);
                realPath=cameraFile.getAbsolutePath();
            }
            Log.e("EEE", "realPath: "+realPath);

            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(realPath))));

            return realPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * is content://
     *
     * @param url
     * @return
     */
    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param ctx The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context ctx, final Uri uri) {
        Context context = ctx.getApplicationContext();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), toLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return "";
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (IllegalArgumentException ex) {
            Log.i("EE", String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static long toLong(Object o) {
        return toLong(o, 0);
    }
    public static long toLong(Object o, long defaultValue) {
        if (o == null) {
            return defaultValue;
        }
        long value = 0;
        try {
            String s = o.toString().trim();
            if (s.contains(".")) {
                value = Long.valueOf(s.substring(0, s.lastIndexOf(".")));
            } else {
                value = Long.valueOf(s);
            }
        } catch (Exception e) {
            value = defaultValue;
        }


        return value;
    }
}
