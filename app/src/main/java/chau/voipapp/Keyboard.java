package chau.voipapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class Keyboard extends Fragment  implements OnClickListener
{
	private Button btnOne;
	private Button btnTwo;
	private Button btnThree;
	private Button btnFour;
	private Button btnFive;
	private Button btnSix;
	private Button btnSeven;
	private Button btnEIGHT;
	private Button btnNine;
	private Button btnZero;
	private Button btnStar;
	private Button btnHash;
	
	Button btnCall;
	
	private ImageButton backSpace;
	
	public static EditText edNumInput;
	public static String numCall;
	
	int maxLength = 15;
	int currentLength;
	
	View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) 
	{
		rootView = inflater.inflate(R.layout.keyboard, container, false);
        initWiget();
		seteventClick(); 
		InputMethodManager ipm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		ipm.hideSoftInputFromWindow(edNumInput.getWindowToken(), 0);
		edNumInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				ContactActivity.adapter.getFilter().filter(s.toString());
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
		});
		return rootView;
	}
	
	@Override
	public void onResume() 
	{
		edNumInput.setText(null);
		super.onResume();
	}
	
	public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button b1 = (Button)v;
		edNumInput.setText(edNumInput.getText().toString() + b1.getText().toString());
	}
	
	public void initWiget()
	{
		edNumInput = (EditText)rootView.findViewById(R.id.edNumInput);
        btnOne = (Button)rootView.findViewById(R.id.btnOne);
        btnTwo = (Button)rootView.findViewById(R.id.btnTwo);
        btnThree = (Button)rootView.findViewById(R.id.btnThree);
        btnFour = (Button)rootView.findViewById(R.id.btnFour);
        btnFive = (Button)rootView.findViewById(R.id.btnFive);
        btnSix = (Button)rootView.findViewById(R.id.btnSix);
        btnSeven = (Button)rootView.findViewById(R.id.btnSeven);
        btnEIGHT = (Button)rootView.findViewById(R.id.btnEight);
        btnNine = (Button)rootView.findViewById(R.id.btnNine);
        btnZero = (Button)rootView.findViewById(R.id.btnZero);
        btnStar = (Button)rootView.findViewById(R.id.btnStar);
        btnHash = (Button)rootView.findViewById(R.id.btnHash);
        backSpace = (ImageButton)rootView.findViewById(R.id.btnBackSpace);
        
        btnCall = (Button)rootView.findViewById(R.id.btnCall);
	}
	
	public void seteventClick()
	{
		btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnFour.setOnClickListener(this);
        btnFive.setOnClickListener(this);
        btnSix.setOnClickListener(this);
        btnSeven.setOnClickListener(this);
        btnEIGHT.setOnClickListener(this);
        btnNine.setOnClickListener(this);
        btnZero.setOnClickListener(this);
        btnStar.setOnClickListener(this);
        btnHash.setOnClickListener(this);
        
        backSpace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str=edNumInput.getText().toString();
		        if (str.length() >1 ) { 
		            str = str.substring(0, str.length() - 1);
		            edNumInput.setText(str);
		            }
		       else if (str.length() <=1 ) {
		    	   edNumInput.setText(null);
		    	   edNumInput.setHint("Nhập Số..");
		        }
			}
		});
        
        btnCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				numCall = edNumInput.getText().toString();
				if(numCall == null || numCall.length() == 0)
				{
					AlertDialog.Builder noLogin = new AlertDialog.Builder(getContext());
					noLogin.setTitle("Oops!!!");
					noLogin.setMessage("Hãy Nhập Số Điện Thoại!!!");
					noLogin.setPositiveButton("OK", null);
					noLogin.setCancelable(false);
					noLogin.show();
				}
				else
				{
					Intent intent = new Intent(getActivity().getApplicationContext(), OnCallingActivity.class);
					startActivity(intent);
				}
			}
		});
	}
}
