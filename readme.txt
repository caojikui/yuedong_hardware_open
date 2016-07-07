注意!!!
硬件插件 基本思想及交互指南 参照悦动圈开放平台SDK文档
一切程序开发都使用Android studio及gradle

YDApplication,SdkApplication 是demo示例,但是 只是一个开发原型, 在开发初期可以通过修改YDApplication,来快速调试程序
具体使用参照Demo指南

当使用YDApplication, SdkApplication调试通过之后, 需要使用插件化打包方式打包,请参照文档 插件打包文档 打包完成后联系悦动开发人员进行测试验收,通过后会登录悦动圈硬件开放平台


从第三方App直接跳转到悦动圈某个插件主界面:
代码

    private void tryJumpYDPlugActivity() {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.yuedong.sport", "com.yuedong.yue.open.hardware.ui.ActivityHardwareHome");
            intent.putExtra("plug_name", "demo");
//            TODO 请添入正确插件包名 才能跳转
            startActivity(intent);
        } catch(Throwable t) {
            Toast.makeText(this, "请安装新版悦动圈", Toast.LENGTH_SHORT).show();
        }
    }


二维码扫描
        private static final int kReqScanCode = 27;
    private void openActivityScan() {
        Intent intent = new Intent();
        intent.setClassName(PlugConst.kPlugServicePkg, "com.yuedong.yuebase.ui.ActivityScanCode");
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
