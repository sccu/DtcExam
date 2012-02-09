package net.skcomms.search.backend.server;

public class Visitor implements Person {

	String name = "";
	
	@Override
	public String getName() {
		
		if (name != "")
			return name;
		else
			return "Invalid name";
	}
	
	public void setName(String aName) {
		name = aName;
	}

}
