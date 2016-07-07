package com.yuedong.open.hardware.support.net;


/**
 * Created by virl on 7/7/16.
 */
public abstract class NetFile {
    public abstract boolean needDownload();
    public abstract void download();
    public abstract void delete();
    public abstract void cancelDownload();
}
