package com.fang.starfang.util;

import android.os.Build;

/**
 * Created by jj on 26/05/17.
 */

public class VersionUtils {

    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean isNougat() { return Build.VERSION.SDK_INT >= 24;}

    public static boolean isOreo() { return Build.VERSION.SDK_INT >= 26;}

    public static boolean isPie() { return Build.VERSION.SDK_INT >= 28;}

    public static String currentVersion() {
        double release=Double.parseDouble(Build.VERSION.RELEASE.replaceAll("(\\d+[.]\\d+)(.*)","$1"));
        String codeName="Unsupported";//below Jelly bean OR above Oreo
        if(release>=4.1 && release<4.4)codeName="Jelly Bean";
        else if(release<5)codeName="Kit Kat";
        else if(release<6)codeName="Lollipop";
        else if(release<7)codeName="Marshmallow";
        else if(release<8)codeName="Nougat";
        else if(release<9)codeName="Oreo";
        else if(release<10)codeName="Pie";
        return codeName+" v"+release+", API Level: "+Build.VERSION.SDK_INT;
    }

}
