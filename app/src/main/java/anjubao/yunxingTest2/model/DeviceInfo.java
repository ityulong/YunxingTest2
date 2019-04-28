package anjubao.yunxingTest2.model;

public class DeviceInfo {

    /**
     * “robotId”:机器人编号
     * “ip”:机器人 IP
     * “portId”:出入口 ID
     * “portName”:出入口名称,
     * “parkId”:车场编号
     * “parkName”:车场名称
     * “parkAddr”:车场地址
     * “portDirect”（int）类型 0：出口 1：入口
     */
    private String robotId ="";
    private String parkId ="";
    private String parkName="";
    private String ip="";
    private String portId;
    private String portName;
    private String parkAddr;
    private Integer portDirect;


    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getParkAddr() {
        return parkAddr;
    }

    public void setParkAddr(String parkAddr) {
        this.parkAddr = parkAddr;
    }

    public Integer getPortDirect() {
        return portDirect;
    }

    public void setPortDirect(Integer portDirect) {
        this.portDirect = portDirect;
    }

    @Override
    public String toString() {
        return '{' +
                "\"robotId\":\"" + robotId + '\"' +
                ", \"parkId\":\"" + parkId + '\"' +
                ", \"parkName\":\"" + parkName + '\"' +
                ", \"ip\":\"" + ip + '\"' +
                ", \"portId\":\"" + portId + '\"' +
                ", \"portName\":\"" + portName + '\"' +
                ", \"parkAddr\":\"" + parkAddr + '\"' +
                ", \"portDirect\":\"" + (portDirect==0 ? "出口":"入口") + '\"' +
                '}';
    }


    /**
     *   "robotId": "测试41113",
     *   "parkId": "4401181001",
     *   "parkName": "安居宝科技园测试场",
     *   "ip": "192.168.41.117",
     *   "portId": "44011810011553583908PT",
     *   "portName": "出口117",
     *   "parkAddr": "广州市黄埔区起云路",
     *   "portDirect": "0"
     */

    public static DeviceInfo getTestInstance(){
        DeviceInfo info = new DeviceInfo();
        info.setRobotId("测试41113");
        info.setParkId("4401181001");
        info.setParkName("安居宝科技园测试场");
        info.setIp("192.168.41.117");
        info.setPortId("44011810011553583908PT");
        info.setPortName("出口117");
        info.setParkAddr("广州市黄埔区起云路");
        info.setPortDirect(0);
        return info;
    }
}
