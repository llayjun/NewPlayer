package nj.com.myplayer.common;


import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nj.com.myplayer.model.PlayerBean;
import nj.com.myplayer.model.PlayerTimeList;
import nj.com.myplayer.model.TextBean;


/**
 * Created by CaoYK on 2017/11/27.
 */

public class FileAnalyzeUtil {

    private static final String FLAG_TEXT = "text";  //弹幕指令标识
    private static final String FLAG_PLAYER = "player";  //播放指令标识

    private static final String END_TEXT = ".json";  //弹幕指令文件扩展名
    private static final String END_PLAYER = ".xml"; //播放指令文件扩展名

    /**
     * 解析弹幕指令
     *
     * @param filePath 文件路径
     * @return List
     */
    public static List<TextBean> getTextList(String filePath) {
        List<TextBean> resultList = new ArrayList<TextBean>();
        try {
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return resultList;
            } else {
                String tempFileName, tempFileContents, instructFileType;
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (tempFileName.endsWith(END_TEXT)) {
                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
                        instructFileType = getValueByKey("instructFileType", tempFileContents);
                        if (FLAG_TEXT.equals(instructFileType)) {
                            TextBean textBean = JsonUtil.fromJson(tempFileContents, TextBean.class);
                            if (!ObjectUtils.isNullOrEmpty(textBean)) {
                                String currentTime = textBean.getTimeCheck(); //当前时间 时间校验加在此处
                                resultList.add(textBean);
//                                tempFile.delete();//指令文件解析后直接删除
                            }
                        }
                    }
                }
                Collections.sort(resultList);
                return resultList;
            }
        } catch (Exception e) {
            resultList = new ArrayList<TextBean>();
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 解析播放列表指令
     *
     * @param filePath 文件路径
     * @return Map playTimeList-播放时间表  playerList-播放文件列表
     */
    public static Map<String, List> getPlayerFileList(String filePath) {
        Map<String, List> resultMap = new HashMap<String, List>();
        List<PlayerBean> playerList = new ArrayList<PlayerBean>();
        List<PlayerTimeList> playTimeList = new ArrayList<PlayerTimeList>();
        try {
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return resultMap;
            } else {
                String tempFileName, tempFileContents, instructFileType, listJson, objectJson;
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (tempFileName.endsWith(END_PLAYER)) {
                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
                        instructFileType = getValueByKey("instructFileType", tempFileContents);
                        if (FLAG_PLAYER.equals(instructFileType)) {
                            //播放列表
                            listJson = getStringByKey("list", tempFileContents);
                            List<PlayerBean> tempPlayerList = JsonUtil.fromJson(listJson, new TypeToken<List<PlayerBean>>() {
                            }.getType());
                            if (!ObjectUtils.isNullOrEmpty(tempPlayerList)) {
                                playerList.addAll(tempPlayerList);
                            }
                            //播列表参数（列表序号、开始时间、结束时间）
                            objectJson = getValueByKey("playTimeInfo", tempFileContents);
                            PlayerTimeList playerTime = JsonUtil.fromJson(objectJson, PlayerTimeList.class);
                            if (!ObjectUtils.isNullOrEmpty(playerTime)) {
                                playTimeList.add(playerTime);
                            }
//                            tempFile.delete();//指令文件解析后直接删除
                        }
                    }
                }
                Collections.sort(playerList);
                Collections.sort(playTimeList);
                resultMap.put("playerList", playerList);
                resultMap.put("playTimeList", playTimeList);
                return resultMap;
            }
        } catch (Exception e) {
            resultMap = new HashMap<String, List>();
            e.printStackTrace();
        }
        return resultMap;
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
