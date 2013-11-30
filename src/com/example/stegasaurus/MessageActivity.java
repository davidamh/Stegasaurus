package com.example.stegasaurus;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MessageActivity extends Activity {
	
	final int PHOTO_REQUEST = 1;
	String message = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void openCamera(View view) {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(takePhotoIntent, 0);
		
		if(activities.size() > 0) {
			EditText ETMsg = (EditText) findViewById(R.id.make_message);
			message = ETMsg.getText().toString();
			
			startActivityForResult(takePhotoIntent, PHOTO_REQUEST);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap pic = (Bitmap) extras.get("data");
			
			Bitmap encPic = encrypt(pic, message);
			
			//TODO write the picture
		}
	}
	
	private Bitmap encrypt(Bitmap pic, String msg) {
		Bitmap ret = pic.copy(Bitmap.Config.ARGB_8888, true);
		ret.setHasAlpha(true);
		
		char[] msgArr = msg.toCharArray();
		int msgLen = msgArr.length;
		
		// Create the array with the message length prepended
		char[] newArr = new char[msgArr.length + 4];
		for(int i = 0; i < 4; i++) {
			newArr[i] = (char) ((msgLen >>> ((3-i) * 8)) & 0x000000FF);
		}
		for(int i = 0; i < msgArr.length; i++) {
			newArr[i+4] = msgArr[i];
		}
		msgArr = newArr;
		
		int bMapH = ret.getHeight();
		int bMapW = ret.getWidth();
		
		// Add one byte to offset integer division, then divide by two since
		// there will be two bytes encoded per pixel.
		if((msgArr.length + 1)/2 > bMapH * bMapW) {
			// The Bitmap can't contain the string fully
			return null;
		}
		
		int numRows = (msgArr.length + bMapW - 1) / bMapW;
		int[] bMapPx = new int[numRows * bMapW];
		ret.getPixels(bMapPx, 0, bMapW, 0, 0, bMapW, numRows);
		
		int idx = 0;
		while(idx < msgArr.length) {
			int pIdx = idx / 2;
			
			char c1 = msgArr[idx++];
			char c2 = idx < msgArr.length ? msgArr[idx++] : 0;
			
			int pixel = bMapPx[pIdx];
			pixel &= 0xF0F0F0F0;
			
			pixel |= (int) ((c1 & 0xF0) << 20);
			pixel |= (int) ((c1 & 0x0F) << 16);
			pixel |= (int) ((c2 & 0xF0) << 4);
			pixel |= (int) (c2 & 0x0F);
			
			bMapPx[pIdx] = pixel;
		}
		
		ret.setPixels(bMapPx, 0, bMapW, 0, 0, bMapW, numRows);
		
		return ret;
	}
}
