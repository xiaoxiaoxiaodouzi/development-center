package com.chinacreator.c2.project.bug;

public class BugConstans {
	
	/**
	 * bug类型
	 * @author hushowly
	 * 0关闭、1创建、2打开、3reopen、4延后、 5拒绝、 6完成
	 */
    public enum BUG_STATUS{
    	/**
    	 * 关闭
    	 */
    	CLOSED,

    	/**
    	 * 创建
    	 */
    	CREATED,
    	
    	/**
    	 * 打开
    	 */
    	OPENED,
    	
    	/**
    	 * 重新打开
    	 */
    	REOPEN,
    	
    	/**
    	 * 延后
    	 */
    	FIXED_LATER,
    	
    	/**
    	 * 拒绝
    	 */
    	REJECT,
    	
    	/**
    	 * 完成
    	 */
    	DONE
    }
}
