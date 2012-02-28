package net.skcomms.search.backend.client;

import java.util.HashMap;

import net.skcomms.search.backend.shared.ContactInfo;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

public class ContactInfoTreeView implements TreeViewModel {

	ListDataProvider<String> categoryDataProvider;

	private HashMap<String, ListDataProvider<ContactInfo>> contactInfoDataProviderMap;
	
	public ContactInfoTreeView(){
	
		categoryDataProvider = new ListDataProvider<String>();
		contactInfoDataProviderMap = 
				new HashMap <String, ListDataProvider<ContactInfo>>();
		
	}
	
	public void addNode(ContactInfo info)
	{

		if (contactInfoDataProviderMap.containsKey(info.getCategory()) == false)
		{
        	categoryDataProvider.getList().add(info.getCategory());

			ListDataProvider<ContactInfo> contactInfoDataProvider = 
					new ListDataProvider<ContactInfo>();
			
			contactInfoDataProvider.getList().add(info);
			
			contactInfoDataProviderMap.put(info.getCategory(), contactInfoDataProvider);
			contactInfoDataProvider.refresh();

		}
		else
		{
			ListDataProvider<ContactInfo> contactInfoDataProvider = 
					contactInfoDataProviderMap.get(info.getCategory());
			
			contactInfoDataProvider.getList().add(info);			
			contactInfoDataProvider.refresh();
		}
		
		categoryDataProvider.refresh();
	}
	
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) 
	{
		if (value == null)
		{

            return new DefaultNodeInfo<String>(categoryDataProvider, new TextCell());
		}
		else if (value instanceof String)
		{

			ListDataProvider<ContactInfo> dataProvider = 
					contactInfoDataProviderMap.get((String)value);
					
            return new DefaultNodeInfo<ContactInfo>(dataProvider, ContactInfoCell.getInstance());
		}
		
		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		return value instanceof ContactInfo;
	}

	

}
