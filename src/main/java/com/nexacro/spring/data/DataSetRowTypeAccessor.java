package com.nexacro.spring.data;

import com.nexacro.xapi.data.DataSet;


/**
 * <p>데이터 변환 시 <code>DataSet</code>의 행(row)의 형식(type)을 처리하기 위한 인터페이스이다.
 *
 * @author Park SeongMin
 * @since 07.31.2015
 * @version 1.0
 * @see DataSet#getRowType(int)
 */
public interface DataSetRowTypeAccessor {

	/**
	 * <code>Map</code>으로 데이터 변환 시 행의 타입을 획득하기 위한 식별자(key)이다.
	 */
    static final String NAME = "DataSetRowType";
    
    /**
     * 행(row)의 형식(type)을 반환한다.
     * @return rowType 행의 형식
     * @see DataSet#ROW_TYPE_NORMAL
	 * @see DataSet#ROW_TYPE_INSERTED
	 * @see DataSet#ROW_TYPE_UPDATED
	 * @see DataSet#ROW_TYPE_DELETED
     */
    int getRowType();

    /**
     * 행(row)의 형식(type)을 설정한다.
     * @param rowType 행의 형식
     * @see DataSet#ROW_TYPE_NORMAL
	 * @see DataSet#ROW_TYPE_INSERTED
	 * @see DataSet#ROW_TYPE_UPDATED
	 * @see DataSet#ROW_TYPE_DELETED
     */
    void setRowType(int rowType);
    
}
