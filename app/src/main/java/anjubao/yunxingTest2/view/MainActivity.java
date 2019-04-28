package anjubao.yunxingTest2.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import anjubao.yunxingTest2.R;
import anjubao.yunxingTest2.cloud.HttpCallbackListener;
import anjubao.yunxingTest2.cloud.Okhttp3Utils;
import anjubao.yunxingTest2.constants.AVChatExitCode;
import anjubao.yunxingTest2.controller.IYunApiController;
import anjubao.yunxingTest2.controller.YunApiController;
import anjubao.yunxingTest2.mInterface.RequestCallback;
import anjubao.yunxingTest2.model.DeviceInfo;
import anjubao.yunxingTest2.model.ResultData;
import anjubao.yunxingTest2.model.UserToken;
import anjubao.yunxingTest2.util.Preferences;
import anjubao.yunxingTest2.util.SimpleAVChatStateObserver;
import anjubao.yunxingTest2.util.ToastHelper;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnOperation;
    private View vStatusBar;
    private TextView notifyBarText;

    protected AVChatData avChatData;
    private YunApiController mController;


    public static final int STATUS_NO_LOGIN = 1;
    public static final int STATUS_LOGIN = 2;
    public static final int STATUS_CALL = 3;
    public static final int STATUS_CALLING = 4;

    private int currentStatus = STATUS_NO_LOGIN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mController = new IYunApiController();
        initView();
        noLoginStatus();

        if (getLoginInfo() != null) {
            loginStatus();
//            doLogin(getLoginInfo());
        } else {
            DeviceInfo deviceInfo = DeviceInfo.getTestInstance();
            Map<String, String> map = new HashMap<>();
            map.put("userName", "android");
            map.put("userSource", "1");
            map.put("userType", "2");
            map.put("robotId", deviceInfo.getRobotId());
            map.put("parkId", deviceInfo.getParkId());
            map.put("parkName", deviceInfo.getParkName());
            Okhttp3Utils.getInstance().login(Okhttp3Utils.APPLY_ACCOUNT, map, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d("apply_account", "onFinish: " + response);

                    Type objectType = new TypeToken<ResultData<UserToken>>() {
                    }.getType();
                    ResultData<UserToken> tokenResult = new Gson().fromJson(response, objectType);
                    if (tokenResult.getResult() == 0) {
                        UserToken token = tokenResult.getData();
                        LoginInfo info = new LoginInfo(token.getAccid(), token.getToken());
//                        LoginInfo info = new LoginInfo("40b4bd260dfc41669b50789d95d1e44e", "418e87f4d274219744e8d7c5a558c7d8");
                        doLogin(info);
                    }
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onError(final String errorInfo) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            });

        }
        registerObserves(true);

    }

    private LoginInfo getLoginInfo() {
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private void doLogin(LoginInfo info) {
        mController.doLogin(info, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                saveLoginMsg(loginInfo);
                loginStatus();
            }

            @Override
            public void onFailed(int code) {
                if (code == 302 || code == 404) {
                    ToastHelper.showToast(MainActivity.this, "帐号或密码错误");
                } else {
                    ToastHelper.showToast(MainActivity.this, "登录失败: " + code);
                }
            }

            @Override
            public void onException(Throwable var1) {

            }
        });
    }

    private void saveLoginMsg(LoginInfo loginInfo) {
        Preferences.saveUserAccount(loginInfo.getAccount());
        Preferences.saveUserToken(loginInfo.getToken());
    }

    /************************************* 界面操作 ***************************************************************/

    private void initView() {
        tvStatus = findViewById(R.id.tv_call_status);
        vStatusBar = findViewById(R.id.ly_status_bar);
        notifyBarText = findViewById(R.id.status_desc_label);
        btnOperation = findViewById(R.id.btn_operation);
        btnOperation.setOnClickListener(mListener);
    }

    private void loginStatus() {
        currentStatus = STATUS_LOGIN;
        tvStatus.setText("空闲中");
        btnOperation.setText("呼叫主机");
        btnOperation.setEnabled(true);
    }

    private void noLoginStatus() {
        currentStatus = STATUS_NO_LOGIN;
        btnOperation.setEnabled(false);
    }

    private void callStatus() {
        currentStatus = STATUS_CALL;
        tvStatus.setText("呼叫中");
        btnOperation.setText("取消呼叫");
    }

    private void callingStatus() {
        currentStatus = STATUS_CALLING;
        tvStatus.setText("通话中");
        btnOperation.setText("挂断");
    }


    /************************************* 通话操作 ***************************************************************/

    //点击拨通按钮
    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (currentStatus) {
                case STATUS_LOGIN:
                    preCall();
                    break;
                case STATUS_CALL:
                case STATUS_CALLING:
                    manualHangUp(AVChatExitCode.HANGUP);
            }


        }
    };

    //呼叫之前获取对方账号信息
    private void preCall() {
        //获取在线客服
        String account = Preferences.getUserAccount();
        Map<String, String> map = new HashMap<>();
        map.put("sourceAccid", account);
        Okhttp3Utils.getInstance().sendPostResquest(Okhttp3Utils.GET_CUSTOM, map, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Type objectType = new TypeToken<ResultData<UserToken>>() {
                }.getType();
                ResultData<UserToken> tokenResult = new Gson().fromJson(response, objectType);
                if (tokenResult.getResult() == 0) {
                    executeCall(tokenResult.getData().getAccid(),DeviceInfo.getTestInstance());
                } else if (tokenResult.getResult() == 104) {
                    ToastHelper.showToast(MainActivity.this, "没有在线客服");
                }
            }

            @Override
            public void onError(Exception e) {
                ToastHelper.showToast(MainActivity.this, "请求不到服务器");
            }

            @Override
            public void onError(String errorInfo) {
                ToastHelper.showToast(MainActivity.this, errorInfo);

            }
        });
    }


    private void executeCall(String account,DeviceInfo deviceInfo) {
        mController.doCall(account,deviceInfo, new RequestCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                avChatData = data;
                callStatus();
                Log.d("MainActivity", "发起呼叫成功！！");
            }

            @Override
            public void onFailed(int code) {
                if (code == ResponseCode.RES_FORBIDDEN) {
                    Toast.makeText(MainActivity.this, "暂无权限，请开通音视频服务", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "发起通话失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });

    }

    /************************************* 监听状态操作 ***************************************************************/


    private void registerObserves(boolean register) {
        AVChatManager.getInstance().observeAVChatState(avchatStateObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);

    }

    // 通话过程状态监听
    private SimpleAVChatStateObserver avchatStateObserver = new SimpleAVChatStateObserver() {

        @Override
        public void onUserLeave(String account, int event) {
            Log.d("MainActivity", "通话时收到对方用户离线通知");
            manualHangUp(AVChatExitCode.HANGUP);
        }


        @Override
        public void onCallEstablished() {
            callingStatus();
            Log.d("MainActivity", "通讯会话建立成功！");

        }
    };

    // 通话过程中，收到对方挂断电话
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>() {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo) {
            Log.d("MainActivity", "通话时收到对方挂断电话通知");
            if (avChatData != null && avChatData.getChatId() == avChatHangUpInfo.getChatId()) {
                hangUpByOther(AVChatExitCode.HANGUP);

            }

        }
    };

    // 呼叫时，被叫方的响应（接听、拒绝、忙）
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>() {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo) {
            Log.d("MainActivity", "呼叫时时收到对方状态：" + ackInfo.getEvent());
            if (avChatData != null && avChatData.getChatId() == ackInfo.getChatId()) {
                if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
                    hangUpByOther(AVChatExitCode.PEER_BUSY);
                } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
                    hangUpByOther(AVChatExitCode.REJECT);
                } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
                }
            }
        }
    };


    /**
     * 用户状态变化
     */
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code == StatusCode.NET_BROKEN) {
                vStatusBar.setVisibility(View.VISIBLE);
                notifyBarText.setText("当前网络不可用");
            } else if (code == StatusCode.UNLOGIN) {
                vStatusBar.setVisibility(View.VISIBLE);
                notifyBarText.setText("未登录");
            } else if (code == StatusCode.CONNECTING) {
                vStatusBar.setVisibility(View.VISIBLE);
                notifyBarText.setText("连接中...");
            } else if (code == StatusCode.LOGINING) {
                vStatusBar.setVisibility(View.VISIBLE);
                notifyBarText.setText("登录中...");
            } else {
                vStatusBar.setVisibility(View.GONE);
            }
        }
    };

    // 主动挂断
    private void manualHangUp(int exitCode) {
        loginStatus();
        mController.hangUp(exitCode);
    }

    // 被对方挂断
    private void hangUpByOther(int exitCode) {
        loginStatus();
        if (exitCode == AVChatExitCode.PEER_BUSY) {
            mController.hangUp(AVChatExitCode.HANGUP);
//            finish();
        } else {
            mController.onHangUp(exitCode);
        }
    }

}
