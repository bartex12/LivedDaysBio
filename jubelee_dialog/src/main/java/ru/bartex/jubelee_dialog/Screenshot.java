package ru.bartex.jubelee_dialog;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class Screenshot {

    public static final String TAG = "33333";

    public static Bitmap takeScreenshot(View v){
        v = v.getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static File saveBitmap(Bitmap bm, String fileName){

        final String path = Environment.getExternalStorageDirectory() + "/Screenshots";

        Log.d(TAG, "isExternalStorageEmulated = " + Environment.isExternalStorageEmulated() +
                "    getExternalStorageState = " + Environment.getExternalStorageState());
        Log.d(TAG, "path: " + path);

        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        // запись в файл по пути : /storage/emulated/0/Screenshots/mantis_image.png
        File file = new File(dir, fileName);
        Log.d(TAG, "getAbsolutePath: " + file.getAbsolutePath());

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "file.length: " + file.length() + "  getFreeSpace: "+ file.getFreeSpace());
        return file;
    }


}
