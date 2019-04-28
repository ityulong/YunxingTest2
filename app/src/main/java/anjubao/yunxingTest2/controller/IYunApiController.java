package anjubao.yunxingTest2.controller;

import android.util.Log;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;

import anjubao.yunxingTest2.constants.AVChatExitCode;
import anjubao.yunxingTest2.mInterface.RequestCallback;
import anjubao.yunxingTest2.model.DeviceInfo;

public class IYunApiController implements YunApiController {

    private AVChatData avChatData;
    private AVChatCameraCapturer mVideoCapturer;

    @Override
    public void doLogin(LoginInfo info, final RequestCallback<LoginInfo> callback) {

        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(info);

        loginRequest.setCallback(new com.netease.nimlib.sdk.RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                callback.onSuccess(loginInfo);
            }

            @Override
            public void onFailed(int code) {
                callback.onFailed(code);
            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
            }
        });
    }

    //默认是视频通话
    @Override
    public void doCall(String account, DeviceInfo info, final RequestCallback<AVChatData> callback) {
        //附加字段

        AVChatManager.getInstance().enableRtc();


        if (mVideoCapturer == null) {
            mVideoCapturer = AVChatVideoCapturerFactory.createCameraCapturer();
        }
        AVChatManager.getInstance().setupVideoCapturer(mVideoCapturer);
        AVChatManager.getInstance().enableVideo();
        AVChatManager.getInstance().startVideoPreview();


        AVChatNotifyOption notifyOption = new AVChatNotifyOption();
        notifyOption.extendMessage = "extra_data";
        notifyOption.extendMessage = info.toString();
        notifyOption.forceKeepCalling = false;

        AVChatManager.getInstance().call2(account, AVChatType.VIDEO, notifyOption, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                avChatData = data;
                callback.onSuccess(data);
            }

            @Override
            public void onFailed(int code) {

                callback.onFailed(code);
                releaseVideo();

            }

            @Override
            public void onException(Throwable exception) {
                AVChatManager.getInstance().disableRtc();
                callback.onException(exception);
            }
        });

    }

    private void releaseVideo() {
            Log.d("controller","进入结束视频通话流程");
            AVChatManager.getInstance().stopVideoPreview();
            AVChatManager.getInstance().disableVideo();
    }

    @Override
    public void hangUp(int type) {
        releaseVideo();
        if ((type == AVChatExitCode.HANGUP || type == AVChatExitCode.PEER_NO_RESPONSE
                || type == AVChatExitCode.CANCEL || type == AVChatExitCode.REJECT) && avChatData != null) {
            AVChatManager.getInstance().hangUp2(avChatData.getChatId(), new AVChatCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }

                @Override
                public void onFailed(int code) {

                }

                @Override
                public void onException(Throwable exception) {
                }
            });
        }
        AVChatManager.getInstance().disableRtc();

    }

    // 收到挂断通知，自己的处理
    @Override
    public void onHangUp(int exitCode) {
        releaseVideo();
        AVChatManager.getInstance().disableRtc();

    }
}
