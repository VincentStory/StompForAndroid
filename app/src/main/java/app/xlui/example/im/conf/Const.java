package app.xlui.example.im.conf;

public class Const {
    public static final String TAG = "xlui==";
    public static final String placeholder = "placeholder";

    /**
     * <code>im</code> in address is the endpoint configured in server.
     * If you are using AVD provided by Android Studio, you should uncomment the upper address.
     * If you are using Genymotion, nothing else to do.
     * If you are using your own phone, just change the server address and port.
     */
    // private static final String address = "ws://10.0.2.2:8080/im/websocket";
    public static final String address = "ws://192.168.0.101:6551/ws/websocket";

    public static final String broadcast = "/req/kline/1/BTC/USDT/1/1";
    //	public static final String broadcastResponse = "/market/BTC/USDT/1";
//    public static final String broadcastResponse = "/user/103/history/trade";
    public static final String broadcastResponse = "/market/trade";

    // replace {@code placeholder} with group id
    public static final String group = "/group/" + placeholder;
    public static final String groupResponse = "/g/" + placeholder;
    public static final String chat = "/chat";
    // replace {@code placeholder} with user id
    public static final String chatResponse = "/user/" + placeholder + "/msg";
}
