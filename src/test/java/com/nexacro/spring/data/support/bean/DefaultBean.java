package com.nexacro.spring.data.support.bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author Park SeongMin
 * @since 08.04.2015
 * @version 1.0
 * @see
 */
public class DefaultBean {

    private int employeeId;
    private long access;
    private float height;
    private double commissionPercent;
    private boolean male;
    private byte[] image;

    private String firstName;
    private String lastName;
    private String email;

    private Date hireDate;
    private BigDecimal salary;

    private Object obj;

    private DefaultBean savedData;
    private int rowType;
    
    /**
     * @return the employeeId
     */
    public int getEmployeeId() {
        return employeeId;
    }

    /**
     * @param employeeId the employeeId to set
     */
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * @return the access
     */
    public long getAccess() {
        return access;
    }

    /**
     * @param access the access to set
     */
    public void setAccess(long access) {
        this.access = access;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return the commissionPercent
     */
    public double getCommissionPercent() {
        return commissionPercent;
    }

    /**
     * @param commissionPercent the commissionPercent to set
     */
    public void setCommissionPercent(double commissionPercent) {
        this.commissionPercent = commissionPercent;
    }

    /**
     * @return the male
     */
    public boolean isMale() {
        return male;
    }

    /**
     * @param male the male to set
     */
    public void setMale(boolean male) {
        this.male = male;
    }

    /**
     * @return the image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the hireDate
     */
    public Date getHireDate() {
        return hireDate;
    }

    /**
     * @param hireDate the hireDate to set
     */
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * @return the salary
     */
    public BigDecimal getSalary() {
        return salary;
    }

    /**
     * @param salary the salary to set
     */
    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    /**
     * @return the obj
     */
    public Object getObj() {
        return obj;
    }

    /**
     * @param obj the obj to set
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }

}
