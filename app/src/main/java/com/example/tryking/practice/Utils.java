package com.example.tryking.practice;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.text.DecimalFormat;

/**
 * Created by Tryking on 2017/2/8.
 */

public class Utils {
    public static String getFileNameByPath(String path) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                builder.setLength(0);
            } else {
                builder.append(path.charAt(i));
            }
        }
        return builder.toString();
    }

    public static String formatTimeByMss(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = ((mss - days * (1000 * 60 * 60 * 24)) % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) % (1000 *
                60) / 1000;
        return (days == 0 ? "" : (days + "天")) + (hours == 0 ? "" : hours + "小时") + (minutes == 0
                ? "" : minutes + "分钟") + (seconds == 0 ? "" : (seconds + "秒"));
    }

    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1024 * 1024) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) fileS / (1024 * 1024)) + "MB";
        } else {
            fileSizeString = df.format(fileS / ((double) 1024 * 1024 * 1024)) + "GB";
        }
        return fileSizeString;
    }

    public static Bitmap getVideoThumbnail(String videopath, int width, int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videopath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils
                .OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
