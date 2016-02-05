package oos.group6.musiccomposer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class FileWriter {
	
	private Context context;
	
	public FileWriter()
	{
		
	}
	
	public static String songNameToFileName(String songName)
	{
		return songName + ".csv";
	}
	
	public static File createFile(String fileName)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"csv";
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
	
	public void buildCSV(String fileName, Song song)
	{
		String outputText = "";
		outputText += song.name + "\n" + song.author + "\n";
		int numMeasures = song.measures.size();
		outputText += numMeasures + "\n";
		
		for(int i = 0; i < numMeasures; i++)
		{
			int numNotes = song.measures.get(i).notes.size();
			outputText += numNotes;
			
			for(int j = 0; j < numNotes; j++)
			{
				Note note = song.measures.get(i).notes.get(j);
				outputText += "," + note.beats + "," + note.pitchLetter + "," + note.pitchOctave + "," + note.accidentalToString();
			}
			
			outputText += "\n";
		}
		
		Log.i("Building CSV", outputText);
		
		File file = createFile(fileName);
		writeToFile(file, outputText);
		
	}

	public void writeToFile(File file, String outputText)
	{
		  try {
		     FileOutputStream fOut = new FileOutputStream(file);;
		     fOut.write(outputText.getBytes());
		     fOut.close();
		  }
		  catch (Exception e) {
		     // TODO Auto-generated catch block	
		     e.printStackTrace();
		  }
	}
	
	public String readFromFile(String fileName)
	{
		try{
			String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"csv";
		    //create folder
		    File folder = new File(dir); //folder name
		    folder.mkdirs();

		    //create file
		    File file = new File(dir, fileName);
		    
	         FileInputStream fin = new FileInputStream(file);
	         int c;
	         String temp="";
	         while( (c = fin.read()) != -1){
	            temp = temp + Character.toString((char)c);
	         }
	         fin.close();
	         
	         Log.i("Read file", temp);
	         
	         return temp;

	      }
		catch(Exception e) {
			Log.i("Read file", "false");
			return "";
	      }
	}
	
	public void deleteFile(String fileName)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator+"PocketComposer"+File.separator+"csv";
	    //create folder
	    File folder = new File(dir); //folder name
	    folder.mkdirs();

	    //create file
	    File file = new File(dir, fileName);
	    
	    file.delete();
	}
}
