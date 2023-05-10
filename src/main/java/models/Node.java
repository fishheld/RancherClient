package models;

import java.util.Map;

public class Node {
    //
    public String nodeName;
    public String clusterId;

    public Map<String, String> labels;
    public String labelCode;

    public Node() {
        nodeName = null;
        clusterId = null;
        labels = null;
        labelCode = "";
    }

    public Node(String nodeName, String clusterId, Map<String, String> labels) {
        this.nodeName = nodeName;
        this.clusterId = clusterId;
        this.labels = labels;
        labelCode = "";
    }

    public Node(Node node) {
        Node res = new Node(node.nodeName, node.clusterId, node.labels);
        res.labelCode = node.labelCode;
    };


    @Override
    public String toString() {
        return "Node{" +
                "nodeName='" + nodeName + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", labels=" + labels +
                ", labelCode=" + labelCode +
                '}';
    }
}
