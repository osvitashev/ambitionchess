package util;

import java.util.Calendar;

public class ResettableArray32 {
	private int data[];
	private int capacity_data, size_data;
	
	public ResettableArray32(int capacity) {
		capacity_data=capacity;
		size_data=0;
		data = new int[capacity];
	}
	
	public int size() {
		return size_data;
	}
	
	public void reset() {
		size_data=0;
	}
	
	public int get(int i) {
		assert i < size_data;
		return data[i];
	}
	
	/**
	 * appends a value to the array
	 */
	public void add(int value) {
		assert capacity_data != size_data;
		data[size_data++]=value;
	}

}
