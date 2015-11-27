package com.nexacro.spring.data.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.spring.util.ReflectionUtil;
import com.nexacro.xapi.data.DataSet;

/**
 * <p><code>DataSet</code>에서 Object 형태의 데이터로 변환을 수행한다.
 *
 * @author Park SeongMin
 * @since 08.17.2015
 * @version 1.0
 * @see
 */
public class DataSetToObjectConverter extends AbstractDataSetConverter implements NexacroConverter<DataSet, Object> {

    @Override
    public boolean canConvert(Class source, Class target) {
        if(source == null || target == null) {
            return false;
        }
        
        // support type
        if(DataSet.class.equals(source) && !List.class.equals(target) && NexacroConverterHelper.isSupportedBean(target)) {
            return true;
        } 
        
        if (DataSet.class.equals(source) && Map.class.isAssignableFrom(target)) {
        	return true;
        }
        
        return false;
    }
    
    @Override
    public Object convert(DataSet source, ConvertDefinition definition) throws NexacroConvertException {

        if(definition ==null) {
            throw new NexacroConvertException(ConvertDefinition.class.getSimpleName()+" must not be null.");
        }
        
        // bean 혹은 Map만을 대상으로 한다.
        Class genericType = definition.getGenericType();
        
        if(source == null) {
            return ReflectionUtil.instantiateClass(genericType);
        }
        
        if(genericType == null) {
            throw new NexacroConvertException("generic type must be declared.");
        }
        
        Object obj = null;
        if(Map.class.equals(genericType)) {
            obj = convertDataSetToMap(source, definition);
        } else {
            obj = convertDataSetToBean(source, definition);
        }
        
        return obj;
        
    }

    private Object convertDataSetToBean(DataSet ds, ConvertDefinition definition) throws NexacroConvertException {
        Class genericType = definition.getGenericType();
        
        // 지원하는 형식인지 확인 한다.
        if(!NexacroConverterHelper.isSupportedBean(genericType)) {
            throw new NexacroConvertException("unsupported generic type. type="+genericType);
        }
        
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(genericType);
        addRowAndOrgRowIntoBean(beanWrapper, ds, 0);
        
        Object bean = beanWrapper.getInstance();
        
        return bean;
    }

    private Object convertDataSetToMap(DataSet ds, ConvertDefinition definition) {
        
        String[] columnNames = getDataSetColumnNames(ds);
        
        // default row
        Map<String, Object> dataRow = new HashMap<String, Object>();
        addRowIntoMap(dataRow, ds, 0, columnNames);
    
        return dataRow;
    }

   

}
