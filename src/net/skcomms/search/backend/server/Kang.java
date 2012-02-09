package net.skcomms.search.backend.server;

public class Kang implements Person {
	private String myName;
	public Kang(String name) {
		this.myName = name;
	}
	public void setName(String name) {
		this.myName = name;
	}
	@Override
	public String getName() {
		return this.myName;
	}

}
