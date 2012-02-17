/**
 * 
 */
package net.skcomms.search.backend.client;

import net.skcomms.search.backend.shared.ContactInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;

/**
 * @author jujang@sk.com
 *
 */
final class ContactInfoCell extends AbstractCell<ContactInfo> {
	
	private static final ContactInfoCell instance = new ContactInfoCell();

	public static ContactInfoCell getInstacne() {
		return instance;
	}
	
	private ContactInfoCell() {}
	
	@Override
	public void render(Context context, ContactInfo contactInfo, SafeHtmlBuilder sb) {
		Image image = new Image();
		if (contactInfo.getCategory().equals("Unknown")) {
			image.setUrl("data:image/jpeg;base64,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");
		}
		else if (contactInfo.getName().equals("Jang")) {
			image.setUrl("http://ssl.gstatic.com/onebox-korea/e5adcf92403713df.jpg");
		}
		else {
			image.setUrl("http://t1.gstatic.com/images?q=tbn:ANd9GcSlCSbbm87uDJiPgjXdvfq6msCJf3v9Jb6XYrEn2CtkyYGrFhBJ3Q");
		}
		image.setSize("100%", "100%");
		sb.appendHtmlConstant("<table><tbody>");
		sb.appendHtmlConstant("<tr>");
			sb.appendHtmlConstant("<td rowspan=\"3\" style=\"width:62px; height:70px;\">");
				sb.append(SafeHtmlUtils.fromTrustedString(image.toString()));
			sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("<td><b>");
				sb.appendEscaped(contactInfo.getName());
			sb.appendHtmlConstant("</b></td>");
		sb.appendHtmlConstant("</tr>");
		sb.appendHtmlConstant("<tr>");
			sb.appendHtmlConstant("<td>");
				sb.appendEscaped(contactInfo.getEmail());
			sb.appendHtmlConstant("</td>");
		sb.appendHtmlConstant("</tr>");
		sb.appendHtmlConstant("<tr>");
			sb.appendHtmlConstant("<td>");
				sb.appendEscaped(contactInfo.getCategory());
			sb.appendHtmlConstant("</td>");
		sb.appendHtmlConstant("</tr>");
		sb.appendHtmlConstant("</tbody></table>");
	}
}