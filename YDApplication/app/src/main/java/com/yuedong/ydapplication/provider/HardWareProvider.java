package com.yuedong.ydapplication.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yuedong.open.hardware.PlugConst;
import com.yuedong.ydapplication.Const;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LaiXiaodong
 * Date ： 2016/1/14.
 */
public class HardWareProvider extends ContentProvider {

    private DBLiteHelper dbLiteHelper = null;
    private SQLiteDatabase db = null;

    private static final UriMatcher sMatcher;

    public static final int kTableStep = 1;

    public static final int kTableSleep = 3;

    private static final int kTableHeartRate = 5;

    private static final int kTableScale = 7;

    private static final int kTableRealTimeStep = 8;
    private static final int kTableUserTarget = 9;

    private static final String kTag = "open_hardware";

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableSteps, kTableStep);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableSleep, kTableSleep);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableHeartRate, kTableHeartRate);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableIntelligentScale, kTableScale);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableRealTimeStep, kTableRealTimeStep);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableUserSportTarget, kTableUserTarget);
    }
    @Override
    public boolean onCreate() {
        dbLiteHelper = new DBLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case kTableStep:
                return PlugConst.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
    }

    private static String kDeviceCheckReg = PlugConst.kColDeviceIdentify + "=";

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(!selection.contains(kDeviceCheckReg)) {
            throw new IllegalArgumentException("need device identify");
        }

        db = dbLiteHelper.getWritableDatabase();
        Cursor cursor = null;
        String id = null;
        switch (sMatcher.match(uri)) {
            case kTableStep:
                cursor = db.query(PlugConst.kTableSteps, projection, selection, selectionArgs, null, null, null);
                break;
            case kTableSleep:
                cursor = db.query(PlugConst.kTableSleep, projection, selection, selectionArgs, null, null, null);
                break;
            case kTableHeartRate:
                cursor = db.query(PlugConst.kTableHeartRate, projection, selection, selectionArgs, null, null, null);
                break;
            case kTableScale:
                cursor = db.query(PlugConst.kTableIntelligentScale, projection, selection, selectionArgs, null, null, null);
                break;
            case kTableRealTimeStep:
                if(selectionArgs == null || selectionArgs.length == 0) {
                    throw new IllegalArgumentException("You should give deviceIdentify in selectionArgs[0]");
                }
                cursor = getRealTimeStepData(selectionArgs[0]);
                break;
            case kTableUserTarget:
                return getUserTarget();
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private static String[] kUserTargetCols = new String[]{PlugConst.kColStepCount};
    private Cursor getUserTarget() {
        MatrixCursor cursor = new MatrixCursor(kUserTargetCols);
        cursor.addRow(new Object[]{3000});
        return cursor;
    }

    private static String[] kRealTimeStepCols = new String[]{PlugConst.kColStepCount, PlugConst.kColDistanceM, PlugConst.kColCalorie};
    private Cursor getRealTimeStepData(String deviceIdentify) {
        if (!deviceIdentify.equalsIgnoreCase(getRegisterDeviceId())) {
            Log.e(kTag, "deviceIdentify不合法 确认已经注册device");
            throw new IllegalArgumentException();
        }
        MatrixCursor cursor = new MatrixCursor(kRealTimeStepCols);
        cursor.addRow(new Object[]{realTimeStep, realTimeDisM, realTimeCal});
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        checkPermission(values);
        db = dbLiteHelper.getWritableDatabase();
        long rowId;
        Uri noteUri = null;
        try {
            if (sMatcher.match(uri) == kTableStep) {
                rowId = db.insertOrThrow(PlugConst.kTableSteps, null, values);
                if (rowId > 0) {
                    noteUri = ContentUris.withAppendedId(PlugConst.STEP_CALCULATION_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
            } else if (sMatcher.match(uri) == kTableSleep) {
                rowId = db.insertOrThrow(PlugConst.kTableSleep, null, values);
                if (rowId > 0) {
                    noteUri = ContentUris.withAppendedId(PlugConst.SLEEP_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
            } else if (sMatcher.match(uri) == kTableHeartRate) {
                rowId = db.insertOrThrow(PlugConst.kTableHeartRate, null, values);
                if (rowId > 0) {
                    noteUri = ContentUris.withAppendedId(PlugConst.HEART_RATE_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
            } else if (sMatcher.match(uri) == kTableScale) {
                rowId = db.insertOrThrow(PlugConst.kTableIntelligentScale, null, values);
                if (rowId > 0) {
                    noteUri = ContentUris.withAppendedId(PlugConst.INTELLIGENT_SCALE_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e("hardware_open", t.getLocalizedMessage());
            throw t;
        }
        throw new IllegalArgumentException("Unknown URI"+uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sMatcher.match(uri)) {
            case kTableRealTimeStep:
                checkPermission(values);
                if(!updateRealTimeStep(values)) {
                    return -1;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        return 0;
    }

    private String getRegisterDeviceId() {
        SharedPreferences sp = getContext().getSharedPreferences(Const.kSpName, Context.MODE_PRIVATE);
        return sp.getString(Const.kKeyDeviceId, null);
    }

    private boolean checkPermission(ContentValues values) {
        if (!values.containsKey(PlugConst.kColDeviceIdentify)) {
            Log.e(kTag, "deviceIdentify是必须的");
            throw new IllegalArgumentException("need device identify");
        }

        String deviceIdentify = values.getAsString(PlugConst.kColDeviceIdentify);
        if (!deviceIdentify.equalsIgnoreCase(getRegisterDeviceId())) {
            Log.e(kTag, "deviceIdentify不合法 确认已经注册device");
            throw new IllegalArgumentException();
        }
        return true;
    }

    private int realTimeStep;
    private float realTimeDisM;
    private int realTimeCal;

    private boolean updateRealTimeStep(ContentValues contentValues) {
        realTimeStep = contentValues.getAsInteger(PlugConst.kColStepCount);
        realTimeDisM = contentValues.getAsFloat(PlugConst.kColDistanceM);
        realTimeCal = contentValues.getAsInteger(PlugConst.kColCalorie);
        return true;
    }
}
