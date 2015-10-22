package com.nexacro.spring.data.support;

import java.util.Set;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.convert.NexacroConverter;
import com.nexacro.xapi.data.Variable;

/**
 * <p>Object에서 <code>Variable</code>으로 데이터로 변환을 수행한다. 
 *
 * @author Park SeongMin
 * @since 07.28.2015
 * @version 1.0
 * @see
 */
public class ObjectToVariableConverter extends AbstractListenerHandler implements NexacroConverter<Object, Variable> {

    @Override
    public boolean canConvert(Class source, Class target) {
        if(source == null || target == null) {
            return false;
        }
        
        ConvertiblePair comparePair = new ConvertiblePair(source, target);
        Set<ConvertiblePair> objectToVariableConvertibleTypes = NexacroConverterHelper.getObjectToVariableConvertibleTypes();
        
        for(ConvertiblePair pair: objectToVariableConvertibleTypes) {
            if(pair.equals(comparePair)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Variable convert(Object source, ConvertDefinition definition) throws NexacroConvertException {
        
        Variable variable = NexacroConverterHelper.toVariable(definition.getName(), source);
        
        // fire event
        Object obj = fireVariableConvertedValue(variable, variable.getObject());
        variable.set(obj);
        
        return variable;
    }
    
}
