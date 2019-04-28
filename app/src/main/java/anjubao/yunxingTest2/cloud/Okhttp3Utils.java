package anjubao.yunxingTest2.cloud;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Okhttp3Utils {

    //需要提供公司自己的调用接口

    public final static String SERVER_IP = "http://192.168.41.75:8090/yunxinim";
    public final static String TEST_IP = "";

    public final static String APPLY_ACCOUNT = TEST_IP + "";

    public final static String GET_CUSTOM = TEST_IP +"";

    private OkHttpClient mClient;
    private Handler mHandler;
    private static Okhttp3Utils okhttp3Utils;
    private static int maxLoadTimes = 3;
    private static int serversLoadTimes;

    private Okhttp3Utils() {
        mClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     *
     * @return
     */
    public static Okhttp3Utils getInstance() {
        if (okhttp3Utils == null) {
            synchronized (Okhttp3Utils.class) {
                if (okhttp3Utils == null) {
                    okhttp3Utils = new Okhttp3Utils();
                }
            }
        }
        return okhttp3Utils;
    }

    /**
     * 发送Post请求数据(json)
     * @param uri
     * @param map
     * @param httpCallbackListener
     */
    public void sendPostResquest(final String uri, final Map<String, String> map, final HttpCallbackListener httpCallbackListener) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject();
                    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                    Map.Entry<String, String> entry;
                    while (it.hasNext()) {
                        entry = it.next();
                        object.putOpt(entry.getKey(), entry.getValue());
                    }
                    String json = object.toString();
                    RequestBody requestBody = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url(uri)
                            .post(requestBody)
                            .build();

                    Response response = mClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        success(body,httpCallbackListener);
                    }else {
                        error(String.valueOf(response.code()),httpCallbackListener);
                    }
                } catch (final Exception e) {
                    error(e,httpCallbackListener);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //登录重连3次
    public void login(final String uri, final Map<String, String> map, final HttpCallbackListener httpCallbackListener) {
        try {
            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            serversLoadTimes = 0;
            JSONObject object = new JSONObject();
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            Map.Entry<String, String> entry;
            while (it.hasNext()) {
                entry = it.next();
                object.putOpt(entry.getKey(), entry.getValue());
            }
            String json = object.toString();
            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(uri)
                    .post(requestBody)
                    .build();
            mClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (e.getCause().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes )//如果超时并未超过指定次数，则重新连接
                        {
                        serversLoadTimes++;
                        Log.i("okhttp3util", "onFailure: " + serversLoadTimes);
                        mClient.newCall(call.request()).enqueue(this);
                    } else {
                        e.printStackTrace();
                        error("网络异常，请检查网络后重试",httpCallbackListener);
//                        httpCallbackListener.onError();
                    }
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    success(response.body().string(), httpCallbackListener);
//                    httpCallbackListener.onFinish(response.body().string());
                }
            });
        }catch(Exception e){
            error(e, httpCallbackListener);
//            e.printStackTrace();
        }
    }


    /******************************************* 回到主线程操作 ****************************************/

    private void success(final String resule, final HttpCallbackListener httpCallbackListener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                httpCallbackListener.onFinish(resule);
            }
        });
    }

    private void error(final Exception e, final HttpCallbackListener httpCallbackListener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                httpCallbackListener.onError(e);
            }
        });
    }

    private void error(final String errorInfo, final HttpCallbackListener httpCallbackListener) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                httpCallbackListener.onError(errorInfo);
            }
        });
    }
}
