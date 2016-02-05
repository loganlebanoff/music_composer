package oos.group6.musiccomposer;

import java.util.ArrayList;
import java.util.Scanner;

import oos.group6.musiccomposer.Note.Accidental;
import android.content.Context;
import android.util.Log;

public class Song {
	
	public int DEFAULT_TEMPO = 120;
	
	public enum Clef
	{
		Treble,
		Bass;
	}

	private Context context;
	public String name;
	public String author;
	public Clef clef;
	public int tempo = DEFAULT_TEMPO;
	public int beatsPerMeasure;
	public int beatLength;
	public ArrayList<Measure> measures;
	
	public Song()
	{
		this.measures = new ArrayList<Measure>();
		measures.add(new Measure());
	}
	
	public Song(String name, String author)
	{
		this.name = name;
		this.author = author;
		this.measures = new ArrayList<Measure>();
	}
	
	public void parseFile(String fileName)
	{
		String text = new FileWriter().readFromFile(fileName);
		Scanner scanner = new Scanner(text);
		
		ArrayList<String> lines = new ArrayList<String>();
		while(scanner.hasNextLine())
			lines.add(scanner.nextLine());
		
		name = lines.get(0);
		author = lines.get(1);
		int numMeasures = Integer.parseInt(lines.get(2));
		int numLines = lines.size();
		for(int i = 3; i < numLines; i++)
		{
			String[] lineItems = lines.get(i).split(",");
			Measure measure = new Measure();
			
			for(int j = 1; j < lineItems.length; j += 4)
			{
				int beats = Integer.parseInt(lineItems[j]);
				char pitchLetter = lineItems[j + 1].charAt(0);
				int pitchOctave = Integer.parseInt(lineItems[j + 2]);
				String accidental = lineItems[j + 3];
				
				Note note = new Note(beats, String.format("%c%d", pitchLetter, pitchOctave), accidental);
				measure.addNote(note);
			}
			
			addMeasure(measure);
		}
	}
	
	public void addMeasure(Measure measure)
	{
		if(measures.size() == 1 && measures.get(0).notes.size() == 0)
			measures.set(0, measure);
		else
			measures.add(measure);
	}
	
	public void deleteMeasure(Measure measure)
	{
		measures.remove(measure);
	}
}
