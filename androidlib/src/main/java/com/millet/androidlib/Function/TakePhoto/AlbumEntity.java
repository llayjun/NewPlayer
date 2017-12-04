package com.millet.androidlib.Function.TakePhoto;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class AlbumEntity {

    /**
     * 相册Id
     */
    private String albumId;

    /**
     * 相册名字
     */
    private String name;

    /**
     * 封面图片
     */
    private String coverPath;

    /**
     * 相册path
     */
    private String path;

    /**
     * 相册照片数量
     */
    private int picCount;

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPicCount() {
        return picCount;
    }

    public void setPicCount(int picCount) {
        this.picCount = picCount;
    }
}
