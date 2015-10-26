package com.nexacro.spring.data.support.bean;

import java.util.List;

/**
 * 
 * @author Park SeongMin
 * @since 08.10.2015
 * @version 1.0
 * @see
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
