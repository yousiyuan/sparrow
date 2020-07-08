package com.sparrow.zk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadUtils {

    final static private String SERVER_URL = "https://apis.map.qq.com";
    final static private String SERVER_URL2 = "https://apis.map.qq.com";
    final static private String APP_KEY = "X7LBZ-2RTCG-77TQ7-IISBF-764YE-ABFOZ";
    final static private String SECRET_KEY = "gB7H1NQ9rwEysvRKOyt9WDk4cq6Cqdgm";

    /**
     * 读取本地Properties文件
     */
    public static Map<String, Object> getConfig(String fileName) throws Exception {
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (inStream == null) {
            throw new RuntimeException("file " + fileName + " not found.");
        }

        Properties properties = new Properties();
        properties.load(inStream);

        if (properties.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Object> map = properties.entrySet()
                .stream()
                .flatMap(Stream::of)
                .collect(Collectors.toMap(
                        entry -> String.valueOf(entry.getKey()),
                        Map.Entry::getValue,
                        (oldKey, newKey) -> newKey)
                );
        properties.clear();
        inStream.close();

        return map;
    }

    /**
     * 获取本地计算机内网IP
     */
    public static String getHostAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * 获取本地计算机外网IP
     */
    public static String getNetworkAddress() {
        return ThreadUtils.httpRequestGet("/ws/location/v1/ip", new HashMap<>());
    }

    /**
     * 发送http请求GET
     * 如 ThreadUtils.httpRequestGet("/ws/location/v1/ip", new HashMap<>())
     */
    public static String httpRequestGet(String apiUrl, Map<String, Object> params) {
        HttpURLConnection connection = null;
        String response = null;
        try {
            params.put("key", APP_KEY);
            String signature = ThreadUtils.createSignature(apiUrl, params);
            params.put("sig", signature);
            String requestUrl = SERVER_URL + apiUrl + "?" + params.entrySet()
                    .stream()
                    .map(param -> param.getKey() + "=" + param.getValue())
                    .collect(Collectors.joining("&"));
            System.out.println("get request url ===>> " + requestUrl);

            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
            response = result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    /**
     * 发送http请求POST
     */
    public static String httpRequestPost(String apiUrl, String json) {
        // 发送POST请求
        HttpURLConnection connection = null;
        String response = null;
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<>();
            JsonNode jsonNode = jsonMapper.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> element = fields.next();
                map.put(element.getKey(), element.getValue().getNodeType() == JsonNodeType.ARRAY
                        ? element.getValue().toString()
                        : element.getValue().asText());
            }

            map.put("key", APP_KEY);
            String signature = ThreadUtils.createSignature(apiUrl, map);
            String requestContent = map.entrySet()
                    .stream()
                    .map(entry -> "\"" + entry.getKey() + "\":" + entry.getValue())
                    .collect(Collectors.joining(","));
            String requestUrl = SERVER_URL2 + apiUrl + "?sig=" + signature;
            requestContent = "{" + requestContent + "}";
            System.out.println("post request url ===>> " + requestUrl);

            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            PrintWriter pw = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()));
            pw.write(requestContent);
            pw.flush();
            pw.close();

            response = readResponse(connection.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    /**
     * 将字节流转为字符串
     */
    private static String readResponse(InputStream inStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) { // 读取数据
            result.append(line).append("\n");
        }
        return result.toString();
    }

    /**
     * 创建签名
     */
    private static String createSignature(String apiUrl, Map<String, Object> params) {
        TreeMap<String, Object> treeParams = new TreeMap<>(
                (String key1, String key2) -> Integer.compare(key1.compareTo(key2), 0));
        treeParams.putAll(params);
        String requestString = treeParams.entrySet()
                .stream()
                .map(param -> param.getKey() + "=" + String.valueOf(param.getValue()))
                .collect(Collectors.joining("&"));
        return MD5(MessageFormat.format("{0}?{1}{2}", apiUrl, requestString, SECRET_KEY));
    }

    /**
     * 计算字符创的md5值
     */
    private static String MD5(String key) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 对请求参数进行排序
     */
    private static String castParam(KeyValuePair... params) {
        Arrays.sort(params);
        return Arrays.stream(params)
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));
    }

}
