
/**
 * @ClassName RancherClientUtils
 * @Description 参考类
 * @Author Getech
 * @Date 2021/9/23 10:20
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gflourenco
 */
public class RancherClientUtils {

    /**
     *  bearer token
     */
    private String rancherBearerToken;

    /**
     * rancher地址
     */
    private String rancherAddress;

    /**
     *  项目地址前缀
     */
    private static String rancherProjects = "/v3/projects/";

    /**
     *  工作负载地址前缀
     */
    private static String rancherWorkloads = "/workloads/";

    /**
     *  节点地址前缀
     */
    private static String rancherNodes = "/v3/nodes/";

    /**
     * 默认构造方法
     */
    public RancherClientUtils() {
        this("cwp.com", "token-abc:123456");
    }

    /**
     * @Description 通过地址和 bearer token设置token
     * @Author  chengweiping
     * @Date   2021/9/23 14:17
     */
    public RancherClientUtils(String rancherAddress, String token) {
        this.rancherAddress = rancherAddress;
        this.rancherBearerToken = token;
    }


    /**
     *  新增工作负载
     */
    public JSONObject addWorkload(String projectId, JSONObject workloadAdd) throws Exception {
        String addWorkloadUrl="https://" + rancherAddress + rancherProjects + projectId +"/workload";
        return postDataGetJson(addWorkloadUrl, workloadAdd);
    }

