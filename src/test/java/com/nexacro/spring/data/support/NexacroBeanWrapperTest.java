package com.nexacro.spring.data.support;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.NotWritablePropertyException;

import com.nexacro.spring.data.support.bean.DefaultBean;

/**
 *
 * @author Park SeongMin
 * @since 08.04.2015
 * @version 1.0
 * @see
 */
public class NexacroBeanWrapperTest {

    @Test
    public void testInstantiateClass() {
        
        NexacroBeanWrapper accessor = createAccessor(DefaultBean.class);
        Object insatance = accessor.getInstance();
        assertNotNull(insatance);
        
        if(!(insatance instanceof DefaultBean)) {
            fail("instantiate class must be instance of " + DefaultBean.class);
        }
    }
    
    @Test
    public void testSetPropertyValue() {
        
        NexacroBeanWrapper accessor = createAccessor(DefaultBean.class);
        accessor.setPropertyValue("firstName", "tom");
        DefaultBean target = (DefaultBean) accessor.getInstance();
        assertTrue("Set name to tom", target.getFirstName().equals("tom"));
    }
    
    @Test
    public void testNotDefiendReadableProperty() {
        
        NexacroBeanWrapper accessor = createAccessor(NotDefinedStaticGetter.class);
        
        NexacroBeanProperty property = accessor.getProperty("staticProp");
        assertNull("not defined static getter must be null.", property);
        
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
        DefaultBean target = (DefaultBean) accessor.getInstance();
        Assert.assertEquals("Set name to tom", "tom", target.getFirstName());
    }
    
    private NexacroBeanWrapper createAccessor(Class clazz) {
        NexacroBeanWrapper beanWrapper = NexacroBeanWrapper.createBeanWrapper(clazz);
        return beanWrapper;
    }
    
    private static class NotDefinedStaticGetter {
    	
    	private static String staticProp;
    	
    	private String dummy;

		public static void setStaticProp(String staticProp) {
			NotDefinedStaticGetter.staticProp = staticProp;
		}

		public String getDummy() {
			return dummy;
		}

		public void setDummy(String dummy) {
			this.dummy = dummy;
		}
		
    }
}
