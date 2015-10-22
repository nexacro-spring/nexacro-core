package com.nexacro.spring.data.support;

import java.util.List;
import java.util.Map;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.spring.data.metadata.NexacroMetaData;
import com.nexacro.xapi.data.DataSet;

/**
 * <p>List에서 <code>DataSet</code>으로 데이터로 변환을 수행한다. 
 *
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see
 */
public class ListToDataSetConverter extends AbstractDataSetConverter implements NexacroConverter<List, DataSet> {

    private static final int CHECK_INDEX = 0;
    
    @Override
    public boolean canConvert(Class source, Class target) {
        
        if(source == null || target == null) {
            return false;
        }
        
        // support list sub class
        if(List.class.isAssignableFrom(source) && DataSet.class.equals(target)) {
            return true;
        }
        
        return false;
    }
    
    /*
     * @see com.nexacro.spring.data.NexacroConverter#convert(java.lang.Object)
     */
    @Override
    public DataSet convert(List source, ConvertDefinition definition) throws NexacroConvertException {
        
        if(definition ==null) {
            throw new IllegalArgumentException(ConvertDefinition.class.getSimpleName()+" must not be null.");
        }
        if(source == null) {
            return new DataSet(definition.getName());
        }
        
        // check first value
        Object availableFirstData = checkAvailable(source);
        if(availableFirstData == null) {
            return new DataSet(definition.getName());
        }
        
        // DataSet SavedType은 처리 하지 않는다. (UI 처리 불필요)
        DataSet ds = null;
        if(availableFirstData instanceof Map) {
            ds = convertListMapToDataSet(source, definition, (Map) availableFirstData);
        } else {
            ds = convertListBeanToDataSet(source, definition, availableFirstData);
        }
        
        return ds;
    }
    
    private Object checkAvailable(List source) {
        
        // for ibatis empty data
        if(source instanceof NexacroMetaData) {
            NexacroMetaData metaData = (NexacroMetaData) source;
            return metaData.getMetaData();
        }
        
        if(source.size() == 0) {
            return null;
        }
        for(Object obj: source) {
            if(obj != null) {
                return obj;
            }
        }
        
        return null;
    }
    
    private DataSet convertListMapToDataSet(List source, ConvertDefinition definition, Map availableFirstData) throws NexacroConvertException {
        
        DataSet ds = new DataSet(definition.getName());
        
        // dynamic하게 변경 된 데이터는 처리 하지 않는다.
        addColumnIntoDataSet(ds, availableFirstData);
        
        for(Object obj: source) {
            
            if(!(obj instanceof Map)) {
                throw new NexacroConvertException("list should use the generic type. target="+ds.getName());
            }
            
            addRowIntoDataSet(ds, (Map) obj);
        }
        
        return ds;
    }

    private DataSet convertListBeanToDataSet(List source, ConvertDefinition definition, Object availableFirstData) throws NexacroConvertException {
        
        // 지원하는 형식인지 확인 한다.
        if(!NexacroConverterHelper.isSupportedBean(availableFirstData.getClass())) {
            throw new NexacroConvertException("unsupported generic type. type="+availableFirstData.getClass());
        }
        
        DataSet ds = new DataSet(definition.getName());
        
        addColumnIntoDataSet(ds, availableFirstData);
        
        for(Object obj: source) {
            addRowIntoDataSet(ds, obj);
        }
        
        return ds;
    }
    
}
