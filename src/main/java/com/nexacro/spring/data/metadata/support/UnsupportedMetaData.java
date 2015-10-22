package com.nexacro.spring.data.metadata.support;

import com.nexacro.spring.data.metadata.NexacroMetaData;

/**
 * <p>{@code NexacroMetaData}의 구현체로 메타데이터 정보가 존재하지 않는다.
 *
 * @author Park SeongMin
 * @since 2015. 8. 6.
 * @version 1.0
 * @see
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
