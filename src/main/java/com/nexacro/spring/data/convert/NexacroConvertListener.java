package com.nexacro.spring.data.convert;

import java.util.EventListener;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroConverterListener.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 9.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 9.     Park SeongMin     최초 생성
 * </pre>
 */

public interface NexacroConvertListener extends EventListener {
    
    void convertedValue(ConvertEvent event);

}
