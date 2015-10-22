package com.nexacro.spring;

/**
 * 
 * <p>Nexacro 예외를 의미하는 <code>Exception</code>이다.
 * 추가적으로 설정되는 에러코드(errorCode) 에러메시지(errorMsg)는 nexacro platform으로 전송되는 데이터이다.
 *
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
     * @param message 메시지
     */
    public NexacroException(String message) {
        this(message, null);
    }

    /**
     * 메시지와 원천(cause) 예외를 가지는 생성자이다.
     * 
     * @param message 메시지
     * @param cause 원천 예외
     */
    public NexacroException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 메시지와 에러코드, 에러메시지를 가지는 생성자이다.
     * 
     * @param message 메시지
     * @param errorCode 에러코드
     * @param errorMsg 에러메시지
     */
    public NexacroException(String message, int errorCode, String errorMsg) {
    	this(message, null, errorCode, errorMsg);
    }
    
    /**
     * 메시지와 원천(cause), 에러코드, 에러메시지 예외를 가지는 생성자이다.
     * 
     * @param message 메시지
     * @param cause 원천 예외
     * @param errorCode 에러코드
     * @param errorMsg 에러메시지
     */
    public NexacroException(String message, Throwable cause, int errorCode, String errorMsg) {
        super(message, cause);
        setErrorCode(errorCode);
        setErrorMsg(errorMsg);
    }

    /**
     * 설정 된 에러코드를 반환한다.
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 에러코드를 설정한다.
     * 
     * @param errorCode 에러코드
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 설정 된 에러메시지를 반환한다.
     * @return errorMsg
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 에러메시지를 설정한다.
     * @param errorMsg 에러메시지
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
