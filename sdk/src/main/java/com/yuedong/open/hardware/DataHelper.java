package com.yuedong.open.hardware;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

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
        values.put(PlugConst.kColSleepSection, sleepSection);
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

    private static final long kDayMillis = 86400000;
    private static final long kHourMillis = 3600000;
    private static final long kMinMillis = 60000;

    private static long dayBeginningOf(long millis) {
        int offset = TimeZone.getDefault().getRawOffset();
        return (millis + offset) / kDayMillis * kDayMillis - offset;
    }

    public static class StepCountSum {
        public int stepCount = 0;
        public float disM = 0;
        public int calorie = 0;
    }

    public StepCountSum dayTotalStepCount(String deviceIdentify, long beginTSec, long endTSec) {
        ContentResolver resolver = context.getContentResolver();
        String selection = PlugConst.kColStartTSec + ">=" + beginTSec
                + " and " + PlugConst.kColEndTSec + "<" + endTSec
                + " and " + PlugConst.kColDeviceIdentify + "=\"" + deviceIdentify + '\"';
        Cursor cursor = resolver.query(kUriStep, null, selection, null, null);
        StepCountSum sum = new StepCountSum();
        if(cursor.moveToFirst()) {
            int indexStep = cursor.getColumnIndex(PlugConst.kColStepCount);
            int indexCalorie = cursor.getColumnIndex(PlugConst.kColCalorie);
            int indexDisM = cursor.getColumnIndex(PlugConst.kColDistanceM);
            do {
                sum.stepCount += cursor.getInt(indexStep);
                sum.disM += cursor.getFloat(indexDisM);
                sum.calorie += cursor.getInt(indexCalorie);
            } while (cursor.moveToNext());
        }
        return sum;
    }

    public void saveStepCountByDayTotal(String deviceIdentify, int stepCount, float disM, int calorie, long dayBeginSec) {
        SharedPreferences sp = context.getSharedPreferences(deviceIdentify, Context.MODE_PRIVATE);
        long dayBegin = dayBeginningOf(dayBeginSec * 1000);
        String key = Long.toString(dayBegin);
        String valueStr = sp.getString(key, null);
        int oldStep = 0;
        float oldDisM = 0;
        int oldCalorie = 0;
        long lastTSec = dayBegin / 1000;
        long endTSec = System.currentTimeMillis() / 1000;
        if(valueStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(valueStr);
                oldStep = jsonObject.optInt("step");
                oldDisM = (float) jsonObject.optDouble("dis_m");
                oldCalorie = jsonObject.optInt("calorie");
                lastTSec = jsonObject.optLong("last_t_sec");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        writeStepData(deviceIdentify, stepCount-oldStep, disM-oldDisM, calorie-oldCalorie, lastTSec, endTSec);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("step", stepCount);
            jsonObject.put("dis_m", disM);
            jsonObject.put("calorie", calorie);
            jsonObject.put("last_t_sec", endTSec);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sp.edit().putString(key, jsonObject.toString()).commit();
    }
}
