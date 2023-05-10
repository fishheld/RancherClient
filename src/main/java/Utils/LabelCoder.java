package Utils;

import models.Node;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class LabelCoder {

    /**
     * 标签组合编码成串。可用来编码Node和Pod。
     * @param targetLabels
     * @param labelKeys
     * @return
     */
    public static String encodeLabelCombination(Map<String, String> targetLabels, Map<String, Integer> labelKeys) {
        byte[] res = new byte[labelKeys.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = 48;
        }
        //找到Node或Pod拥有的标签
        for (String key : targetLabels.keySet()) {
            if (labelKeys.containsKey(key)) {
                res[labelKeys.get(key)] = 49;
            }
        }
        //将标签组合的位串转换为字符串
        return new String(res, StandardCharsets.UTF_8);
    };

}
