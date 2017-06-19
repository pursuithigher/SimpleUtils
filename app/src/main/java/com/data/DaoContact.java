package com.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;


/**
 * Created by banthalos on 5/12/15.
 */
public class DaoContact
{
	//private final static Class<DaoContact> TAG = DaoContact.class;

	private static List<HashMap<String, String>> getPhoneContact(Context context)
	{
		//MyLog.d(TAG, " [ContactHelper.java] getPhoneContact()");
		List<HashMap<String, String>> contacts= new ArrayList<>();
		//Log.i("uri", ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString());
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,null);
		if (cursor != null)
		{
			while (cursor.moveToNext())
			{
				// String photoID = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_ID));
				String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				if (TextUtils.isEmpty(display_name))
					continue;
				HashMap<String, String> contact = new HashMap<>();
				String numbers = getContactPhone(context, contactId);
				if(TextUtils.isEmpty(numbers))
					continue;
				contact.put("number", numbers);
				contact.put("name", display_name);
				contact.put("_id", contactId);

				contacts.add(contact);
			}
			cursor.close();
		}

		return contacts;
	}
//
	private static String getContactPhone(Context context, String contactId)
	{
		//MyLog.d(TAG, "getContactPhone");
		Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
		StringBuilder stringBuilder = new StringBuilder();
		if(phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				stringBuilder.append(phoneNumber).append(",");
			}
			if (stringBuilder.length() > 0)
				stringBuilder.deleteCharAt(stringBuilder.length() - 1);

			phoneCursor.close();
			//MyLog.d(TAG, "Local phone number :" + stringBuilder.toString());
		}
		return stringBuilder.toString();
	}

}
