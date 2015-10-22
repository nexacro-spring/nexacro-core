package com.nexacro.spring.data.metadata;

import java.util.ArrayList;

/**
 * <p>DataSet의 메타데이터 정보를 가지는 추상클래스이다.
 *
 * @author Park SeongMin
 * @since 08.06.2015
 * @version 1.0
 * @see
 */

public abstract class NexacroMetaData extends ArrayList {

    public abstract void setMetaData(Object obj);
    
    public abstract Object getMetaData();
    
}
