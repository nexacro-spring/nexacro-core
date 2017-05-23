package com.nexacro.spring.data.support;

import java.util.List;
import java.util.Map;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.xapi.data.DataSet;

/**
 * <p>Object에서 <code>DataSet</code>으로 데이터로 변환을 수행한다. 
 *
 * @author Park SeongMin
 * @since 08.17.2015
 * @version 1.0
 * @see
 */
public class ObjectToDataSetConverter extends AbstractDataSetConverter implements NexacroConverter<Object, DataSet> {

    @Override
    public boolean canConvert(Class source, Class target) {

    	if(source == null || target == null) {
    		return false;
    	}

    	// support type
    	if(!List.class.isAssignableFrom(source) && NexacroConverterHelper.isSupportedBean(source) && DataSet.class.equals(target)) {
    		return true;
    	}
    	
    	return false;
    }
    
    @Override
    public DataSet convert(Object source, ConvertDefinition definition) throws NexacroConvertException {
        
        if(definition ==null) {
            throw new IllegalArgumentException(ConvertDefinition.class.getSimpleName()+" must not be null.");
        }

        if(source == null) {
            return new DataSet(definition.getName());
        }
        
        // DataSet SavedType은 처리 하지 않는다. (UI 처리 불필요)
        DataSet ds = null;
        if(source instanceof Map) {
            ds = convertMapToDataSet((Map)source, definition);
        } else {
            ds = convertBeanToDataSet(source, definition);
        }
        
        return ds;
    }

    private DataSet convertBeanToDataSet(Object source, ConvertDefinition definition) throws NexacroConvertException {
        
        // 지원하는 형식인지 확인 한다.
        if(!NexacroConverterHelper.isSupportedBean(source.getClass())) {
            throw new NexacroConvertException("unsupported source type. type="+source.getClass());
        }

        DataSet ds = null;
        if(definition.getSchemaDataSet() != null) {
        	// set schema dataSet
	        ds = definition.getSchemaDataSet();
	        
	        // map과 달리 bean은 이미 정의가 되어 있기 때문에 row를 추가할때 컬럼을 추가하지 않고, 미리 설정한다.
	        if(!definition.isDisallowChangeStructure()) {
	        	addColumnIntoDataSet(ds, source);
	        }
	        
        } else {
        	ds = new DataSet(definition.getName());
	        addColumnIntoDataSet(ds, source);
        }
        
        addRowIntoDataSet(ds, source);
        return ds;
    }
    
    private DataSet convertMapToDataSet(Map source, ConvertDefinition definition) throws NexacroConvertException {
        DataSet ds = null;
        if(definition.getSchemaDataSet() != null) {
        	// set schema dataSet
	        ds = definition.getSchemaDataSet();
        } else {
        	ds = new DataSet(definition.getName());
	        addColumnIntoDataSet(ds, source);
        }
        
        addRowIntoDataSet(ds, source, definition.isDisallowChangeStructure());
        return ds;
    }
    
    @Override
    public void addColumnIntoDataSet(DataSet ds, Map source) throws NexacroConvertException {
        super.addColumnIntoDataSet(ds, source);
    }
    
    @Override
    public void addColumnIntoDataSet(DataSet ds, Object source) {
        super.addColumnIntoDataSet(ds, source);
    }
    
    @Override
    public void addRowIntoDataSet(DataSet ds, Map source, boolean disallowChangeStructure) throws NexacroConvertException {
        super.addRowIntoDataSet(ds, source, disallowChangeStructure);
    }
    
    @Override
    public void addRowIntoDataSet(DataSet ds, Object source) {
        super.addRowIntoDataSet(ds, source);
    }

}
