package com.shiphola.dto.common;

/**
 * BaseResponse<T> - Response wrapper chuẩn
 * Dùng cho REST API response (nếu có)
 */
public class BaseResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    public BaseResponse() {
    }

    public BaseResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, "Thành công", data);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(true, message, data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null);
    }

    public static <T> BaseResponse<T> error(String message, String errorCode) {
        BaseResponse<T> response = new BaseResponse<>(false, message, null);
        response.setErrorCode(errorCode);
        return response;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
}
