package com.nexacro.spring.data.support;

import static junit.framework.Assert.assertEquals;

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
import com.nexacro.xapi.data.Debugger;
import com.nexacro.xapi.data.datatype.PlatformDataType;

/**
 *
 * @author Park SeongMin
 * @since 08.04.2015
 * @version 1.0
 * @see
 */
public class ListToDataSetConverterTest {

    private ListToDataSetConverter converter;
    
    @Before
    public void setUp() {
        converter = new ListToDataSetConverter();
    }
    
    @Test
    public void testSupportedType() {
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        source = List.class;
        target = DataSet.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
        
        // List sub class support.
        source = ArrayList.class;
        target = DataSet.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
    }
    
    @Test
    public void testUnSupportedType() {
        
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        source = Object.class;
        target = DataSet.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        
        List list = new ArrayList();
        list.add(new Object[]{1, 2});
        list.add(new Object[]{3, 4});
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        try {
            converter.convert(list, definition);
            Assert.fail("Object[] is unsupported type.");
        } catch (NexacroConvertException e) {
        }
        
    }
    
    @Test
    public void testConvertListBeanToDataSet() {
        
        List<DefaultBean> defaultBean = NexacroTestUtil.createDefaultBeans();
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        Object ds = null;
        try {
            ds = converter.convert(defaultBean, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(ds instanceof DataSet)) {
            Assert.fail("converted object must be implemented DataSet");
        }
        
        NexacroTestUtil.compareDefaultDataSet((DataSet) ds, 0);
        
    }
    
    @Test
    public void testConvertListMapToDataSet() {
        
        List<Map<String, Object>> defaultMap = NexacroTestUtil.createDefaultMaps();
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        Object ds = null;
        try {
            ds = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(ds instanceof DataSet)) {
            Assert.fail("converted object must be implemented DataSet");
        }
        
        NexacroTestUtil.compareDefaultDataSet((DataSet) ds, 0);
        
    }
    
    @Test
    public void testConvertListFlexibleMapToDataSet() {

    	String addColumnName = "otherColumn";
    	String addData = "otherColumnData";
    	
    	List<Map<String, Object>> defaultMap = NexacroTestUtil.createDefaultMaps();
    	Map<String, Object> otherStructureMap = new HashMap<String, Object>();
    	otherStructureMap.put(addColumnName, addData);
    	// add other structure map..
    	defaultMap.add(otherStructureMap);
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        Object ds = null;
        try {
            ds = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(ds instanceof DataSet)) {
            Assert.fail("converted object must be implemented DataSet");
        }
        
        DataSet convertedDs = (DataSet) ds;
        
        // original column 12, added column 1
        int expectedColumnCount = 12 + 1;
        int actualColumnCount = convertedDs.getColumnCount();
        Assert.assertEquals("other key in the Map column should be added.", expectedColumnCount, actualColumnCount);
        
        ColumnHeader column = convertedDs.getColumn(addColumnName);
        Assert.assertNotNull(column);
        
        // original row 2, added row 1
        int expectedRowCount = 2 + 1;
        int actualRowCount = convertedDs.getRowCount();
        Assert.assertEquals(expectedRowCount, actualRowCount);

        String addedData = convertedDs.getString(2, addColumnName);
        Assert.assertEquals(addData, addedData);
        
    }
    
    @Test
    public void testNullData() {
        
        List list = null;
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        DataSet ds = null;
        try {
            ds = converter.convert(list, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("dataset should not be null", ds);
        Assert.assertEquals("ds", ds.getName());
        
    }
    
    @Test
    public void testStaticColumns() {
        
        StaticPropertyBean staticBean;
        double commissionPercent = 10.0d;
        
        List<StaticPropertyBean> staticBeanList = new ArrayList<StaticPropertyBean>();
        staticBean = new StaticPropertyBean();
        staticBean.setName("tom");
        staticBean.setCommissionPercent(commissionPercent);
        staticBeanList.add(staticBean);
        
        staticBean = new StaticPropertyBean();
        staticBean.setName("david");
        staticBeanList.add(staticBean);
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        DataSet ds = null;
        try {
            ds = converter.convert(staticBeanList, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("converted list should not be null.", ds);
        
        int columnCount = ds.getColumnCount();
        Assert.assertEquals("two columns must be exist.", 2, ds.getColumnCount());
        
        ColumnHeader column = ds.getColumn("name");
        Assert.assertFalse(column.isConstant());
        
        column = ds.getColumn("commissionPercent");
        Assert.assertTrue(column.isConstant());
        ConstantColumnHeader constColumn = (ConstantColumnHeader) column;
        
        // check const column value
        Assert.assertEquals(commissionPercent, constColumn.getValue());
        
        Assert.assertEquals("tom", ds.getString(0, "name"));
        Assert.assertEquals("david", ds.getString(1, "name"));
        
    }
    
    @Test
    public void testAllowChangeStructure() {
    	// v1.0.0에서 Map의 null 컬럼 추가 되는 것을 확인한다.
    	List<Map<String, Object>> defaultMap = new ArrayList<Map<String, Object>>();
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col1", "value");
    		defaultMap.add(map);
    	}
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col2", "value");
    		defaultMap.add(map);
    	}
    	
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setDisallowChangeStructure(false); // set allow structure change
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        DataSet ds = (DataSet) dsObj;
        
        Assert.assertTrue(ds.containsColumn("col1"));
        Assert.assertTrue(ds.containsColumn("col2"));
        
    }
    
    @Test
    public void testDisallowChangeStructure() {
    	
    	List<Map<String, Object>> defaultMap = new ArrayList<Map<String, Object>>();
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col1", "value");
    		defaultMap.add(map);
    	}
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col2", "value");
    		defaultMap.add(map);
    	}
    	
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setDisallowChangeStructure(true); // set disallow structure change
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        DataSet ds = (DataSet) dsObj;
        
        Assert.assertTrue(ds.containsColumn("col1"));
        Assert.assertFalse(ds.containsColumn("col2"));

    }
    
    @Test
    public void testAllowChangeStructureWithSchemaDataSet() {
    	
    	DataSet schemaDataSet = new DataSet("schemaDa");
    	schemaDataSet.addColumn("defaultCol1", PlatformDataType.STRING);
    	
    	List<Map<String, Object>> defaultMap = new ArrayList<Map<String, Object>>();
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col1", "value");
    		defaultMap.add(map);
    	}
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col2", "value");
    		defaultMap.add(map);
    	}
    	
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setDisallowChangeStructure(false); // set allow structure change
        definition.setSchemaDataSet(schemaDataSet); // set schema dataSet
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        DataSet ds = (DataSet) dsObj;
        Assert.assertTrue(schemaDataSet.equals(ds));
        
