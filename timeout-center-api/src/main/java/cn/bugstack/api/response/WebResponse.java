package cn.bugstack.api.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class WebResponse <T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SUCCESS_CODE = "000001";

    public static final String ERROR_CODE = "000002";

    private String code;

    private String msg;

    private Boolean isSuccess;

    private T data;

    private WebResponse(String code) {
        this.code = code;
        setIsSuccess(code);
    }

    private WebResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
        setIsSuccess(code);
    }

    private WebResponse(String code, T t) {
        this.code = code;
        setIsSuccess(code);
        this.data = t;
    }

    private WebResponse(String code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        setIsSuccess(code);
        this.data = t;
    }

    private void setIsSuccess(String code) {
        this.isSuccess = Objects.equals(code, SUCCESS_CODE);
    }

    public static <T> WebResponse<T> returnSuccess() {
        return new WebResponse<>(SUCCESS_CODE);
    }

    public static <T> WebResponse<T> returnSuccess(T t) {
        return new WebResponse<>(SUCCESS_CODE, t);
    }

    public static <T> WebResponse<T> returnFail(String code, String msg) {
        return new WebResponse<>(code, msg);
    }

    public static <T> WebResponse<T> returnFail(String code, String msg, T t) {
        return new WebResponse<>(code, msg, t);
    }

}
