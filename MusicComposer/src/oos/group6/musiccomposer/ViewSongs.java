package oos.group6.musiccomposer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ViewSongs extends ActionBarActivity {

	Activity activity = this;
	SimpleAdapter simpleAdpt;
	ListView lv;
	MidiController midiController = new MidiController(this);
	String songNameSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_songs);

		// BEGIN EXAMPLE FOR PLAYING CREATING MIDI FILE AND PLAYING MIDI FILE
		/*
		Song song = new Song();
		song.name = "jarble2";
		song.tempo = 120;
		Measure measure = new Measure();
		measure.notes.add(new Note(1, "C4", "0"));
		measure.notes.add(new Note(1, "D4", "0"));
		measure.notes.add(new Note(1, "R2", "0"));
		measure.notes.add(new Note(1, "E4", "0"));
		measure.notes.add(new Note(1, "F4", "#"));
		song.measures.add(measure);

		
		FileWriter fileEditor = new FileWriter();
		File file = midiController.createMidiFile("jarble2.mid", song);
		File csvfile = fileEditor.createFile("jarble2.csv");
		fileEditor.buildCSV("jarble2.csv", song);
		
		*/
		
		
		initList();
		// We get the ListView component from the layout
		lv = (ListView) findViewById(R.id.listView);

		simpleAdpt = new SimpleAdapter(this, SongList,
				android.R.layout.simple_list_item_1, new String[] { "Cancion" },
				new int[] { android.R.id.text1 });
		lv.setAdapter(simpleAdpt);

		// we register for the contextmneu
		registerForContextMenu(lv);

		// React to user clicks on item
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view,
					int position, long id) {

				openContextMenu(view);

			}

		});

		// END EXAMPLE
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_songs, menu);
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		
		// We know that each row in the adapter is a Map
		HashMap map = (HashMap) simpleAdpt.getItem(aInfo.position);
		songNameSelected = map.get("Cancion").toString();
		//menu.setHeaderTitle("Options for " + map.get("Cancion"));
		menu.setHeaderTitle("Options for " + songNameSelected);
		menu.add(1, 1, 1, "Edit");
		menu.add(1, 2, 2, "Play");
		menu.add(1, 3, 3, "Share");
		menu.add(1, 4, 4, "Delete");

	}
	
	// This method is called when user selects an Item in the Context menu
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
	    int itemId = item.getItemId();
	    // Implements our logic
	    
	    
	    if(itemId == 1){
			Intent openEdit = new Intent(ViewSongs.this,
					EditSong.class);
			openEdit.putExtra("fileName", FileWriter.songNameToFileName(songNameSelected));
			startActivity(openEdit);
	    }
	    else if(itemId == 2){
			
			String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"midi";
		    //create folder
		    File folder = new File(dir); //folder name
		    folder.mkdirs();
		   
		    String filename =  songNameSelected + ".mid";
		    //create file
		    File songfile = new File(dir, filename);
		    
		    String csvFileName =  songNameSelected + ".csv";
		    Song song = new Song();
		    song.parseFile(csvFileName);
		    String authorname = song.author;		    
	    	//midiController.playMidiFile(songfile);	
	    	
	    	Intent openPlay = new Intent(ViewSongs.this,
					PlaySong.class);
	    	openPlay.putExtra("songname", songNameSelected);
	    	openPlay.putExtra("dir", dir);
	    	openPlay.putExtra("filename", filename);
	    	openPlay.putExtra("authorname", authorname);
	    	startActivity(openPlay);
	    	
	    }
	    else if(itemId == 3){
			String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"csv";
		    //create folder
		    File folder = new File(dir); //folder name
		    folder.mkdirs();
	    	
	    	Song song = new Song();
	    	song.parseFile(FileWriter.songNameToFileName(songNameSelected));
	    	
			dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"midi";
		    //create folder
		    folder = new File(dir); //folder name
		    folder.mkdirs();
		   
		    String midiFileName =  songNameSelected + ".mid";
		    //create file
		    File file = new MidiController(this.getApplicationContext()).createMidiFile(midiFileName, song);
	    	
	    	Intent intent = new Intent(Intent.ACTION_SEND);
	    	intent.putExtra("sms_body", "Hi how are you");
	    	intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
	    	intent.setType("message/rfc822");
	    	startActivity(Intent.createChooser(intent,"Send"));
	    }
	    else if(itemId == 4){
	    	// 1. Instantiate an AlertDialog.Builder with its constructor
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	    	// 2. Chain together various setter methods to set the dialog characteristics
	    	builder.setMessage("Are you sure want to delete the song \"" + songNameSelected + "\"?")
	    	       .setTitle("Delete song");
	    	
	    	// Add the buttons
	    	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // User clicked OK button
		    	    		new FileWriter().deleteFile(FileWriter.songNameToFileName(songNameSelected));
		    	    		new MidiController(activity.getApplicationContext()).deleteMidiFile(songNameSelected + ".mid");
		    				Intent reopenViewSongs = new Intent(ViewSongs.this,
		    						ViewSongs.class);
		    				startActivity(reopenViewSongs);
	    	           }
	    	       });
	    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // User cancelled the dialog
	    	        	   
	    	           }
	    	       });


	    	// 3. Get the AlertDialog from create()
	    	AlertDialog dialog = builder.create();
	    	dialog.show();
	    }

	    
	    return true;
	}


	// The data to show

	List<Map<String, String>> SongList = new ArrayList<Map<String, String>>();

	private void initList() {

		File sdCardRoot = Environment.getExternalStorageDirectory();
		File yourDir = new File(sdCardRoot, "PocketComposer/csv");
		for (File f : yourDir.listFiles()) {
			if (f.isFile()) {
				String filename = f.getName();
				FileWriter reader = new FileWriter();
				String contents = reader.readFromFile(filename);
				String[] lines = contents.split(System.getProperty("line.separator"));
				String songname = lines[0];
				SongList.add(createSong("Cancion", songname));
			}
		}
		// We populate the planets
	
	}

	private HashMap<String, String> createSong(String key, String name) {

		HashMap<String, String> Cancion = new HashMap<String, String>();

		Cancion.put(key, name);

		return Cancion;

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent openMain = new Intent(ViewSongs.this,
				MainActivity.class);
		startActivity(openMain);
		finish();
	}

}
