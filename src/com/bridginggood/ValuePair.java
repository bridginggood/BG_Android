package com.bridginggood;

public class ValuePair<K, V> {
	private K key;
	private V value;
	
	public ValuePair(K k, V v){
		key = k;
		value = v;
	}
	
	public void setKey(K k){
		key = k;
	}
	
	public void setValue(V v){
		value = v;
	}
	
	public K getKey(){
		return key;
	}
	
	public V getValue(){
		return value;
	}
}
