package com.weijie.firerunning.bean;

/**
 * 每公里所用的时间
 * @author weijie
 *
 */
public class KileMeterSpeed {

	//kilometer按照1、2、3.....
	public int kilometer;
	public String time;
	public KileMeterSpeed(int kilometer, String time) {
		this.kilometer = kilometer;
		this.time = time;
	}
	public KileMeterSpeed() {
	}
}
