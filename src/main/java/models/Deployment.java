package models;

import java.util.List;

public class Deployment {
    String name;
    String namespaceId;
    String projectId;
    List<Pod> containers;

}
