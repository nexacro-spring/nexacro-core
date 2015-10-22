package com.nexacro.spring.data.convert;

import com.nexacro.xapi.data.DataSet;

/**
 * <p>DataSet의 데이터 변환 시  처리되는 EventObject이다.
 * <p>현재 데이터 변환이 이루어지는 컬럼의 명칭, 행의 위치를 저장하며, 추가적으로 데이터, 원본데이터, 삭제데이터 처리에 대한 식별값을 제공한다.
 * 
 * 
 * @author Park SeongMin
 * @since 08.09.2015
 * @version 1.0
 * @see
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

    /**
     * 컬럼의 명칭을 반환한다.
     * @return columnName
     */
    public String getColumnName() {
        DataSet ds = (DataSet) getSource();
        return ds.getColumn(columnIndex).getName();
    }
    
    /**
     * 원본데이터 처리중인지를 반환한다.
     * @return the isSavedData
     */
    public boolean isSavedData() {
        return isSavedData;
    }

    /**
     * 삭제 된 데이터를 처리중인지를 반환한다.
     * @return the isRemovedData
     */
    public boolean isRemovedData() {
        return isRemovedData;
    }

}
