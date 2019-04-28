package anjubao.yunxingTest2.mInterface;

public interface RequestCallback<T> {
    void onSuccess(T var1);

    void onFailed(int var1);

    void onException(Throwable var1);
}
