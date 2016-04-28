package chau.voipapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class EditContactActivity extends Activity {
	
	Button btnCancelEdit, btnOkEdit;
	EditText edName, edNum;
	
	ContactItem contact = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		initWiget();
		getDefaultData();
		initEvent();
	}
	
	public void getDefaultData()
	{
		Intent in = getIntent();
		Bundle b = in.getBundleExtra("CONTACT_DATA");
		contact = (ContactItem)b.getSerializable("CONTACT");
		edName.setText(contact.getName());
		edNum.setText(contact.getNum());
	}
	
	public void initWiget()
	{
		btnOkEdit = (Button)findViewById(R.id.btnOkayEdit);
		btnCancelEdit = (Button)findViewById(R.id.btnCancelEdit);
		
		edName = (EditText)findViewById(R.id.contact_name);
		edNum = (EditText)findViewById(R.id.contact_number);
	}
	
	public void initEvent()
	{
		btnCancelEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		btnOkEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = getIntent();
				contact.setName(edName.getText() + "");
				contact.setNum(edNum.getText() + "");
				
				Bundle bun = new Bundle();
				bun.putSerializable("CONTACT", contact);
				i.putExtra("CONTACT_DATA", bun);
				setResult(8, i);
				finish();
			}
		});
	}
}
