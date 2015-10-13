package com.nexacro.spring.data.metadata.support;

import com.nexacro.spring.data.metadata.NexacroMetaData;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : UnsupportedMetaData.java
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

public class UnsupportedMetaData extends NexacroMetaData {

    private String message;
    
    public UnsupportedMetaData(String message) {
        this.message = message;
    }
    
    @Override
    public void setMetaData(Object obj) {
//        throw new UnsupportedOperationException(this.message);
    }

    @Override
    public Object getMetaData() {
//        throw new UnsupportedOperationException(this.message);
        return message;
    }

}
