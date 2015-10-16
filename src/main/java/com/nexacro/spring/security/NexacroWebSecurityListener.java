package com.nexacro.spring.security;

import com.nexacro.spring.data.convert.ConvertEvent;
import com.nexacro.spring.data.convert.NexacroConvertListener;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : WebSecurityListener.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 9.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 9.     Park SeongMin     최초 생성
 * </pre>
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
