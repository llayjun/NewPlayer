package nj.com.myplayer.model;

/**
 * 播放器文件bean
 * Created by Administrator on 2017/11/27.
 */
public class PlayerBean extends BasicBean {

    private int indexPlayTable;  //文件所属的播放列表的序号

    private int index; //文件于该播放列表中的序号

    private String fileName; //文件名称

    private String playLength; //播放时长（针对图片而言）

    private String fileType; //文件类型

    public int getIndexPlayTable() {
        return indexPlayTable;
    }

    public void setIndexPlayTable(int indexPlayTable) {
        this.indexPlayTable = indexPlayTable;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPlayLength() {
        return playLength;
    }

    public void setPlayLength(String playLength) {
        this.playLength = playLength;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public int compareTo(Object o) {
        return this.indexPlayTable - ((PlayerBean) o).indexPlayTable;
    }
}
