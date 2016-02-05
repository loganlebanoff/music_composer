package oos.group6.musiccomposer;

import android.util.Log;

public class Note
{
	public enum Accidental
	{
		Flat,
		Natural,
		Sharp;
	}

	public int beats;
	public char pitchLetter;
	public int pitchOctave;
	public Accidental accidental;
	public String pitchStr;
	public String accidentalStr;

	public Note(int beats, String pitchString, String accidental)
	{
		this.beats = beats;
		this.pitchLetter = pitchString.charAt(0);
		this.pitchStr = pitchString;
		
		if(pitchLetter == 'R')
		{
			this.pitchOctave = 0;
			this.accidental = Accidental.Natural;
			this.accidentalStr = accidentalToString();
		}
		else
		{
			this.pitchOctave = Integer.parseInt(pitchString.substring(1));
			this.accidental = stringToAccidental(accidental);
			this.accidentalStr = accidental;
		}
		
	}
	
	public String beatsToFraction(){
		switch(this.beats){
		case 1:
			return "1/8";
		case 2:
			return "1/4";
		case 4: 
			return "1/2";
		case 8:
			return "1";
		}
		return "NULL";
	}

	private Accidental stringToAccidental(String str)
	{
		if(str.equals("0"))
			return Accidental.Natural;
		else if(str.equals("b"))
			return Accidental.Flat;
		else
			return Accidental.Sharp;
	}
	
	public String accidentalToString()
	{
		if(accidental == Accidental.Natural)
			return "0";
		else if(accidental == Accidental.Flat)
			return "b";
		else
			return "#";
	}

	public int pitchAsInt()
	{
		int pitch = 0;

		switch(pitchLetter)
		{
		case 'A' : pitch = 1; break;
		case 'B' : pitch = 3; break;
		case 'C' : pitch = 4; break;
		case 'D' : pitch = 6; break;
		case 'E' : pitch = 8; break;
		case 'F' : pitch = 9; break;
		case 'G' : pitch = 11; break;
		case 'R' : pitch = 0;
		default : pitch = 1; break;
		}

		if(accidental == Accidental.Flat)
			pitch--;
		else if(accidental == Accidental.Sharp)
			pitch++;

		pitch += pitchOctave * 12;

		return pitch;
	}


}
