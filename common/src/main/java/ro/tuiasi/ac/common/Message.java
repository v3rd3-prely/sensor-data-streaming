package ro.tuiasi.ac.common;

import java.io.Serializable;

public class Message implements Serializable{
	private String val;
	
	public String getVal() {
		return this.val;
	}
	public void setVal(String val) {
		this.val = val;
	}
}
