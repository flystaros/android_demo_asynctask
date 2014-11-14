package com.example.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asynctask.R;
import com.example.domain.Contact;
import com.example.engine.ContactEngin;

public class ContactAdapter extends BaseAdapter 
{
	private List<Contact> data;
	private int listviewItem;
	private File cache;
	private LayoutInflater layoutInflater;
	
	public ContactAdapter(Context context, List<Contact> data, int listviewItem, File cache) 
	{
		this.data = data;
		this.listviewItem = listviewItem;
		this.cache = cache;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	/**
	 * 得到数据的总数 
	 */
	@Override
	public int getCount() {
		return data.size();
	}

	/**
	 * 根据数据的索引得到集合对应的数据
	 */
	@Override
	public Object getItem(int position) 
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ImageView imageView = null;
		TextView textView = null;
		if(convertView == null)
		{
			convertView = layoutInflater.inflate(listviewItem, null);
			imageView = (ImageView) convertView.findViewById(R.id.imageView);
			textView = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(new DataWrapper(imageView, textView));
		}
		else
		{
			DataWrapper dataWrapper = (DataWrapper) convertView.getTag();
			imageView = dataWrapper.imageView;
			textView = dataWrapper.textView;
		}
		
		Contact contact = data.get(position);
		textView.setText(contact.name);
		
		//在这里加载图片容易出现无响应错误
		asyncImageLoad(imageView,contact.image);
		return convertView;
	}
	
	private void asyncImageLoad(ImageView imageView, String path) {
		AsyncImageTask asyncImageTask = new AsyncImageTask(imageView);
		asyncImageTask.execute(path);
	}

	private final class AsyncImageTask extends AsyncTask<String,Integer,Uri>
	{
		private ImageView imageView;
		public AsyncImageTask(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Uri doInBackground(String... params) {
			try {
				return ContactEngin.getImage(params[0],cache);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Uri result) {
			if(result != null && imageView != null)
			{
				imageView.setImageURI(result);
			}
		}
		
		
	}
	
	/**
	 * 异步加载显示图片  会开启很多的线程，不够健壮
	 * @param imageView
	 * @param path
	 */
	/*private void asyncImageLoad(final ImageView imageView, final String path)
	{
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				Uri uri = (Uri) msg.obj;
				if(uri != null)
				{
					imageView.setImageURI(uri);
				}
			}
			
		};
		Runnable runnable = new Runnable() 
		{
			@Override
			public void run() {
				try {
					Uri uri = ContactEngin.getImage(path,cache);
					handler.sendMessage(handler.obtainMessage(11, uri));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		new Thread(runnable).start();
	}*/

	private final class DataWrapper
	{
		public ImageView imageView;
		public TextView textView;
		public DataWrapper(ImageView imageView, TextView textView) {
			this.imageView = imageView;
			this.textView = textView;
		}
		
		
	}

}
