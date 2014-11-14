package com.example.asynctask;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.ListView;

import com.example.adapter.ContactAdapter;
import com.example.domain.Contact;
import com.example.engine.ContactEngin;

public class MainActivity extends Activity
{

	private ListView listView;
	private File cache;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//MainActivity.this 明确制定访问外部类的对象
			listView.setAdapter(new ContactAdapter(MainActivity.this,
					(List<Contact>)msg.obj,R.layout.listview_item,cache));
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) this.findViewById(R.id.listView);
		cache = new File(Environment.getExternalStorageDirectory(), "cache");
		
		if(!cache.exists())
		{
			cache.mkdir();
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					List<Contact> data = ContactEngin.getContacts();
					handler.sendMessage(handler.obtainMessage(22,data));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
	}

	

}
