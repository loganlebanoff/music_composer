package oos.group6.musiccomposer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class Help extends Activity {
	
	Expandable_List_Adapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	Button bBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		//get the listview
		expListView = (ExpandableListView) findViewById(R.id.expandableListHelp);
		
		//prepare the list's data
		prepareListData();
		
		listAdapter = new Expandable_List_Adapter(this, listDataHeader, listDataChild);
		
		//setting list adapter
		expListView.setAdapter(listAdapter);
		
		bBack = (Button) findViewById(R.id.buttonBackHelp);
		
		bBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openMain = new Intent(Help.this, MainActivity.class);
				startActivity(openMain);
			}
		});
	}
	
	//prepares the list data
	private void prepareListData(){
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		//add header list
		listDataHeader.add("Create Song");
		listDataHeader.add("Edit Song");
		listDataHeader.add("Song List");
		listDataHeader.add("Play Song");
		
		//add child lists
		List<String> createSong = new ArrayList<String>();
		createSong.add("On the create song page, you need to input an author and song name. Click 'Next' to create the song and begin writing msuic");
		
		List<String> editSong = new ArrayList<String>();
		editSong.add("Note - choose what pitch to play. For example, F4 is higher than E4, and F5 is higher than F4.");
		editSong.add("Accidental - choose modifications to the sound. '#' will make the note slightly higher, and 'b' will make the note slightly lower. 0 will keep it the same.");
		editSong.add("Beat - choose how long to play the sound, corresponding to what fraction of the measure that the note will last.");
		editSong.add("Place - place the note on the staff with the options that you set.");
		editSong.add("Save - save the song to the song list.");
		editSong.add("Play - play the current measure.");
		editSong.add("Advanced options - press the Android settings button to open a list of advanced options.");

		
		List<String> songList = new ArrayList<String>();
		songList.add("Tap a song name to open up the options for that song.");
		
		List<String> playSong = new ArrayList<String>();
		playSong.add("Clock - shows current time in song.");
		playSong.add("► - starts music.");
		playSong.add("|| - pauses music.");
		playSong.add("■ - stops music and resets track to beginning.");

		
		listDataChild.put(listDataHeader.get(0),  createSong);
		listDataChild.put(listDataHeader.get(1),  editSong);
		listDataChild.put(listDataHeader.get(2),  songList);
		listDataChild.put(listDataHeader.get(3),  playSong);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
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
		Intent openMain = new Intent(Help.this,
				MainActivity.class);
		startActivity(openMain);
		finish();
	}
}
