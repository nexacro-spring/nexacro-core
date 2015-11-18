package com.nexacro.spring.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.data.datatype.PlatformDataType;
import com.nexacro.xapi.tx.DataDeserializer;
import com.nexacro.xapi.tx.DataSerializerFactory;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformType;

/**
 * 
 * @author Park SeongMin
 * @since 2015. 8. 18.
 * @version 1.0
 * @see
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/context-*.xml" } )
public class NexacroFirstRowHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    public void testSendDataSet() {
    	
    	FirstRowTrackableServletOutputStream outputStream = new FirstRowTrackableServletOutputStream();
        NexacroFirstRowHandler firstRowHandler = createFirstRowHandler(outputStream);
        
        String sendDataSetName = "ds1";
        DataSet ds = new DataSet(sendDataSetName);
        ds.addColumn("column1", PlatformDataType.INT);
        ds.addColumn("column2", PlatformDataType.STRING);

        int dataCount = 1000;
        for(int i=0; i<dataCount; i++) {
        	int newRow = ds.newRow();
        	ds.set(newRow, "column1", i);
        	ds.set(newRow, "column2", "string"+i);
        }
        
        try {
			firstRowHandler.sendDataSet(ds);
		} catch (PlatformException e) {
			Assert.fail("send dataset failed. e=" + e);
		}
        
     // end tag
        try {
            NexacroFirstRowAccessor.end(firstRowHandler);
        } catch (PlatformException e) {
            Assert.fail("fail. call end method. e=" + e);
        }
        
        PlatformData platformData = readPlatformData(outputStream);
     
        Assert.assertNotNull("first row function is not work. check it.", platformData);
        
        DataSet sendedDataSet = platformData.getDataSet(sendDataSetName);
        Assert.assertNotNull("first row function is not work. check it.", sendedDataSet);
        
        int actualRowCount = sendedDataSet.getRowCount();
        int expectedRowCount = dataCount;
        Assert.assertEquals("sended row count not same.", expectedRowCount, actualRowCount);
        
    }
    
    @Test
    public void testSendPlatformType() {
    	
    }
    
    @Test
    public void testSendVariableAfterSendDataSet() {
        
        FirstRowTrackableServletOutputStream outputStream = new FirstRowTrackableServletOutputStream();
        NexacroFirstRowHandler firstRowHandler = createFirstRowHandler(outputStream);
        
        DataSet ds = new DataSet("ds1");
        try {
            firstRowHandler.sendDataSet(ds);
        } catch (PlatformException e) {
            Assert.fail("send dataset failed. e=" + e);
        }
        
        Variable var = Variable.createVariable("var1", "test");
        try {
            firstRowHandler.sendVariable(var);
            Assert.fail("DataSet aleady sended. can't send a variable after sending dataSet.");
        } catch (PlatformException e) {
        }
    }

    private NexacroFirstRowHandler createFirstRowHandler(ServletOutputStream outputStream) {
        
        // response를 생성한다.
        FirstRowHttpResponse httpServletResponse = new FirstRowHttpResponse();

        // output stream을 입력합니다.
        httpServletResponse.setOutputStream(outputStream);

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("text/xml;charset=UTF-8");
        
        NexacroFirstRowHandler firstRowHandler = new NexacroFirstRowHandler(httpServletResponse);
        
        return firstRowHandler;
        
    }
    
    private PlatformData readPlatformData(FirstRowTrackableServletOutputStream outputStream) {
        
        // check outputstream..
        List<byte[]> flushedOutputList = outputStream.getFlushedByteList();
        String platformXml = "";
        int flushedSize = flushedOutputList.size();
        for (int listIndex = 0; listIndex < flushedSize; listIndex++) {
            byte[] outputBytes = (byte[]) flushedOutputList.get(listIndex);
            String xmlflagment = new String(outputBytes);
            platformXml = platformXml + xmlflagment;
            // the data of last flush by xapi, aways come a blank data. so check
            // without last data for blank data.
            if (listIndex != (flushedSize - 1) && xmlflagment.trim().equals("")) {
                Assert.fail("xml flagment must not be blank");
            }
        }
        
        DataDeserializer deserializer = DataSerializerFactory.getDeserializer(PlatformType.CONTENT_TYPE_XML);
        InputStream inputStream = new ByteArrayInputStream(platformXml.getBytes());

        PlatformData platformData = null;
        try {
            platformData = deserializer.readData(inputStream, null, "utf8");
        } catch (PlatformException e) {
            Assert.fail("get platformData failed. e=" + e);
        }
        
        return platformData;
    }
    
}
