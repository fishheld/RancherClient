package services;

import models.Cluster;
import models.Node;
import models.Pod;

import java.util.List;

public class Scheduler {

    public boolean schedulePod(Pod pod, List<Cluster> clusters) {
        //筛选可用节点
        List<Node> canNodes = new Predicates().predicatePod(pod, clusters);
        if (canNodes.size() == 0)
            return false;

        //排序可用节点
        Node bestNode = new Priorities().prioriryPod(pod, canNodes);

        //Bing
        pod.nodeName = bestNode.nodeName;


        return false;
    }
}
