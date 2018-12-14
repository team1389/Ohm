package com.team1389.util;

public class Pair<T, U> {

	protected T one;
	protected U two;
	public Pair(T one, U two){
		this.one = one;
		this.two = two;
	}
	
	public T getFirst(){
		return one;
	}
	
	public void setFirst(T one){
		this.one = one;
	}
	
	public U getSecond(){
		return two;
	}
	
	public void setSecond(U two){
		this.two = two;
	}
	
}
