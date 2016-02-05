package oos.group6.musiccomposer;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import oos.group6.musiccomposer.Note.Accidental;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateSong extends Activity {

	Button bBack;
	Button bNext;
	EditText etSongName;
	EditText etAuthorName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_song);

		bBack = (Button) findViewById(R.id.buttonBackHelp);

		bBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				Intent openCreate = new Intent(CreateSong.this,
						MainActivity.class);
				startActivity(openCreate);
			}
		});

		bNext = (Button) findViewById(R.id.buttonNext);

		bNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String songName = etSongName.getText().toString();
				if(songName.length() < 1){
					songName = "Untitled";
				}
				String authorName = etAuthorName.getText().toString();
				if(authorName.length() < 1){
					authorName = "Unknown";
				}
				String fileName = FileWriter.songNameToFileName(songName);
				Song song = new Song(songName, authorName);
				
				// Create CSV file
				new FileWriter().buildCSV(fileName, song);
				
				// TODO Auto-generated method stub
				Intent openEdit = new Intent(CreateSong.this,
						EditSong.class);
				openEdit.putExtra("fileName", fileName);
				startActivity(openEdit);
			}
		});
		
		etSongName = (EditText) findViewById(R.id.editTextSongName);
		etAuthorName = (EditText) findViewById(R.id.editTextAuthorName);

	}// end onCreate

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_song, menu);
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
		// TODO Auto-generated method stub
		Intent openMain = new Intent(CreateSong.this,
				MainActivity.class);
		startActivity(openMain);
		finish();
	}
}
