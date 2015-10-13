package com.nexacro.spring.data;


/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : DataSetSavedDataAccessor.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 7. 31.
 * @version 1.0
 * @see
 * @Modification Information
 * <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 7. 31.     Park SeongMin     최초 생성
 * </pre>
 */

public interface DataSetSavedDataAccessor<T> {

    static final String NAME = "DataSetSavedData";
    
    T getData();
    
    void setData(T t);
}
