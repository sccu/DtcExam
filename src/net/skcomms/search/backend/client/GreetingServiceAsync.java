package net.skcomms.search.backend.client;


import net.skcomms.search.backend.shared.ContactInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	/**
	 * @param text
	 * @param text2
	 * @param asyncCallback
	 */
	void createNameCard(String name, String email, AsyncCallback<ContactInfo> asyncCallback);
}
