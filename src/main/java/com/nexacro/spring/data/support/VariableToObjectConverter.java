package com.nexacro.spring.data.support;

import java.util.Set;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.xapi.data.Variable;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : VariableToObjectConverter.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 7. 28.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 7. 28.     Park SeongMin     최초 생성
 * </pre>
 */

public class VariableToObjectConverter extends AbstractListenerHandler implements NexacroConverter<Variable, Object> {

    @Override
    public boolean canConvert(Class source, Class target) {
        if(source == null || target == null) {
            return false;
        }
        
        ConvertiblePair comparePair = new ConvertiblePair(source, target);
        Set<ConvertiblePair> variableToObjectConvertibleTypes = NexacroConverterHelper.getVariableToObjectConvertibleTypes();
        
        for(ConvertiblePair pair: variableToObjectConvertibleTypes) {
            if(pair.equals(comparePair)) {
                return true;
            }
        }
        
        return false;
    }
    
    /*
     * @see com.nexacro.spring.data.NexacroConverter#convert(java.lang.Object)
     */
    @Override
    public Object convert(Variable source, ConvertDefinition definition) throws NexacroConvertException {
        
        Object object = NexacroConverterHelper.toObject(source, definition.getGenericType());
        
        // fire event
        object = fireVariableConvertedValue(source, object);
        
        return object;
    }

}
