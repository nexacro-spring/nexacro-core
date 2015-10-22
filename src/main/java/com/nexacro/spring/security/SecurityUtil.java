package com.nexacro.spring.security;

import java.util.HashMap;
import java.util.Map;

import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.DataSetList;
import com.nexacro.xapi.data.PlatformData;
import com.nexacro.xapi.data.Variable;
import com.nexacro.xapi.data.VariableList;

/**
 * <p>
 * 
 * @author Park SeongMin
 * @since 2015. 8. 11.
 * @version 1.0
 * @see
 */
@Deprecated
public abstract class SecurityUtil {

    public static void checkSecurity(PlatformData platformData) throws SecurityException {

        VariableList variableList = platformData.getVariableList();
        int size = variableList.size();
        for(int i=0; i<size; i++) {
            Variable variable = variableList.get(i);
            checkSecurity(variable);
        }
        
        
        DataSetList dataSetList = platformData.getDataSetList();
        size = dataSetList.size();
        for(int i=0; i<size; i++) {
            DataSet ds = dataSetList.get(i);
            checkSecurity(ds);
        }
        
    }

    public static void checkSecurity(Variable variable) throws SecurityException {

        String value = null;
        try {
            value = variable.getString();
        } catch(Exception e) {
            // ignore
            return;
        }
        
        checkPathTraversal(value);
        // convert xss
        String convertedValue = convertXSS(value);
        if(value != null && !value.equals(convertedValue)) {
            variable.set(convertedValue);
        }
        
    }

    public static void checkSecurity(DataSet ds) throws SecurityException {

        int rowCount = ds.getRowCount();
        int colCount = ds.getColumnCount();
        for(int rowIndex=0; rowIndex<rowCount; rowIndex++) {
            
            boolean hasSavedRow = ds.hasSavedRow(rowIndex);
            for(int colIndex=0; colIndex<colCount; colIndex++) {
                
                String value = ds.getString(rowIndex, colIndex);
                
                checkPathTraversal(value);
                // convert xss
                String convertedValue = convertXSS(value);
                
                if(value != null && !value.equals(convertedValue)) {
                    ds.set(rowIndex, colIndex, value);
                }
                
                // saved data..
                if(hasSavedRow) {
                    value = ds.getSavedStringData(rowIndex, colIndex);
                    
                    checkPathTraversal(value);
                    // convert xss
                    convertedValue = convertXSS(value);
                    
                    if(value != null && !value.equals(convertedValue)) {
                        ds.setSavedData(rowIndex, colIndex, convertedValue);
                    }
                }
                
            }
            
        } // end data
        
        int removedCount = ds.getRemovedRowCount();
        for(int removedIndex=0; removedIndex<removedCount; removedIndex++) {
            
                for(int colIndex=0; colIndex<colCount; colIndex++) {
                String value = null;
                try {
                    value = ds.getRemovedStringData(removedIndex, colIndex);
                } catch(Exception e) {
                    // ignore
                    continue; 
                }
                
                checkPathTraversal(value);
                // convert xss
                String convertedValue = convertXSS(value);
                
                if(value != null && !value.equals(convertedValue)) {
                    ds.setRemovedData(removedIndex, colIndex, convertedValue);
                }
                
            }
        } // end removed data
        
    }
    
    public static void checkUploadFileExt(String fileName) throws SecurityException {

        if(fileName == null) {
            return;
        }
        
        Map<String, String> securityResult = new HashMap<String, String>();
//        // check file extention
//        securityResult = WebSecurityUtil.uploadFileExtCheck(fileName, "uploadExt");
//        if ("true".equals(securityResult.get("result"))) {
//          throw new SecurityException("Web Security Violation " + ((String)securityResult.get("securitySort")) + ", Violation Char:: ' " + ((String)securityResult.get("violationChar")) + "'");
//        }
//
//        // check file upload detour
//        securityResult = WebSecurityUtil.uploadFileExtCheck(fileName, "uploadDetour");
//        if ("true".equals(securityResult.get("result"))) {
//          throw new SecurityException("Web Security Violation : " + ((String)securityResult.get("securitySort")) + ", Violation Char:: ' " + ((String)securityResult.get("violationChar")) + "'");
//        }
        
    }
    
    public static void checkPathTraversal(String value) throws SecurityException {
        
        if(value == null) {
            return;
        }
        
        // check path traversal attack..
        Map<String, String> securityResult = new HashMap<String, String>();
//        try {
//            securityResult = WebSecurityUtil.checkDownloadParams(value);
//        } catch (UnsupportedEncodingException e) {
//            // logging..
//            throw new SecurityException("encoding format does not support. value="+value, e);
//        }
        
        if ("true".equals(securityResult.get("result"))) {
            // logging..
            
            throw new SecurityException("Web Security Violation :  " + ((String) securityResult.get("securitySort"))
                    + ", Violation Char:: ' " + ((String) securityResult.get("violationChar")) + "'");
        }
    }

    public static String convertXSS(String value) {
        if(value == null) {
            return null;
        }
        String convertedValue = value;
//        String convertedValue = WebSecurityUtil.convertXSSParam(value);
        return convertedValue;
    }
    
}
