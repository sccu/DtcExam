package net.skcomms.search.backend.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.TreeViewModel;
import net.skcomms.search.backend.client.ContactList;
import net.skcomms.search.backend.server.Person;
import net.skcomms.search.backend.shared.ContactInfo;

public class ContactInfoTreeView implements TreeViewModel {

	ListDataProvider<String> categoryDataProvider;

	@SuppressWarnings("rawtypes")
	private HashMap<String, ListDataProvider> contactInfoDataProviderMap;
	
	@SuppressWarnings("rawtypes")
	public ContactInfoTreeView(){
	
		categoryDataProvider = new ListDataProvider<String>();
		contactInfoDataProviderMap = 
				new HashMap <String, ListDataProvider>();
		
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
			@SuppressWarnings("unchecked")
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

			@SuppressWarnings("unchecked")
			ListDataProvider<ContactInfo> dataProvider = 
					contactInfoDataProviderMap.get((String)value);
					
            return new DefaultNodeInfo<ContactInfo>(dataProvider, ContactInfoCell.getInstacne());
		}
		
		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		return value instanceof ContactInfo;
	}

	

}
