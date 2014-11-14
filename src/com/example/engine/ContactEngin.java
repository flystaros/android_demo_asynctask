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
	//�������ȡ��ϵ������
	public static List<Contact> getContacts() throws Exception 
	{
		String path = "http://www.ishaohe.com/daxuebao/api";
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if(conn.getResponseCode() == 200)
		{
			return parseXML(conn.getInputStream());  //���ؽ����������
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
	 * ��ȡ����ͼƬ  ���ͼƬ�����ڻ����У��ͷ��ظ�ͼƬ���������·�м��� 
	 * @param path ͼƬ��·�������������ص�����·��)
	 * @return
	 * @throws Exception 
	 * @throws  
	 */
	public static Uri getImage(String path,File cacheDir) throws Exception 
	{
		//�ڻ���Ŀ¼��Ѱ��ͼƬ
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
