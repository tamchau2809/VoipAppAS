package chau.voipapp;

import java.io.Serializable;
import java.util.Comparator;

public class ContactItem implements Serializable, Comparable<ContactItem>
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String number;
	
	public ContactItem(String name, String num) {
		// TODO Auto-generated constructor stub
		super();
		this.name = name;
		this.number = num;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getNum()
	{
		return number;
	}
	
	public void setNum(String num)
	{
		this.number = num;
	}

	@Override
	public int compareTo(ContactItem another) {
		// TODO Auto-generated method stub
		return this.getName().compareTo(another.getName());
	}
}
