package com.nexacro.spring.data.metadata.support;

import java.util.Map;

import com.nexacro.spring.data.metadata.NexacroMetaData;

/**
 * <pre>
 * Statements
 * </pre>
 * 
 * @ClassName : MapMetaData.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 6.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 6.     Park SeongMin     최초 생성
 * </pre>
 */

public class MapMetaData extends NexacroMetaData {

    private Map<String, Object> metaDataMap;

    /**
     * Statements
     *
     * @param mapData
     */
    public MapMetaData(Map<String, Object> mapData) {
        setMetaData(mapData);
    }

    @Override
    public void setMetaData(Object metaDataMap) {
        this.metaDataMap = (Map) metaDataMap;
    }

    @Override
    public Object getMetaData() {
        return this.metaDataMap;
    }

    @Override
    public String toString() {
        return "MapMetaData [metaDataMap=" + metaDataMap + "]";
    }

}
