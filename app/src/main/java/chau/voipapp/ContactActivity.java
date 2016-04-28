package chau.voipapp;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class ContactActivity extends Fragment
{
	View rootView;
	Keyboard keyboard;
	String name;
	String phoneNo;
	public static ListView lvContact;	
	EditText edSearch;
	public static ListviewContactAdapter adapter;
	
	TextWatcher watcher;
	
	ContactItem contactSelected;
	
	AdapterView.OnItemClickListener listenerlvContact;
	int lvContactPos;
	
	ArrayList<ContactItem> mContactArrayList = new ArrayList<ContactItem>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{	
		setHasOptionsMenu(true);
		rootView = inflater.inflate(R.layout.activity_contact, container, false);
		
		initWiget();
		initListener();
		
		InputMethodManager ipm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		ipm.hideSoftInputFromWindow(edSearch.getWindowToken(), 0);
		
		registerForContextMenu(lvContact);
		
		mContactArrayList = getContact();
		adapter = new ListviewContactAdapter(getActivity(), mContactArrayList);		

		Collections.sort(mContactArrayList);
		lvContact.setAdapter(adapter);
		lvContact.setOnItemClickListener(listenerlvContact);
		
		edSearch.addTextChangedListener(watcher);
		
		
		return rootView;
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        
		inflater.inflate(R.menu.contact_bottom_bar, menu);
		
        super.onCreateOptionsMenu(menu,inflater);
        
    }
	
	@Override
	public void onStart() 
	{
		super.onStart();
//		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.contact_click_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId())
		{
		case R.id.action_call_contact:
			Keyboard.numCall = (mContactArrayList.get(info.position).getNum());
			Intent intent = new Intent(getActivity(), OnCallingActivity.class);
			startActivity(intent);
			break;
		case R.id.action_edit_contact:
//			Intent intent1 = new Intent(Intent.ACTION_EDIT);
//			intent1.setData(Uri.parse("content://com.android.contacts/raw_contacts/" + contactSelected.getNum()));
			Intent intent1 = new Intent(getActivity(), EditContactActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("CONTACT", contactSelected);
			intent1.putExtra("CONTACT_DATA", bundle);
			startActivityForResult(intent1, 8);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == R.id.action_contact_search)
		{
			if(edSearch.isShown())
				edSearch.setVisibility(View.GONE);
			else edSearch.setVisibility(View.VISIBLE);
		}
		if(id == R.id.action_contact_add_contact)
		{
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);	
			//Tranh cho viec mo activity bi loi do chua khoi tao Edittext
			if(keyboard.isVisible())
			{
				intent.putExtra(ContactsContract.Intents.Insert.PHONE, Keyboard.edNumInput.getText().toString());
			}
			startActivity(intent);
		}
		if(id == R.id.action_contact_show_keyboard)
		{
            if(keyboard==null)
            {
            	adapter.restoreList();
//            	keyboard = new Keyboard();
            	getActivity().getSupportFragmentManager().beginTransaction().add(R.id.Key_board_container, keyboard).commit();
            }
            else
            {
                if(keyboard.isVisible())
                {
                	adapter.restoreList();
                	getActivity().getSupportFragmentManager().beginTransaction().hide(keyboard).commit();
                }
                else
                {
                	adapter.restoreList();
                	keyboard = new Keyboard();
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.Key_board_container, keyboard).commit();
                }
            }
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) 
	{
		
		super.onPrepareOptionsMenu(menu);
//		getActivity().invalidateOptionsMenu();
//		MenuItem filter = menu.findItem(R.id.bottomSearch).setVisible(false);
//		filter = menu.findItem(R.id.bottomStatus).setVisible(false);
//		filter.setVisible(false);
		
	}
	
	@Override
	public void onResume() 
	{
		edSearch.setText(null);
		super.onResume();
	}
	
	/**
	 * Khởi tạo các thành phần
	 */
	public void initWiget()
	{
		keyboard = new Keyboard();
		edSearch = (EditText)rootView.findViewById(R.id.edSearchContact);
		lvContact = (ListView)rootView.findViewById(R.id.lvContact);
	}
	
	public void initListener()
	{
		listenerlvContact = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				lvContactPos = arg2;
				contactSelected = (ContactItem)arg0.getAdapter().getItem(arg2);
				getActivity().openContextMenu(arg1);
			}
		};
		
		watcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				adapter.getFilter().filter(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	private ArrayList<ContactItem> getContact()
	{
		ArrayList<ContactItem> list = new ArrayList<ContactItem>();
		ContentResolver cr = getActivity().getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		if(cur.getCount() > 0)
		{
			while(cur.moveToNext())
			{
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        		name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        		if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
        		{
                     Cursor pCur = cr.query(
                    		 ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                    		 null, 
                    		 ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
                    		 new String[]{id}, null);
                     while (pCur.moveToNext()) {
                    	 phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    	 phoneNo = phoneNo.replace(" ", "");
                    	 list.add(new ContactItem(name, phoneNo));
                     } 
      	        pCur.close();
        		}
			}
		}
		return list;
	}
	
	public class ListviewContactAdapter extends BaseAdapter implements Filterable
	{
		private ArrayList<ContactItem> listOriginContact;
		private ArrayList<ContactItem> listDisplayValue;
		ArrayList<ContactItem> arr;
		private LayoutInflater mInflater;
		
		public ListviewContactAdapter(Context contactFragment, ArrayList<ContactItem> mContactArrayList) 
		{
			// TODO Auto-generated constructor stub
			this.listOriginContact = mContactArrayList;
			this.listDisplayValue = mContactArrayList;
			this.arr = mContactArrayList;
			mInflater = LayoutInflater.from(contactFragment);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listDisplayValue.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listDisplayValue.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
		
		public void restoreList()
		{
			this.listDisplayValue = arr;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int pos, View view, ViewGroup parent) 
		{
			// TODO Auto-generated method stub
			ViewHolder holder;
			if(view == null)
			{
				view = mInflater.inflate(R.layout.activity_contact_custom_view, null);
				holder = new ViewHolder();
				holder.contactName = (TextView)view.findViewById(R.id.contact_name);
				view.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)view.getTag();
			}
			
			holder.contactName = (TextView)view.findViewById(R.id.contact_name);
			holder.contactName.setText(listDisplayValue.get(pos).getName());
			
			holder.contactNum = (TextView)view.findViewById(R.id.contact_number);
			holder.contactNum.setText(listDisplayValue.get(pos).getNum());
			
			return view;
		}
		class ViewHolder
		{
		    TextView contactName, contactNum;
		}
		@SuppressLint("DefaultLocale")
		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			Filter filter = new Filter() {
				
				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					// TODO Auto-generated method stub
					if(results.count == 0)
					{
						notifyDataSetInvalidated();
					}
					else
					{
						listDisplayValue = (ArrayList<ContactItem>)results.values;
						notifyDataSetChanged();
					}
				}
				
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					// TODO Auto-generated method stub
					FilterResults filterResults = new FilterResults();
					ArrayList<ContactItem> filterList = new ArrayList<ContactItem>();
					if(listOriginContact == null)
					{
						listOriginContact = new ArrayList<ContactItem>(listDisplayValue);
					}
					if(constraint == null || constraint.length() == 0)
					{
						filterResults.count = listOriginContact.size();
						filterResults.values = listOriginContact;
					}
					else
					{
						constraint = constraint.toString().toLowerCase();
						for(int i = 0; i < listOriginContact.size(); i++)
						{
							String data = listOriginContact.get(i).getName();
							String num = listOriginContact.get(i).getNum();
							if (data.toLowerCase().contains(constraint.toString()) 
									|| num.contains(constraint.toString())) {
		                        filterList.add(new ContactItem(
		                        		listOriginContact.get(i).getName(),
		                        		listOriginContact.get(i).getNum()));
		                    }
						}
						filterResults.count = filterList.size();
						filterResults.values = filterList;
					}
					return filterResults;
				}
			};
			return filter;
		}
	}
}
