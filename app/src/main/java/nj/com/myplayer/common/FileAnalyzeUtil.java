package nj.com.myplayer.common;


import android.content.Context;

import com.millet.androidlib.Utils.DateUtils;
import com.millet.androidlib.Utils.SharedPreferencesHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nj.com.myplayer.model.PlayerBean;
import nj.com.myplayer.model.TextBean;
import nj.com.myplayer.utils.DateUtil;


/**
 * Created by CaoYK on 2017/11/27.
 */

public class FileAnalyzeUtil {

    private static final String FLAG_TEXT = "text";  //弹幕指令标识
    private static final String FLAG_PLAYER = "player";  //播放指令标识

    private static final String END_TEXT = ".json";  //弹幕指令文件扩展名
    private static final String END_PLAYER = ".xml"; //播放指令文件扩展名

    public static final String PLAYER_LIST = "playerList";//播放列表
    public static final String PLAY_TIME_LIST = "playTimeList";//播放列表时间

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

//    /**
//     * 解析播放列表指令
//     *
//     * @param filePath 文件路径
//     * @return Map playTimeList-播放时间表  playerList-播放文件列表
//     */
//    public static Map<String, List> getPlayerFileList(String filePath) {
//        Map<String, List> resultMap = new HashMap<String, List>();
//        List<PlayerBean> playerList = new ArrayList<PlayerBean>();
//        List<PlayerTime> playTimeList = new ArrayList<PlayerTime>();
//        try {
//            File rootPath = new File(filePath);
//            File fileList[] = rootPath.listFiles();
//            if (ObjectUtils.isNullOrEmpty(fileList)) {
//                return resultMap;
//            } else {
//                String tempFileName, tempFileContents, instructFileType, listJson, objectJson;
//                for (File tempFile : fileList) {
//                    tempFileName = tempFile.getName();
//                    if (tempFileName.endsWith(END_PLAYER)) {
//                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
//                        instructFileType = getValueByKey("instructFileType", tempFileContents);
//                        if (FLAG_PLAYER.equals(instructFileType)) {
//                            //播放列表
//                            listJson = getStringByKey("list", tempFileContents);
//                            List<PlayerBean> tempPlayerList = JsonUtil.fromJson(listJson, new TypeToken<List<PlayerBean>>() {
//                            }.getType());
//                            if (!ObjectUtils.isNullOrEmpty(tempPlayerList)) {
//                                playerList.addAll(tempPlayerList);
//                            }
//                            //播列表参数（列表序号、开始时间、结束时间）
//                            objectJson = getValueByKey("playTimeInfo", tempFileContents);
//                            PlayerTime playerTime = JsonUtil.fromJson(objectJson, PlayerTime.class);
//                            if (!ObjectUtils.isNullOrEmpty(playerTime)) {
//                                playerTime.setBeginTime(DateUtil.timeFormat(playerTime.getBeginTime()));
//                                playerTime.setEndTime(DateUtil.timeFormat(playerTime.getEndTime()));
//                                playTimeList.add(playerTime);
//                            }
////                            tempFile.delete();//指令文件解析后直接删除
//                        }
//                    }
//                }
//                Collections.sort(playerList);
//                Collections.sort(playTimeList);
//                resultMap.put(PLAYER_LIST, playerList);
//                resultMap.put(PLAY_TIME_LIST, playTimeList);
//                return resultMap;
//            }
//        } catch (Exception e) {
//            resultMap = new HashMap<String, List>();
//            e.printStackTrace();
//        }
//        return resultMap;
//    }

    /**
     * 解析播放列表指令，根据文件的开始时间存储到sp中
     *
     * @param filePath 文件路径
     */
    public static void savePlayInfo2Shared(Context _context, String filePath) {
        try {
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return;
            } else {
                String tempFileName, tempFileContents, instructFileType, objectJson;
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (tempFileName.endsWith(END_PLAYER)) {
                        tempFileContents = FileReadUtil.getStringFromFile(tempFile);
                        instructFileType = getValueByKey("instructFileType", tempFileContents);
                        if (FLAG_PLAYER.equals(instructFileType)) {
                            //播列表参数（列表序号、开始时间、结束时间）
                            objectJson = getValueByKey("playTimeInfo", tempFileContents);
                            PlayerBean.PlayTimeInfo playerTime = JsonUtil.fromJson(objectJson, PlayerBean.PlayTimeInfo.class);
                            if (null == playerTime) return;
                            if (!ObjectUtils.isNullOrEmpty(playerTime)) {
                                playerTime.setBeginTime(DateUtil.timeFormat(playerTime.getBeginTime()));
                            }
                            if (null == playerTime.getBeginTime()) return;
                            long _time = DateUtils.formatToLongTime(playerTime.getBeginTime()) / 1000;
                            SharedPreferencesHelper.getInstance(Constant.PLAYER_NAME, _context).put(String.valueOf(_time), tempFileContents);
//                            tempFile.delete();//指令文件解析后直接删除
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析播放列表指令，根据文件的开始时间，获取的播放列表解析
     *
     * @param _jsonString Json文件
     * @return Map playTimeList-播放时间表  playerList-播放文件列表
     */
    public static PlayerBean getReadyPlayerFileList(String _jsonString) {
        PlayerBean _playerBean = null;
        try {
            if (ObjectUtils.isNullOrEmpty(_jsonString)) {
                return _playerBean;
            } else {
                _playerBean = JsonUtil.fromJson(_jsonString, PlayerBean.class);
                Collections.sort(_playerBean.getList());
                return _playerBean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _playerBean;
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
