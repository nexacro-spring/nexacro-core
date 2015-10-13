package com.nexacro.spring.data.metadata;

import java.util.ArrayList;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroList.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 6.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 6.     Park SeongMin     최초 생성
 * </pre>
 */

public abstract class NexacroMetaData extends ArrayList {

    public abstract void setMetaData(Object obj);
    
    public abstract Object getMetaData();
    
}
