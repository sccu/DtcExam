package net.skcomms.search.backend.client;

import java.util.ArrayList;
import java.util.List;

import net.skcomms.search.backend.shared.ContactInfo;
import net.skcomms.search.backend.shared.FieldVerifier;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
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

	private static final SelectionModel<ContactInfo> SELECTION_MODEL = new SingleSelectionModel<ContactInfo>();
	static {
		SELECTION_MODEL.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				ContactInfo selected = ((SingleSelectionModel<ContactInfo>) SELECTION_MODEL).getSelectedObject();
				if (selected != null) {
					// Window.alert("You selected: " + selected);
				}
			}
		});
	}

	public static class CategoryNode {
		private final String name;
		private final List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();

		public CategoryNode(String name) {
			this.name = name;

		}

		public String getName() {
			return name;
		}

		public List<ContactInfo> getContactInfos() {
			return contactInfos;
		}

	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		stackLayoutPanel.setSize("1024px", "512px");
		RootPanel.get("topPanelContainer").add(stackLayoutPanel);
		
		addPersonalPanel("Jang's Contact List");
		addPersonalPanel("Kang's Contact List");
		addPersonalPanel("Kuwon's Contact List");
		addPersonalPanel("Seok's Contact List");
		addPersonalPanel("Shin's Contact List");
		addShinsPanel("Contact List");

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
					nameErrorLabel
							.setText("Please enter at least 3 characters");
					return;
				}

				if (!FieldVerifier.isValidEmail(emailField.getText())) {
					emailErrorLabel.setText("Please check the e-mail valid");
					return;
				}

				// Then, we send the input to the server.
				greetingService.createNameCard(nameField.getText(),
						emailField.getText(), new AsyncCallback<ContactInfo>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}

							@Override
							public void onSuccess(ContactInfo contactInfo) {
								Widget scrollPanel = stackLayoutPanel.getVisibleWidget();
								Widget cellWidget = ((ScrollPanel) scrollPanel).getWidget();
								
								@SuppressWarnings("unchecked")
								DataBox<ContactInfo> box = (DataBox<ContactInfo>) cellWidget.getLayoutData();
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

	/**
	 * @param header
	 */
	private void addPersonalPanel(String header) {
		final List<ContactInfo> values = new ArrayList<ContactInfo>();

		final CellList<ContactInfo> cellList = new CellList<ContactInfo>(ContactInfoCell.getInstacne());
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
		cellList.setLayoutData(box);

		stackLayoutPanel.add(new ScrollPanel(cellList), header, 2);
	}

	private void addShinsPanel(String header) {
		// Create a model for the tree.
		final TreeViewModel model = new CustomTreeModel1();

		final CellTree cellTree = new CellTree(model, null);		
		cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		
		final List<ContactInfo> values = new ArrayList<ContactInfo>();
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() {
			@Override
			public void add(ContactInfo contactInfo) {
				if (!values.contains(contactInfo)) {
					values.add(contactInfo);
					((CustomTreeModel1)model).addContactInfo(contactInfo);
					cellTree.
					//cellList.setRowCount(values.size(), true);
					//cellList.setRowData(0, values);
				}
			}
		};
		cellTree.setLayoutData(box);
		stackLayoutPanel.add(new ScrollPanel(cellTree), header, 2);
	}
	
	private static class CustomTreeModel1 implements TreeViewModel {

		private final List<Composer> composers;
				
		/**
		 * This selection model is shared across all leaf nodes. A selection
		 * model can also be shared across all nodes in the tree, or each set of
		 * child nodes can have its own instance. This gives you flexibility to
		 * determine how nodes are selected.
		 */
		private final static SingleSelectionModel<ContactInfo> selectionModel = new SingleSelectionModel<ContactInfo>();
		static {
			selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			public void onSelectionChange(SelectionChangeEvent event) {
				ContactInfo selected = selectionModel.getSelectedObject();
					if (selected != null) {
						Window.alert("You selected: " + selected);					
					}
				}
			});
		}
		
		public CustomTreeModel1() {
			// Create a database of information.
			composers = new ArrayList<Composer>();

			// Add compositions by Beethoven.
			{
				Composer Friend = new Composer("Friend");
				
				Friend.addContactList(new ContactList("Best"));				
				Friend.addContactList(new ContactList("Nomal"));
				
				// symphonies.addSong("No. 9 - D Minor");
				
				composers.add(Friend);
			}

			// Add compositions by Brahms.
			{
				Composer unknown = new Composer("Unknown");
				unknown.addContactList(new ContactList("Best"));				
				unknown.addContactList(new ContactList("Nomal"));
				
				// symphonies.addSong("No. 9 - D Minor");
				
				composers.add(unknown);
			}			
		}

		/**
		 * Get the {@link NodeInfo} that provides the children of the specified
		 * value.
		 */
		public <T> NodeInfo<?> getNodeInfo(T value) {
			if (value == null) {
				// LEVEL 0.
				// We passed null as the root value. Return the composers.

				// Create a data provider that contains the list of composers.
				ListDataProvider<Composer> dataProvider = new ListDataProvider<Composer>(composers);

				// Create a cell to display a composer.
				Cell<Composer> cell = new AbstractCell<Composer>() {
					@Override
					public void render(Context context, Composer value, SafeHtmlBuilder sb) {
						if (value != null) {
							sb.appendEscaped(value.getName());
						}
					}
				};

				// Return a node info that pairs the data provider and the cell.
				return new DefaultNodeInfo<Composer>(dataProvider, cell);
			} else if (value instanceof Composer) {
				// LEVEL 1.
				// We want the children of the composer. Return the playlists.
				ListDataProvider<ContactList> dataProvider = new ListDataProvider<ContactList>(((Composer) value).getContactLists());
				Cell<ContactList> cell = new AbstractCell<ContactList>() {
					@Override
					public void render(Context context, ContactList value,	SafeHtmlBuilder sb) {
						if (value != null) {
							sb.appendEscaped(value.getCategoryName());
						}
					}
				};
				return new DefaultNodeInfo<ContactList>(dataProvider, cell);
			} else if (value instanceof ContactList) {
				// LEVEL 2 - LEAF.
				// We want the children of the playlist. Return the songs.
				ListDataProvider<ContactInfo> dataProvider = new ListDataProvider<ContactInfo>(((ContactList) value).getContactInfoList());

				// Use the shared selection model.
				return new DefaultNodeInfo<ContactInfo>(dataProvider, ContactInfoCell.getInstacne(), selectionModel, null);
			}

			return null;
		}

		/**
		 * Check if the specified value represents a leaf node. Leaf nodes
		 * cannot be opened.
		 */
		
		public boolean isLeaf(Object value) {
			// The leaf nodes are the songs, which are Strings.
			if (value instanceof String) {
				return true;
			}
			return false;
		}
		
		
		public void addContactInfo(ContactInfo contactInfo) {			
			this.composers.get(0).contactLists.get(0).addContactInfo(contactInfo);
		}

	}
	
	

	private static class Composer {
		private final String name;
		private final List<ContactList> contactLists = new ArrayList<ContactList>();

		public Composer(String name) {
			this.name = name;
		}

		/**
		 * Add a playlist to the composer.
		 * 
		 * @param playlist
		 *            the playlist to add
		 */
		public ContactList addContactList(ContactList contactList) {
			contactLists.add(contactList);
			return contactList;
		}

		public String getName() {
			return name;
		}

		/**
		 * Return the rockin' playlist for this composer.
		 */
		public List<ContactList> getContactLists() {
			return contactLists;
		}
	}
	
	private static class ContactList {

		private final String categoryName;
		private final List<ContactInfo> listContacInfo = new ArrayList<ContactInfo>();

		public ContactList(String category) {
			this.categoryName = category;
		}

		/**
		 * Add a song to the playlist.
		 * 
		 * @param name
		 *            the name of the song
		 */
		public void addContactInfo(ContactInfo contactInfo) {
			listContacInfo.add(contactInfo);
		}

		public String getCategoryName() {
			return categoryName;
		}

		/**
		 * Return the list of songs in the playlist.
		 */
		public List<ContactInfo> getContactInfoList() {
			return listContacInfo;
		}
	}	
}