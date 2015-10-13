package com.nexacro.spring.data.support;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nexacro.spring.data.convert.ConvertDataSetEvent;
import com.nexacro.spring.data.convert.ConvertVariableEvent;
import com.nexacro.spring.data.convert.NexacroConvertListener;
import com.nexacro.xapi.data.DataSet;
import com.nexacro.xapi.data.Variable;

/**
 * <pre>
 * Statements
 * </pre>
 *
 * @ClassName   : AbstractNexacroConverter.java
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

public abstract class AbstractListenerHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /* EventListenerList */
    private EventListenerList listenerList;
    
    /**
     * <code>NexacroConverterListener</code>를 등록한다.
     *
     * @param listener NexacroConverterListener
     * @see #removeListener(NexacroConvertListener)
     */
    public void addListener(NexacroConvertListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("addListener=" + listener);
        }
        
        listenerList.add(NexacroConvertListener.class, listener);
    }
    
    /**
     * <code>NexacroConverterListener</code>를 제거한다.
     *
     * @param listener NexacroConverterListener
     * @see #addListener(NexacroConvertListener)
     */
    public void removeListener(NexacroConvertListener listener) {
        if (listenerList == null || listenerList.getListenerCount() == 0) {
            int listenerCount = (listenerList == null) ? -1 : listenerList.getListenerCount();

            if (logger.isDebugEnabled()) {
                logger.debug("removeListener:"
                        + " listenerCount=" + listenerCount
                        + ", listener=" + listener);
            }

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("removeListener=" + listener);
        }

        listenerList.remove(NexacroConvertListener.class, listener);
    }
    
    public Object fireDataSetConvertedValue(DataSet ds, Object targetValue, int rowIndex, int columnIndex, boolean isSavedData, boolean isRemovedData) {
        if (listenerList == null || listenerList.getListenerCount() == 0) {
            return targetValue;
        }
        
        ConvertDataSetEvent event = new ConvertDataSetEvent(ds, targetValue, rowIndex, columnIndex, isSavedData, isRemovedData);
        Object[] listeners = listenerList.getListenerList();
        
        for (int i=0; i<listeners.length; i += 2) {
            if (listeners[i] == NexacroConvertListener.class) {
                ((NexacroConvertListener) listeners[i+1]).convertedValue(event);
            }
        }
        
        return event.getValue();
        
    }
    
    public Object fireVariableConvertedValue(Variable var, Object targetValue) {
        if (listenerList == null || listenerList.getListenerCount() == 0) {
            return targetValue;
        }
        
        ConvertVariableEvent event = new ConvertVariableEvent(var, targetValue);
        Object[] listeners = listenerList.getListenerList();

        for (int i=0; i<listeners.length; i += 2) {
            if (listeners[i] == NexacroConvertListener.class) {
                ((NexacroConvertListener) listeners[i+1]).convertedValue(event);
            }
        }
        
        return event.getValue();
        
    }
    
}
