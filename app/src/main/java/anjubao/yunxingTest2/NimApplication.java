package anjubao.yunxingTest2;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

import anjubao.yunxingTest2.util.Preferences;

public class NimApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Preferences.init(this);

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), NimSDKOptionConfig.getSDKOptions(this));
        Log.d("进程初始化","进程进入onCreate()方法中");

        // ... your codes
        if (NIMUtil.isMainProcess(this)) {
            Log.d("进程初始化","主进程进入onCreate()方法中");

//            NimUIKit.init(this, buildUIKitOptions());

            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用

        }
    }

//    private UIKitOptions buildUIKitOptions() {
//        UIKitOptions options = new UIKitOptions();
//        // 设置app图片/音频/日志等缓存目录
//        options.appCacheDir = NimSDKOptionConfig.getAppCacheDir(this) + "/app";
//        return options;
//    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }



}
