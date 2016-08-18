package com.nexacro.spring.data.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.support.NexacroTestUtil.StaticPropertyBean;
import com.nexacro.spring.data.support.bean.DefaultBean;
import com.nexacro.spring.data.support.bean.UpperCaseBean;
import com.nexacro.spring.util.ReflectionUtil;
import com.nexacro.xapi.data.ColumnHeader;
import com.nexacro.xapi.data.ConstantColumnHeader;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.datatype.PlatformDataType;
import com.nexacro.xapi.tx.DataDeserializer;
import com.nexacro.xapi.tx.DataSerializerFactory;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;

public class DataSetToObjectConverterTest {

    private DataSetToObjectConverter converter;
    
    @Before
    public void setUp() {
        converter = new DataSetToObjectConverter();
    }
    
    @Test
    public void testSupportedType() {
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        source = DataSet.class;
        target = Object.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
    }
    
    @Test
	public void testConvertDataSetBeanToObject() throws IOException, PlatformException {

		String responseFileName = "src/test/java/com/nexacro/spring/resolve/httpRequestForMap.xml";
		InputStream responseInputStream = new FileInputStream(new File(responseFileName));

		DataDeserializer deserializer = DataSerializerFactory.getDeserializer(PlatformType.CONTENT_TYPE_XML);
		PlatformData readData = deserializer.readData(responseInputStream, null, PlatformType.DEFAULT_CHAR_SET);
    	
    	DataSet dataSet = readData.getDataSet("ds");
    	
		ConvertDefinition definition = new ConvertDefinition("ds");
		definition.setGenericType(Object.class);
		
		Object obj = null;
		try {
			obj = converter.convert(dataSet, definition);
		} catch (NexacroConvertException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		if (!(obj instanceof Object)) {
			Assert.fail("It must be Object");
		}
	}
    
    @Test
    public void testNullData() {
        
    	DataSet dataSet = null;
    	
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setGenericType(Object.class);
        
        Object ds = null;
        try {
            ds = converter.convert(dataSet, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        if (!(ds instanceof Object)) {
			Assert.fail("converted object must be implemented DataSet");
		}
    }
    
    @Test
    public void testUpperCase() {
    	
    	String[] columnNames = {
      		  "firstOnly"
      		  , "FIrstAndSecond"
      		  , "ALL"
        };
    	
    	String[] columnValues = {
    			"first"
    			, "second"
    			, "all"
    	};
    	
    	DataSet ds = new DataSet("dsUpper");
    	for(int i=0; i<columnNames.length; i++) {
    		ds.addColumn(columnNames[i], PlatformDataType.STRING);
    	}

    	ds.newRow();
    	for(int i=0; i<columnValues.length; i++) {
    		ds.set(0, columnNames[i], columnValues[i]);
    	}
    	
    	ConvertDefinition definition = new ConvertDefinition("dsUpper");
    	definition.setGenericType(UpperCaseBean.class);
    	
    	UpperCaseBean upperCaseBean = null;
    	try {
    		upperCaseBean = (UpperCaseBean) converter.convert(ds, definition);
		} catch (NexacroConvertException e) {
			Assert.fail(e.getMessage());
		}
    	
    	Assert.assertNotNull(upperCaseBean);
    	
    	Assert.assertEquals(columnValues[0], upperCaseBean.getFirstOnly());
    	Assert.assertEquals(columnValues[1], upperCaseBean.getFIrstAndSecond());
    	Assert.assertEquals(columnValues[2], upperCaseBean.getALL());
    	
    }
    
    @Test
    public void testUpperCaseInvalidColumnName() {
    	
    	String[] columnNames = {
      		  "FirstOnly"
      		  , "FIrstAndSecond"
      		  , "ALL"
        };
    	
    	String[] columnValues = {
    			"first"
    			, "second"
    			, "all"
    	};
    	
    	DataSet ds = new DataSet("dsUpper");
    	for(int i=0; i<columnNames.length; i++) {
    		ds.addColumn(columnNames[i], PlatformDataType.STRING);
    	}

    	ds.newRow();
    	for(int i=0; i<columnValues.length; i++) {
    		ds.set(0, columnNames[i], columnValues[i]);
    	}
    	
    	ConvertDefinition definition = new ConvertDefinition("dsUpper");
    	definition.setGenericType(UpperCaseBean.class);
    	
    	UpperCaseBean upperCaseBean = null;
    	try {
    		upperCaseBean = (UpperCaseBean) converter.convert(ds, definition);
		} catch (NexacroConvertException e) {
			Assert.fail(e.getMessage());
		}
    	
    	Assert.assertNotNull(upperCaseBean);
    	
    	// 필드의 첫번째 자리의 글자만 대문자일 경우 데이터셋의 컬럼 명칭은 소문자로 변환이 되어야 한다.
    	Assert.assertNull("Only the first letter of the name of the column position of the field, if one data set must be uppercase is converted to lowercase."
    			, upperCaseBean.getFirstOnly());
    	Assert.assertEquals(columnValues[1], upperCaseBean.getFIrstAndSecond());
    	Assert.assertEquals(columnValues[2], upperCaseBean.getALL());
    	
    }
    
    @Test
    public void testMapConvert() {
        
        Map beanMap = new HashMap();
        
//        Result result = new Result();
////        List<?> list = result.getList();
//        
//        
//        DefaultBean bean = new DefaultBean();
//        beanList.add(bean);
//        
//        Object[] array = beanList.toArray();
//        array[0].getClass();
//        
//        Class<? extends List> clazz= beanList.getClass();
//        
//        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
//        System.out.println(parameterizedType.getRawType());
//        
//        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//        for(Type type: actualTypeArguments) {
//            System.out.println(type);
//        }
        
        DefaultBean bean = new DefaultBean();
        beanMap.put(bean.getClass().getSimpleName(), bean);
        
        System.out.println("key = " + beanMap);
        Class<?> clazz = beanMap.getClass();
        System.out.println("clazz = " + clazz);
        ParameterizedType superclass = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = superclass.getActualTypeArguments();
        Class<?> actualdataType = null;
        if(types != null && types.length >0 && (types[0] instanceof Class<?>) ) {
            actualdataType = (Class<?>) (Class<?>) types[0];
        }
        System.out.println("actualdataType = " + actualdataType);
    }
    
    private static final String TYPE_CLASS_NAME_PREFIX = "class ";
    private static final String TYPE_INTERFACE_NAME_PREFIX = "interface ";
    
    public static String getClassName(Type type) {
        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
            className = className.substring(TYPE_CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length());
        }
        return className;
    }
    
    /**
     * Returns the {@code Class} object associated with the given {@link Type}
     * depending on its fully qualified name. 
     * 
     * @param type the {@code Type} whose {@code Class} is needed.
     * @return the {@code Class} object for the class with the specified name.
     * 
     * @throws ClassNotFoundException if the class cannot be located.
     * 
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    public static Class<?> getClass(Type type) throws ClassNotFoundException {
        String className = getClassName(type);
        if (className==null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }
    
    public static Type[] getParameterizedTypes(Object object) {
        Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }
        
        return ((ParameterizedType)superclassType).getActualTypeArguments();
    }
    
//    public static Class<?> getClass(Type type) {
//        if (type instanceof Class) {
//          return (Class) type;
//        }
//        else if (type instanceof ParameterizedType) {
//          return getClass(((ParameterizedType) type).getRawType());
//        }
//        else if (type instanceof GenericArrayType) {
//          Type componentType = ((GenericArrayType) type).getGenericComponentType();
//          Class<?> componentClass = getClass(componentType);
//          if (componentClass != null ) {
//            return Array.newInstance(componentClass, 0).getClass();
//          }
//          else {
//            return null;
//          }
//        }
//        else {
//          return null;
//        }
//      }
    
    
    private static class Result {
        private List<?> list;
        public void setList(List<?> list) {
            this.list = list;
        }
        
        public List<?> getList() {
            return null;
        }
        
    }
    
    
}
