package net.skcomms.search.backend.server;

import java.util.HashMap;
import java.util.Map;

import net.skcomms.search.backend.client.GreetingService;
import net.skcomms.search.backend.shared.ContactInfo;
import net.skcomms.search.backend.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {
	
	private static final Map<String, Person> friends = new HashMap<String, Person>();
	
	static {
		Person jang = new Jang();
		friends.put(jang.getName(), jang);
		
		Person kang = new Kang();
		friends.put(kang.getName(), kang);

		Person seok = new Seok();		
		friends.put(seok.getName(), seok);
		
		Person kim = new Kim();
		friends.put(kim.getName(), kim);
	}

	@Override
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 3 characters long");
		}


		// Escape data from the client to avoid cross-site script vulnerabilities.
		String name = escapeHtml(input);
		
		Person person = friends.get(name);
		if (person == null) {
			return name + ".. Have I met you?";
		}
		else {
			return "Welcome back, " + person.getName() + "!!";
		}

	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	/**
	 * 
	 */
	@Override
	public ContactInfo createNameCard(String name, String email) {
		if (!FieldVerifier.isValidName(name)) {
			throw new IllegalArgumentException(
					"Name must be at least 3 characters long");
		}

		if (!FieldVerifier.isValidEmail(email)) {
			throw new IllegalArgumentException(
					"Email must match the vaild format");
		}
		
		name = escapeHtml(name);
		email = escapeHtml(email);
		String category;
		
		if (friends.containsKey(name)) {
			category = "Friend";
		}
		else {
			category = "Unknown";
		}
		
		return new ContactInfo(name, email, category);
	}
}
