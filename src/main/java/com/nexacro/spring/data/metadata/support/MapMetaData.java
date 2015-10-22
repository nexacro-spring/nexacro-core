package com.nexacro.spring.data.metadata.support;

import java.util.Map;

import com.nexacro.spring.data.metadata.NexacroMetaData;

/**
 * <p>{@code NexacroMetaData}의 구현체로 Map의 설정 정보를 가진다.
 * 
 * @author Park SeongMin
 * @since 08.06.2015
 * @version 1.0
 * @see
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
