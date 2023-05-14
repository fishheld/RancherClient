import Utils.HttpHelper;
import models.Cluster;
import models.Node;
import org.junit.Test;

import java.util.List;

public class RancherClientTest {

    @Test
    public void testGetNodes() {
        RancherClient client = new RancherClient();
        List<Node> nodes = client.getNodes();
        for (Node e : nodes) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testGetClusters() {
        RancherClient client = new RancherClient();
        List<Cluster> clusters = client.getClusters();
        for (Cluster e : clusters) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void updateLabelKeysTest() {
        RancherClient client = new RancherClient();
        for (Node e : client.nodeList) {
            System.out.println(e.toString());
        }
        client.updateLabelKeys();
        System.out.println(client.labelKeys.toString());
        System.out.println(client.labelKeysCount);
    }

    @Test
    public void updateNodeLabelCode() {
        RancherClient c = new RancherClient();
        c.updateNodeLabelCode();
        for (Node e : c.nodeList) {
            System.out.print(e.nodeName);
            System.out.println(" " + e.labelCode);
        }
    }

    @Test
    public void deployTest() {
        RancherClient c = new RancherClient();
//        c.deployTest();
    }
}
