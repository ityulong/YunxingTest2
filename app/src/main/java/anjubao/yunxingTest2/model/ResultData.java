package anjubao.yunxingTest2.model;

/**
 * {
 *     "total": 0,
 *     "result": "0",
 *     "message": "操作成功",
 *     "datetime": "2019-03-18T07:18:11.075+0000",
 *     "data": {
 *         "accid": "0ed8dfeb1db4468fa1830b1b1d433f8f",
 *         "token": "24d52e8fc5e5427f4f746bf670a35899"
 *     }
 * }
 */
public class ResultData<T> {
    private int total;
    private int result;
    private String message;
    private String datetime;
    private T data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
