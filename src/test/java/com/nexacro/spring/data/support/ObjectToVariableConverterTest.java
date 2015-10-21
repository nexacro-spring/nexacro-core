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
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ObjectToVariableTest.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 19.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 19.     Park SeongMin     최초 생성
 * </pre>
 */

public class ObjectToVariableConverterTest {

    private ObjectToVariableConverter converter;
    
    @Before
    public void setUp() {
        converter = new ObjectToVariableConverter();
    }
    
    @Test
    public void testSupportedType() {
        Class target = Variable.class;
        boolean canConvert;
        
        Map<Class, Object> supportedClassValues = NexacroTestUtil.getSupportedClassesValue();
        Set<Class> classSet = supportedClassValues.keySet();
        for(Class clazz: classSet) {
            Class source = clazz;
            canConvert = converter.canConvert(source, target);
            Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
        }
        
    }
    
    @Test
    public void testUnSupportedType() {
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        target = Variable.class;
        
        source = byte.class;
        canConvert= converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        source = char.class;
        canConvert= converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        source = short.class;
        canConvert= converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
     // java.sql.Date support
//        source = java.sql.Date.class;
//        canConvert= converter.canConvert(source, target);
//        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
    }
    
    @Test
    public void testConvertAll() {
        
        Map<Class, Object> supportedClassValues = NexacroTestUtil.getSupportedClassesValue();
        
        Set<Class> classSet = supportedClassValues.keySet();
        for(Class clazz: classSet) {
            
            String variableName = "var";
            Object value = supportedClassValues.get(clazz);
            ConvertDefinition definition = new ConvertDefinition(variableName);
            
            Variable convertedVar = null;
            try {
                convertedVar = converter.convert(value, definition);
            } catch (NexacroConvertException e) {
                Assert.fail(clazz + " -> variable convert failed. e="+e.getMessage());
            }
            
            Assert.assertNotNull("'"+clazz + " -> variable' converted value should not be null.", convertedVar);
            
            String actualVariableName = convertedVar.getName();
            Assert.assertEquals(variableName, actualVariableName);
            
            // compare data.
            Object actualValue = convertedVar.getObject();
            if(Byte[].class.equals(clazz)) {
                // Byte[]의 경우 Variable은 byte[]로 존재하기 때문에 실제데이터를 비교한다.
                Byte[] expected = (Byte[]) value;
                byte[] actual = (byte[]) convertedVar.getBlob();
                Assert.assertEquals("Byte[] data was not properly converted.", expected.length, actual.length);
                for(int i=0; i<expected.length; i++) {
                    Assert.assertEquals("Byte[] data was not properly converted.", expected[i].byteValue(), actual[i]);
                }
                
            } else {
                Assert.assertEquals("'"+clazz + " -> variable' data was not properly converted.", value, actualValue);
            } 
            
        }
            
    }
    
    @Test
    public void testSupportAutomaticConversionByteArr() {
        
        Byte[] byteArr = new Byte[]{1, 2};
        
        ConvertDefinition definition = new ConvertDefinition("var");
        Variable convertedVar = null;
        try {
            convertedVar = converter.convert(byteArr, definition);
        } catch (NexacroConvertException e) {
            Assert.fail("variable -> Byte[] convert failed. e="+e.getMessage());
        }
        
        Assert.assertNotNull("'Byte[] -> variable' converted value should not be null.", convertedVar);
        
        byte[] actualByteArr = convertedVar.getBlob();
        Assert.assertNotNull("'Byte[] -> variable' converted value should not be null.", actualByteArr);
        Assert.assertEquals("Byte[] data was not properly converted.", byteArr.length, actualByteArr.length);
        
        for(int i=0; i<byteArr.length; i++) {
            Assert.assertEquals("Byte[] data was not properly converted.", byteArr[i].byteValue(), actualByteArr[i]);
        }
        
    }
    
}
