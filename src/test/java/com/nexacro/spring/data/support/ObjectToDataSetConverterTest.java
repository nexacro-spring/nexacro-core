package com.nexacro.spring.data.support;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.support.NexacroTestUtil.StaticPropertyBean;
import com.nexacro.spring.data.support.bean.DefaultBean;
import com.nexacro.spring.util.ReflectionUtil;
import com.nexacro.xapi.data.ColumnHeader;
import com.nexacro.xapi.data.ConstantColumnHeader;
import com.nexacro.xapi.data.DataSet;

public class ObjectToDataSetConverterTest {

    private ObjectToDataSetConverter converter;
    
    @Before
    public void setUp() {
        converter = new ObjectToDataSetConverter();
    }
    
    @Test
    public void testSupportedType() {
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        source = Object.class;
        target = DataSet.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
    }
    
    @Test
	public void testConvertObjectBeanToDataSet() {

		DefaultBean defaultBean = new DefaultBean();
		defaultBean.setLastName("Kim");

		ConvertDefinition definition = new ConvertDefinition("ds");

		Object ds = null;
		try {
			ds = converter.convert(defaultBean, definition);
		} catch (NexacroConvertException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		if (!(ds instanceof DataSet)) {
			Assert.fail("converted object must be implemented DataSet");
		}

		DataSet dataset = (DataSet) ds;
		Assert.assertEquals("ds", dataset.getName());
		Assert.assertEquals("Kim", dataset.getObject(0, "lastName"));
	}
    
    @Test
	public void testConvertMapToDataSet() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("firstName", "firstName");
		map.put("lastName", "lastName");

		ConvertDefinition definition = new ConvertDefinition("ds");

		Object ds = null;
		try {
			ds = converter.convert(map, definition);
		} catch (NexacroConvertException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		if (!(ds instanceof DataSet)) {
			Assert.fail("converted object must be implemented DataSet");
		}

		DataSet dataset = (DataSet) ds;
		Assert.assertEquals("lastName", dataset.getObject(0, "lastName"));
	}
    
    @Test
    public void testNullData() {
        
        Object object = null;
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        Object ds = null;
        try {
            ds = converter.convert(object, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        if (!(ds instanceof DataSet)) {
			Assert.fail("converted object must be implemented DataSet");
		}
        
        DataSet dataset = (DataSet) ds;
        Assert.assertNotNull("dataset should not be null", dataset);
        Assert.assertEquals("ds", dataset.getName());
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
