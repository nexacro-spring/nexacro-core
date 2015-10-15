package com.nexacro.spring;

/**
 * 
 * <p>Nexacro 예외를 의미하는 <code>Exception</code>이다.
 *
 * @ClassName   : NexacroException.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 7. 30.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 7. 30.     Park SeongMin     최초 생성
 * </pre>
 */
public class NexacroException extends Exception {

    /* serialVersionUID */
    private static final long serialVersionUID = 4095922735986385233L;

    public static final int DEFAULT_ERROR_CODE = -1;
    public static final String DEFAULT_MESSAGE = "An Error Occured. check the ErrorCode for detail of error infomation.";

    private int errorCode = DEFAULT_ERROR_CODE;
    private String errorMsg = DEFAULT_MESSAGE;

    /**
     * 기본 생성자이다.
     */
    public NexacroException() {
        ;
    }

    /**
     * 메시지를 가지는 생성자이다.
     * 
     * @param message
     *            메시지
     */
    public NexacroException(String message) {
        this(message, null);
    }

    /**
     * 메시지와 원천(cause) 예외를 가지는 생성자이다.
     * 
     * @param message
     *            메시지
     * @param cause
     *            원천 예외
     */
    public NexacroException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
