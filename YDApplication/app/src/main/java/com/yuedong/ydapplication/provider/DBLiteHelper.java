package com.yuedong.ydapplication.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuedong.open.hardware.PlugConst;

/**
 * Created by LaiXiaodong
 * Date ： 2016/1/13.
 */
public class DBLiteHelper extends SQLiteOpenHelper {
    public static final String HARDWARE_DBNAME = "hardware_dbname";
    public static final int VERSION = 2;
    public DBLiteHelper(Context context) {
        super(context, HARDWARE_DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + PlugConst.kTableSteps + "(" +
                PlugConst.kColId + " integer primary key autoincrement," +
                PlugConst.kColDeviceIdentify + " text not null," +
                PlugConst.kColStartTSec + " integer not null," +
                PlugConst.kColEndTSec + " integer not null," +
                PlugConst.kColStepCount + " integer default 0," +
                PlugConst.kColDistanceM + " REAL default 0," +
                PlugConst.kColCalorie + " integer default 0);");
        db.execSQL("create table " + PlugConst.kTableSleep + "(" +
                PlugConst.kColId + " integer primary key autoincrement not null," +
                PlugConst.kColDeviceIdentify + " text not null," +
                PlugConst.kColSleepSection + " integer not null," +
                PlugConst.kColStartTSec + " integer not null," +
                PlugConst.kColEndTSec + " integer not null);");

        db.execSQL("create table " + PlugConst.kTableHeartRate + "(" +
                PlugConst.kColId + " integer primary key autoincrement not null," +
                PlugConst.kColDeviceIdentify + " text not null," +
                PlugConst.kColHeartRate + " integer not null," +
                PlugConst.kColStartTSec + " integer not null," +
                PlugConst.kColEndTSec + " integer not null);");

        db.execSQL("create table " + PlugConst.kTableIntelligentScale + "(" +
                PlugConst.kColId + " integer primary key autoincrement not null," +
                PlugConst.kColDeviceIdentify + " text not null," +
                PlugConst.kColTimeSec + " integer not null," +
                PlugConst.kColBodyFatPercentage + " REAL default 0," +
                PlugConst.kColBodyMassIndex + " REAL default 0," +
                PlugConst.kColBodyMusclePercentage + " REAL default 0," +
                PlugConst.kColBodyWaterPercentage + " REAL default 0," +
                PlugConst.kColBasalMetabolismRate + " REAL default 0," +
                PlugConst.kColWeightG + " integer not null," +
                PlugConst.kColExtra + " text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == 2) {
            db.execSQL("DROP TABLE IF EXISTS " + PlugConst.kTableSteps);
            db.execSQL("create table if not exists " + PlugConst.kTableSteps + "(" +
                    PlugConst.kColId + " integer primary key autoincrement," +
                    PlugConst.kColDeviceIdentify + " text not null," +
                    PlugConst.kColStartTSec + " integer not null," +
                    PlugConst.kColEndTSec + " integer not null," +
                    PlugConst.kColStepCount + " integer default 0," +
                    PlugConst.kColDistanceM + " REAL default 0," +
                    PlugConst.kColCalorie + " integer default 0);");
        }
    }

}
