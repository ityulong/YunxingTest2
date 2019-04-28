package anjubao.yunxingTest2.model;

public class LoginEntity {
    /**
     * {
     * "total": 0,
     * "result": "0",
     * "message": "success",
     * "datetime": "2019-01-21T06:41:26.068+0000",
     * "data": {
     * "accid": "813a7ff9d8174652b14942402f8ed955",
     * "token": "57c112e16b60af8957e0adc7244e6a2a"
     * }
     * }
     */
    private int errorCode;
    private String errorMessage;
    private DataBean data;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * "accid": "813a7ff9d8174652b14942402f8ed955",
         * "token": "57c112e16b60af8957e0adc7244e6a2a"
         */
        private String accid;
        private String token;
        public String getAccid() {
            return accid;
        }

        public void setAccid(String accid) {
            this.accid = accid;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
