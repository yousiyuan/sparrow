package com.sparrow.framework.response;

import java.util.Date;
import java.util.Objects;

public class JsonResponse {
    private boolean success = true;
    private String message;
    private Object data;
    private Date timestamp = new Date();

    public JsonResponse() {
    }

    public JsonResponse(String message) {
        this.message = message;
    }

    public JsonResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public JsonResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public JsonResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static JsonResponse newInstance() {
        return new JsonResponse();
    }

    public boolean isSuccess() {
        return this.success;
    }

    public JsonResponse setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public JsonResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return this.data;
    }

    public JsonResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public JsonResponse setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof JsonResponse)) {
            return false;
        } else {
            JsonResponse that = (JsonResponse)o;
            return this.isSuccess() == that.isSuccess() && Objects.equals(this.getMessage(), that.getMessage()) && Objects.equals(this.getData(), that.getData()) && Objects.equals(this.getTimestamp(), that.getTimestamp());
        }
    }

    public int hashCode() {
        return Objects.hash(this.isSuccess(), this.getMessage(), this.getData(), this.getTimestamp());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("JsonResponse{");
        sb.append("success=").append(this.success);
        sb.append(", message='").append(this.message).append('\'');
        sb.append(", data=").append(this.data);
        sb.append(", timestamp=").append(this.timestamp);
        sb.append('}');
        return sb.toString();
    }
}
