package com.nexacro.spring.data.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StopWatch;

import com.nexacro.spring.data.DataSetRowTypeAccessor;
import com.nexacro.spring.data.DataSetSavedDataAccessor;
import com.nexacro.spring.data.convert.ConvertDefinition;
import com.nexacro.spring.data.convert.NexacroConvertException;
import com.nexacro.spring.data.support.NexacroTestUtil.DataSetRowTypeBean;
import com.nexacro.spring.data.support.NexacroTestUtil.DataSetSavedDataBean;
import com.nexacro.spring.data.support.NexacroTestUtil.StaticPropertyBean;
import com.nexacro.spring.data.support.bean.DefaultBean;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.datatype.PlatformDataType;



/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : DataSetToListConverterTest.java
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

public class DataSetToListConverterTest {
    
    private DataSetToListConverter converter;
    
    @Before
    public void setUp() {
        converter = new DataSetToListConverter();
    }

    @Test
    public void testSupportedType() {
        Class<?> source;
        Class<?> target;
        
        source = DataSet.class;
        target = List.class;
        boolean canConvert = converter.canConvert(source, target);
        
        Assert.assertTrue(source + " to " + target + " must be converted", canConvert);
    }
    
    @Test
    public void testUnSupportedType() {
        Class<?> source;
        Class<?> target;
        boolean canConvert;
        
        source = DataSet.class;
        target = ArrayList.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
        
        source = List.class;
        target = DataSet.class;
        canConvert = converter.canConvert(source, target);
        Assert.assertFalse(source + " to " + target + " can not convertible", canConvert);
    }
    
