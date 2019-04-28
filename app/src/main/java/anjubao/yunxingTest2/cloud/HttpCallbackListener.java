package anjubao.yunxingTest2.cloud;

/**
 * Created by t_jm on 2017/6/15.
 * 类说明
 */

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

    void onError(String errorInfo);
}
