package com.nexacro.spring.data.support;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.xapi.data.Variable;

/**
 *
 * @author Park SeongMin
 * @since 08.19.2015
 * @version 1.0
 * @see
 */
public class VariableToObjectConverterTest {

    private VariableToObjectConverter converter;
    
    @Before
    public void setUp() {
        converter = new VariableToObjectConverter();
    }
    
    @Test
    public void testSupportedType() {
        Class source = Variable.class;
        Class target;
        boolean canConvert;
        
        Map<Class, Object> supportedClassValues = NexacroTestUtil.getSupportedClassesValue();
        Set<Class> classSet = supportedClassValues.keySet();
        for(Class clazz: classSet) {
            target = clazz;
            canConvert = converter.canConvert(source, target);
            Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
        }
        
    }
    
    @Test
    public void testUnSupportedType() {
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        source = Variable.class;
        
        target = byte.class;
        canConvert= converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        target = char.class;
        canConvert= converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        target = short.class;
        canConvert= converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        // java.sql.Date support
//        target = java.sql.Date.class;
//        canConvert= converter.canConvert(source, target);
//        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
    }
    
    @Test
    public void testConvertAll() {
        
        Map<Class, Object> supportedClassValues = NexacroTestUtil.getSupportedClassesValue();
        
        Set<Class> classSet = supportedClassValues.keySet();
        for(Class clazz: classSet) {
            
            Object value = supportedClassValues.get(clazz);
            Variable var = new Variable("var");
            
            // variable에 Byte[]는 삽입되어서는 안된다. Byte[]는 byte[]로 변환되어 삽입되어져야 한다.
            if(Byte[].class.equals(clazz)) {
                Object object = NexacroConverterHelper.toObject(value);
                var.set(object);
            } else {
                var.set(value);
            }
            
            ConvertDefinition definition = new ConvertDefinition(var.getName());
            definition.setGenericType(clazz);
            
            Object convertedObj = null;
            try {
                convertedObj = converter.convert(var, definition);
            } catch (NexacroConvertException e) {
                Assert.fail("variable -> " + clazz + " convert failed. e="+e.getMessage());
            }
            
            Assert.assertNotNull("'variable -> " + clazz + "' converted value should not be null.", convertedObj);
            if(Byte[].class.equals(clazz)) {
                // Byte[]의 경우 실제데이터를 비교한다.
                Byte[] expected = (Byte[]) value;
                Byte[] actual = (Byte[]) convertedObj;
                Assert.assertEquals("Byte[] data was not properly converted.", expected.length, actual.length);
                for(int i=0; i<expected.length; i++) {
                    Assert.assertEquals("Byte[] data was not properly converted.", expected[i], actual[i]);
                }
                
            } else {
                Assert.assertEquals("'variable -> " + clazz + "' data was not properly converted", value, convertedObj);
            }
            
        }
            
    }
    
    @Test
    public void testUnSupportedByteArrInsertedVariable() {
        
        Byte[] byteArr = new Byte[]{1, 2};
        
        Variable var = new Variable("var");
        var.set(byteArr);
        
        
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(byteArr.getClass());
        Object convertedObj = null;
        try {
            convertedObj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail("variable -> Byte[] convert failed. e="+e.getMessage());
        }
        
        Assert.assertNull("Byte[] is inserted in the variable is not supported.", convertedObj);
        
    }
    
    @Test
    public void testInteger() {
        int value = 1;
        Variable var = Variable.createVariable("var", value);
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(int.class);
        
        Object obj = null;
        try {
            obj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("object should not be null.", obj);
        Assert.assertEquals(value, obj);
    }
    
    @Test
    public void testIntegerWrapper() {
        Integer value = new Integer(1);
        Variable var = Variable.createVariable("var", value);
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(Integer.class);
        
        Object obj = null;
        try {
            obj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("object should not be null.", obj);
        Assert.assertEquals(value, obj);
    }
    
    @Test
    public void testLong() {
        long value = 1l;
        Variable var = Variable.createVariable("var", value);
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(long.class);
        
        Object obj = null;
        try {
            obj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("object should not be null.", obj);
        Assert.assertEquals(value, obj);
    }
    
    @Test
    public void testLongWrapper() {
        Long value = new Long(1l);
        Variable var = Variable.createVariable("var", value);
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(Long.class);
        
        Object obj = null;
        try {
            obj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("object should not be null.", obj);
        Assert.assertEquals(value, obj);
    }
    
    @Test
    public void testFloat() {
        float value = 1.1f;
        Variable var = Variable.createVariable("var", value);
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(float.class);
        
        Object obj = null;
        try {
            obj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("object should not be null.", obj);
        Assert.assertEquals(value, obj);
    }
    
    @Test
    public void testFloatWrapper() {
        Float value = 1.1f;
        Variable var = Variable.createVariable("var", value);
        ConvertDefinition definition = new ConvertDefinition(var.getName());
        definition.setGenericType(Float.class);
        
        Object obj = null;
        try {
            obj = converter.convert(var, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("object should not be null.", obj);
        Assert.assertEquals(value, obj);
    }
    
}
