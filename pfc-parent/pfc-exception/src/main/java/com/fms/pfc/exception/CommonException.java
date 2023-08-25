package com.fms.pfc.exception;

import java.util.Date;

public class CommonException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String moduleDesc;
	private Date exceptionThrownDate;

	public CommonException() {
	}

	public CommonException(String msg) {
		super(msg);
	}

	public CommonException(Throwable thr) {
		super(thr);
	}

	public CommonException(String msg, Throwable thr) {
		super(msg, thr);
	}

	public CommonException(String msg, String moduleDesc) {
		super(msg);
		this.moduleDesc = moduleDesc;
		this.exceptionThrownDate = new Date();
	}

	public CommonException(String msg, Throwable thr, String moduleDesc) {
		super(msg, thr);
		this.moduleDesc = moduleDesc;
		this.exceptionThrownDate = new Date();
	}

	public CommonException(Throwable thr, String moduleDesc) {
		super(thr);
		this.moduleDesc = moduleDesc;
		this.exceptionThrownDate = new Date();
	}

	public Date getExceptionThrownDate() {
		return exceptionThrownDate;
	}

	public String getModuleDesc() {
		return moduleDesc;
	}

}
