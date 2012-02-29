package net.skcomms.search.backend.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
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
		
		stackLayoutPanel.setSize("800px", "500px");
		RootPanel.get("topPanelContainer").add(stackLayoutPanel);
		
		addPersonalPanel("Jang's Contact List");
		addJangPanel("Jang's Contact Browser");
		addKangPanel("Kang's Contact List");
		addKuwonPanel("Kuwon's Contact Browser");
		addKimPanel("Kim's Contact List");
		addSeokPanel("Seok's Contact List");
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
					return new DefaultNodeInfo<ContactInfo>(provider, ContactInfoCell.getInstance());
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
					return new DefaultNodeInfo<ContactInfo>(provider, ContactInfoCell.getInstance());
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
	
	public static class CategoryNode {
		private final String Categoryname;
		private final List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();

		private CategoryNode parent;
	
		
		public CategoryNode(String Categoryname) {
			this.Categoryname = Categoryname;
			parent = null;
		}

		public String getName() {
			return Categoryname;
		}

		public List<ContactInfo> getContactInfos() {
			return contactInfos;
		}
		
		public ContactInfo addInfo(ContactInfo contactInfo) {
			contactInfos.add(contactInfo);
			return contactInfo;
		}
		
		public void refresh() {
		
		      if(parent!=null) {  
		          parent.refresh();  
		        }  
		}

		public String getCategoryName() {
			return Categoryname;
		}
        
	}
	
	private void addKimPanel(String header) {
		final List<CategoryNode> categorynodes;
		categorynodes = new ArrayList<CategoryNode>();
		
		CategoryNode myself = new CategoryNode("Myself");
		myself.addInfo(new ContactInfo("Kim", "Kim@sk.com", "Myself"));
		categorynodes.add(myself);	
		
		final ListDataProvider<CategoryNode> rootProvider = new ListDataProvider<CategoryNode>(categorynodes);
	
		class MyTreeViewModel implements TreeViewModel {
			final Map<String, ListDataProvider<ContactInfo>> dataProviderMap = new HashMap<String, ListDataProvider<ContactInfo>>();
			@Override
			public <T> NodeInfo<?> getNodeInfo(T value) {
				if (value == null) {
					Cell<CategoryNode> cell = new AbstractCell<CategoryNode>(){
						@Override
						public void render( Context context, CategoryNode value, SafeHtmlBuilder sb) {
							sb.appendEscaped(value.getCategoryName());
						}
					};
					return new DefaultNodeInfo<CategoryNode>(rootProvider, cell);
				}
				else if (value instanceof CategoryNode) {
					String CategoryName = ((CategoryNode) value).getCategoryName();
					ListDataProvider<ContactInfo> provider;
					
					if (dataProviderMap.containsKey(CategoryName)) {
						provider = dataProviderMap.get(CategoryName);
					} else { // 하드코딩 대비 ex) Myself
						provider = new ListDataProvider<ContactInfo>();
						provider.getList().addAll(((CategoryNode) value).getContactInfos());
						dataProviderMap.put(CategoryName, provider);
					}				
					return new DefaultNodeInfo<ContactInfo>(provider, ContactInfoCell.getInstance());
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
				String categoryname = contactInfo.getCategory();
				
				if(!dataProviderMap.containsKey(categoryname)) {
					ListDataProvider<ContactInfo> provider = new ListDataProvider<ContactInfo>();
					dataProviderMap.put(categoryname, provider);
					provider.getList().add(contactInfo);
				} else {
					ListDataProvider<ContactInfo> provider = dataProviderMap.get(categoryname);
					if (!provider.getList().contains(contactInfo)) {
						provider.getList().add(contactInfo);
					}
				}
	
			}
			
			public int getIndex(String CategoryName) {
				int Index = -1;
				for( int i=0; i<categorynodes.size(); i++) {
					if (CategoryName.equals(categorynodes.get(i).getCategoryName())) {
						Index = i;
					}
				}
				return Index;
			}
			
			public void add(ContactInfo contactInfo) {
				if (this.getIndex(contactInfo.getCategory())<0) {
					categorynodes.add(new CategoryNode(contactInfo.getCategory()));	
				}
				if (!categorynodes.get(this.getIndex(contactInfo.getCategory())).getContactInfos().contains(contactInfo)) {
                    categorynodes.get(this.getIndex(contactInfo.getCategory())).addInfo(contactInfo);
				} else {
					java.lang.System.out.println("already exx");
				}
			}
		};
		final MyTreeViewModel treeViewModel = new MyTreeViewModel();
		final CellBrowser browser = new CellBrowser(treeViewModel, null);
		browser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() {
			@Override
			public void add(ContactInfo contactInfo) {
				treeViewModel.add(contactInfo);
				treeViewModel.addProvider(contactInfo);
				treeViewModel.refresh();
				if (!browser.getRootTreeNode().isChildOpen(treeViewModel.getIndex(contactInfo.getCategory()))) {
					browser.getRootTreeNode().setChildOpen(treeViewModel.getIndex(contactInfo.getCategory()), true);
				} else {
					java.lang.System.out.println("already open");
				}
				
				
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
			ContactInfoCell.getInstance());
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
		
	private void addSeokPanel(String header) {
		final ContactInfoTreeView model = new ContactInfoTreeView();
		final CellBrowser cellBrowser = new CellBrowser(model, null);		
		cellBrowser.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);		
		//cellBrowser.setSelectionModel(SELECTION_MODEL);
		
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() 
		{
			@Override
			public void add(ContactInfo contactInfo) {

				model.addNode(contactInfo);
			}
		};
		
		widgetDataMap.put(cellBrowser, box);
		stackLayoutPanel.add(cellBrowser, header, 2);
	}

	private static class Category {
		private final String name;
		private final List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		
		public Category(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public List<ContactInfo> getContactInfos() {
			return contactInfos;
		}

		public ContactInfo add(ContactInfo contactInfo) {
			contactInfos.add(contactInfo);
			return contactInfo;
		}
	}
	
	private void addKangPanel(String header) {
		class KangsTreeViewModel implements TreeViewModel {
			final List<Category> categories = new ArrayList<Category>();
			final ListDataProvider<Category> rootProvider;
			final Map<Object, ListDataProvider<ContactInfo>> providerMap = new HashMap<Object, ListDataProvider<ContactInfo>>();
			
			public KangsTreeViewModel() {
				Category category = new Category("I");
				category.add(new ContactInfo("Kang", "pelcious@sk.com", "I"));
				categories.add(category);
				rootProvider = new ListDataProvider<Category>(categories);
			}

			public List<Category> getRootList() {
				return categories;
			}
				
			public <T> NodeInfo<?> getNodeInfo(T value) {
				if (value == null) { // root node
					Cell<Category> cell = new AbstractCell<Category>() {
						public void render(Context context, Category value, SafeHtmlBuilder sb) {
							sb.appendEscaped(value.getName());
						}
					};
					
					return new DefaultNodeInfo<Category>(rootProvider, cell);
				} else if (value instanceof Category) {
					ListDataProvider<ContactInfo> dataProvider = new ListDataProvider<ContactInfo>(((Category) value).getContactInfos());
					
					for (ContactInfo contactInfo : ((Category) value).getContactInfos()) {
						if (contactInfo.getCategory().equals(value)) {
							dataProvider.getList().add(contactInfo);
							
						}
					}
					
					providerMap.put(value, dataProvider);
					
					return new DefaultNodeInfo<ContactInfo>(dataProvider, ContactInfoCell.getInstance());
				}

				return null;
			}
			public boolean isLeaf(Object value) {
			      return (value instanceof ContactInfo);
			}

			public void refresh(Category category) {
				rootProvider.refresh();
				providerMap.get(category).refresh();
			}
			
			public int getIndex(String CategoryName) {
				int Index = -1;
				for( int i=0; i<categories.size(); i++) {
					if (CategoryName.equals(categories.get(i).getName())) {
						Index = i;
					}
				}
				return Index;
			}
			
			public void add(ContactInfo contactInfo) {
				if (this.getIndex(contactInfo.getCategory())<0) {
					categories.add(new Category(contactInfo.getCategory()));	
					java.lang.System.out.println("OK");
				}
				if (!categories.get(this.getIndex(contactInfo.getCategory())).contactInfos.contains(contactInfo)) {
					 categories.get(this.getIndex(contactInfo.getCategory())).add(contactInfo);
				} else {
					java.lang.System.out.println("Not");
				}
			}
		}
			
		final KangsTreeViewModel kangsTreeViewModel = new KangsTreeViewModel();
		
		final ScrollPanel scrollPanel = new ScrollPanel(new CellTree(kangsTreeViewModel, null));
		
		DataBox<ContactInfo> box = new DataBox<ContactInfo>() {
			public void add(ContactInfo contactInfo) {
				List<Category> categories = kangsTreeViewModel.getRootList();
				kangsTreeViewModel.add(contactInfo);
				kangsTreeViewModel.refresh(categories.get(kangsTreeViewModel.getIndex(contactInfo.getCategory())));
			}
		};
		
		widgetDataMap.put(scrollPanel, box);
		stackLayoutPanel.add(scrollPanel, header, 2);
	}
}
