package com.example.stegasaurus;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
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
		
		Intent choosePictureIntent = new Intent(Intent.ACTION_PICK);
		choosePictureIntent.setType("image/*");
		startActivityForResult(choosePictureIntent, REQUEST_CHOOSE_IMAGE);
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
		return "";
	}
}
