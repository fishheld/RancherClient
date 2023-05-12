package services;

import models.Node;
import models.Pod;

import java.util.List;

public class Priorities {
    /**
     * 重写 prioriryPod()方法实现自定义调度排序器
     * @param pod
     * @param nodes
     * @return
     */
    Node prioriryPod(Pod pod, List<Node> nodes) {
        return nodes.get(0);
    };
}
