package net.skcomms.search.backend.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.search.backend.shared.ContactInfo;
import net.skcomms.search.backend.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DtcExam implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private final StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Unit.EM);

	private static final SelectionModel<ContactInfo> SELECTION_MODEL = 
			new SingleSelectionModel<ContactInfo>();
	static {
		SELECTION_MODEL.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				ContactInfo selected = ((SingleSelectionModel<ContactInfo>) SELECTION_MODEL)
						.getSelectedObject();
				if (selected != null) {
					// Window.alert("You selected: " + selected);
				}
			}
		});
	}
	
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		
		stackLayoutPanel.setSize("300px", "300px");
		stackLayoutPanel.animate(100);
		RootPanel.get("topPanelContainer").add(stackLayoutPanel);
		
		addPersonalPanel("Jang's Contact List");
		addPersonalPanel("Kang's Contact List");
		addPersonalPanel("Kuwon's Contact List");
		addPersonalPanel("Kim's Contact List");
		addPersonalPanel("Seok's Contact List");
		addPersonalPanel("Shin's Contact List");
	    
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT user");
		final TextBox emailField = new TextBox();
		final Label nameErrorLabel = new Label();
		final Label emailErrorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("emailFieldContainer").add(emailField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("nameErrorLabelContainer").add(nameErrorLabel);
		RootPanel.get("emailErrorLabelContainer").add(emailErrorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			@Override
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					emailField.setFocus(true);
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a
			 * response.
			 */
			private void sendNameToServer() {
				nameErrorLabel.setText("");
				emailErrorLabel.setText("");

				// First, we validate the input.
				if (!FieldVerifier.isValidName(nameField.getText())) {
					nameErrorLabel.setText("Please enter at least 3 characters");
					return;
				}

				if (!FieldVerifier.isValidEmail(emailField.getText())) {
					emailErrorLabel.setText("Please check the e-mail valid");
					return;
				}

				// Then, we send the input to the server.
				greetingService.createNameCard(nameField.getText(), emailField.getText(), 
					new AsyncCallback<ContactInfo>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(ContactInfo contactInfo) {
							Widget w = stackLayoutPanel.getVisibleWidget();
							@SuppressWarnings("unchecked")
							CellList<ContactInfo> list = 
								((CellList<ContactInfo>) ((ScrollPanel) w).getWidget());
							@SuppressWarnings("unchecked")
							List<ContactInfo> values = (List<ContactInfo>) list.getLayoutData();
							if (!values.contains(contactInfo)) {
							    values.add(contactInfo);
							    list.setRowCount(values.size(), true);
								list.setRowData(0, values);
							}
						}
					});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}

	/**
	 * @param header
	 */
	private void addPersonalPanel(String header) {
		List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		
		CellList<ContactInfo> cellList = new CellList<ContactInfo>(
			ContactInfoCell.getInstacne());
		cellList.setLayoutData(contactInfos);
		
	    cellList.setSelectionModel(SELECTION_MODEL);
		
		stackLayoutPanel.add(new ScrollPanel(cellList), header, 2);
	}
}
