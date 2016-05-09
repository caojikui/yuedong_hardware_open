package com.yuedong.yue.open.hardware;
interface YDHardwarePlugInterface{
//  绑定硬件设备后需要想用用注册
    String registerDevice(String deviceId, String plugName);
//  解除绑定设备后要注销
    void unRegisterDevice(String deviceIdentify, String plugName);
//  向应用注册Action通知回调 具体值参照YDHardwarePlugConst
    void registerServiceAction(String intentUri, String action, String deviceIdentify);
//    获取到用户信息的json 格式的字符串
    String userInfoJsonStr();
}