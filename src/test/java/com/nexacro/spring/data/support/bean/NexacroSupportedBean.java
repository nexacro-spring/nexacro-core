package com.nexacro.spring.data.support.bean;

import com.nexacro.spring.data.DataSetRowTypeAccessor;
import com.nexacro.spring.data.DataSetSavedDataAccessor;

/**
 *
 * @author Park SeongMin
 * @since 08.04.2015
 * @version 1.0
 * @see
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