    @Test
    public void testConvertDataSetToListBean() {
        
        DataSet defaultDataSet = NexacroTestUtil.createDefaultDataSet();
        
        ConvertDefinition definition = new ConvertDefinition(defaultDataSet.getName());
        definition.setGenericType(DefaultBean.class); // for bean
        
        Object convertedList = null;
        try {
            convertedList = converter.convert(defaultDataSet, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        NexacroTestUtil.compareDefaultBeans((List<DefaultBean>) convertedList);
        
    }
    
    @Test
    public void testConvertDataSetToListMap() {
        
        DataSet defaultDataSet = NexacroTestUtil.createDefaultDataSet();
        
        ConvertDefinition definition = new ConvertDefinition(defaultDataSet.getName());
        definition.setGenericType(Map.class); // for map
        
        Object convertedList = null;
        try {
            convertedList = converter.convert(defaultDataSet, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        NexacroTestUtil.compareDefaultMaps((List<Map<String, Object>>) convertedList);
        
    }
    
    @Test
    public void testUndefinedGenericType() {
        DataSet defaultDataSet = NexacroTestUtil.createDefaultDataSet();
        
        ConvertDefinition definition = new ConvertDefinition(defaultDataSet.getName());
//        definition.setGenericType(DefaultBean.class);
        
        Object convertedList = null;
        try {
            convertedList = converter.convert(defaultDataSet, definition);
            Assert.fail("generic type not declared. exception must be occured.");
        } catch (NexacroConvertException e) {
            // nothing..
        }
    }
    
    @Test
    public void testNullData() {
        
        DataSet ds = null;
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setGenericType(DefaultBean.class);
        
        List list = null;
        try {
            list = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("converted list should not be null", list);
        Assert.assertEquals(ArrayList.class, list.getClass());
        
    }
    
    @Test
    public void testStaticColumns() {
        
        double commissionPercent = 10.0d;
        
        DataSet ds = new DataSet("static");
        ds.addConstantColumn("commissionPercent", PlatformDataType.DOUBLE, commissionPercent);
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow;
        newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        newRow = ds.newRow();
        ds.set(newRow, "name", "david");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(StaticPropertyBean.class);
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            Assert.fail(e.getMessage());
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        StaticPropertyBean staticBean = (StaticPropertyBean) convertedList.get(0);
        Assert.assertEquals("tom", staticBean.getName());
        Assert.assertEquals(commissionPercent, staticBean.getCommissionPercent());
        
        staticBean = (StaticPropertyBean) convertedList.get(1);
        Assert.assertEquals("david", staticBean.getName());
        Assert.assertEquals(commissionPercent, staticBean.getCommissionPercent());
        
    }
    
    @Test
    public void testUpperCase() {
        
    }
    
    @Test
    public void testLowerCase() {
        
    }
    
    @Test
    public void testDataSetRowTypeWithBean() {
        
        Class convertedType = DataSetRowTypeBean.class;// for bean
        
        DataSet ds = new DataSet("dsRowType");
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        // update..
        ds.startStoreDataChanges();
        ds.set(newRow, "name", "david");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(convertedType); 
        
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        int expectedCnt = ds.getRowCount();
        int actualCnt = convertedList.size();
        Assert.assertEquals(expectedCnt, actualCnt);
        
        DataSetRowTypeBean rowTypeBean = (DataSetRowTypeBean) convertedList.get(0);
        Assert.assertEquals("david", rowTypeBean.getName());
        // expected DataSet.ROW_TYPE_NAME_UPDATED
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_UPDATED, rowTypeBean.getRowType());
        
    }
    
    @Test
    public void testDataSetRowTypeWithMap() {
        
        Class convertedType = Map.class;
        
        DataSet ds = new DataSet("dsRowType");
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        // update..
        ds.startStoreDataChanges();
        ds.set(newRow, "name", "david");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(convertedType);
        
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        int expectedCnt = ds.getRowCount();
        int actualCnt = convertedList.size();
        Assert.assertEquals(expectedCnt, actualCnt);
        
        Map rowTypeMap = (Map) convertedList.get(0);
        Assert.assertEquals("david", rowTypeMap.get("name"));
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_UPDATED, rowTypeMap.get(DataSetRowTypeAccessor.NAME));
        
    }
    
    @Test
    public void testDataSetSavedDataWithBean() {
        
        Class convertedType = DataSetSavedDataBean.class;// for bean
        
        DataSet ds = new DataSet("dsRowType");
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        // update..
        ds.startStoreDataChanges();
        ds.set(newRow, "name", "david");
        
        // inserted..
        newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(convertedType); 
        
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        int expectedCnt = ds.getRowCount();
        int actualCnt = convertedList.size();
        Assert.assertEquals(expectedCnt, actualCnt);
        
        DataSetSavedDataBean bean, savedData = null;
        String expectedSavedData, actualSavedData;
        
        // first row is updated data..
        bean = (DataSetSavedDataBean) convertedList.get(0);
        Assert.assertEquals("david", bean.getName());
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_UPDATED, bean.getRowType());
        savedData = bean.getData();
        expectedSavedData = "tom";
        actualSavedData = savedData.getName();
        Assert.assertEquals("saved data should be maintained.", expectedSavedData, actualSavedData);
        
        
        // second row is inserted data..
        bean = (DataSetSavedDataBean) convertedList.get(1);
        Assert.assertEquals("tom", bean.getName());
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_INSERTED, bean.getRowType());
        savedData = bean.getData();
        Assert.assertNull("additional data should be null data.", savedData);
    }
    
    @Test
    public void testDataSetSavedDataWithMap() {
        
        Class convertedType = Map.class; // for map
        
        DataSet ds = new DataSet("dsRowType");
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        // update..
        ds.startStoreDataChanges();
        ds.set(newRow, "name", "david");
        
        // inserted..
        newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(convertedType); 
        
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        int expectedCnt = ds.getRowCount();
        int actualCnt = convertedList.size();
        Assert.assertEquals(expectedCnt, actualCnt);
        
        Map<String, Object> dataMap, savedData = null;
        String expectedSavedData, actualSavedData;
        
        // first row is updated data..
        dataMap = (Map<String, Object>) convertedList.get(0);
        Assert.assertEquals("david", dataMap.get("name"));
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_UPDATED, dataMap.get(DataSetRowTypeAccessor.NAME));
        
        savedData = (Map<String, Object>) dataMap.get(DataSetSavedDataAccessor.NAME);
        expectedSavedData = "tom";
        actualSavedData = (String) savedData.get("name");
        Assert.assertEquals("saved data should be maintained.", expectedSavedData, actualSavedData);

        // second row is inserted data..
        dataMap = (Map<String, Object>) convertedList.get(1);
        Assert.assertEquals("tom", dataMap.get("name"));
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_INSERTED, dataMap.get(DataSetRowTypeAccessor.NAME));
        savedData = (Map<String, Object>) dataMap.get(DataSetSavedDataAccessor.NAME);
        Assert.assertNull("additional data should be null data.", savedData);
    }
    
    @Test
    public void testRemovedDataWithBean() {
        
        Class convertedType = DataSetSavedDataBean.class;
        
        DataSet ds = new DataSet("dsRowType");
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        // delete..
        ds.startStoreDataChanges();
        ds.set(newRow, "name", "david");
        ds.removeRow(0);
        
        // inserted..
        newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(convertedType); // for bean
        
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        int expectedCnt = ds.getRowCount() + 1; // +1 is removed data..
        int actualCnt = convertedList.size();
        Assert.assertEquals(expectedCnt, actualCnt);
        
        DataSetSavedDataBean bean, savedData = null;
        
        // first row is inserted data.
        bean = (DataSetSavedDataBean) convertedList.get(0);
        Assert.assertEquals("tom", bean.getName());
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_INSERTED, bean.getRowType());
        savedData = bean.getData();
        Assert.assertNull("additional data should be null data.", savedData);
        
        // second row is deleted data..
        bean = (DataSetSavedDataBean) convertedList.get(1);
        Assert.assertEquals("deleted data must be saved data", "tom", bean.getName()); // original data..
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_DELETED, bean.getRowType());
        savedData = bean.getData();
        Assert.assertNull("saved data must be null.", savedData);
    }
    
    @Test
    public void testRemovedDataWithMap() {
        
        Class convertedType = Map.class;
        
        DataSet ds = new DataSet("dsRowType");
        ds.addColumn("name", PlatformDataType.STRING);
        int newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        // delete..
        ds.startStoreDataChanges();
        ds.set(newRow, "name", "david");
        ds.removeRow(0);
        
        // inserted..
        newRow = ds.newRow();
        ds.set(newRow, "name", "tom");
        
        ConvertDefinition definition = new ConvertDefinition(ds.getName());
        definition.setGenericType(convertedType); // for bean
        
        List convertedList = null;
        try {
            convertedList = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        
        if(!(convertedList instanceof List)) {
            Assert.fail("converted object must be implemented List");
        }
        
        Assert.assertNotNull("converted list should not be null.", convertedList);
        
        int expectedCnt = ds.getRowCount() + 1; // +1 is removed data..
        int actualCnt = convertedList.size();
        Assert.assertEquals(expectedCnt, actualCnt);
        
        Map<String, Object> dataMap, savedData = null;
        String expectedSavedData, actualSavedData;
        
        // first row is inserted data.
        dataMap = (Map<String, Object>) convertedList.get(0);
        Assert.assertEquals("tom", dataMap.get("name"));
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_INSERTED, dataMap.get(DataSetRowTypeAccessor.NAME));
        savedData = (Map<String, Object>) dataMap.get(DataSetSavedDataAccessor.NAME);
        Assert.assertNull("additional data should be null data.", savedData);
        
        // second row is deleted data..
        dataMap = (Map<String, Object>) convertedList.get(1);
        Assert.assertEquals("deleted data must be saved data", "tom", dataMap.get("name")); // original data..
        Assert.assertEquals("rowType data should be maintained.", DataSet.ROW_TYPE_DELETED, dataMap.get(DataSetRowTypeAccessor.NAME));
        savedData = (Map<String, Object>) dataMap.get(DataSetSavedDataAccessor.NAME);
        Assert.assertNull("saved data must be null.", savedData);
    }
    
    @Test
    public void testStaticSpringBeanWrapper() {
        
        BeanWrapper wrapper = new BeanWrapperImpl(StaticPropertyBean.class);
        wrapper.setPropertyValue("commissionPercent", 10.0d);
//        System.out.println(wrapper.getPropertyValue("comissionPercent"));
        StaticPropertyBean bean = (StaticPropertyBean) wrapper.getWrappedInstance();
//        System.out.println(bean.getComissionPercent());
        
    }
    
