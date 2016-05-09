package com.yuedong.ydapplication.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.yuedong.open.hardware.PlugConst;

/**
 * Created by LaiXiaodong
 * Date ： 2016/1/14.
 */
public class HardWareProvider extends ContentProvider {

    private DBLiteHelper dbLiteHelper = null;
    private SQLiteDatabase db = null;
    private static final UriMatcher sMatcher;

    public static final int STEP_TABLE_ITEM = 1;

    public static final int STEP_TABLE_ITEM_ID = 2;

    public static final int SLEEP_TABLE_ITEM = 3;

    private static final int SLEEP_TABLE_ITEM_ID = 4;

    private static final int HEART_RATE_TABLE_ITEM = 5;

    private static final int HEART_RATE_TABLE_ITEM_ID = 6;

    private static final int INTELLIGENT_SCALE_TABLE_ITEM = 7;

    private static final int INTELLIGENT_SCALE_TABLE_ITEM_ID = 8;

    static{
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableSteps, STEP_TABLE_ITEM);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableSteps +"/#",STEP_TABLE_ITEM_ID);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableSleep,SLEEP_TABLE_ITEM);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableSleep +"/#",SLEEP_TABLE_ITEM_ID);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableHeartRate,HEART_RATE_TABLE_ITEM);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableHeartRate +"/#",HEART_RATE_TABLE_ITEM_ID);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableIntelligentScale,INTELLIGENT_SCALE_TABLE_ITEM);
        sMatcher.addURI(PlugConst.AUTOHORITY, PlugConst.kTableIntelligentScale +"/#",INTELLIGENT_SCALE_TABLE_ITEM_ID);
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
            case STEP_TABLE_ITEM:
                return PlugConst.CONTENT_TYPE;
            case STEP_TABLE_ITEM_ID:
                return PlugConst.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = dbLiteHelper.getWritableDatabase();
        Cursor cursor = null;
        String id = null;
        switch (sMatcher.match(uri)) {
            case STEP_TABLE_ITEM:
                cursor = db.query(PlugConst.kTableSteps, projection, selection, selectionArgs, null, null, null);
                break;
            case STEP_TABLE_ITEM_ID:
                id = uri.getPathSegments().get(1);
                cursor = db.query(PlugConst.kTableSteps, projection, PlugConst.kColId +"="+id,selectionArgs, null, null, sortOrder);
                break;
            case SLEEP_TABLE_ITEM:
                cursor = db.query(PlugConst.kTableSleep, projection, selection, selectionArgs, null, null, null);
                break;
            case SLEEP_TABLE_ITEM_ID:
                id = uri.getPathSegments().get(1);
                cursor = db.query(PlugConst.kTableSleep, projection, PlugConst.kColId +"="+id,selectionArgs, null, null, sortOrder);
                break;
            case HEART_RATE_TABLE_ITEM:
                cursor = db.query(PlugConst.kTableHeartRate, projection, selection, selectionArgs, null, null, null);
                break;
            case HEART_RATE_TABLE_ITEM_ID:
                id = uri.getPathSegments().get(1);
                cursor = db.query(PlugConst.kTableHeartRate, projection, PlugConst.kColId +"="+id,selectionArgs, null, null, sortOrder);
                break;
            case INTELLIGENT_SCALE_TABLE_ITEM:
                cursor = db.query(PlugConst.kTableIntelligentScale, projection, selection, selectionArgs, null, null, null);
                break;
            case INTELLIGENT_SCALE_TABLE_ITEM_ID:
                id = uri.getPathSegments().get(1);
                cursor = db.query(PlugConst.kTableIntelligentScale, projection, PlugConst.kColId +"="+id,selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = dbLiteHelper.getWritableDatabase();
        long rowId;
        Uri noteUri = null;
        if (sMatcher.match(uri) == STEP_TABLE_ITEM){
            rowId = db.insert(PlugConst.kTableSteps,"",values);
            if(rowId>0){
                noteUri= ContentUris.withAppendedId(PlugConst.STEP_CALCULATION_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            }
        }else if(sMatcher.match(uri) == SLEEP_TABLE_ITEM){
            rowId = db.insert(PlugConst.kTableSleep,"",values);
            if(rowId>0){
                noteUri= ContentUris.withAppendedId(PlugConst.SLEEP_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            }
        }else if (sMatcher.match(uri) == HEART_RATE_TABLE_ITEM){
            rowId = db.insert(PlugConst.kTableHeartRate,"",values);
            if(rowId>0){
                noteUri= ContentUris.withAppendedId(PlugConst.HEART_RATE_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            }
        }else if(sMatcher.match(uri) == INTELLIGENT_SCALE_TABLE_ITEM){
            rowId = db.insert(PlugConst.kTableIntelligentScale,"",values);
            if(rowId>0){
                noteUri= ContentUris.withAppendedId(PlugConst.INTELLIGENT_SCALE_URI, rowId);
                getContext().getContentResolver().notifyChange(noteUri, null);
                return noteUri;
            }
        }
        throw new IllegalArgumentException("Unknown URI"+uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
