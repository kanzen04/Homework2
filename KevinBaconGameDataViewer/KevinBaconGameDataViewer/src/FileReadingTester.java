import java.io.*;
import java.net.*;

public class FileReadingTester 
{
	public static void main(String[] args) 
	{
		try
		{
			String testURL = "http://www.imdb.com/title/tt1315213/";
			URL url;
			URLConnection urlC;
			InputStream inStream;
			InputStreamReader inStreamReader;
			BufferedReader reader;
			url = new URL(testURL);
			urlC = url.openConnection();
			inStream = urlC.getInputStream();
			inStreamReader = new InputStreamReader(inStream);
			reader = new BufferedReader(inStreamReader);
			String text = reader.readLine();
			while(text != null)
			{
				System.out.println(text);
				text = reader.readLine();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
