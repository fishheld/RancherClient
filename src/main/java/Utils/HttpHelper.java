package Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    /*
    public static boolean request(HttpURLConnection conn, String method, Map<String, String> properties, String body) {
        try {
            //设置请求方法
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置请求属性
            for (String e : properties.keySet()) {
                conn.setRequestProperty(e, properties.get(e));
            }
            //设置请求体
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

//            conn.connect();
            //发送请求,并返回响应代码
            int responseCode = conn.getResponseCode();
            if (responseCode >= 400) {
                // 处理错误响应
                System.out.println("Http请求失误 code:" + responseCode + " in HttpHelper.request()");
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    */

    /**
     * 读取响应内容.
     * @param conn
     * @return 响应内容的String。
     */
    public static String getResponseString(HttpURLConnection conn) {
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

    public static HttpURLConnection doPost(HttpURLConnection conn, Map<String, String> properties, String body){
        try {
            //设置请求方法
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            //设置请求属性
            for (String e : properties.keySet()) {
                conn.setRequestProperty(e, properties.get(e));
            }
            //设置请求体
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            //发送请求,并返回响应代码
            int responseCode = conn.getResponseCode();
            if (responseCode >= 400) {
                // 处理错误响应
                System.out.println("Http请求失误 code:" + responseCode + " in HttpHelper.doPost()");
            }
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpURLConnection doGet(HttpURLConnection conn, Map<String, String> properties) {
        try {
            //设置请求方法
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //设置请求属性
            for (String e : properties.keySet()) {
                conn.setRequestProperty(e, properties.get(e));
            }
//            conn.connect();
            //发送请求,并返回响应代码
            int responseCode = conn.getResponseCode();
            if (responseCode >= 400) {
                // 处理错误响应
                System.out.println("Http请求失误 code:" + responseCode + " in HttpHelper.doGet()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
