package models;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class Cluster {
    //
    public String name;
    public String id;
    public int nodeCount;
    public List<Node> nodeList;
    public BloomFilter<CharSequence> bloom;

    //
    public Cluster(String name, String id, int nodeCount) {
        this.name = name;
        this.id = id;
        this.nodeCount = nodeCount;
        this.nodeList = new ArrayList<>();
        bloom = BloomFilter.create(Funnels.stringFunnel(Charset.forName("utf-8")), 100, 0.001);
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", nodeCount=" + nodeCount +
                ", nodeList=" + nodeList +
                '}';
    }

    //BloomFilter
    public boolean bloomPut(String labelCode) {
        bloom.put(labelCode);
        return true;
    }
    public boolean bloomContains(String labelCode) {
        return bloom.mightContain(labelCode);
    }
}
