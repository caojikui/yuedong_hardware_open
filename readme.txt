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
