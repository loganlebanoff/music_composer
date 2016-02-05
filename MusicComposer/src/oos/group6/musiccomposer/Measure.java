package oos.group6.musiccomposer;

import java.util.ArrayList;

public class Measure 
{
	public ArrayList<Note> notes;
	public int numBeats;
	
	public Measure()
	{
		notes = new ArrayList<Note>();
	}
	
	public void addNote(Note note)
	{
		notes.add(note);
		numBeats += note.beats;
	}
	
	public void deleteLastNote()
	{
		if(notes.size() > 0)
		{
			numBeats -= notes.get(notes.size() - 1).beats;
			notes.remove(notes.size() - 1);
		}
	}
	
	public boolean isValid()
	{
		return numBeats == 8;
	}
	
	public boolean tooManyNotes()
	{
		return numBeats > 8;
	}

}
