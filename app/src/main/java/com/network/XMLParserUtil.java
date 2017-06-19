package com.network;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * this class is to parse XML
 */
public class XMLParserUtil
{
	public static List<XMLBean> parseXml(InputStream stream) throws XmlPullParserException, IOException
	{
		List<XMLBean> datas = null;
		XMLBean data = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(stream, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			switch (eventType)
			{
			case XmlPullParser.START_DOCUMENT:
				datas = new ArrayList<>();
				break;
			case XmlPullParser.START_TAG:
				String tagName = parser.getName();
				if ("RESULT".equals(tagName))
				{
					data = new XMLBean();
				}
				if (data != null)
				{
					if ("HELIX".equals(tagName))
					{
						data.setHelixnt(parser.nextText());
					} else if ("COMPANY_ID".equals(tagName))
					{
						data.setCompany_id(parser.nextText());
					} else if ("COMPANY_DOMAIN".equals(tagName))
					{
						data.setCompany_domain(parser.nextText());
					} else if ("USER_NAME".equals(tagName))
					{
						data.setUser_name(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if ("RESULT".equals(parser.getName()))
				{
					datas.add(data);
					data = null;
				}
				break;
			}
			eventType = parser.next();
		}
		stream.close();
		return datas;
	}

	//next = [type = start tag name = property]
	//next = [type = text value = content]
	//next = [type = start tag name = property]
	//next
	private String getTag(XmlPullParser pullParser,String tag) throws IOException, XmlPullParserException {
		String content = null;
		pullParser.require(XmlPullParser.START_TAG,null,tag);
			if(pullParser.next() == XmlPullParser.TEXT)
			{
				content = pullParser.getText();
				pullParser.nextTag();
			}
		pullParser.require(XmlPullParser.END_TAG,null,tag);
		return content;
	}
}