//    @Test
    public void testStaticIbatis() {
        
//        StaticPropertyBean bean = new StaticPropertyBean();
//        bean.setCommissionPercent(10.0d);
//        
//        ClassInfo ref = ClassInfo.getInstance(StaticPropertyBean.class);
//        
//        Invoker setInvoker = ref.getSetInvoker("commissionPercent");
//        try {
//            setInvoker.invoke(bean, new Object[]{11.0d});
//        } catch (IllegalAccessException e1) {
//            e1.printStackTrace();
//        } catch (InvocationTargetException e1) {
//            e1.printStackTrace();
//        }
//        
//        Invoker getInvoker = ref.getGetInvoker("commissionPercent");
//        try {
//            Object invoke = getInvoker.invoke(bean, new Object[]{});
////            System.out.println(invoke);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
    }
    
    @Test
    public void testConvertPerformance() {
            
        StopWatch sw = new StopWatch(getClass().getSimpleName());
        
        int columnCount = 10;
        DataSet ds = new DataSet("ds");
        for(int i=0; i<columnCount; i++) {
            ds.addColumn("column"+i, PlatformDataType.STRING);
        }
        
        sw.start("set data");
        int rowCount = 10000;
        for(int rowIndex=0; rowIndex<rowCount; rowIndex++) {
            int newRow = ds.newRow();
            for(int colIndex=0; colIndex<columnCount; colIndex++) {
                ds.set(newRow, colIndex, "value"+rowIndex+colIndex);
            }
        }
        sw.stop();
        
        sw.start("get data");
        for(int rowIndex=0; rowIndex<rowCount; rowIndex++) {
            for(int colIndex=0; colIndex<columnCount; colIndex++) {
                ds.getString(rowIndex, colIndex);
            }
        }
        sw.stop();
        
        
        
        DataSetToListConverter converter = new DataSetToListConverter();
        ConvertDefinition definition = new ConvertDefinition("ds");
        definition.setGenericType(Map.class);
        try {
            sw.start("convert List<Map>");
            java.util.List convert = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
        } finally {
            sw.stop();
        }
        
        definition.setGenericType(DataSetObj.class);
        try {
            sw.start("convert List<DataSetObj>");
            java.util.List convert = converter.convert(ds, definition);
        } catch (NexacroConvertException e) {
            e.printStackTrace();
        } finally {
            sw.stop();
        }
        
        System.out.println(sw.prettyPrint());
        
    }
    
    
    public static class DataSetObj {
        private String column0;
        private String column1;
        private String column2;
        private String column3;
        private String column4;
        private String column5;
        private String column6;
        private String column7;
        private String column8;
        private String column9;
        public String getColumn0() {
            return column0;
        }
        public void setColumn0(String column0) {
            this.column0 = column0;
        }
        public String getColumn1() {
            return column1;
        }
        public void setColumn1(String column1) {
            this.column1 = column1;
        }
        public String getColumn2() {
            return column2;
        }
        public void setColumn2(String column2) {
            this.column2 = column2;
        }
        public String getColumn3() {
            return column3;
        }
        public void setColumn3(String column3) {
            this.column3 = column3;
        }
        public String getColumn4() {
            return column4;
        }
        public void setColumn4(String column4) {
            this.column4 = column4;
        }
        public String getColumn5() {
            return column5;
        }
        public void setColumn5(String column5) {
            this.column5 = column5;
        }
        public String getColumn6() {
            return column6;
        }
        public void setColumn6(String column6) {
            this.column6 = column6;
        }
        public String getColumn7() {
            return column7;
        }
        public void setColumn7(String column7) {
            this.column7 = column7;
        }
        public String getColumn8() {
            return column8;
        }
        public void setColumn8(String column8) {
            this.column8 = column8;
        }
        public String getColumn9() {
            return column9;
        }
        public void setColumn9(String column9) {
            this.column9 = column9;
        }

    }
}
