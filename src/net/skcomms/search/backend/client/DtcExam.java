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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
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

	private List<ContactInfo> values = new ArrayList<ContactInfo>();

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		
		final StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Unit.EM);
		stackLayoutPanel.setSize("300px", "400px");
		RootPanel.get("topPanelContainer").add(stackLayoutPanel);
		
		final CellList<ContactInfo> jangList = new CellList<ContactInfo>(
			new ContactInfoCell());
		
		final CellList<ContactInfo> kuwonList = new CellList<ContactInfo>(
			new ContactInfoCell());
		
		stackLayoutPanel.add(new ScrollPanel(jangList), new HTML("Jang's Contact List"), 2);
		stackLayoutPanel.add(new HTML("Kang's content"), new HTML("Kang's Contact List"), 2);
		stackLayoutPanel.add(new HTML("Kim's content"), new HTML("Kim's Contact List"), 2);
		stackLayoutPanel.add(new ScrollPanel(kuwonList), new HTML("Kuwon's Contact List"), 2);
		stackLayoutPanel.add(new HTML("Lee's content"), new HTML("Lee's Contact List"), 2);
		stackLayoutPanel.add(new HTML("Shin's content"), new HTML("Shin's Contact List"), 2);
		stackLayoutPanel.animate(100);
		
		// Add a selection model to handle user selection.
	    final SelectionModel<ContactInfo> selectionModel = new SingleSelectionModel<ContactInfo>();
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        ContactInfo selected = ((SingleSelectionModel<ContactInfo>) selectionModel).getSelectedObject();
	        if (selected != null) {
	          //Window.alert("You selected: " + selected);
	        }
	      }
	    });
	    jangList.setSelectionModel(selectionModel);
	    
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
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			@Override
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
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
					nameErrorLabel
							.setText("Please enter at least 3 characters");
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
							stackLayoutPanel.getVisibleWidget();
							// FIXME values 리스트를 현재 표시되는 스택에 따라 선택되도록 수정.
							if (!values.contains(contactInfo)) {
							    values.add(contactInfo);
							    jangList.setRowCount(values.size(), true);
								jangList.setRowData(0, values);
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
}
