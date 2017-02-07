package im.sdk.debug.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ${chenyn} on 16/3/14.
 *
 * @desc :获取内置的图片和mp3文件
 */
public class GetImageAndMusicUtil {
    public static File mFileDirectory;

    public static void getImage(Context mContext) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Image/");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            mFileDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/Image/test.png");
            InputStream in = mContext.getAssets().open("ic_launcher.png");
            OutputStream out = null;
            out = new FileOutputStream(mFileDirectory);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
