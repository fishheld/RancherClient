import Utils.HttpHelper;
import Utils.LabelCoder;
import models.Cluster;
import models.Node;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class RancherClient {
    //成员变量
    String apiEndpoint = "https://222.201.144.187";
    String accessKey = "token-plmwl";
    String secretKey = "8x5pvj2vbxtrztqhwrctwhhjdtvfkjjfpkvlbnv5gxwzcqzv9946nz";
    HttpURLConnection conn;
    List<Node> nodeList;
    List<Cluster> clusterList;

    Map<String, Integer> labelKeys;
    int labelKeysCount = 0;

    //成员方法
    RancherClient() {
        //更新顺序固定，不能改变，具有前后依赖关系。
        this.updateNodes(); //更新所有Node。不包括labelCode。
        this.updateLabelKeys(); //更新全局标签集合。
        this.updateNodeLabelCode(); //更新所有Node的labelCode。
        this.updateClusters();  //更新所有Cluster。包括集群里的相应节点。
    };

    /**
     * 更新Client的节点列表
     * @return
     */
    boolean updateNodes() {
        nodeList = new ArrayList<>();
        //创建连接
        conn = HttpHelper.connect(apiEndpoint, "/v3/nodes");
        Map<String, String> property = new HashMap<>();
        property.put("Authorization", "Bearer "+accessKey+":"+secretKey);
        HttpHelper.request(conn, "GET", property);
        String response = HttpHelper.getResponse(conn);
        //处理响应数据(JSON)
        try {
            JSONObject json = new JSONObject(response);
            //获取node的json对象数组
            JSONArray nodes = json.getJSONArray("data");
            for (int i = 0; i < nodes.length(); i++) {
                //获取单个node的json对象
                JSONObject node = nodes.getJSONObject(i);
                //获取单个node信息
                String nodeName = node.getString("nodeName");
                String clusterId = node.getString("clusterId");
                JSONObject labels = node.getJSONObject("labels");
                //获取多标签信息
                Map<String, String> labelMap = new HashMap<>();
                Iterator<String> it = labels.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    labelMap.put(key, labels.getString(key));
                }
                Node aNode = new Node(nodeName, clusterId, labelMap);
                this.nodeList.add(aNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //断开连接
        HttpHelper.disConnect(conn);
        return true;
    };

    /**
     * 查询所有节点
     * @return
     */
    List<Node> getNodes() {
        this.updateNodes();
        return this.nodeList;
    }

    /**
     * 根据节点名称查询单个节点信息
     * @param nodename
     * @return
     */
    Node getNode(String nodename) {
        Node res =null;
        for (Node e : nodeList) {
            if (e.nodeName.equals(nodename)) {
                res = new Node(e);
                break;
            }
        }
        return res;
    };

    /**
     * 更新Client中的集群列表
     * @return
     */
    boolean updateClusters() {
        clusterList = new ArrayList<Cluster>();

        //创建连接
        conn = HttpHelper.connect(apiEndpoint, "/v3/clusters");
        Map<String, String> property = new HashMap<>();
        property.put("Authorization", "Bearer "+accessKey+":"+secretKey);
        HttpHelper.request(conn, "GET", property);
        String response = HttpHelper.getResponse(conn);

        //处理响应数据(JSON)
        try {
            JSONObject json = new JSONObject(response);
            //获取cluster的json对象数组
            JSONArray clusters = json.getJSONArray("data");
            for (int i = 0; i < clusters.length(); i++) {
                //获取单个cluster的json对象
                JSONObject cluster = clusters.getJSONObject(i);
                //获取单个cluster信息
                String name = cluster.getString("name");
                String id = cluster.getString("id");
                int nodeCount = cluster.getInt("nodeCount");
                Cluster aCluster = new Cluster(name, id, nodeCount);
                //将属于本集群的节点加入本集群
                for (Node e : nodeList) {
                    if (e.clusterId.equals(id)) {
                        aCluster.nodeList.add(e);
                        //加入的节点的标签组合编码要输入布隆过滤器
                        aCluster.bloom.put(e.labelCode);
                    }
                }
                //将集群加入集群列表
                clusterList.add(aCluster);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //断开连接
        HttpHelper.disConnect(conn);
        return true;
    };

    /**
     * 查询所有集群
     * @return
     */
    List<Cluster> getClusters() {
        if (this.updateClusters())
            return clusterList;
        else
            System.out.println("Update cluster failed!");
            return null;
    }

    /**根据集群ID查询单个集群信息
     * @param id
     * @return
     */
    Cluster getCluster(String id) {
        for (Cluster e : clusterList) {
            if (e.id.equals(id))
                return e;
        }
        return null;
    };

    /**
     * 更新Client里的labeKey列表，即整个系统所拥有的标签集合
     * @return
     */
    boolean updateLabelKeys() {
        labelKeys = new HashMap<>();
        for (Node e : nodeList) {
            for (String key : e.labels.keySet()) {
                if (labelKeys.containsKey(key)) {
                    continue;
                } else {
                    labelKeys.put(key, this.labelKeysCount++);
                }
            }
        }
        return true;
    };

    /**
     * 更新每个Node对象的标签组合编码
     * @return
     */
    boolean updateNodeLabelCode() {
        for (Node e : nodeList) {
            String tmp = LabelCoder.encodeLabelCombination(e.labels, labelKeys);
            e.labelCode = tmp;
        }
        return true;
    }
}