    /**
     *  更新工作负载
     */
    public JSONObject updateWorkload(String projectId, String workloadId, JSONObject containerUpdt) throws Exception {
        JSONObject workloadObj = getWorkload(projectId, workloadId, true);

        try {

            for (int i = 0; i < workloadObj.getJSONArray("containers").length(); i++) {

                if (workloadObj.getJSONArray("containers").getJSONObject(i).get("name").equals(containerUpdt.get("name"))) {
                    workloadObj.getJSONArray("containers").getJSONObject(i).put("image", containerUpdt.get("image"));
                    if (containerUpdt.has("environment")) {
                        workloadObj.getJSONArray("containers").getJSONObject(i).put("environment", containerUpdt.get("environment"));
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }
        String updateWorkloadUrl="https://" + rancherAddress + rancherProjects + projectId + rancherWorkloads + "/" + workloadId;
        return putDataGetJson(updateWorkloadUrl, workloadObj);
    }

    /**
     * @Description 获取节点
     * @Author  chengweiping
     * @Date   2021/9/23 14:13
     */
    public JSONObject getNode(String nodeID) throws Exception {
        JSONObject returnObj = new JSONObject();

        try {

            JSONObject workloadObj = readJsonFromUrl("https://" + rancherAddress + rancherNodes + nodeID);
            returnObj.put("id", workloadObj.get("id"));
            returnObj.put("hostname", workloadObj.get("hostname"));
            returnObj.put("externalIpAddress", workloadObj.get("externalIpAddress"));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }
        return returnObj;
    }

    /**
     * @Description  获取工作负载,raw:true,返回原始数据
     * @Author  chengweiping
     * @Date   2021/9/23 14:14
     */
    public JSONObject getWorkload(String projectId, String workloadId, boolean raw) throws Exception {
        if (raw) {
            return readJsonFromUrl("https://" + rancherAddress + rancherProjects + projectId + rancherWorkloads + "/" + workloadId);
        }
        return getWorkload(projectId, workloadId);
    }

    /**
     * @Description 获取工作负载
     * @Author  chengweiping
     * @Date   2021/9/23 14:14
     */
    public JSONObject getWorkload(String projectId, String workloadId) throws Exception {
        JSONObject returnObj = new JSONObject();
        try {
            JSONObject workloadObj = readJsonFromUrl("https://" + rancherAddress + rancherProjects + projectId + rancherWorkloads + "/" + workloadId);
            returnObj.put("name", workloadObj.get("name"));
            returnObj.put("id", workloadObj.get("id"));
            returnObj.put("created", workloadObj.get("created"));
            returnObj.put("createdTS", workloadObj.get("createdTS"));
            returnObj.put("projectId", workloadObj.get("projectId"));
            /*  returnObj.put("daemonSetStatus", workloadObj.get("daemonSetStatus"));*/

            returnObj.put("state", workloadObj.get("state"));
            if (workloadObj.has("publicEndpoints")) {
                returnObj.put("publicEndpoints", workloadObj.get("publicEndpoints"));
            }
            JSONArray containersArr = workloadObj.getJSONArray("containers");
            JSONArray returnContainersArr = new JSONArray();

            for (int i = 0; i < containersArr.length(); i++) {
                JSONObject containerObj = containersArr.getJSONObject(i);
                JSONObject returnContainerObj = new JSONObject();
                if (containerObj.has("environment")) {
                    returnContainerObj.put("environment", containerObj.get("environment"));
                }
                if (containerObj.has("image")) {
                    returnContainerObj.put("image", containerObj.get("image"));
                }

                if (containerObj.has("ports")) {
                    returnContainerObj.put("ports", containerObj.get("ports"));
                }
                returnContainersArr.put(returnContainerObj);
            }
            returnObj.put("containers", returnContainersArr);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }
        return returnObj;
    }

    /**
     *  获取项目下的所有工作负载
     */
    public JSONArray getWorkloads(String projectId) throws Exception {
        JSONArray returnArr = new JSONArray();

        try {
            JSONObject json = readJsonFromUrl("https://" + rancherAddress + rancherProjects + projectId + rancherWorkloads);
            JSONArray dataArr = json.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                JSONObject returnObj = new JSONObject();
                returnObj.put("name", dataObj.get("name"));
                returnObj.put("created", dataObj.get("created"));
                returnObj.put("createdTS", dataObj.get("createdTS"));
                returnObj.put("id", dataObj.get("id"));

                returnArr.put(returnObj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }
        return returnArr;

    }

    /**
     * @Description  获取所有项目
     * @Author  chengweiping
     * @Date   2021/9/23 14:14
     */
    public JSONArray getProjects() throws Exception {
        JSONArray returnArr = new JSONArray();
        try {
            JSONObject json = readJsonFromUrl("https://" + rancherAddress + rancherProjects);

            JSONArray dataArr = json.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); i++) {
                JSONObject dataObj = dataArr.getJSONObject(i);
                JSONObject returnObj = new JSONObject();
                returnObj.put("name", dataObj.get("name"));
                returnObj.put("created", dataObj.get("created"));
                returnObj.put("createdTS", dataObj.get("createdTS"));
                returnObj.put("creatorId", dataObj.get("creatorId"));
                returnObj.put("description", dataObj.get("description"));
                returnObj.put("id", dataObj.get("id"));
                returnArr.put(returnObj);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw  ex;
        }
        return returnArr;

    }

    /**
     * @Description 读取记录
     * @Author  chengweiping
     * @Date   2021/9/23 14:15
     */
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    /**
     *  从url地址中获取json数据
     */
    public JSONObject readJsonFromUrl(String url) throws Exception {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URLConnection connection = new URL(url).openConnection();
            HttpsURLConnection httpConn = (HttpsURLConnection) connection;
            httpConn.setRequestProperty("Authorization", "Bearer " + rancherBearerToken);

            InputStream is = httpConn.getInputStream();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } catch (JSONException ex) {
                Logger.getLogger(RancherClientUtils.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                is.close();
            }
        } catch (NoSuchAlgorithmException | KeyManagementException | IOException ex) {
            Logger.getLogger(RancherClientUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    /**
     * @Description 通过put请求设置数据
     * @Author  chengweiping
     * @Date   2021/9/23 14:16
     */
    public JSONObject putDataGetJson(String url, JSONObject data) throws Exception{
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URLConnection connection = new URL(url).openConnection();
            HttpsURLConnection httpConn = (HttpsURLConnection) connection;

            httpConn.setRequestMethod("PUT");
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Authorization", "Bearer " + rancherBearerToken);
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(httpConn.getOutputStream());
            osw.write(data.toString());
            osw.flush();
            osw.close();
            InputStream is = httpConn.getInputStream();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } catch (JSONException ex) {
                Logger.getLogger(RancherClientUtils.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }
        return null;
    }


    /**
     * @Description 通过post请求设置数据
     * @Author  chengweiping
     * @Date   2021/9/23 14:16
     */
    public JSONObject postDataGetJson(String url, JSONObject data) throws Exception {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URLConnection connection = new URL(url).openConnection();
            HttpsURLConnection httpConn = (HttpsURLConnection) connection;

            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setRequestProperty("Authorization", "Bearer " + rancherBearerToken);
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter osw = new OutputStreamWriter(httpConn.getOutputStream());
            osw.write(data.toString());
            osw.flush();
            osw.close();
            InputStream is = httpConn.getInputStream();

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                JSONObject json = new JSONObject(jsonText);
                return json;
            } catch (JSONException ex) {
                ex.printStackTrace();
                throw ex;
            } finally {
                is.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw  ex;
        }
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            RancherClientUtils rancherClient = new RancherClientUtils();

            System.out.println("\nProjects");
            JSONArray projects = rancherClient.getProjects();
            for (int i = 0; i < projects.length(); i++) {
                System.out.println(projects.getJSONObject(i).toString());
            }

            System.out.println("\nWorkloads");
            JSONArray workloads = rancherClient.getWorkloads("c-v69ln:p-sgwmx");
            for (int i = 0; i < workloads.length(); i++) {
                System.out.println(workloads.getJSONObject(i).toString());
            }

            System.out.println("\nWorkload");
            JSONObject workload = rancherClient.getWorkload("c-v69ln:p-sgwmx", "deployment:dip-ims:data-service-job");
            System.out.println(workload.toString());

         /*   System.out.println("\nNode");
            JSONObject node = rancherClient.getNode("c-sm5kw:m-81dd8018f312");
            System.out.println(node.toString());*/

            System.out.println("\nWorkload Raw");
            JSONObject workloadRaw = rancherClient.getWorkload("c-v69ln:p-sgwmx", "deployment:dip-ims:data-service-job", true);
            System.out.println(workloadRaw.toString());

            System.out.println("\nUpdate Workload");
            JSONObject updateContainer = new JSONObject();

          /*  updateContainer.put("name", "data-service-job");
            updateContainer.put("image", "registry.getech.cn/dip/data-service-job-ims:2.3.34");
            JSONObject environment = new JSONObject();
            environment.put("VAR1", "false");
            environment.put("VAR2", "Teste222");
            updateContainer.put("environment", environment);
            System.out.println(rancherClient.updateWorkload("c-v69ln:p-sgwmx", "deployment:dip-ims:data-service-job", updateContainer).toString());
*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}