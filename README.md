# 悦动圈 硬件开放平台SDK
## 请把该文档完整阅读之后再提问题,真的,请读读文档吧!
##开发工具
Android Studio
## 综述
硬件厂商提供一个插件, 插件运行在悦动圈App中
## 插件都需要做什么?
插件需要进行与硬件的连接,数据交互,以及其它个性化操作
## 悦动圈都提供什么?
悦动圈提供插件列表,允许用户按需下载安装插件,以及直接跳转到购买链接 
悦动圈提供入口,可以跳转到插件的页面, 执行插件自己的逻辑  
悦动圈提供硬件数据的保存读取以及同步的支持
## 概念说明
device_id:硬件设备唯一标识,相同硬件device_id应该保持一致  
user_id:用户唯一标识  
device_identify:悦动圈根据插件提供的device_id结合当前登录用户生成的唯一标识, 同一用户同一硬件生成的device_identify唯一
## 插件与悦动圈的交互接口
### AIDL接口
<pre><code>
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
</code></pre>
### 数据接口ContentProvider
<pre><code>
//    contentProvider相关
    public static final String AUTOHORITY = "com.yuedong.sport.yue.open.hardware";

    //计步kColId,kColDeviceIdentify,kColStepCount,kColStartTSec,kColEndTSec,kColDistanceM, kColCalorie

    //实时步数
    kColDeviceIdentify, kColStepCount, kColDistanceM, kColCalorie

    //睡眠部分
    kColId,kColDeviceIdentify,kColStartTSec,kColEndTSec, kColSleepSection

    //心率
    kColId,kColDeviceIdentify,kColHeartRate,kColTimeSec

    //智能秤
    kColId,kColDeviceIdentify,kColTimeSec, kColWeightG,
    kColBodyFatPercentage, kColBodyMusclePercentage, kColBodyMassIndex
    kColBasalMetabolismRate,kColBodyWaterPercentage, kColExtra
    </code></pre>
支持插入及查讯,不过要求查讯及插入都需要明确指定device_identify,如果没有指定,或者使用错误的device_identify,都是会执行失败的
## 计步分段数据要求
为了悦动圈对各个设备（不同硬件及手机）计步数据的合并去重，我们要求智能硬件上报来的数据按照15分钟一段，每天96段的标准进行数据分段
## 插件具体应该执行一个什么样的流程呢?
### 第一次开启插件
* 跳转到插件界面, 同时会传递当前用户的user_id, 插件发现没有任何硬件绑定, 这时候就是应该引导用户绑定硬件
* 绑定硬件后,通过AIDL接口向悦动圈注册设备,取到device_identify之后与user_id一起保存下来(建议按照user_id保存)
* 注册其它自己需要的Action  



###已经存在绑定的硬件后
* 跳转到插件界面,应该先检查user_id是否与之前保存的一致, 不一致走第一次开启插件,新绑定流程
* 确认是同一个用户后, 展示device_identify对应硬件的数据(从悦动圈读取), 以及执行与硬件交互,获取数据,保存到悦动圈

## demo使用说明
因为插件是运行在悦动圈内的,如果开发过程中也使用与线上同样的方式的话速度会非常忙,所以专门实现了demo,来模拟悦动圈的行为,提供给一个快速开发测试的环境  
里面两个工程都是Android Studio工程  
其中YDApplication 是一个简单的客户端，插件与悦动交互的代码这里面都包含了，主要用于方便调试硬件插件 可以认为是一个简版的悦动圈  
SdkApplication 是一个简单的插件示例  
DataHelper 对硬件数据写入做了简单的封装，数据读取  

自己实现一个类似SDKApplication的程序  
修改YDApplication工程中YDActivity 文件中  
SDK_APPLICATION_PACKAGE_NAME  
SDK_APPLICATION_SDkActivity_NAME  
两个值对应到自己的包名及对应插件入口Activity名字

## Action说明
注册了设备之后 就可以注册需要用到的Action,当对应Action产生的时候悦动圈会以此分发Action  
同时为了省电及统一管理, 新的短信电话通知也有悦动圈分发给插件
支持的Action如下  

    public static final String kActionWakeUp = "wake_up";
    //    应用会定时发送该action 保持插件service长时间存活
    public static final String kActionKeepAlive = "keep_alive";
    //    应用会在蓝牙设备状态发生变化后广播该Action
    public static final String kActionBluetoothStatusChanged = "bluetooth_status_changed";
    //    应用请求插件暂时释放蓝牙资源
    public static final String kActionReleaseBluetooth = "release_bluetooth";
    //    蓝牙状态改变时 intent中的另一个key 对应一个boolean 表示蓝牙是否可用
    
    public static final String kActionPhoneNewCallIn = "new_call";
    public static final String kActionPhoneNewSMS = "new_sms";
    public static final String kActionNewNotification = "new_notification";
    
