package com.nexacro.spring.data.support;

import java.util.Set;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.xapi.data.Variable;

/**
 * <p><code>Variable</code>에서 Object로 데이터로 변환을 수행한다. 
 *
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see
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
