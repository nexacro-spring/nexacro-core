package com.nexacro.spring.data.support;

import java.util.Map;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.xapi.data.DataSet;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ObjectToDataSetConverter.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 17.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 17.     Park SeongMin     최초 생성
 * </pre>
 */

public class ObjectToDataSetConverter extends AbstractDataSetConverter implements NexacroConverter<Object, DataSet> {

    @Override
    public boolean canConvert(Class source, Class target) {

        if(source == null || target == null) {
            return false;
        }
        
        // support list sub class
        if(NexacroConverterHelper.isSupportedBean(source) && DataSet.class.equals(target)) {
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
        
        return null;
    }

    private DataSet convertBeanToDataSet(Object source, ConvertDefinition definition) throws NexacroConvertException {
        
        // 지원하는 형식인지 확인 한다.
        if(!NexacroConverterHelper.isSupportedBean(source.getClass())) {
            throw new NexacroConvertException("unsupported source type. type="+source.getClass());
        }
        
        DataSet ds = new DataSet(definition.getName());
        addColumnIntoDataSet(ds, source);
        addRowIntoDataSet(ds, source);
        return ds;
    }
    
    private DataSet convertMapToDataSet(Map source, ConvertDefinition definition) throws NexacroConvertException {
        DataSet ds = new DataSet(definition.getName());
        addColumnIntoDataSet(ds, source);
        addRowIntoDataSet(ds, source);
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
    public void addRowIntoDataSet(DataSet ds, Map source) throws NexacroConvertException {
        super.addRowIntoDataSet(ds, source);
    }
    
    @Override
    public void addRowIntoDataSet(DataSet ds, Object source) {
        super.addRowIntoDataSet(ds, source);
    }

}
