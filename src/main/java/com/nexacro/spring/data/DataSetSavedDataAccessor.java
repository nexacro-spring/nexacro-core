package com.nexacro.spring.data;


/**
 * <p>데이터 변환 시 <code>DataSet</code>의 원본데이터를 처리하기 위한 인터페이스이다.
 *
 * @author Park SeongMin
 * @since 07.31.2015
 * @version 1.0
 */
public interface DataSetSavedDataAccessor<T> {

	/**
	 * <code>Map</code>으로 데이터 변환 시 원본데이터를 획득하기 위한 식별자(key)이다.
	 */
    static final String NAME = "DataSetSavedData";
    
    /**
     * 데이터 변경시 저장된 원본데이터를 반환한다.
     * @return t 원본데이터
     */
    T getData();
    
    /**
     * 데이터 변경시 저장되는 원본데이터를 설정한다.
     * @param t 원본데이터
     */
    void setData(T t);
}
