package anjubao.yunxingTest2.controller;

import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.model.AVChatData;

import anjubao.yunxingTest2.mInterface.RequestCallback;
import anjubao.yunxingTest2.model.DeviceInfo;

public interface YunApiController {
    void doLogin(LoginInfo info, RequestCallback<LoginInfo> callback);
    void doCall(String account, DeviceInfo info, RequestCallback<AVChatData> callback);
    void hangUp(int type);
    void onHangUp(int exitCode);

}
