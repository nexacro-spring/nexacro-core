package com.nexacro.spring;

/**
 * 
 * <pre>
 * Statements
 * </pre>
 * 
 * @ClassName : NexacroConstants.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 5.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 5.     Park SeongMin     최초 생성
 * </pre>
 */
public final class NexacroConstants {

    // attribute data
    public final class ATTRIBUTE {
        public static final String NEXACRO_REQUEST = "NexacroRequest";
        public static final String NEXACRO_CACHE_DATA = "NexacroCachedData";
        public static final String NEXACRO_PLATFORM_DATA = "NexacroPlatformData";
        public static final String NEXACRO_FILE_DATA = "NexacroFileData";
    }

    // error
    public final class ERROR {
       
        public final static int DEFAULT_ERROR_CODE = 0; 
        public final static String ERROR_CODE = "ErrorCode"; // nexacro 결과 코드값 (ex '0' 이상 일경우 성공)
        public final static String ERROR_MSG = "ErrorMsg"; // nexacro 에러 메시지

    }
    
    // first row
    public final class ERROR_FIRST_ROW {
        public final static String ERROR_DATASET = "FirstRowStatus"; // exception after firstrow
        public final static String ERROR_CODE = ERROR.ERROR_CODE;
        public final static String ERROR_MSG = ERROR.ERROR_MSG;
    }

    public static final String PERFORMANCE_LOGGER = "com.nexacro.performance";
    
}
