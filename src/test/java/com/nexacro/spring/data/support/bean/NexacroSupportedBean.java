package com.nexacro.spring.data.support.bean;

import com.nexacro.spring.data.DataSetRowTypeAccessor;
import com.nexacro.spring.data.DataSetSavedDataAccessor;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : NexacroBean.java
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

public class NexacroSupportedBean extends DefaultBean implements DataSetRowTypeAccessor, DataSetSavedDataAccessor<NexacroSupportedBean>{

    private int rowType;
    private NexacroSupportedBean savedData;
    
    @Override
    public NexacroSupportedBean getData() {
        return savedData;
    }

    @Override
    public void setData(NexacroSupportedBean t) {
        this.savedData = t;
    }

    @Override
    public int getRowType() {
        return this.rowType;
    }

    @Override
    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

}
