package nj.com.myplayer.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/1 0001.
 */

public class MediaBean implements Serializable {

    public static final int MEDIA_TYPE_VIDEO = 0;//视屏
    public static final int MEDIA_TYPE_IMAGE = 1;//图片

    private String mediaName;

    private String mediaPath;

    private int type;//0:视屏,1图片

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String _mediaName) {
        mediaName = _mediaName;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String _mediaPath) {
        mediaPath = _mediaPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int _type) {
        type = _type;
    }
}
