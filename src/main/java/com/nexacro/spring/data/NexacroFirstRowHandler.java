package com.nexacro.spring.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataSetList;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.data.VariableList;
import com.nexacro.xapi.tx.HttpPartPlatformResponse;
import com.nexacro.xapi.tx.PlatformException;
import com.nexacro.xapi.tx.PlatformRequest;
import com.nexacro.xapi.tx.PlatformType;

/**
 * 데이터 분할 전송을 위해 사용되는 <code>HttpPartPlatformResponse<code>를 이용하여 데이터를 전송한다.
 * 데이터의 전송은 <Code>Variable</Code> 부터 <Code>DataSet</Code> 순으로 전송 된다.
 * 
 * @author Park SeongMin
 * @since 08.05.2015
 * @version 1.0
 * @see
 */

public class NexacroFirstRowHandler {

    private PlatformRequest platformRequest;
    private HttpServletResponse httpResponse;
    private HttpPartPlatformResponse partPlatformResponse = null;
    private PrintWriter writer = null;

    private String contentType;
    
    private DataSet data = null;
    private boolean isFirstRowFired = false;
    private boolean dataSetSended = false;
    private boolean isInit = false;
    private boolean isEncrypted = false;
    private Set sendOutVariableNameSet = new HashSet();
    private Set sendOutDataSetNameSet = new HashSet();

    /**
     * 
     * 
     *
     * @param httpServletResponse
     */
    public NexacroFirstRowHandler(HttpServletResponse httpServletResponse) {
        this(httpServletResponse, null);
    }
    
    public NexacroFirstRowHandler(HttpServletResponse httpServletResponse, PlatformRequest platformRequest) {
        if(httpServletResponse == null) {
            throw new IllegalArgumentException("HttpServletResponse should not be null.");
        }
        this.httpResponse = httpServletResponse;
        this.platformRequest = platformRequest;
    }
    
    public String getContentType() {
        return contentType;
    }

    /**
     * 
     * set send ContentType
     * <p>must be set before the transfer. 
     *
     * @param contentType
     * @see PlatformType#CONTENT_TYPE_XML
     * @see PlatformType#CONTENT_TYPE_BINARY
     * @see PlatformType#CONTENT_TYPE_SSV
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void sendPlatformData(PlatformData platformData) throws PlatformException {
        isFirstRowFired = true;

        VariableList variableList = platformData.getVariableList();
        DataSetList dataSetList = platformData.getDataSetList();

        for (int variableListIndex = 0; variableListIndex < variableList.size(); variableListIndex++) {
            sendVariable(variableList.get(variableListIndex));
        }

        for (int datasetListIndex = 0; datasetListIndex < dataSetList.size(); datasetListIndex++) {
            sendDataSet(dataSetList.get(datasetListIndex));
        }

    }
    
    public void sendVariable(Variable variable) throws PlatformException {
        isFirstRowFired = true;
        intPartPlatformResponse();
        if (dataSetSended) {
            throw new PlatformException("DataSet aleady sended. can't send a variable after sending dataSet.");
        }

        partPlatformResponse.sendVariable(variable);
        sendOutVariableNameSet.add(variable.getName());

    }

    public void sendDataSet(DataSet dataSet) throws PlatformException {
        if (dataSet == null) {
            return;
        }

        isFirstRowFired = true;
        dataSetSended = true;
        intPartPlatformResponse();

        // 기존에 firstRow 로 전송 된 DataSet 잉여 data 전송.
        // if(data == ds) {
        if (data != null && !data.getName().equals(dataSet.getName())) {
            partPlatformResponse.sendDataSet(data);
        }

        partPlatformResponse.sendDataSet(dataSet);

        data = dataSet;
        sendOutDataSetNameSet.add(dataSet.getName());
    }

    public boolean isFirstRowStarted() {
        return isFirstRowFired;
    }

    private void intPartPlatformResponse() {
        if (!isInit) {
            if(platformRequest == null) {
                partPlatformResponse = new HttpPartPlatformResponse(httpResponse);
            } else {
                partPlatformResponse = new HttpPartPlatformResponse(httpResponse, platformRequest);
            }
            if(contentType != null) {
                partPlatformResponse.setContentType(contentType);
            }
            //setWriter();
            isInit = true;
        }
    }

    /**
     * 
     * check current first row status
     * <p><Code>PlatformType.CONTENT_TYPE_SSV</Code> or <Code>CONTENT_TYPE_BIN</Code> may not operate normally.
     * 
     * @return errorStatus
     */
    public boolean checkError() {
        setWriter();
        if (writer != null) {
            return writer.checkError();
        }

        return false;
    }

    private void setWriter() {
        // writer or outputstream choice
        if (this.httpResponse != null && this.writer != null) {
            try {
                this.writer = this.httpResponse.getWriter();
            } catch(UnsupportedEncodingException e) {
            } catch(IllegalStateException e) {
            } catch (IOException e) {
                // log.
            }
        }
    }
    
    int getSendOutVariableCount() {
        return sendOutVariableNameSet.size();
    }

    String[] getSendOutVariableNames() {

        List sendOutVariableNameList = new ArrayList();
        Iterator iterator = sendOutVariableNameSet.iterator();
        while (iterator.hasNext()) {
            sendOutVariableNameList.add(iterator.next());
        }

        String[] sendOutVariableNames = new String[sendOutVariableNameList.size()];
        return (String[]) sendOutVariableNameList.toArray(sendOutVariableNames);
    }

    int getSendOutDataSetCount() {
        return sendOutDataSetNameSet.size();
    }

    String[] getSendOutDataSetNames() {

        List sendOutDataSetNameList = new ArrayList();
        Iterator iterator = sendOutDataSetNameSet.iterator();
        while (iterator.hasNext()) {
            sendOutDataSetNameList.add(iterator.next());
        }

        String[] sendOutDataSetNames = new String[sendOutDataSetNameList.size()];
        return (String[]) sendOutDataSetNameList.toArray(sendOutDataSetNames);
    }
    
    HttpPartPlatformResponse getHttpPartPlatformResponse() {
        return partPlatformResponse;
    }

    void end() throws PlatformException {
        if (isInit) {
            isInit = false;
            partPlatformResponse.end();
        }

    }

}
