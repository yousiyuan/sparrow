package com.sparrow.framework.response;

import com.sparrow.framework.enums.ReturnCode;
import com.sparrow.framework.enums.ReturnEnum;
import org.springframework.beans.BeanUtils;

import java.text.MessageFormat;

public class BaseResponse {

    public static BaseResult success() {
        return new BaseResult(ReturnEnum.SUCCESS.getCode(), ReturnEnum.SUCCESS.getMessage());
    }

    public static BaseResult<Object> success(Object data) {
        return new BaseResult<>(ReturnEnum.SUCCESS.getCode(), ReturnEnum.SUCCESS.getMessage(), data);
    }

    public static BaseResult<Object> success(Object data, Object output) {
        if (data != null) {
            BeanUtils.copyProperties(data, output);
        } else {
            output = null;
        }
        return new BaseResult<>(ReturnEnum.SUCCESS.getCode(), ReturnEnum.SUCCESS.getMessage(), output);
    }

    public static BaseResult<Object> success(ReturnCode returnEnum) {
        return new BaseResult<>(returnEnum.getCode(), returnEnum.getMessage());
    }

    public static BaseResult<Object> success(Integer code, String message) {
        return new BaseResult<>(code, message);
    }

    public static BaseResult<Object> success(ReturnCode returnEnum, Integer s1) {
        return new BaseResult<>(returnEnum.getCode(), MessageFormat.format(returnEnum.getMessage(), s1));
    }

    public static BaseResult<Object> success(Integer code, String message, Integer s1) {
        return new BaseResult<>(code, MessageFormat.format(message, s1));
    }

    public static BaseResult<Object> success(ReturnCode returnEnum, Object data) {
        return new BaseResult<>(returnEnum.getCode(), returnEnum.getMessage(), data);
    }

    public static BaseResult<Object> success(Integer code, String message, Object data) {
        return new BaseResult<>(code, message, data);
    }

    public static BaseResult<Object> error(ReturnCode returnEnum) {
        return new BaseResult<>(returnEnum.getCode(), returnEnum.getMessage());
    }

    public static BaseResult<Object> error(Integer code, String message) {
        return new BaseResult<>(code, message);
    }

    public static BaseResult<Object> error(String msg) {
        return new BaseResult<>(400, msg);
    }

    public static BaseResult<Object> error(ReturnCode returnEnum, Object s1) {
        return new BaseResult<>(returnEnum.getCode(), MessageFormat.format(returnEnum.getMessage(), s1));
    }

    public static BaseResult<Object> error(Integer code, String message, Object s1) {
        return new BaseResult<>(code, MessageFormat.format(message, s1));
    }

}
