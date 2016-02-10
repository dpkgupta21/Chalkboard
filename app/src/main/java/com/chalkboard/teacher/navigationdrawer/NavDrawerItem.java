package com.chalkboard.teacher.navigationdrawer;

public class NavDrawerItem {
	
	private String title;
	private int icon;
	private int selectIcon;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;
	
	public NavDrawerItem(){}

	public NavDrawerItem(String title, int icon, int selectIcon){
		this.title = title;
		this.icon = icon;
		this.selectIcon = selectIcon;
	}

	public int getSelectIcon() {
		return selectIcon;
	}

	public void setSelectIcon(int selectIcon) {
		this.selectIcon = selectIcon;
	}

	public boolean isCounterVisible() {
		return isCounterVisible;
	}

	public void setIsCounterVisible(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}
	
	public void setCount(String count){
		this.count = count;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
}
