package com.example.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.net.Uri;
import android.util.Xml;

import com.example.domain.Contact;
import com.example.utils.MD5;

public class ContactEngin {
	//从网络获取联系人数据
	public static List<Contact> getContacts() throws Exception 
	{
		String path = "http://www.ishaohe.com/daxuebao/api";
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if(conn.getResponseCode() == 200)
		{
			return parseXML(conn.getInputStream());  //返回解析后的数据
		}
		return null;
	}

	private static List<Contact> parseXML(InputStream inputStream) throws Exception 
	{
		List<Contact> contacts = new ArrayList<Contact>();
		Contact contact = null;
		XmlPullParser parser = Xml.newPullParser();
		int event = parser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT)
		{
			switch (event) {
			case XmlPullParser.START_TAG:
				if("contact".equals(parser.getName()))
				{
					contact = new Contact();
					contact.id = new Integer(parser.getAttributeValue(0));
				}
				if("name".equals(parser.getName()))
				{
					contact.name = parser.nextText();
				}
				if("image".equals(parser.getName()))
				{
					contact.image = parser.getAttributeValue(0);
				}
				
				break;
			case XmlPullParser.END_TAG:
				if("contact".equals(parser.getName()))
				{
					contacts.add(contact);
				}
				break;

			default:
				break;
			}
			event = parser.next();
		}
		return contacts;
	}

	/**
	 * 获取网络图片  如果图片存在于缓存中，就返回该图片，否则从网路中加载 
	 * @param path 图片的路径（服务器返回的网络路径)
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	public static Uri getImage(String path,File cacheDir) throws Exception 
	{
		//在缓存目录中寻找图片
		File localFile = new File(cacheDir,MD5.getMD5(path)+path.substring(path.lastIndexOf(".")));
		if(localFile.exists())
		{
			return Uri.fromFile(localFile);
		}
		else
		{
			HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			if(conn.getResponseCode() == 200)
			{
				FileOutputStream fos = new FileOutputStream(localFile);
				InputStream inputStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = inputStream.read(buffer)) != -1)
				{
					fos.write(buffer, 0, len);
				}
				fos.close();
				inputStream.close();
				return Uri.fromFile(localFile);
			}
		}
		return null;
	}

}
