import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Vector;

public class ActorIDRetriever
{

    public static Vector<String> actors = new Vector<String>();
    public static Vector<String> actorIDs = new Vector<String>();
    
    public static void loadAllActors()
    {
        BufferedReader reader;
        try
        {
            FileInputStream fis = new FileInputStream("AllActors.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            reader = new BufferedReader(isr);
            String inputLine = reader.readLine();
            char charToTest = inputLine.charAt(0);
            while (charToTest != 'K')
            {
                inputLine = inputLine.substring(1);
                charToTest = inputLine.charAt(0);
            }
            while (inputLine != null)
            {
                actors.add(inputLine);
                inputLine = reader.readLine();
            }
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    
    public static void loadAllActorIDs()
    {
        BufferedReader reader;
        try
        {
            FileInputStream fis = new FileInputStream("AllActorsIDs.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            reader = new BufferedReader(isr);
            String inputLine = reader.readLine();
            while (inputLine != null)
            {
                actorIDs.add(inputLine);
                inputLine = reader.readLine();
            }
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }    
    
    public static void main(String[] args)    
    {
        loadAllActors();
        extractAllActorIDs();
        saveAllActorIDs();
    }
    
    public static void saveAllActorIDs()
    {
        PrintWriter writer;
        try
        {
            writer = new PrintWriter("AllActorsIDs.txt");
            Iterator<String> it = actorIDs.iterator();
            while (it.hasNext())
            {
                String id = it.next();
                System.out.println(id);
                writer.println(id);
            }
            writer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void extractAllActorIDs()
    {
        Iterator<String> it = actors.iterator();
        while (it.hasNext())
        {
            String actor = it.next();
            String actorID = getActorID(actor);
            if (actorID.contains("/"))
            {
                actorID = actorID.substring(0, actorID.indexOf("/"));
            }
            System.out.println(actor + ": " + actorID);
            actorIDs.add(actorID);
        }
    }
    
    public static String getActorID(String actor)
    {
        try
        {
            actor = actor.replaceAll(" ", "%20").trim();
            String actorQueryURL = "http://www.imdb.com/find?s=all&q=";
            actorQueryURL += actor + "&x=0&y=0";
            System.out.println(actorQueryURL);
            URL url;
            URLConnection urlC;
            InputStream inStream;
            InputStreamReader inStreamReader;
            BufferedReader reader;
            url = new URL(actorQueryURL);
            urlC = url.openConnection();
            inStream = urlC.getInputStream();
            inStreamReader = new InputStreamReader(inStream);
            reader = new BufferedReader(inStreamReader);
            
            String inputLine;
            inputLine = reader.readLine();
            while (inputLine != null)
            {
                int indexOfID = inputLine.indexOf("<a href=\"/name/nm");
                //System.out.println(inputLine);
                if (indexOfID >= 0)
                {
                    inputLine = inputLine.substring(indexOfID + 15);
                    int indexOfEndQuote = inputLine.indexOf("\"");
                    String id = inputLine.substring(0, indexOfEndQuote);
                    reader.close();
                    return id;
                }
                inputLine = reader.readLine();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Problems reading data for " + actor);
        }
        return "";
    }
}
