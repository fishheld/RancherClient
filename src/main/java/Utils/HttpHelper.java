package Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpHelper {
    /**
     * 创建连接
     * @param baseUrl
     * @param path
     * @return
     */
    public static HttpURLConnection connect(String baseUrl, String path) {
        try {
            URL url = new URL(baseUrl + path);
            HttpURLConnection res = (HttpURLConnection) url.openConnection();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    /**
     * 断开连接
     * @param conn
     * @return
     */
    public static boolean disConnect(HttpURLConnection conn) {
        try {
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 发送请求
     * @param conn
     * @param method
     * @param properties
     * @return
     */
    public static boolean request(HttpURLConnection conn, String method, Map<String, String> properties) {
        try {
            //设置请求方法
            conn.setRequestMethod(method);
            //设置请求属性
            for (String e : properties.keySet()) {
                conn.setRequestProperty(e, properties.get(e));
            }
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("http请求失误");
                // 处理错误响应
                System.out.println("Error response: " + responseCode + " in connect()");
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取响应内容.
     * @param conn
     * @return 响应内容的String。
     */
    public static String getResponse(HttpURLConnection conn) {
        //读取响应数据
        StringBuffer response = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    //写入请求内容
}
