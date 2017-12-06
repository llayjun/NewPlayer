package nj.com.myplayer.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/11/27.
 */
public class FileReadUtil {

    /**
     * file —> string
     *
     * @param path 文件路径
     * @return string
     */
    public static String getStringFromFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String string = null;
            while ((string = reader.readLine()) != null) {
                sb.append(string);
            }
            return sb.toString().replace("\uFEFF", "");
        } catch (Exception e) {
            sb = new StringBuilder();
        }
        return sb.toString();
    }

    /**
     * file —> string
     * @param file 文件路径
     * @return string
     */
    public static String getStringFromFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String string = null;
            while ((string = reader.readLine()) != null) {
                sb.append(string);
            }
            return sb.toString().replace("\uFEFF", "");
        } catch (Exception e) {
            sb = new StringBuilder();
        }
        return sb.toString();
    }

}
