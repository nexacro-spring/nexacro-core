package com.nexacro.spring.data;

import com.nexacro.xapi.tx.HttpPartPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroFirstRowAccessor.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 5.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 5.     Park SeongMin     최초 생성
 * </pre>
 */

public abstract class NexacroFirstRowAccessor {

    public static HttpPartPlatformResponse getHttpPartPlatformResponse(NexacroFirstRowHandler handler) {
        return handler.getHttpPartPlatformResponse();
    }
    
    public static void end(NexacroFirstRowHandler handler) throws PlatformException {
        handler.end();
    }
    
    public static String[] getSendOutVariableNames(NexacroFirstRowHandler handler) {
        return handler.getSendOutVariableNames();
    }
    
    public static int getSendOutVariableCount(NexacroFirstRowHandler handler) {
        return handler.getSendOutVariableCount();
    }
    
    public static String[] getSendOutDataSetNames(NexacroFirstRowHandler handler) {
        return handler.getSendOutDataSetNames();
    }
    
    public static int getSendOutDataSetCount(NexacroFirstRowHandler handler) {
        return handler.getSendOutDataSetCount();
    }
    
}
