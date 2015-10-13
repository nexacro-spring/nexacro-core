package com.nexacro.spring.data.support.bean;

import java.util.List;

/**
 * <pre>
 * Statements
 * </pre>
 * 
 * @ClassName : ExtendedBean.java
 * @Description : 클래스 설명을 기술합니다.
 * @author Park SeongMin
 * @since 2015. 8. 10.
 * @version 1.0
 * @see
 * @Modification Information
 * 
 *               <pre>
 *     since          author              description
 *  ===========    =============    ===========================
 *  2015. 8. 10.     Park SeongMin     최초 생성
 * </pre>
 */

public class UnsupportedBean extends DefaultBean {

    private List<String> stringList;

    private DefaultBean unsupportedBean;

    /**
     * @return the unsupportedBean
     */
    public DefaultBean getUnsupportedBean() {
        return unsupportedBean;
    }

    /**
     * @param unsupportedBean
     *            the unsupportedBean to set
     */
    public void setUnsupportedBean(DefaultBean unsupportedBean) {
        this.unsupportedBean = unsupportedBean;
    }

    /**
     * @return the stringList
     */
    public List<String> getStringList() {
        return stringList;
    }

    /**
     * @param stringList
     *            the stringList to set
     */
    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

}
