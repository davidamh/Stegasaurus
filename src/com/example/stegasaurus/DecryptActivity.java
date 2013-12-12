package com.example.stegasaurus;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DecryptActivity extends Activity {
	
	final int REQUEST_CHOOSE_IMAGE = 7384;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decrypt);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent choosePictureIntent = new Intent();
		choosePictureIntent.setType("image/*");
		choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
		
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(choosePictureIntent, 0);
		
		if(activities.size() > 0) {
			startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.decrypt, menu);
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
	
	public void returnToMain(View view) {
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		if(requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK){  
			try {
	            Uri selectedImage = imageReturnedIntent.getData();
	            InputStream imageStream = getContentResolver().openInputStream(selectedImage);
	            Bitmap pic = BitmapFactory.decodeStream(imageStream);
	            String msg = decrypt(pic);
	            
	            if(msg != null) {
		            TextView decMsg = (TextView) findViewById(R.id.dec_msg_view);
		            decMsg.setText(msg);
	            }
			}
			catch(Exception e) {
				e.printStackTrace();
			}
        }
	}

	private String decrypt(Bitmap pic) {
		Bitmap map = pic.copy(Bitmap.Config.ARGB_8888, true);
		map.setHasAlpha(true);

		// Get the Height and the Width of the bit map picture
		int bMapH = map.getHeight();
		int bMapW = map.getWidth();
		
		// Set up the int array and then use it in the call 
		int[] bMapPx = new int[bMapH * bMapW];
		map.getPixels(bMapPx, 0, bMapW, 0, 0, bMapW, bMapH);
				
		int length = getLength(bMapPx);
		
		if (length > bMapPx.length - 2)
		{
			return null;
		}
		char[] msgArr = new char[length];
		
		int loop = 0;
		int pIdx = 2;
		while (loop < msgArr.length)
		{
			int pixelP1 = bMapPx[pIdx];	
			int pixelP2 = bMapPx[pIdx];
			
			pixelP1 &= 0x0F000000;
			pixelP2 &= 0x000F0000;

			pixelP1 = (pixelP1 >> 20);
			pixelP2 = (pixelP2 >> 16);
			
			int character1 = 0;
			character1 |= pixelP1;
			character1 |= pixelP2;
			
			msgArr[loop] = (char)character1;
			
			loop++;
			
			if (!(loop < msgArr.length))
			{
				break;
			}
			
			int pixelP3 = bMapPx[pIdx];			
			int pixelP4 = bMapPx[pIdx];
			
			pixelP3 &= 0x00000F00;
			pixelP4 &= 0x0000000F;
			
			pixelP3 = (pixelP3 >> 4);
			
			int character2 = 0;
			character2 |= pixelP3;
			character2 |= pixelP4;
			
			msgArr[loop] = (char)character2;

			loop++;
			pIdx++;
		}
		
		String ret = new String(msgArr);
		return ret;
	}
	
	
	private int getLength (int[] bMapPx)
	{
		// Create a part for each pixel, separate out the relevant part and then merge it into length.		
		int pixelP1 = bMapPx[0];	
		int pixelP2 = bMapPx[0];			
		int pixelP3 = bMapPx[0];			
		int pixelP4 = bMapPx[0];	
		
		int pixelP5 = bMapPx[1];	
		int pixelP6 = bMapPx[1];			
		int pixelP7 = bMapPx[1];			
		int pixelP8 = bMapPx[1];	
		
		// separate out the relevant part
		pixelP1 &= 0x0F000000;
		pixelP2 &= 0x000F0000;
		pixelP3 &= 0x00000F00;
		pixelP4 &= 0x0000000F;
		
		pixelP5 &= 0x0F000000;
		pixelP6 &= 0x000F0000;
		pixelP7 &= 0x00000F00;
		pixelP8 &= 0x0000000F;
		
		// Move it to the correct spot in the int
		pixelP1 = (pixelP1 << 4);
		pixelP2 = (pixelP2 << 8);
		pixelP3 = (pixelP3 << 12);
		pixelP4 = (pixelP4 << 16);
		
		pixelP5 = (pixelP5 >> 12);
		pixelP6 = (pixelP6 >> 8);
		pixelP7 = (pixelP7 >> 4);
		
		// Create the length and then merge them all in
		int length = 0;
		length |= pixelP1;
		length |= pixelP2;
		length |= pixelP3;
		length |= pixelP4;

		length |= pixelP5;
		length |= pixelP6;
		length |= pixelP7;
		length |= pixelP8;
				
		return length;
	}
}
