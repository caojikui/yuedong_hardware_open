package com.yuedong.open.hardware;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

/**
 * Created by LaiXiaodong
 * Date ： 2016/1/18.
 */
public class DataHelper {

    public static final Uri kUriStep = Uri.parse("content://" + PlugConst.AUTOHORITY + "/"+ PlugConst.kTableSteps);
    public static final Uri kUriSleep = Uri.parse("content://" + PlugConst.AUTOHORITY + "/"+ PlugConst.kTableSleep);
    public static final Uri kUriHeartRate = Uri.parse("content://" + PlugConst.AUTOHORITY + "/"+ PlugConst.kTableHeartRate);
    public static final Uri kUriIntelligentScale = Uri.parse("content://" + PlugConst.AUTOHORITY + "/"+ PlugConst.kTableIntelligentScale);

    private static DataHelper sInstance;

    private Context context;

    private DataHelper(Context context) {
        this.context = context;
    }

    public static DataHelper getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataHelper(context);
        }
        return sInstance;
    }

    public void writeStepData(String deviceIdentify, int stepCount, float disM, int calorie, long startTSec, long endTSec) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PlugConst.kColDeviceIdentify, deviceIdentify);
        values.put(PlugConst.kColStepCount, stepCount);
        values.put(PlugConst.kColStartTSec, startTSec);
        values.put(PlugConst.kColEndTSec, endTSec);
        values.put(PlugConst.kColDistanceM, disM);
        values.put(PlugConst.kColCalorie, calorie);
        resolver.insert(kUriStep, values);
    }

    public void writeSleepData(String deviceIdentify, int sleepSection, long startTSec, long endTSec) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PlugConst.kColDeviceIdentify, deviceIdentify);
        values.put(PlugConst.kColStepCount, sleepSection);
        values.put(PlugConst.kColStartTSec, startTSec);
        values.put(PlugConst.kColEndTSec, endTSec);
        resolver.insert(kUriSleep, values);
    }

    public void writeHeartRateData(String deviceIdentify, int heartRate, long startTSec, long endTSec) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PlugConst.kColDeviceIdentify, deviceIdentify);
        values.put(PlugConst.kColHeartRate, heartRate);
        values.put(PlugConst.kColStartTSec, startTSec);
        values.put(PlugConst.kColEndTSec, endTSec);
        resolver.insert(kUriHeartRate, values);
    }

    public void writeIntelligentScaleData(String deviceIdentify, long time_sec, float weight_g, float bft, float bmp, float bmi, float bmr, float bwp, JSONObject extra) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(PlugConst.kColDeviceIdentify, deviceIdentify);
        values.put(PlugConst.kColTimeSec, time_sec);
        values.put(PlugConst.kColWeightG, weight_g);
        values.put(PlugConst.kColBodyFatPercentage, bft);
        values.put(PlugConst.kColBodyMusclePercentage, bmp);
        values.put(PlugConst.kColBodyMassIndex, bmi);
        values.put(PlugConst.kColBasalMetabolismRate, bmr);
        values.put(PlugConst.kColBodyWaterPercentage, bwp);
        if (null != extra) {
            values.put(PlugConst.kColExtra, extra.toString());
        }
        resolver.insert(kUriIntelligentScale, values);
    }
}
