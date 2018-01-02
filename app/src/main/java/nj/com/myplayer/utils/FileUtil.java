package nj.com.myplayer.utils;

import android.content.Context;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Administrator on 2017/12/1 0001.
 */

public class FileUtil {
    private static String TAG = "MEDIA_FILE";

    /**
     * 删除文件
     *
     * @param _context
     * @param _file
     */
    public static void deleteFile(Context _context, File _file) {
        if (_file.isFile()) {
            String filePath = _file.getPath();
            if (filePath.endsWith(".mp4")
                    || filePath.endsWith(".rmvb")) {
                int res = _context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "= \"" + filePath + "\"", null);
                if (res > 0) {
                    _file.delete();
                }
            } else if (filePath.endsWith(".jpg")
                    || filePath.endsWith(".png")
                    || filePath.endsWith(".bmp")) {
                int res = _context.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.DATA + "= \"" + filePath + "\"", null);
                if (res > 0) {
                    _file.delete();
                }
            } else {
                _file.delete();
            }
        }
    }

//    /**
//     * 获取视频文件
//     *
//     * @param list
//     * @param file
//     * @return
//     */
//    public static List<MediaBean> getVideoFile(final List<MediaBean> list, File file) {
//        file.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File file) {
//                String name = file.getName();
//                int i = name.indexOf('.');
//                if (i != -1) {
//                    name = name.substring(i);
//                    if (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp") || name.equalsIgnoreCase(".wmv")
//                            || name.equalsIgnoreCase(".ts") || name.equalsIgnoreCase(".rmvb")
//                            || name.equalsIgnoreCase(".mov") || name.equalsIgnoreCase(".m4v")
//                            || name.equalsIgnoreCase(".avi") || name.equalsIgnoreCase(".m3u8")
//                            || name.equalsIgnoreCase(".3gpp") || name.equalsIgnoreCase(".3gpp2")
//                            || name.equalsIgnoreCase(".mkv") || name.equalsIgnoreCase(".flv")
//                            || name.equalsIgnoreCase(".divx") || name.equalsIgnoreCase(".f4v")
//                            || name.equalsIgnoreCase(".rm") || name.equalsIgnoreCase(".asf")
//                            || name.equalsIgnoreCase(".ram") || name.equalsIgnoreCase(".mpg")
//                            || name.equalsIgnoreCase(".v8") || name.equalsIgnoreCase(".swf")
//                            || name.equalsIgnoreCase(".m2v") || name.equalsIgnoreCase(".asx")
//                            || name.equalsIgnoreCase(".ra") || name.equalsIgnoreCase(".ndivx")
//                            || name.equalsIgnoreCase(".xvid")) {
//                        MediaBean _mediaBean = new MediaBean();
//                        file.getUsableSpace();
//                        _mediaBean.setType(MediaBean.MEDIA_TYPE_VIDEO);
//                        _mediaBean.setMediaName(file.getName());
//                        _mediaBean.setMediaPath(file.getAbsolutePath());
//                        Log.e(TAG, "mediaFileName:" + file.getName() + " mediaFilePath:" + file.getAbsolutePath());
//                        list.add(_mediaBean);
//                        return true;
//                    } else if (name.equalsIgnoreCase(".jpg") || name.equalsIgnoreCase(".png")) {
//                        MediaBean _mediaBean = new MediaBean();
//                        file.getUsableSpace();
//                        _mediaBean.setType(MediaBean.MEDIA_TYPE_IMAGE);
//                        _mediaBean.setMediaName(file.getName());
//                        _mediaBean.setMediaPath(file.getAbsolutePath());
//                        Log.e(TAG, "mediaFileName:" + file.getName() + " mediaFilePath:" + file.getAbsolutePath());
//                        list.add(_mediaBean);
//                        return true;
//                    }
//                    // 判断是不是目录
//                } else if (file.isDirectory()) {
//                    getVideoFile(list, file);
//                }
//                return false;
//            }
//        });
//        return list;
//    }
}
