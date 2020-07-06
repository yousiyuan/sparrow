package com.sparrow.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sparrow.framework.component.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ComUtils {

    private final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static ObjectMapper objectMapper;

    static {
        objectMapper = SpringContextUtils.getBean(ObjectMapper.class);
    }

    /**
     * 打印详细错误信息
     */
    public static String printException(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ex.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        return stringWriter.toString();
    }

    /**
     * 将日期字符串转为日期对象
     */
    public static Date parseDateStr(String dateStr) throws ParseException {
        return DateUtils.parse(dateStr, DATE_PATTERN);
    }

    /**
     * 将日期对象转为日期字符串
     */
    public static String formatDateObj(Date dateObj) {
        return DateUtils.format(dateObj, DATE_PATTERN);
    }

    /**
     * 日期相加得到新的日期
     */
    public static String dateAddDays(String dateStr, int days) {
        Date dateObj;
        try {
            dateObj = DateUtils.parse(dateStr, DATE_PATTERN);
        } catch (ParseException ex) {
            log.error(printException(ex));
            throw new RuntimeException(ex);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObj);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return DateUtils.format(calendar.getTime(), DATE_PATTERN);
    }

    /**
     * 日期相加/相减若干秒得到新的日期
     */
    public static long dateAddSeconds(Date dateObj, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObj);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTimeInMillis();
    }

    /**
     * 校验字符串是否为数字
     */
    public static boolean validNumber(String moneyValue) {
        String regex = "^[-+]?\\d+(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(moneyValue);
        return !matcher.matches();
    }

    /**
     * 判断字符串是否为null或""
     */
    public static boolean isNotEmpty(String value) {
        if (null == value) {
            return false;
        }
        //替换掉转码后的unicode控制字符(这些看不到的空字符无法使用trim移除,每个控制字符的length是1)
        //参考 https://blog.csdn.net/liquantong/article/details/85318595
        value = value.replaceAll("\\u202E", "");//开始从右到左的文字
        value = value.replaceAll("\\u202D", "");//开始从左到右的文字
        value = value.replaceAll("\\u202C", "");//结束上一次定义
        return !("".equals(value.trim()) || "null".equalsIgnoreCase(value.trim()));
    }

    /**
     * 删除字符串source开头的指定字符prefix
     */
    public static String trimStart(String source, String prefix) {
        return source.replaceAll("^" + prefix, "");
    }

    /**
     * 删除字符串source结尾的指定字符suffix
     */
    public static String trimEnd(String source, String suffix) {
        return source.replaceAll(suffix + "$", "");
    }

    /**
     * 删除字符串source首尾的指定字符chr
     */
    public static String trim(String source, String chr) {
        return source.replaceAll("(^" + chr + ")|(" + chr + "$)", "");
    }

    /**
     * Jackson解析Object对象，获取字段值
     */
    public static String readObject(Object object, String fieldName) {
        if (null == object) {
            return "";
        }
        JsonNode rootNode = objectMapper.valueToTree(object);
        if (null == rootNode) {
            return "";
        }
        JsonNode jsonNode = rootNode.get(fieldName);
        if (null == jsonNode) {
            return "";
        }
        return jsonNode.size() == 0 ? jsonNode.asText() : JsonUtils.to(jsonNode);
    }

    /**
     * Jackson解析Json字符串，判断指定字段是否为数组类型，如果是则返回数组的长度
     */
    public static Integer validJsonArray(String jsonStr, String fieldKey) throws IOException {
        JsonNode rootNode = objectMapper.readTree(jsonStr);
        if (rootNode == null) {
            return 0;
        }
        JsonNode fieldNode = rootNode.get(fieldKey);
        if (fieldNode == null) {
            return 0;
        }
        if (JsonNodeType.ARRAY != fieldNode.getNodeType()) {
            return 0;
        }
        return fieldNode.size();
    }

    /**
     * 更新Json的值
     */
    public static String alterJson(String sourceJson, Map<String, Object> targetMap) throws IOException {
        ObjectNode rootObjectNode = (ObjectNode) objectMapper.readTree(sourceJson);
        targetMap.forEach((key, value) -> rootObjectNode.put(key, String.valueOf(value)));
        return objectMapper.writeValueAsString(rootObjectNode);
    }

    /**
     * 判断节点是否为字符串节点且不能为空
     */
    public static Boolean isEmptyJsonNode(JsonNode node) {
        return node == null || node.getNodeType() != JsonNodeType.STRING || !ComUtils.isNotEmpty(node.asText());
    }

    /**
     * 根据路径解析Json节点
     *
     * @param rootNode    JSON根节点
     * @param fullPathKey 节点路径
     * @return 返回最后的节点
     */
    public static JsonNode readPathNode(ObjectNode rootNode, String fullPathKey) {
        if (rootNode == null || rootNode.getNodeType() == JsonNodeType.ARRAY) {
            return null;
        }
        try {
            String[] pathKeyList = fullPathKey.split("\\.");
            JsonNode jsonNode = null;
            String pathKey;
            for (int i = 0; i < pathKeyList.length; i++) {
                pathKey = pathKeyList[i];
                if (jsonNode != null && jsonNode.getNodeType() == JsonNodeType.ARRAY) {
                    break;
                }
                jsonNode = i == 0
                        ? rootNode.get(pathKey)
                        : jsonNode != null
                        ? jsonNode.get(pathKey)
                        : null;
                if (i > 0 && jsonNode == null) {
                    break;
                }
            }
            return jsonNode;
        } catch (Exception ex) {
            log.error("readPathNode解析JSON异常：" + printException(ex));
            return null;
        }
    }

    /**
     * 读取文本节点的值
     */
    public static String readNode(JsonNode node, String key) {
        JsonNode keyNode = node.get(key);
        if (keyNode == null) {
            return "";
        }
        return Arrays.asList(JsonNodeType.STRING, JsonNodeType.NUMBER, JsonNodeType.BOOLEAN, JsonNodeType.NULL).contains(keyNode.getNodeType())
                ? keyNode.asText()
                : "";
    }

    /**
     * 返回对象的字符串格式
     */
    public static String str(Object value) {
        return value instanceof String ? String.valueOf(value) : JsonUtils.to(value);
    }

}