kActionWakeUp 在应用启动主界面的时候发送  
kActionKeepAlive 悦动圈会定时发送  
kActionBluetoothStatusChanged  蓝牙状态发生改变
kActionReleaseBluetooth  要求插件释放蓝牙  
kActionPhoneNewCallIn 新的电话  
kActionPhoneNewSMS 新短信  
kActionNewNotification 新的notification    
	
	public static final String kBluetoothStatus = "bluetooth_status";
	// 对应蓝牙状态 boolean值
	public static final String kKeyNotificationPkgName = "package_name";
	// 对应notification的包名
    public static final String kKeyExtras = "extras";
    // notification 中的 ticker_text
    public static final String kKeyNotificationTickerText = "ticker_text";
    // 短信内容
    public static final String kKeySmsContent = "content";
    // 短信发送者
    public static final String kKeySmsSender = "sender";

###来电Action格式
    "action":"new_call",	PlugConst.kActionKey:PlugConst.kActionPhoneNewCallIn
    "extras": Bundle类型数据，具体内容为 android.intent.action.PHONE_STATE广播对应intent中的extras,
    	主要key有TelephonyManager.EXTRA_INCOMING_NUMBER,TelephonyManager.EXTRA_STATE 等
    	
###短信Action格式
     "action":"new_sms", 	PlugConst.kActionKey:PlugConst.kActionPhoneNewSMS
     "content":"短信内容",	PlugConst.kKeySmsContent:
     "sender":"发送者"		PlugConst.kKeySmsSender:
     "extras": Bundle类型数据，短信其他内容
     
###notifation格式
      "action":"new_notification", 	PlugConst.kActionKey:PlugConst.kActionNewNotification
      "package_name":"pkg",		PlugConst.kKeyNotificationPkgName
      "ticker_text":"",			PlugConst.kKeyNotificationTickerText
      "extras":Bundle类型		PlugConst.kKeyExtras 具体内容格式参照Android开发文档 Notification.extras

## support库的使用
因为悦动圈内已经包含了okhttp,Fresco等库,同时插件中也存在需要使用网络操作与图片使用的场景,所以封装出support库,提供给插件直接使用Fresco,以及一个很简单的对网络操作的封装库  
Fresco 可以直接使用(不能在xml中使用attr)
NetWork是对基本的网络操作的封装
<pre><code>
	public enum HttpMethod {
        kHttpPost,
        kHttpGet,
        kHttpPut,
        kHttpPatch,
        kHttpDelete
    }  
    public abstract NetCall asyncDo(HttpMethod method, String url, Map<String, String> params, Map<String, String> headers, NetWorkCallback callback);
    
    public NetCall asyncDo(HttpMethod method, String url, Map<String, String> params, NetWorkCallback callback) {
        return asyncDo(method, url, params, null, callback);
    }
    
    public NetCall asyncGet(String url, Map<String, String> params, NetWorkCallback callback) {
        return asyncDo(HttpMethod.kHttpGet, url, params, null, callback);
    }

    public NetCall asyncPost(String url, Map<String, String> params, NetWorkCallback callback) {
        return asyncDo(HttpMethod.kHttpPost, url, params, null, callback);
    }
    private static NetWork sInstance;
    public static NetWork netWork() 
    
</code></pre>
AccountInfo 提供了一些关于帐户的部分信息
<pre><code>
	public abstract NetCall queryOpenId(String appId, NetWorkCallback callback);
    public abstract long uid();
    public abstract String avatarUrl();
    public static AccountInfo instance()
</code></pre>
使用support库而不是自己引入新的网络库可以有效降低插件的大小

##打包说明
当使用YDApplication, SdkApplication调试通过之后, 需要使用插件化打包方式打包,请参照文档 插件打包文档 打包完成后联系悦动开发人员进行测试验收,
通过后插件就会登录悦动圈硬件开放平台  
1. 要求gradle使用buildtools 23.0.2  
2. 使用aapt目录中的aapt 替换sdk中buildtools中23.0.2中的aapt

