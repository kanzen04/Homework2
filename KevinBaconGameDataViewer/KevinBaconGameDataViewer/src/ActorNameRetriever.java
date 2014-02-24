import java.net.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.io.*;
import javax.swing.JOptionPane;

public class ActorNameRetriever 
{
	static PrintWriter writer;
	static BufferedReader reader;
	static Vector<String> actors = new Vector<String>();

	public static void main(String[] args) 
	{
		try
		{
/*			readAndSaveActorNames(	"http://en.wikipedia.org/wiki/Academy_Award_for_Best_Actor",
									"BestActor.txt",
									290,
									1106);
			readAndSaveActorNames(	"http://en.wikipedia.org/wiki/Academy_Award_for_Best_Actress",
									"BestActress.txt",
									250,
									1075);
			readAndSaveActorNames(	"http://en.wikipedia.org/wiki/Academy_Award_for_Best_Supporting_Actor",
									"BestSupportingActor.txt",
									230,
									905);
			readAndSaveActorNames(	"http://en.wikipedia.org/wiki/Academy_Award_for_Best_Supporting_Actress",
									"BestSupportingActress.txt",
									220,
									970);
*/        
			loadActors("BestActor.txt");
			loadActors("BestActress.txt");
			loadActors("BestSupportingActor.txt");
			loadActors("BestSupportingActress.txt");
			saveActors();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Incorrect URL");
		}
	}

	public static void readAndSaveActorNames(	String inputURL,
												String saveFileName,
												int linesToSkipAtFront,
												int lineToEndReading) throws Exception
	{
		URL url;
		URLConnection urlC;
		InputStream inStream;
		InputStreamReader inStreamReader;
		BufferedReader reader;
		url = new URL(inputURL);
		urlC = url.openConnection();
		inStream = urlC.getInputStream();
		inStreamReader = new InputStreamReader(inStream);
		reader = new BufferedReader(inStreamReader);
		PrintWriter pw = new PrintWriter(saveFileName);
		
		String inputLine;
		
		// FIRST SKIP THE IRRELEVANT LINES
		int lineCounter = 0;
		while (lineCounter < linesToSkipAtFront)
		{
			inputLine = reader.readLine();
			lineCounter++;
		}
		
		inputLine = reader.readLine();
		if (inputLine != null)
		{
			inputLine = inputLine.trim();
		}
		else
			return;

		System.out.println("Actors found in " + inputURL + ":");
		int actorCounter = 0;
		boolean winner = false;
		while (lineCounter < lineToEndReading)
		{
			lineCounter++;
			if (inputLine.startsWith("<li><b>"))
			{
				String text = extractAfterNextTitle(inputLine);
				if (text != inputLine)
				{
					String title = extractTitleFromFront(text);
					char testChar = title.charAt(0);
					if ((testChar >= 48) && (testChar <= 57))
					{
						text = extractAfterNextTitle(text);
						title = extractTitleFromFront(text);
					}
					pw.println(title);
				}
			}
			
			inputLine = reader.readLine();
			if (inputLine != null)
			{
				inputLine = inputLine.trim();
			}
		}
		if (pw != null)
			pw.close();
	}

	public static String extractTitleFromFront(String line)
	{
		int searchIndex = 0;
		searchIndex = line.indexOf("\"");
		if (searchIndex < 0)
			return line;
		else
			return line.substring(0, searchIndex);		
	}
			
	public static String extractAfterNextTitle(String line)
	{
		int searchIndex = 0;
		searchIndex = line.indexOf("title=\"");
		if (searchIndex < 0)
			return line;
		else
			return line.substring(searchIndex + 7);
	}
	
	public static void saveActors()
	{
		try
		{
			Collections.sort(actors);
			Iterator<String> it = actors.iterator();
			writer = new PrintWriter("AllActors.txt");
			int counter = 1;
			while(it.hasNext())
			{
				String nextActor = it.next();
				if ( (!nextActor.contains("film)"))
						&&
					(!nextActor.contains("movie)"))
						&&
					(!nextActor.contains("Edit section:"))
						&&
					(!nextActor.contains("List of Best")))
				{
					nextActor = removeTrailingText(nextActor, "(");
					System.out.println(counter + ". " + nextActor);
					counter++;					
					writer.println(nextActor);
				}
			}
			if (writer != null)
				writer.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static String removeTrailingText(String text, String textToRemove)
	{
		int index = text.indexOf(textToRemove);
		if (index > 0)
			text = text.substring(0, index);
		text = text.trim();
		return text;
	}
	
	public static void loadActors(String fileToRead)
	{
		try
		{
			FileInputStream fis = new FileInputStream(fileToRead);
			InputStreamReader isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);
			String line = reader.readLine();
			while (line != null)
			{
				if (!actors.contains(line))
					actors.add(line);
				line = reader.readLine();
			}			
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}	
}