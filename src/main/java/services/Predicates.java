package services;

import models.Cluster;
import models.Node;
import models.Pod;

import java.util.ArrayList;
import java.util.List;

public class Predicates {

    List<Node> predicatePod(Pod pod, List<Cluster> clusters) {
        List<Node> res = new ArrayList<>();
        //访问集群布隆过滤器，过滤集群
        for (Cluster e : clusters) {
            //含有可行节点的集群。
            if (e.bloomContains(pod.labelCode)) {
                //遍历集群里每个节点。
                for (Node n : e.nodeList) {
                    //标签组合编码相同的节点。
                    if (n.labelCode.equals(pod.labelCode)) {
                        boolean work = true;
                        //遍历比较节点与Node的标签value。
                        for (String key : n.labels.keySet()) {
                            if (!n.labels.get(key).equals(pod.labels.get(key))){
                                work = false;
                                break;
                            }
                        }
                        if (work) {
                            res.add(n);
                        }
                    }
                }
            }
        }
        return res;
    };

}
