package com.nexacro.spring.context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.context.ApplicationContext;

public class SpringAppContext {

	/*
	 * @autowired 자동처리 시에는 아래와 같이 처리 하면 된다.
	 * 
	 * ApplicationContext ctx = AppContext.getApplicationContext(); 
	 * Honeypotbean honey = ctx.getBean(HoneyPotBean.class); 
	 * 
	 */
	public final static SpringAppContext INSTANCE = new SpringAppContext();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readLock = lock.readLock();
	private final Lock writeLock = lock.writeLock();

	private ApplicationContext ctx;
	
	private SpringAppContext() {
	}

	public static SpringAppContext getInstance() {
		return INSTANCE;
	}

	/**
	 * Injected from the class “ApplicationContextProvider” which is
	 * automatically loaded during Spring-Initialization.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		writeLock.lock();
		try {
			this.ctx = applicationContext;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Get access to the Spring ApplicationContext from everywhere in your
	 * Application.
	 * 
	 * @return
	 */
	public ApplicationContext getApplicationContext() {
		readLock.lock();
		try {
			return this.ctx;
		} finally {
			readLock.unlock();
		}
	}

}
