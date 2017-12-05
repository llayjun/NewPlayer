package nj.com.myplayer.common;


import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nj.com.myplayer.model.PlayerBean;
import nj.com.myplayer.model.PlayerTimeList;
import nj.com.myplayer.model.TextBean;


/**
 * Created by CaoYK on 2017/11/27.
 */

public class FileAnalyzeUtil {

    private static final String HEADER_TEXT = "roll";  //弹幕指令开头
    private static final String HEADER_PLAYER = "player";  //播放文件指令开头
    private static final String HEADER_PLAY_TIME = "time"; //播放列表指令开头

    /**
     * 解析指定文件夹下所有弹幕指令文件
     *
     * @param filePath
     * @return
     */
    public static List<TextBean> getTextList(String filePath) {
        List<TextBean> resultList = new ArrayList<>();
        try {
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return resultList;
            } else {
                String tempFileName, tempFileContents, instructFileType;
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (tempFileName.startsWith(HEADER_TEXT)) {
                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
                        instructFileType = getValueByKey("instructFileType", tempFileContents);
                        if (HEADER_TEXT.equals(instructFileType)) {
                            TextBean textBean = JsonUtil.fromJson(tempFileContents, TextBean.class);
                            if (!ObjectUtils.isNullOrEmpty(textBean)) {
                                //时间校验加在此处
                                resultList.add(textBean);
                            }
                        }
                    }
                }
                Collections.sort(resultList);
                return resultList;
            }
        } catch (Exception e) {
            resultList = new ArrayList<>();
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 解析指定文件夹下所有播放器指令文件
     *
     * @param filePath
     * @return
     */
    public static List<PlayerBean> getPlayerFileList(String filePath) {
        List<PlayerBean> resultList = new ArrayList<PlayerBean>();
        try {
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return resultList;
            } else {
                String tempFileName, tempFileContents, instructFileType, listJson;
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (tempFileName.startsWith(HEADER_PLAYER)) {
                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
                        instructFileType = getValueByKey("instructFileType", tempFileContents);
                        if (HEADER_PLAYER.equals(instructFileType)) {
                            listJson = getStringByKey("list", tempFileContents);
                            List<PlayerBean> playerList = JsonUtil.fromJson(listJson, new TypeToken<List<PlayerBean>>() {
                            }.getType());
                            if (!ObjectUtils.isNullOrEmpty(playerList)) {
                                resultList.addAll(playerList);
                            }
                        }
                    }
                }
                Collections.sort(resultList);
                return resultList;
            }
        } catch (Exception e) {
            resultList = new ArrayList<>();
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 解析指定文件夹下所有播放器指令文件
     *
     * @param filePath
     * @return
     */
    public static List<PlayerTimeList> getPlayerTimeFileList(String filePath) {
        List<PlayerTimeList> resultList = new ArrayList<>();
        try {
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return resultList;
            } else {
                String tempFileName, tempFileContents, instructFileType, listJson;
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (tempFileName.startsWith(HEADER_PLAY_TIME)) {
                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
                        instructFileType = getValueByKey("instructFileType", tempFileContents);
                        if (HEADER_PLAY_TIME.equals(instructFileType)) {
                            listJson = getStringByKey("list", tempFileContents);
                            List<PlayerTimeList> playerList = JsonUtil.fromJson(listJson, new TypeToken<List<PlayerTimeList>>() {
                            }.getType());
                            if (!ObjectUtils.isNullOrEmpty(playerList)) {
                                resultList.addAll(playerList);
                            }
                        }
                    }
                }
                Collections.sort(resultList);
                return resultList;
            }
        } catch (Exception e) {
            resultList = new ArrayList<>();
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 获取json下的特定值
     *
     * @param key
     * @param json
     * @return
     */
    private static String getValueByKey(String key, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取json下的特定对象
     *
     * @param key
     * @param json
     * @return
     */
    private static String getStringByKey(String key, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getJSONArray(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
