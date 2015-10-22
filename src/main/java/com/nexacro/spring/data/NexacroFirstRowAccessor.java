package com.nexacro.spring.data;

import com.nexacro.xapi.tx.HttpPartPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;

/**
 * <p>NexacroFirstRowHandler에 접근 가능한 메서드를 제공한다.
 *
 * @author Park SeongMin
 * @since 08.05.2015
 * @version 1.0
 * @see NexacroFirstRowHandler
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
