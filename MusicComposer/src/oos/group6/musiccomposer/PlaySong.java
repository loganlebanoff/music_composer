package oos.group6.musiccomposer;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Chronometer;

public class PlaySong extends Activity {
	
	MidiController midiController;
	private ProgressBar songProgress;
    private int songProgressStatus = 0;
    private Thread bar;
    private float lastPause;
    private boolean restart = false;

    private Handler mHandler = new Handler();
    private boolean signal;
    Chronometer mChrono;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_song);
		Bundle extras = getIntent().getExtras();
		String songname = extras.getString("songname");
		String dir = extras.getString("dir");
		String filename = extras.getString("filename");
		String authorname = extras.getString("authorname");
		final File songfile = new File(dir, filename);
		
		
		midiController = new MidiController(this.getApplicationContext());
		midiController.setupMidiFile(songfile);
		
		TextView name = (TextView)findViewById(R.id.SongName); 
		name.setText(songname + " - by " + authorname);
	    
	    mChrono = (Chronometer) findViewById(R.id.chronometer1);
		
		ImageButton bPlay = (ImageButton) findViewById(R.id.playButton);
		ImageButton bPause = (ImageButton) findViewById(R.id.pauseButton);
		ImageButton bStop = (ImageButton) findViewById(R.id.stopButton);
		
        songProgress = (ProgressBar) findViewById(R.id.songProgress);

        // Start lengthy operation in a background thread

        mChrono.setOnChronometerTickListener(new OnChronometerTickListener()
        {
        		public void onChronometerTick(Chronometer arg0)
        		{
        			long elapsed_milli = SystemClock.elapsedRealtime() - mChrono.getBase();
        			if (elapsed_milli > midiController.getTotalTime())
        			{
        				midiController.stopMidiFile();
        				midiController.setupMidiFile(songfile);
        				mChrono.stop();
        				mChrono.setBase(SystemClock.elapsedRealtime());
        				mChrono.setFormat(null);
        				lastPause = 0;
        			}
        				
        		}
        });
	    
		bPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Need to send the filename with the intent so that this
				signal = true;

				if (!restart && !midiController.playing())
				{	
					mChrono.setBase(SystemClock.elapsedRealtime());
					mChrono.start();
					midiController.playMidiFile();
				}
				
				else if (restart && !midiController.playing())
				{
					mChrono.setBase((long) (mChrono.getBase() + SystemClock.elapsedRealtime() - lastPause));
					restart = false;
					mChrono.start();
					midiController.playMidiFile();
				}
	
				Thread t = new Thread(new Runnable() {
		        	
		            public void run() {
			        	
		                while (signal) {
		                	Log.i("DEBUG", "PROG-BAR THREAD IS SPINNING");
		                	double b = midiController.getCurrentTime();
		                	double a = midiController.getTotalTime();
		                	songProgressStatus = (int)(b/a*100.0);
		                	
		                	
		                	if (songProgressStatus >= 99){
		                		break;
		                	}

		                    // Update the progress bar
		                    mHandler.post(new Runnable() {
		                        public void run() {
		                            songProgress.setProgress(songProgressStatus);
		                        }
		                    });
		                    Thread.yield();
		                }
		                
	                    songProgress.setProgress(0);
	                      
		                Log.i("DEBUG", "THREAD STOPPED");
		            }
		        });
				t.start();
			}
		});
		
		bPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Need to send the filename with the intent so that this
				lastPause = SystemClock.elapsedRealtime();
				mChrono.stop();
				midiController.pauseMidiFile();
				restart = true;
			}
		});

		bStop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Need to send the filename with the intent so that this
				restart = false;
				signal = false;
				midiController.stopMidiFile();
				midiController.setupMidiFile(songfile);
				mChrono.stop();
				mChrono.setBase(SystemClock.elapsedRealtime());
				mChrono.setFormat(null);
				lastPause = 0;
				
			}
		});
	    
	    
	    
	}//end on create

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_song, menu);
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
		Intent openMain = new Intent(PlaySong.this,
				ViewSongs.class);
		startActivity(openMain);
		finish();
	}
}
