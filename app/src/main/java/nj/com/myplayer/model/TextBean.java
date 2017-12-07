package nj.com.myplayer.model;

/**
 * 弹幕文件bean(包括时间校验)
 * Created by CaoYK on 2017/11/27.
 */
public class TextBean extends BasicBean {

    public static final String TEXT_POSTION = "upper";//文字位置

    public static final String TEXT_COLOR = "first";//文字颜色

    public static final String TEXT_SIZE = "text-big";//文字的大小


    private String position;  //弹幕位置

    private String fontColor;  //文字样式-颜色

    private String fontSize;  //文字样式-尺寸

    private String textBeginTime;  //弹幕开始时间

    private String textEndTime;  //弹幕结束时间

    private String timeCheck;   //当前时间（用于时间校准）

    private String content;  //弹幕内容

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getTextBeginTime() {
        return textBeginTime;
    }

    public void setTextBeginTime(String textBeginTime) {
        this.textBeginTime = textBeginTime;
    }

    public String getTextEndTime() {
        return textEndTime;
    }

    public void setTextEndTime(String textEndTime) {
        this.textEndTime = textEndTime;
    }

    public String getTimeCheck() {
        return timeCheck;
    }

    public void setTimeCheck(String timeCheck) {
        this.timeCheck = timeCheck;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(Object o) {
        return this.textBeginTime.compareTo(((TextBean) o).textBeginTime);
    }
}
