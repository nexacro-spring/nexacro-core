package com.nexacro.spring.data.convert;

import com.nexacro.xapi.data.DataSet;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : ConvertDataSetEvent.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 9.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 9.     Park SeongMin     최초 생성
 * </pre>
 */

public class ConvertDataSetEvent extends ConvertEvent {

    /* serialVersionUID */
    private static final long serialVersionUID = -174881582977983877L;

    private int rowIndex;
    
    private int columnIndex;
    
    private boolean isSavedData;
    private boolean isRemovedData;
    
    public ConvertDataSetEvent(DataSet source, Object targetValue, int rowIndex, int columnIndex) {
        this(source, targetValue, rowIndex, columnIndex, false, false);
    }
    
    public ConvertDataSetEvent(DataSet source, Object targetValue, int rowIndex, int columnIndex, boolean isSavedData, boolean isRemovedData) {
        super(source, targetValue);
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.isSavedData = isSavedData;
        this.isRemovedData = isRemovedData;
    }
    
    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @return the columnIndex
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    public String getColumnName() {
        DataSet ds = (DataSet) getSource();
        return ds.getColumn(columnIndex).getName();
    }
    
    /**
     * @return the isSavedData
     */
    public boolean isSavedData() {
        return isSavedData;
    }

    /**
     * @return the isRemovedData
     */
    public boolean isRemovedData() {
        return isRemovedData;
    }

}
