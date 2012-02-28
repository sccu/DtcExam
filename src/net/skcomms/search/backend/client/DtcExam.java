package net.skcomms.search.backend.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.skcomms.search.backend.shared.ContactInfo;
import net.skcomms.search.backend.shared.FieldVerifier;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

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

	private Map<Object, DataBox<ContactInfo>> widgetDataMap = new HashMap<Object, DataBox<ContactInfo>>();
	private Map<String, ListDataProvider<ContactInfo>> categoryMap = new HashMap<String, ListDataProvider<ContactInfo>>();

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
		
		stackLayoutPanel.setSize("500px", "400px");
		RootPanel.get("topPanelContainer").add(stackLayoutPanel);
		
		addPersonalPanel("Jang's Contact List");
		addJangPanel("Jang's Contact Browser");
		addPersonalPanel("Kang's Contact List");
		//addPersonalPanel("Kuwon's Contact List");
		addKuwonPanel("Kuwon's Contact Browser");
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
							DataBox<ContactInfo> box = widgetDataMap.get(w);
							box.add(contactInfo);
						}
					});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}

	private void addJangPanel(String header) {
		final List<String> categories = new ArrayList<String>();
		//categories.add("Myself");
		categories.add("Family");
		final List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		contactInfos.add(new ContactInfo("Yang", "abc@sk.com", "Family"));
		final ListDataProvider<String> rootProvider = new ListDataProvider<String>(categories);
		
		class MyTreeViewModel implements TreeViewModel {
			@Override
			public <T> NodeInfo<?> getNodeInfo(T value) {
				if (value == null) {
					return new DefaultNodeInfo<String>(rootProvider, new TextCell());
				}
				else if (value instanceof String) {
					ListDataProvider<ContactInfo> provider = new ListDataProvider<ContactInfo>();
					for (ContactInfo ci : contactInfos) {
						if (ci.getCategory().equals(value)) {
							provider.getList().add(ci);
						}
					}
					return new DefaultNodeInfo<ContactInfo>(provider, ContactInfoCell.getInstacne());
				}
				
				return null;
			}

			@Override
			public boolean isLeaf(Object value) {
				return (value instanceof ContactInfo);
			}
			
			public void refresh() {
				rootProvider.refresh();
			}
		};
		final MyTreeViewModel treeViewModel = new MyTreeViewModel();
		final CellBrowser browser = new CellBrowser(treeViewModel, null);
		browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() {
			@Override
			public void add(ContactInfo contactInfo) {
				if (!contactInfos.contains(contactInfo)) {
				    contactInfos.add(contactInfo);
				}
				if (!categories.contains(contactInfo.getCategory())) {
					categories.add(contactInfo.getCategory());
				}
				treeViewModel.refresh();
			}
		};
		
		widgetDataMap.put(browser, box);
		stackLayoutPanel.add(browser, header, 2);
	}

	private void addKuwonPanel(String header) {
		final List<String> categories = new ArrayList<String>();
		categories.add("Myself");
		//categories.add("Family");
		final List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		contactInfos.add(new ContactInfo("Kuwon", "solikang@sk.com", "Myself"));
		//final ListDataProvider<String> rootProvider = new ListDataProvider<String>(categories);
		
		class MyTreeViewModel implements TreeViewModel {
			private ListDataProvider<String> rootProvider = new ListDataProvider<String>(categories);
			@Override
			public <T> NodeInfo<?> getNodeInfo(T value) {
				if (value == null) {
					return new DefaultNodeInfo<String>(rootProvider, new TextCell());
				}
				else if (value instanceof String) {
					String category = (String)value;
					ListDataProvider<ContactInfo> provider;
					
					if(categoryMap.isEmpty()) {
						// add new one
						provider = new ListDataProvider<ContactInfo>();
						provider.getList().add(contactInfos.get(0));
						categoryMap.put(category, provider);
					} else {
						// get existing one
						provider = categoryMap.get(category);
					}										
					return new DefaultNodeInfo<ContactInfo>(provider, ContactInfoCell.getInstacne());
				}
				return null;
			}

			@Override
			public boolean isLeaf(Object value) {
				return (value instanceof ContactInfo);
			}
			
			public void refresh() {
				rootProvider.refresh();
			}

			public void addProvider(ContactInfo contactInfo) {
				String category = contactInfo.getCategory();
				if(!categoryMap.containsKey(category)) {
					ListDataProvider<ContactInfo> provider = new ListDataProvider<ContactInfo>();
					provider.getList().add(contactInfo);
					categoryMap.put(category, provider);
				} else {
					ListDataProvider<ContactInfo> provider = categoryMap.get(category);
					provider.getList().add(contactInfo);
				}
			}
		};
		final MyTreeViewModel treeViewModel = new MyTreeViewModel();
		final CellBrowser browser = new CellBrowser(treeViewModel, null);
		browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() {
			@Override
			public void add(ContactInfo contactInfo) {
				if (!contactInfos.contains(contactInfo)) {
				    contactInfos.add(contactInfo);
				    treeViewModel.addProvider(contactInfo);
				}
				if (!categories.contains(contactInfo.getCategory())) {
					categories.add(contactInfo.getCategory());
				}
				treeViewModel.refresh();
			}
		};
		
		widgetDataMap.put(browser, box);
		stackLayoutPanel.add(browser, header, 2);
	}
	
	/**
	 * @param header
	 */
	private void addPersonalPanel(String header) {
		final List<ContactInfo> values = new ArrayList<ContactInfo>();
		
		final CellList<ContactInfo> cellList = new CellList<ContactInfo>(
			ContactInfoCell.getInstacne());
	    cellList.setSelectionModel(SELECTION_MODEL);
		
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() {
			@Override
			public void add(ContactInfo contactInfo) {
				if (!values.contains(contactInfo)) {
				    values.add(contactInfo);
				    cellList.setRowCount(values.size(), true);
					cellList.setRowData(0, values);
				}
			}
		};
		
		ScrollPanel panel = new ScrollPanel(cellList);
		widgetDataMap.put(panel, box);
		stackLayoutPanel.add(panel, header, 2);
	}
}
