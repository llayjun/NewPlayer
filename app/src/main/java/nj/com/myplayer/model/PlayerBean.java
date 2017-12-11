package nj.com.myplayer.model;

import android.support.annotation.NonNull;

import java.util.List;


/**
 * 播放器文件bean
 * Created by Administrator on 2017/11/27.
 */
public class PlayerBean extends BasicBean {

    private List<PlayerInfo> list;

    private String instructFileType;

    private PlayTimeInfo playTimeInfo;

    public void setList(List<PlayerInfo> list) {
        this.list = list;
    }

    public List<PlayerInfo> getList() {
        return this.list;
    }

    public void setInstructFileType(String instructFileType) {
        this.instructFileType = instructFileType;
    }

    public String getInstructFileType() {
        return this.instructFileType;
    }

    public void setPlayTimeInfo(PlayTimeInfo playTimeInfo) {
        this.playTimeInfo = playTimeInfo;
    }

    public PlayTimeInfo getPlayTimeInfo() {
        return this.playTimeInfo;
    }

    @Override
    public int compareTo(@NonNull Object _o) {
        return 0;
    }

    public class PlayerInfo extends BasicBean {

        private int indexPlayTable;  //文件所属的播放列表的序号

        private int index; //文件于该播放列表中的序号

        private String fileName; //文件名称

        private String playLength; //播放时长（针对图片而言）

        private String fileType; //文件类型

        public int getIndexPlayTable() {
            return indexPlayTable;
        }

        public void setIndexPlayTable(int _indexPlayTable) {
            indexPlayTable = _indexPlayTable;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int _index) {
            index = _index;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String _fileName) {
            fileName = _fileName;
        }

        public String getPlayLength() {
            return playLength;
        }

        public void setPlayLength(String _playLength) {
            playLength = _playLength;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String _fileType) {
            fileType = _fileType;
        }

        @Override
        public int compareTo(Object o) {
            return this.index - ((PlayerInfo) o).index;
        }

    }

    public class PlayTimeInfo extends BasicBean {

        private int index;   //播放列表序号

        private String beginTime;  //列表播放开始时间

        private String endTime;   //列表播放结束时间

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        @Override
        public int compareTo(Object o) {
            return this.index - ((PlayTimeInfo) o).index;
        }

    }

}
