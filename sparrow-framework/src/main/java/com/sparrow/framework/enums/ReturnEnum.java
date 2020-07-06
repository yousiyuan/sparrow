package com.sparrow.framework.enums;

public enum ReturnEnum implements ReturnCode {

    SUCCESS(200, "请求成功"),
    ERROR(500, "服务器异常"),
    CONTENT_TYPE(5001, "post请求仅支持application/x-www-form-urlencoded数据格式"),
    TIMESTAMP_ERROR(5002, "时间戳过期或失效"),
    APP_KEY_ERROR(5003, "appkey无效，appkey在系统中不存在或未分配权限"),
    SIGN_ERROR(5004, "非法签名，签名验证失败"),
    VERSION_ERROR(5005, "接口版本不符合");

    private Integer code;
    private String message;

    ReturnEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
