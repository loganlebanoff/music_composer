package oos.group6.musiccomposer;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditSong extends Activity {
	
	// The Y value of top space for G5
	float gY;
	int cMeasure;

	Song song;
	Measure currentMeasure;
	String fileName;
	ImageView[] noteImages = new ImageView[8];
	ImageView[] accImages = new ImageView[8];;
	Activity activity = this;
	MidiController midiSaver = new MidiController(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_song);
		Bundle extras = getIntent().getExtras();
		fileName = extras.getString("fileName");
		initializeImageViews();
		
		//variable to tell what measure we are on as an integer
		cMeasure = 0;
		
		// Initialize song object
		song = new Song();
		song.parseFile(fileName);
		currentMeasure = song.measures.get(0);

		//adding notes A - G
		Spinner dropdown = (Spinner)findViewById(R.id.spinnerNotes);
		String[] items = new String[]{"D4", "E4", "F4", "G4", "A5", "B5", "C5", "D5", "E5", "F5", "G5", "Rest"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		dropdown.setAdapter(adapter);

		//Adding values 1/8 - 1
		Spinner valDropdown = (Spinner)findViewById(R.id.spinnerValue);
		String[] valItems = new String[]{"1", "1/2", "1/4", "1/8"};
		ArrayAdapter<String> valAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valItems);
		valDropdown.setAdapter(valAdapter);

		//Adding accidentals
		Spinner accDropdown = (Spinner)findViewById(R.id.spinnerAccidental);
		String[] accItems = new String[]{"0", "b", "#"};
		ArrayAdapter<String> accAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accItems);
		accDropdown.setAdapter(accAdapter);

		
		MoveButtons();
		
		// If opening an existing file, place notes from existing song
		 new CountDownTimer(100, 100) {

		     public void onTick(long millisUntilFinished) {
		     }

		     public void onFinish() {
		         updateCurrentMeasure();
		     }
		  }.start();
		  

		Button bPlaceNote = (Button) findViewById(R.id.buttonPlace);

		bPlaceNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Spinner spinnerNotes = (Spinner) findViewById(R.id.spinnerNotes);
				Spinner spinnerValue = (Spinner) findViewById(R.id.spinnerValue);
				Spinner spinnerAccidental = (Spinner) findViewById(R.id.spinnerAccidental);
				String pitch = (String) spinnerNotes.getSelectedItem();
				String value = (String) spinnerValue.getSelectedItem();
				String accidental = (String) spinnerAccidental.getSelectedItem();
				ImageView staff = (ImageView) findViewById(R.id.Staff);

				int staffWidth = staff.getWidth();
				int staffHeight = staff.getHeight();
				int intervalY = (int) (staffHeight * 0.043);
				int gY = (int) (staff.getY() - staffHeight * 0.09);

				
				
				for(int i = 0; i < noteImages.length; i++)
				{
					float x = (float) (staff.getX() + staffWidth * 0.29 + staffWidth * 0.08 * i);
					Log.i("x", x + "");
					noteImages[i].setX(x);
					accImages[i].setX((float) (noteImages[i].getX() - staffWidth * 0.03));
				}
				

				// Add note to back-end
				int measureBeat = currentMeasure.numBeats;
				int noteBeats = fractionToInt(value);
				Note noteobj = new Note(noteBeats, pitch, accidental);
				currentMeasure.addNote(noteobj);
				
				if(currentMeasure.tooManyNotes())
				{
					currentMeasure.deleteLastNote();
					
			        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
			        alertDialog.setTitle("Too many beats in the measure");
			        alertDialog.setMessage("You are trying to insert a note that is too long for the current measure.");

			        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			            // here you can add functions
			        }
			        });
			        
			        alertDialog.show();
				}
				else
				{
					updateNote(measureBeat, staff, noteobj);
				}

			}

		});

		Button bSave = (Button) findViewById(R.id.buttonSave);

		bSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isSongValid = true;
				int badMeasure = -1;
				int numMeasures = song.measures.size();
				for(int i = 0; i < numMeasures; i++)
				{
					if(!song.measures.get(i).isValid())
					{
						isSongValid = false;
						badMeasure = i;
						break;
					}
				}
				
				if(isSongValid)
				{

					new FileWriter().deleteFile(fileName);
					FileWriter writer = new FileWriter();
					writer.buildCSV(FileWriter.songNameToFileName(song.name), song);
					midiSaver.deleteMidiFile(fileName.substring(0, fileName.length() - 4) + ".mid");
					midiSaver.createMidiFile(song.name + ".mid", song);
					Toast.makeText(EditSong.this, "Song saved", Toast.LENGTH_SHORT).show();
					
				}
				else
				{
					showSaveFailDialog(badMeasure);
				}
			}

		});
		
		Button bDelete = (Button) findViewById(R.id.buttonDelete);

		bDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				currentMeasure.deleteLastNote();
				
				noteImages[currentMeasure.numBeats].setVisibility(View.INVISIBLE);
				accImages[currentMeasure.numBeats].setVisibility(View.INVISIBLE);
			}

		});

		Button bNext = (Button) findViewById(R.id.buttonNext);
		
		bNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cMeasure == song.measures.size() - 1 && currentMeasure.notes.size() == 0)
				{
					Toast.makeText(EditSong.this, "No next measure", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(cMeasure == song.measures.size() - 1)
				{
					song.measures.add(new Measure());
				}
				cMeasure++;
				currentMeasure = song.measures.get(cMeasure);
				updateCurrentMeasure();
				Log.i("next", cMeasure + "");

			}
		});
		
		Button bBack = (Button) findViewById(R.id.buttonBack);
		
		bBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cMeasure != 0)
				{
					if(currentMeasure.notes.size() == 0 && cMeasure == song.measures.size() - 1 && cMeasure != 0)
						song.deleteMeasure(currentMeasure);
					
					if(cMeasure != 0)
					{
						cMeasure--;
						currentMeasure = song.measures.get(cMeasure);
					}
					
					updateCurrentMeasure();
					Log.i("back", cMeasure + "");
				}
				else
				{
					Toast.makeText(EditSong.this, "No previous measure", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Button bPlay = (Button) findViewById(R.id.buttonPlay);
		
		bPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playCurrentMeasure();
			}
		});
		
	}
	
	public void trimEmptyMeasures()
	{
		Measure measure = currentMeasure;
		int measureIndex = cMeasure;
		
		if(measureIndex == song.measures.size() - 1)
		{
			while(currentMeasure.notes.size() == 0 && cMeasure != 0 && cMeasure == song.measures.size() - 1)
			{
				song.deleteMeasure(currentMeasure);
				cMeasure--;
				currentMeasure = song.measures.get(cMeasure);
			}
		}
		
		if(cMeasure >= song.measures.size())
		{
			cMeasure = song.measures.size() - 1;
			currentMeasure = song.measures.get(cMeasure);
		}
	}
	
	public void showSaveFailDialog(int badMeasureIndex)
	{
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Invalid measure");
        alertDialog.setMessage("You do not have the right number of beats in measure " + (badMeasureIndex + 1) + ". Make sure there are exactly 4 beats in that measure before moving on.");

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            // here you can add functions
        }
        });
        
        alertDialog.show();
	}
	
	//front end method to update the staff by a single note
	public void updateNote(int slot, ImageView staff, Note n){
		ImageView note = noteImages[slot];
		ImageView imgAccidental = accImages[slot];
		int staffWidth = staff.getWidth();
		int staffHeight = staff.getHeight();
		int intervalY = (int) (staffHeight * 0.043);
		int staffY = (int) staff.getY();
		int gY = (int) (staff.getY() - staffHeight * 0.09);
		
		int height = (int) (staff.getHeight() * 0.55);
		int width = (int) (staff.getWidth() * 0.055);
		Log.i("height", width + " " + height);
		
		
		
		Log.i("pitch",n.pitchStr);
		switch (n.pitchStr)
		{
		case "G5" : note.setY(gY); break;
		case "F5" : note.setY(gY + intervalY * 1f); break;
		case "E5" : note.setY(gY + intervalY * 2f); break;
		case "D5" : note.setY(gY + intervalY * 3f); break;
		case "C5" : note.setY(gY + intervalY * 4f); break;
		case "B5" : note.setY(gY + intervalY * 5f); break;
		case "A5" : note.setY(gY + intervalY * 6f); break;
		case "G4" : note.setY(gY + intervalY * 7f); break;
		case "F4" : note.setY(gY + intervalY * 8f); break;
		case "E4" : note.setY(gY + intervalY * 9f); break;
		case "D4" : note.setY(gY + intervalY * 10f); break;
		case "Rest" : note.setY(staff.getY()); note.setImageResource(R.drawable.wholerest); Log.i("rest",""); break;
		default : break;
		}

		if(n.pitchStr.equals("Rest") || n.pitchLetter == 'R')
		{
			switch(n.beatsToFraction())
			{
			case "1" : note.setImageResource(R.drawable.wholerest); break;
			case "1/2" : note.setImageResource(R.drawable.halfrest); break;
			case "1/4" : note.setImageResource(R.drawable.quarterrest); break;
			case "1/8" : note.setImageResource(R.drawable.eighthrest); break;
			}
			
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) note.getLayoutParams();
			params.height = staffHeight;
			note.setLayoutParams(params);
			note.setVisibility(View.VISIBLE);
			note.setY(staffY);
			Log.i("note height width", note.getHeight() + " " + note.getWidth() + " " + staffHeight + " " + staffWidth + " " + note.getY() + " " + staff.getY());
		}
		else
		{
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) note.getLayoutParams();
			params.height = height;
			params.width = width;
			note.setLayoutParams(params);
			note.setVisibility(View.VISIBLE);
			
			switch(n.beatsToFraction())
			{
			case "1" : note.setImageResource(R.drawable.whole); break;
			case "1/2" : note.setImageResource(R.drawable.half); break;
			case "1/4" : note.setImageResource(R.drawable.quarter); break;
			case "1/8" : note.setImageResource(R.drawable.eighth); break;
			}
		}
		
		FrameLayout.LayoutParams accparams = (FrameLayout.LayoutParams) imgAccidental.getLayoutParams();
		accparams.height = height;
		accparams.width = width / 2;
		imgAccidental.setLayoutParams(accparams);
		
		Log.i("accidental", n.accidental.toString());
		switch(n.accidental)
		{
		case Natural : imgAccidental.setVisibility(ImageView.INVISIBLE); break;
		case Flat : imgAccidental.setVisibility(ImageView.VISIBLE); imgAccidental.setImageResource(R.drawable.flat); break;
		case Sharp : imgAccidental.setVisibility(ImageView.VISIBLE); imgAccidental.setImageResource(R.drawable.sharp); break;
		}
		imgAccidental.setY(note.getY());
	}
	
	//front end method to load a measure
	public void updateCurrentMeasure(){
		
		ImageView staff = (ImageView) findViewById(R.id.Staff);
		for(int i = 0; i < noteImages.length; i++)
		{
			noteImages[i].setVisibility(ImageView.INVISIBLE);
			accImages[i].setVisibility(ImageView.INVISIBLE);
		}
		
		int numNotes = currentMeasure.notes.size();
		int imageViewIndex = 0;
		for (int i = 0; i < numNotes; i++)
		{
			updateNote(imageViewIndex, staff, currentMeasure.notes.get(i));
			imageViewIndex += currentMeasure.notes.get(i).beats;
		}
		displayMeasureNumber();
	}
	
	public void displayMeasureNumber()
	{
		TextView tvMeasureNumber = (TextView) findViewById(R.id.measureNumber);
		tvMeasureNumber.setText(String.format("Measure %d/%d", cMeasure + 1, song.measures.size()));
	}

	// Returns the length in eighth notes
	public int fractionToInt(String fraction)
	{
		int intValue = 0;

		switch(fraction)
		{
		case "1" : intValue = 8; break;
		case "1/2" : intValue = 4; break;
		case "1/4" : intValue = 2; break;
		case "1/8" : intValue = 1; break;
		}

		return intValue;
	}

	public void initializeImageViews()
	{
		noteImages[0] = (ImageView)findViewById(R.id.Note0);
		noteImages[1] = (ImageView)findViewById(R.id.Note1);
		noteImages[2] = (ImageView)findViewById(R.id.Note2);
		noteImages[3] = (ImageView)findViewById(R.id.Note3);
		noteImages[4] = (ImageView)findViewById(R.id.Note4);
		noteImages[5] = (ImageView)findViewById(R.id.Note5);
		noteImages[6] = (ImageView)findViewById(R.id.Note6);
		noteImages[7] = (ImageView)findViewById(R.id.Note7);

		accImages[0] = (ImageView)findViewById(R.id.Accidental0);
		accImages[1] = (ImageView)findViewById(R.id.Accidental1);
		accImages[2] = (ImageView)findViewById(R.id.Accidental2);
		accImages[3] = (ImageView)findViewById(R.id.Accidental3);
		accImages[4] = (ImageView)findViewById(R.id.Accidental4);
		accImages[5] = (ImageView)findViewById(R.id.Accidental5);
		accImages[6] = (ImageView)findViewById(R.id.Accidental6);
		accImages[7] = (ImageView)findViewById(R.id.Accidental7);
	}
	
	public void playCurrentMeasure()
	{
		Song tempSong = new Song();
		tempSong.addMeasure(currentMeasure);
		
		MidiController midiController = new MidiController(this.getApplicationContext());
		midiController.playTempFile(tempSong);
	}
	
	public void MoveButtons()
	{
		/*
		ImageView staff = (ImageView) findViewById(R.id.Staff);
		int staffWidth = staff.getWidth();
		int staffHeight = staff.getHeight();
		int intervalY = (int) (staffHeight * 0.043);
		int gY = (int) (staff.getY() - staffHeight * 0.09);
		float staffY = staff.getY();
		float staffX = staff.getX();
		int screenHeight = getBaseContext().getResources().getDisplayMetrics().heightPixels;
		int screenWidth = getBaseContext().getResources().getDisplayMetrics().widthPixels;
		Log.i("screen width / height", screenWidth + " / " + screenHeight);
		Spinner dropdown = (Spinner)findViewById(R.id.spinnerNotes);
		dropdown.setY(gY);
		Log.i("dropdown y", gY + "");
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.edit_song, menu);
		menu.add(1, 0, 0, "Change Song Name");
		menu.add(1, 1, 1, "Change Author Name");
		menu.add(1, 2, 2, "Alter Tempo");
		menu.add(1, 3, 3, "Go to Play Song");
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		/*
		if (id == R.id.action_settings) {
			return true;
		}
		*/
		
		
		if(id == 0){
			
			
			final EditText inputsong = new EditText(this);
			new AlertDialog.Builder(EditSong.this)
		    .setTitle("Change Song Name")
		    .setMessage("New name:")
		    .setView(inputsong)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            Editable value = inputsong.getText(); 
		            song.name = value.toString();
		        }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            // Do nothing.
		        }
		    }).show();
		    
			
		}
		else if(id == 1){
			
			final EditText inputauthor = new EditText(this);
			new AlertDialog.Builder(EditSong.this)
		    .setTitle("Change Author Name")
		    .setMessage("New name:")
		    .setView(inputauthor)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            Editable value = inputauthor.getText(); 
		            song.author = value.toString();
		        }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            // Do nothing.
		        }
		    }).show();
		}
		else if(id == 2){
			final EditText inputtempo = new EditText(this);
			inputtempo.setFilters(new InputFilter[] {
				    // Maximum 2 characters.
				    new InputFilter.LengthFilter(3),
				    // Digits only.
				    DigitsKeyListener.getInstance(),  // Not strictly needed, IMHO.
				});
			new AlertDialog.Builder(EditSong.this)
		    .setTitle("Change Tempo")
		    .setMessage("Enter a number between 60 and 260")
		    .setView(inputtempo)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            Editable value = inputtempo.getText(); 
		            song.tempo = Integer.parseInt(value.toString());
		        }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            // Do nothing.
		        }
		    }).show();
		}
		else if(id == 3){
			String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"midi";
			Intent openPlay = new Intent(EditSong.this,
					PlaySong.class);
	    	openPlay.putExtra("songname", song.name);
	    	openPlay.putExtra("dir", dir);
	    	openPlay.putExtra("filename", song.name + ".mid");
	    	openPlay.putExtra("authorname", song.author);
	    	startActivity(openPlay);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent openMain = new Intent(EditSong.this,
				ViewSongs.class);
		startActivity(openMain);
		finish();
	}
}
