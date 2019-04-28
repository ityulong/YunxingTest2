package anjubao.yunxingTest2.model;

/**
 * "data": {
 *  *         "accid": "0ed8dfeb1db4468fa1830b1b1d433f8f",
 *  *         "token": "24d52e8fc5e5427f4f746bf670a35899"
 *  *     }
 */
public class UserToken {
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
