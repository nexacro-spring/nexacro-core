package com.nexacro.spring.data.support;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.NotWritablePropertyException;

import com.nexacro.spring.data.support.bean.DefaultBean;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroBeanWrapperTest.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 4.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 4.     Park SeongMin     최초 생성
 * </pre>
 */

public class NexacroBeanWrapperTest {

    @Test
    public void testInstantiateClass() {
        
        NexacroBeanWrapper accessor = createAccessor(DefaultBean.class);
        Object insatance = accessor.getInsatance();
        assertNotNull(insatance);
        
        if(!(insatance instanceof DefaultBean)) {
            fail("instantiate class must be instance of " + DefaultBean.class);
        }
    }
    
    @Test
    public void testSetPropertyValue() {
        
        NexacroBeanWrapper accessor = createAccessor(DefaultBean.class);
        accessor.setPropertyValue("firstName", "tom");
        DefaultBean target = (DefaultBean) accessor.getInsatance();
        assertTrue("Set name to tom", target.getFirstName().equals("tom"));
    }
    
    @Rule 
    public ExpectedException thrown= ExpectedException.none();
    
    @Test
//    @Test (expected=NotWritablePropertyException.class)
    public void testSetInvalidPropertyValue() {
        thrown.expect(NotWritablePropertyException.class);
        thrown.expectMessage("Invalid property 'invalid'");
        
        NexacroBeanWrapper accessor = createAccessor(DefaultBean.class);
        accessor.setPropertyValue("invalid", "tom");
        DefaultBean target = (DefaultBean) accessor.getInsatance();
        Assert.assertEquals("Set name to tom", "tom", target.getFirstName());
    }
    
    private NexacroBeanWrapper createAccessor(Class clazz) {
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(clazz);
        return beanWrapper;
    }
    
}