按照如下配置插件gradle.build文件

	android {
   		compileSdkVersion 23
    	buildToolsVersion "23.0.2"
    	defaultConfig {
        	applicationId “com.acdd.testapp2" //自己的包名
	        minSdkVersion 14
    	    targetSdkVersion 23
        	versionCode 1
	        versionName "1.0"
    }

    buildTypes {
        unsigned {
            minifyEnabled true
           zipAlignEnabled true
	proguardFiles getDefaultProguardFile('proguard-android.txt') , 'proguard-rules.pro'
            signingConfig null
        	}
    }
    productFlavors {
        ydopen {
            aaptOptions.additionalParameters '--ACDD-resoure-id', '0x51' //这个值 每个插件不一样需要向悦动申请       
        }
        normal {
        }
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile project(‘:sdk')
        provided files("main.jar")  //添加这个jar包,里面包涵如下五个包内容,不需要自己单独依赖如下包
        //compile 'com.android.support:appcompat-v7:22.1.1'
        //compile 'com.android.support:support-v4:22.1.1'
        //compile 'com.android.support:recyclerview-v7:22.1.1'
        //compile ‘com.android.support:gridlayout-v7:22.1.1'
        //compile project(':support')
	}

可以代码混淆，不能做签名,发布的时候会使用悦动的证书做签名保证程序安全  
使用 gradle assembleUnsigned 命令打包没有签名的包, 代码混淆等 可以自行配置
其中main.jar下载链接http://7xjqqe.com1.z0.glb.clouddn.com/main.jar

## 其它支持
###第三方应用跳转到插件界面
当没有指定插件包名则跳转插件列表
如果指定了插件包名,如果该插件已经安装则跳转到插件界面,否则则跳转插件列表

	private void tryJumpYDPlugActivity() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.yuedong.sport", "com.yuedong.yue.open.hardware.ui.ActivityHardwareHome");
            intent.putExtra("plug_name", "demo");  //请添入正确插件包名 才能跳转 也可以不插入该参数则跳转插件列表
            startActivity(intent);
        } catch(Throwable t) {
            Toast.makeText(this, "请安装新版悦动圈", Toast.LENGTH_SHORT).show();
        }
    }
    
###二维码扫描支持
提供二维码扫描支持,调用Activity 扫描到的结果就会返回  
使用方法如下:

	private static final int kReqScanCode = 27;
    private void openActivityScan() {
        Intent intent = new Intent();
        intent.setClassName(PlugConst.kPlugServicePkg, "com.yuedong.yuebase.ui.code.ActivityScanCode");
        startActivityForResult(intent, kReqScanCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == kReqScanCode) {
            if(resultCode == RESULT_OK) {
                String code = data.getStringExtra("SCAN_RESULT");
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                Toast.makeText(this, "format:" + format + ", code:" + code, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
   
其中result format类型如下

    public static final Collection<String> PRODUCT_CODE_TYPES = list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "RSS_14");
    public static final Collection<String> ONE_D_CODE_TYPES =
            list("UPC_A", "UPC_E", "EAN_8", "EAN_13", "CODE_39", "CODE_93", "CODE_128",
                    "ITF", "RSS_14", "RSS_EXPANDED");
    public static final Collection<String> QR_CODE_TYPES = Collections.singleton("QR_CODE");
    public static final Collection<String> DATA_MATRIX_TYPES = Collections.singleton("DATA_MATRIX");
    
## 插件限制
1. 当前不支持在xml中使用自定义属性, 需要自己代码实现
2. 支持插件不发版更新,但是前提是,四大组件的名称没有新增及改变,有后续开发计划建议预先声明组件占位,插件第一发布依赖与悦动圈发版
3. 因为安全考虑,所有插件需要经过悦动圈签名,分发

## 发布需要的素材
1. 智能硬件详细描述
2. 硬件详情图片800*800 png
3. 智能硬件名称
4. 智能硬件简要描述
5. 智能硬件icon 132*132 png


## 传送门 ios版SDK
<https://github.com/ZoMinster/YDOpenHardware>

## 测试方法
拿到测试包之后，把自己插件按照要求打包，然后放于sd卡yuedong_test_plug目录（自己创建下）下，命名为test_plug.so  
具体位置 new File(Environment.getExternalStorageDirectory(), "yuedong_test_plug/test_plug.so");  
启动安装的悦动圈，登录之后，选择我，点击 智能硬件，然后 选择 测试，进入插件界面  
