/**
 * 
 */
package net.skcomms.search.backend.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author jujang@sk.com
 * 
 */
public class ContactInfo implements IsSerializable {
	private String name;
	private String email;
	private String category;
	
	public ContactInfo() {
		this.name = null;
		this.email = null;
		this.category = null;
	}
	
	public ContactInfo(String name, String email, String category) {
		this.name = name;
		this.email = email;
		this.category = category;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContactInfo)) {
			return false;
		}
		
		ContactInfo other = (ContactInfo) obj;
		return this.name.equals(other.name) && this.email.equals(other.email);
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getCategory() {
		return category;
	}
}
