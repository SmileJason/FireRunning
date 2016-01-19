package com.weijie.firerunning.bean;

import java.io.Serializable;

public class PlanBase implements Serializable {

	public static final int ITEM = 0;
	public static final int SECTION = 1;
	public int type;
	
	public int sectionPosition;
	public int listPosition;

}
