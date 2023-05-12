package services;

import models.Node;
import models.Pod;

import java.util.List;

public class Scheduler {

    boolean schedulePod(Pod pod) {
        //筛选可用节点
        List<Node> canNodes = new Predicates().predicatePod(pod);

        //排序可用节点
        Node bestNode = new Priorities().prioriryPod(pod, canNodes);

        //部署到节点上

        return false;
    }
}
