package services;

import Utils.HttpHelper;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Operations {
    //
    HttpURLConnection conn = null;
    String token;
    URL url = null;
    Map<String, String> properties;

    //
    public Operations(String token) {
        this.token = token;
        properties = new HashMap<>();
        properties.put("Authorization", "Bearer "+token);
    }

    /**
     * 执行查询操作
     * @param url
     * @return 返回查询的响应String
     */
    public String query(String url) {
        try {
            this.url = new URL(url);
            this.conn = (HttpURLConnection) this.url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String res = HttpHelper.getResponseString(HttpHelper.doGet(conn, properties));
        HttpHelper.disConnect(conn);
        return res;
    }

    /**
     * 执行新建资源操作
     * @param url
     * @param body
     * @return 返回操作成功
     */
    public boolean create(String url, String body) {
        properties.put("Content-Type", "application/json");
        try {
            this.url = new URL(url);
            this.conn = (HttpURLConnection) this.url.openConnection();
            this.conn = HttpHelper.doPost(conn, properties, body);
            HttpHelper.disConnect(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
