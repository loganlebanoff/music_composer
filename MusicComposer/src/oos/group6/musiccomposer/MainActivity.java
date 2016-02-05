
package oos.group6.musiccomposer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	
	Button bCreate;
	Button bView;
	Button bHelp;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bCreate = (Button) findViewById(R.id.bCreate);
		bView = (Button) findViewById(R.id.bView);
		bHelp = (Button) findViewById(R.id.bHelp);
		

		
		bCreate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openCreate = new Intent(MainActivity.this, CreateSong.class);
				startActivity(openCreate);
			}
		});
		
		
		bView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openView = new Intent(MainActivity.this, ViewSongs.class);
				startActivity(openView);
			}
		});
		
		bHelp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openHelp = new Intent(MainActivity.this, Help.class);
				startActivity(openHelp);
			}
		});
		
	}//end on create

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
}
