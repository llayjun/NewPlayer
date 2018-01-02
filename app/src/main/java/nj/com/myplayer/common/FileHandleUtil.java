package nj.com.myplayer.common;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;

import nj.com.myplayer.utils.FileUtil;

/**
 * 文件处理
 * Created by Administrator on 2017/11/28.
 */
public class FileHandleUtil {

    /**
     * 删除文件夹下失去引用的文件
     *
     * @param filePath 文件路径
     */
    public static void deleteLossFile(Context _context, String filePath) {
        try {
            if (android.text.TextUtils.isEmpty(filePath)) return;
            Set<String> fileNameSet = FileAnalyzeUtil.getPlayInfoList(filePath);
            if (fileNameSet.size() == 0) return;
            File rootPath = new File(filePath);
            File fileList[] = rootPath.listFiles();
            if (!ObjectUtils.isNullOrEmpty(fileList)) {
                String tempFileName; //文件名-临时变量
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    //文件不需要播放且不是播放指令文件  此类文件删除
                    if (!fileNameSet.contains(tempFileName) && !tempFileName.endsWith(FileAnalyzeUtil.END_PLAYER)) {
                        FileUtil.deleteFile(_context, tempFile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * cp指定文件到APP的特定路径下
     *
     * @param appPath  APP下存放文件的路径
     * @param wifiPath WiFi接受文件存放的路径
     * @param nameSet  需要cp的文件
     */
    public static void copyNeedFile(String appPath, String wifiPath, Set<String> nameSet) {
        try {
            File rootPath = new File(wifiPath);
            File fileList[] = rootPath.listFiles();
            if (ObjectUtils.isNullOrEmpty(fileList)) {
                return;
            } else {
                String tempFileName; //文件名
                for (File tempFile : fileList) {
                    tempFileName = tempFile.getName();
                    if (!nameSet.contains(tempFileName)) {
                        continue;
                    } else {
                        copyFile(tempFile.getCanonicalPath(), appPath + "\\" + tempFileName);  //绝对路径
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list(); //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 文件复制
     *
     * @param oldPath
     * @param newPath
     */
    public static void copyFile(String oldPath, String newPath) {
        FileInputStream in = null;
        try {
            File oldFile = new File(oldPath);
            File file = new File(newPath);
            in = new FileInputStream(oldFile);
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[2 * 1024]; //一次读取2k
            int a;
            while ((a = in.read(buffer)) != -1) {
                out.write(buffer, 0, a);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
