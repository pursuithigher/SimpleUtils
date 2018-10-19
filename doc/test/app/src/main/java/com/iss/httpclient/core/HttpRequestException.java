package com.iss.httpclient.core;

/**
 * upstream code to receive an HTTP status code and any content received as well
 * as the underlying exception.
 *
 * @author David M. Chandler
 */
public class HttpRequestException extends Exception {


    /**
     * 超时异常
     */
    public static final int TIME_OUT_EXCEPTION = 1;
    /**
     * 其他异常
     */
    public static final int OTHER_EXCEPTION = 2;

    private static final long serialVersionUID = -2413629666163901633L;
    private int exceptionCode;

    /**
     * Constructs the exception with
     *
     * @param e             e
     * @param exceptionCode exceptionCode
     */
    public HttpRequestException(Exception e, int exceptionCode) {
        super(e);
        this.exceptionCode = exceptionCode;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public boolean isTimeOutException() {
        return exceptionCode == TIME_OUT_EXCEPTION;
    }

    @Override
    public String toString() {
        if (isTimeOutException()) {
            return "DZ_TIME_OUT_EXCEPTION";
        }
        return super.toString();
    }
}
