package com.mobilewrapper.base.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by dumin on 13. 8. 9..
 */
public class Utilities {
    public static final int NETWORK_WIFI = 0;
    public static final int NETWORK_3G = 1;
    public static final int NETWORK_NONE = 2;

    /**
     * 어플 버전 리턴
     *
     * @param context
     * @return 어플리케이션 버전
     */
    public static String appVersion(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {

        }
        return packageInfo.versionName;
//		return context.getString(context.getApplicationInfo().labelRes);
    }

    /**
     * 접속 경로 확인
     *
     * @param context
     * @return
     */
    public static int checkStatus(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED) {
            return NETWORK_WIFI;
        } else if (mobile != null && mobile.getState() == NetworkInfo.State.CONNECTED) {
            return NETWORK_3G;
        } else {
            return NETWORK_NONE;
        }
    }

    /**
     * 어플 이름 리턴
     *
     * @param context
     * @return 어플리케이션 이름
     */
    public static String appName(Context context) {
        return context.getString(context.getApplicationInfo().labelRes);
    }

    /**
     * 어플 이름 리턴
     *
     * @param context
     * @return 어플리케이션 이름
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * sd카드 위치 반환
     *
     * @return
     */
    public static String getExternalSdCardPath() {
        return android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 외장 디렉토리 생성
     *
     * @param path 생성할 경로
     * @return 생성 여부
     */
    public static boolean makeExternalDir(String path) {
        File wallpaperDirectory = new File(path);
        return wallpaperDirectory.mkdirs();
    }

    /**
     * 해당 디렉토리 삭제
     *
     * @param path
     */
    public static void deleteDir(String path) {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        if (childFileList != null) {
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    deleteDir(childFile.getAbsolutePath()); // 하위 디렉토리 루프
                } else {
                    childFile.delete(); // 하위 파일삭제
                }
            }
        }
        file.delete(); // root 삭제
    }

    /**
     * heightPixels,widthPixels,density 를 확인하기 위한 DisplayMetrics를 가져온다
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null) {
            return null;
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    public static int getDDay(long pMTime) {
        try {
            long day = pMTime;
            long tday = new Date().getTime();

            long count = (day - tday) / (60 * 60 * 24 * 1000);
            return (int) count;
        } catch (Exception e) {
            return -1;
        }
    }

    public static int getAgeFromBirthday(Date birthday) {
        Calendar birth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        birth.setTime(birthday);
        today.setTime(new Date());

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        return age + 1;
    }

    public static long getFolderSize(String path) {
        long totalMemory = 0;
        File file = new File(path);
        File[] childFileList = file.listFiles();

        if (childFileList == null) {
            return 0;
        }

        for (File childFile : childFileList) {
            if (childFile.isDirectory()) {
                totalMemory += getFolderSize(childFile.getAbsolutePath());

            } else {
                totalMemory += childFile.length();
            }
        }
        return totalMemory;
    }

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    public static void openURLOnBrowser(Context context, String targetURL) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetURL));
            context.startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
        }
    }
}