        Assert.assertTrue(ds.containsColumn("defaultCol1"));
        Assert.assertTrue(ds.containsColumn("col1"));
        Assert.assertTrue(ds.containsColumn("col2"));
    	
    }
    
    @Test
    public void testAllowChangeStructureWithSchemaDataSetWithBean() {
    	
    	DataSet schemaDataSet = new DataSet("schemaDa");
    	schemaDataSet.addColumn("defaultCol1", PlatformDataType.STRING);
    	
    	List<DefaultBean> defaultBean = NexacroTestUtil.createDefaultBeans();
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setDisallowChangeStructure(false); // set allow structure change
        definition.setSchemaDataSet(schemaDataSet); // set schema dataSet
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultBean, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        

        DataSet ds = (DataSet) dsObj;
        Assert.assertTrue(schemaDataSet.equals(ds));
        Assert.assertTrue(ds.containsColumn("defaultCol1"));
        NexacroTestUtil.compareDefaultDataSet(ds, 1); // defaultColumn
        
    }
    
    @Test
    public void testDisallowChangeStructureWithSchemaDataSet() {
    	
    	DataSet schemaDataSet = new DataSet("schemaDa");
    	schemaDataSet.addColumn("defaultCol1", PlatformDataType.STRING);
    	
    	List<Map<String, Object>> defaultMap = new ArrayList<Map<String, Object>>();
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col1", "value");
    		defaultMap.add(map);
    	}
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col2", "value");
    		defaultMap.add(map);
    	}
    	
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setDisallowChangeStructure(true); // set disallow structure change
        definition.setSchemaDataSet(schemaDataSet); // set schema dataSet
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        DataSet ds = (DataSet) dsObj;
        Assert.assertTrue(schemaDataSet.equals(ds));
        
        Assert.assertTrue(ds.containsColumn("defaultCol1"));
        Assert.assertFalse(ds.containsColumn("col1"));
        Assert.assertFalse(ds.containsColumn("col2"));
    	
    }
    
    @Test
    public void testDisallowChangeStructureWithSchemaDataSetWithBean() {
    	
    	DataSet schemaDataSet = new DataSet("schemaDa");
    	schemaDataSet.addColumn("defaultCol1", PlatformDataType.STRING);
    	
    	List<DefaultBean> defaultBean = NexacroTestUtil.createDefaultBeans();
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setDisallowChangeStructure(true); // set disallow structure change
        definition.setSchemaDataSet(schemaDataSet); // set schema dataSet
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultBean, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        

        DataSet ds = (DataSet) dsObj;
        Assert.assertTrue(schemaDataSet.equals(ds));
        Assert.assertTrue(ds.containsColumn("defaultCol1"));
        
        Assert.assertEquals(ds.getColumnCount(), 1);
    	
    }
    
    @Test
    public void testConvertListMapToDataSetIncludeNullRow() {
        
    	List<Map<String, Object>> defaultMap = new ArrayList<Map<String, Object>>();
    	defaultMap.add(null); // add null row
    	
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col1", "value");
    		defaultMap.add(map);
    	}
    	defaultMap.add(null);     	// add null row
    	{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("col1", "value");
    		defaultMap.add(map);
    	}
    	
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultMap, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        DataSet ds = (DataSet) dsObj;
        
        assertEquals(defaultMap.size(), ds.getRowCount());
        Assert.assertTrue(ds.containsColumn("col1"));
        
    }
    
    @Test
    public void testConvertListBeanToDataSetIncludeNullRow() {
        
    	List<DefaultBean> defaultBean = NexacroTestUtil.createDefaultBeans();
    	defaultBean.add(0, null); // add null row
    	defaultBean.add(null); // add null row
        
        ConvertDefinition definition = new ConvertDefinition("ds");
        
        Object dsObj = null;
        try {
        	dsObj = converter.convert(defaultBean, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        DataSet ds = (DataSet) dsObj;
        
        assertEquals(defaultBean.size(), ds.getRowCount());
        Assert.assertTrue(ds.containsColumn("employeeId"));
        
    }
    
    @Test
    public void testUpperCase() {
        
    }
    
    @Test
    public void testLowerCase() {
        
    }
    
    // 
    @Test
    public void testNotSupportedRowType() {
    }
    
    @Test
    public void testNotSupportedSavedData() {
    }
    
    @Test
    public void testNotSupportedRemovedData() {
    }
    
    
    
    
    
    
    
    @Test
    public void testListConvert() {
        
        List<DefaultBean> beanList = new ArrayList<DefaultBean>();
        
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
        beanList.add(bean);
        
        System.out.println("key = " + beanList);
        Class<?> clazz = beanList.getClass();
        System.out.println("clazz = " + clazz);
        ParameterizedType superclass = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] types = superclass.getActualTypeArguments();
        Class<?> actualdataType = null;
        if(types != null && types.length >0 && (types[0] instanceof Class<?>) ) {
            actualdataType = (Class<?>) (Class<?>) types[0];
        }
        System.out.println("actualdataType = " + actualdataType);
    }
    
    @Test
    public void testListType() {
        
        List<DefaultBean> beanList = new ArrayList<DefaultBean>();
        Type[] parameterizedTypes = getParameterizedTypes(beanList);
        
        Class<?> class1;
        try {
            class1 = getClass(parameterizedTypes[0]);
            System.out.println(class1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
//        List<DefaultBean> beanList = new ArrayList<DefaultBean>();
//        
//        Class<?> class1 = getClass(beanList.getClass());
//        System.out.println(class1);
//        System.out.println(getClass(class1));
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
