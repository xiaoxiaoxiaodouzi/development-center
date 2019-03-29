package com.chinacreator.c2.project.log;


public class Change {
	
	private String type;     //属性关联对象类型
	private String name; //属性名称
	
	private Object oldVal;    //老值
    private Object newVal;    //新值
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getOldVal() {
		return oldVal;
	}
	public void setOldVal(Object oldVal) {
		this.oldVal = oldVal;
	}
	public Object getNewVal() {
		return newVal;
	}
	public void setNewVal(Object newVal) {
		this.newVal = newVal;
	}
    

	
	
}
