package cn.com.iresearch.mvideotracker;

import android.content.Context;

public class DataProvider{

    public static boolean isLoaded = true;
    static {
        try {
            /*if(Build.CPU_ABI.startsWith("arm"))
            {*/
                DataProvider.isLoaded = true;
                System.loadLibrary("mvvtracker");
            /*}
            else {
                DataProvider.isLoaded = false;
            }*/
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            DataProvider.isLoaded = false;
            e.printStackTrace();
        }

    }
    public static native String getDesU(Context context) ;
}
