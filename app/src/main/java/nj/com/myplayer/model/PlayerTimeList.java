package nj.com.myplayer.model;

/**
 * 播放列表bean
 * Created by Administrator on 2017/11/27.
 */
public class PlayerTimeList extends BasicBean {

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
        return this.index - ((PlayerTimeList) o).index;
    }

}
