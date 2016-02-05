package oos.group6.musiccomposer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

public class MidiController
{
	public Context context;
	public MediaPlayer player;
	
	public MidiController(Context context)
	{
		this.context = context;
	}
	
	public File createMidiFile(String fileName, Song song)
	{
		List<MidiTrack> tracks = createTracks(song);
		MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, (ArrayList<MidiTrack>) tracks);
		File actualFile = createFile(fileName);
		actualFile = writeMidiToFile(midi, actualFile);

		return actualFile;
	}
	
	public void deleteMidiFile(String fileName)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"midi";
		File file = new File(dir, fileName);
	    
		file.delete();
	}
	
	public void setupMidiFile(File midiFile)
	{
		player = MediaPlayer.create(context, Uri.parse(midiFile.getAbsolutePath()));
		Log.i("My media player", midiFile.getAbsolutePath());
	}
	
	public void playMidiFile()
	{
		player.start();
	}
	
	public void pauseMidiFile()
	{
		player.pause();
	}
	
	public void stopMidiFile()
	{
		player.stop();
	}

	public int getTotalTime()
	{
		return player.getDuration();
	}
	
	public int getCurrentTime()
	{
		return player.getCurrentPosition();
	}
	
	public boolean playing()
	{
		return player.isPlaying();
	}
	
	public void playTempFile(Song song)
	{
		File midiFile = createMidiFile("tempMidiFile.mid", song);
		setupMidiFile(midiFile);
		playMidiFile();
		midiFile.delete();
	}
	
	
	
	private List<MidiTrack> createTracks(Song song)
	{
		MidiTrack tempoTrack = convertSongToTempoTrack(song);
		MidiTrack noteTrack = convertSongToNoteTrack(song);

		// 3. Create a MidiFile with the tracks we created
		List<MidiTrack> tracks = new ArrayList<MidiTrack>();
		tracks.add(tempoTrack);
		tracks.add(noteTrack);
		
		return tracks;
	}
	
	private File createFile(String fileName)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"midi";
	    //create folder
	    File folder = new File(dir); //folder name
	    folder.mkdirs();

	    //create file
	    File file = new File(dir, fileName);
	    try 
	    {
			file.createNewFile();
			Log.i("New File", "true");
		} 
	    catch (IOException e) 
	    {
			Log.i("New file", "false");
		}
	    
	    Log.i("Creating File", file.getAbsolutePath());
	    
	    return file;
	}
	
	private File writeMidiToFile(MidiFile midi, File actualFile)
	{
		try
		{
			midi.writeToFile(actualFile);
			Log.i("midi", "true");
		}
		catch(Exception e)
		{
			Log.i("midi", e.getLocalizedMessage());
		}
		Log.i("midi", "are you working?");
		
		return actualFile;
	}
	
	private MidiTrack convertSongToNoteTrack(Song song)
	{
		MidiTrack noteTrack = new MidiTrack();
		long curTick = 0;
		
		for(Measure measure : song.measures)
		{
			for(Note note : measure.notes)
			{
				// Still have problems with tempo
				if(note.pitchLetter == 'R')
				{
				    curTick += note.beats * 240;
					continue;
				}
				int channel = 0;
			    int pitch = note.pitchAsInt();
			    int velocity = 100;
			    long tick = curTick;
			    long duration = note.beats * 240;

			    noteTrack.insertNote(channel, pitch, velocity, tick, duration);
			    
			    curTick += note.beats * 240;
			}
		}
		
		return noteTrack;
	}
	
	private MidiTrack convertSongToTempoTrack(Song song)
	{
		MidiTrack tempoTrack = new MidiTrack();
		
		// 2. Add events to the tracks
		// Track 0 is the tempo map
		TimeSignature ts = new TimeSignature();
		ts.setTimeSignature(song.beatsPerMeasure, song.beatLength, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

		Tempo tempo = new Tempo();
		tempo.setBpm(song.tempo);
		tempoTrack.insertEvent(ts);
		tempoTrack.insertEvent(tempo);
		
		return tempoTrack;
	}
}
