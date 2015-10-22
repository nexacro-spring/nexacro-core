package com.nexacro.spring.security;

import com.nexacro.spring.data.convert.ConvertEvent;
import com.nexacro.spring.data.convert.NexacroConvertListener;

/**
 * <p>
 *
 * @author Park SeongMin
 * @since 08.09.2015
 * @version 1.0
 * @see
 */
@Deprecated
public class NexacroWebSecurityListener implements NexacroConvertListener {

    @Override
    public void convertedValue(ConvertEvent event) {
        
        if(event == null) {
            return;
        }
        
        // security 
        checkSecurity(event);
        
    }

    /**
     * Statements
     *
     * @param event
     * @throws Exception 
     */
    private void checkSecurity(ConvertEvent event) {
        
        Object value = event.getValue();
        
        if(value == null) {
            return;
        }
        
        String strValue = value.toString();
        try {
            SecurityUtil.checkPathTraversal(strValue);
        } catch (Exception e) {
            
        }
        
        String convertXSS = SecurityUtil.convertXSS(strValue);
        
        if(!strValue.equals(convertXSS)) {
            event.setValue(value);
        }

    }

}
