package Utils;

import models.Deployment;
import models.Pod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BeanHelper {

    /**
     * Pod对象生成Json对象
     * @param pod
     * @return
     */
    public static JSONObject fromPodToJson(Pod pod) {
        JSONObject res = new JSONObject();
        try {
            res.put("name", pod.name);
            res.put("image", pod.image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    };

    /**
     * Deployment对象生成Json对象
     * @param d
     * @return
     */
    public static JSONObject fromDeploymentToJson(Deployment d) {
        JSONObject res = new JSONObject();
        try {
            //Deployment 的其他属性
            res.put("name", d.name);
            res.put("namespaceId", d.namespaceId);
            res.put("projectId", d.projectId);
            //Deployment 的容器列表
            JSONArray containers = new JSONArray();
            for (Pod p : d.containers) {
                containers.put(BeanHelper.fromPodToJson(p));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return res;
    }

    //Json对象生成Pod对象
    public static Pod fromJsonToPod(JSONObject json) {

        return null;
    }

}
