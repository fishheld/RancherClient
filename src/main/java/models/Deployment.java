package models;

import java.util.List;

public class Deployment {
    public String name;

    public String clusterId;
    public String projectId;    //
    public String namespaceId;
    public List<Pod> containers;



}
