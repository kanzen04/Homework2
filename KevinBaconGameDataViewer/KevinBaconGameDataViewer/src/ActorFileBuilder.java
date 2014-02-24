import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;

public class ActorFileBuilder
{

    public static TreeMap<String, Actor> actors = new TreeMap<String, Actor>();
    public static TreeMap<String, Film> films = new TreeMap<String, Film>();
    public static Vector<String> filmsToExclude = new Vector<String>();

    public static void main(String[] args)
    {
        loadFilmsToExclude();
        ActorIDRetriever.loadAllActors();
        ActorIDRetriever.loadAllActorIDs();
        buildActors();
        retrieveAllActorsFilms();
        loadActorIDsIntoFilms();
        saveActorsFile();
        saveFilmsFile();
    }

    public static void loadFilmsToExclude()
    {
        try
        {
            FileInputStream fisEx = new FileInputStream("FilmsToExclude.txt");
            InputStreamReader isrEx = new InputStreamReader(fisEx);
            BufferedReader brEx = new BufferedReader(isrEx);
            String line = brEx.readLine();
            while (line != null)
            {
                filmsToExclude.add(line);
                line = brEx.readLine();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void retrieveAllActorsFilms()
    {
        Iterator it = actors.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, Actor> mapEntry = (Map.Entry<String, Actor>) it.next();
            String id = mapEntry.getKey();
            Actor actor = mapEntry.getValue();
            System.out.println("Retrieving movies for " + actor.firstName + " " + actor.lastName);
            retrieveAndLoadActorFilmsData(actor);
            Collections.sort(actor.filmIDs);
        }
    }

    public static void retrieveAndLoadActorFilmsData(Actor actor)
    {
        try
        {
            String actorFile = "ActorDataFiles/";
            actorFile += actor.id;

            FileInputStream fis = new FileInputStream(actorFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            String inputLine;
            inputLine = reader.readLine();
            String advancePoint = "<div class=\"filmo-category-section";
            while (!inputLine.contains(advancePoint))
            {
                inputLine = reader.readLine();
            }
            while (inputLine != null)
            {
                if (inputLine.contains("<div id=\"filmo-head"))
                    return;
                String yearLine = "<span class=\"year_column\">";
                if (inputLine.contains(yearLine))
                {
                    inputLine = reader.readLine();
                    String yearText = inputLine.substring(6).trim();
                    if (yearText.length() > 4)
                        yearText = yearText.substring(0, 4);
                    Integer year = Integer.parseInt(yearText);
                    inputLine = reader.readLine();
                    inputLine = reader.readLine();
                    int searchIndex = inputLine.indexOf("/title/tt");
                    // while (searchIndex >= 0)
                    // {
                    // MOVE UP TO THE FILM

                    inputLine = inputLine.substring(searchIndex + 7);

                    // EXTRACT THE ID
                    int endIndex = inputLine.indexOf("/");
                    int qIndex = inputLine.indexOf("?");
                    if ((endIndex < 0) || (qIndex < endIndex))
                        endIndex = qIndex;
                    String filmID = inputLine.substring(0, 9);
                    if (filmID.charAt(filmID.length()-1) == '/')
                        filmID = filmID.substring(0, filmID.length()-1);

                    // EXTRACT THE TITLE
                    int ltIndex = inputLine.indexOf(">");
                    inputLine = inputLine.substring(ltIndex + 1);
                    int gtIndex = inputLine.indexOf("<");
                    String title = inputLine;
                    if (gtIndex < 0)
                    {
                        title = inputLine.substring(0);
                        inputLine = reader.readLine();
                    } else
                    {
                        title = inputLine.substring(0, gtIndex);
                    }
                    title = title.trim();

                    boolean legalTitle = true;
                    if (title.startsWith("Episode #"))
                    {
                        legalTitle = false;
                    }
                    if (title.startsWith("Episode dated"))
                    {
                        legalTitle = false;
                    }
                    if (title.startsWith("The One "))
                    {
                        legalTitle = false;
                    }
                    if (title.startsWith("\""))
                    {
                        legalTitle = false;
                    }
                    if (title.startsWith("\'"))
                    {
                        legalTitle = false;
                    }

                    Iterator<String> itEx = filmsToExclude.iterator();
                    while (itEx.hasNext())
                    {
                        String ex = itEx.next();
                        if (title.contains(ex))
                        {
                            legalTitle = false;
                        }
                    }
    
                    // EXTRACT THE YEAR
                    // int openParenIndex = inputLine.indexOf("(");
                    // inputLine = inputLine.substring(openParenIndex+1);
                    // int closeParenIndex = inputLine.indexOf(")");
                    // if ((openParenIndex >= 0)
                    // &&
                    // (closeParenIndex >= 0)
                    // &&
                    // legalTitle)
                    // {
                    // String yearText = inputLine.substring(0, closeParenIndex);
                    // try
                    // {
                    // int year = Integer.parseInt(yearText);
                    if (!actor.filmIDs.contains(filmID))
                    {
                        actor.filmIDs.add(filmID);
                    }
                    Film film = new Film(filmID, title, year);
                    if (!films.containsKey(filmID))
                    {
                        System.out.println("\tAdding: " + film);
                        films.put(filmID, film);
                    }
                }
                // }catch (NumberFormatException nfe)
                // {
                // nfe.printStackTrace();
                // }
                // }
                // searchIndex = inputLine.indexOf("/title/tt");
                // }
                inputLine = reader.readLine();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Problems reading data for " + actor);
        }
    }

public static void buildActors()
	{
		Iterator<String> nameIt = ActorIDRetriever.actors.iterator();
		Iterator<String> idIt = ActorIDRetriever.actorIDs.iterator();
		
		while (nameIt.hasNext())
		{
			String name = nameIt.next();
			String id = idIt.next();
System.out.println(name);			
			StringTokenizer st = new StringTokenizer(name, " ");
			String firstName = st.nextToken();
			String lastName = "";
                        
                        if (st.hasMoreTokens())
                            lastName = st.nextToken();
			Actor actorToAdd = new Actor(
								id,
								firstName,
								lastName);
			actors.put(id, actorToAdd);
		}
	}

	public static void loadActorIDsIntoFilms()
	{
		Iterator<Map.Entry<String, Actor>> it = actors.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, Actor> entry = it.next();
			Actor actor = entry.getValue();
			Iterator<String> filmIDIt = actor.filmIDs.iterator();
			while (filmIDIt.hasNext())
			{
				String filmID = filmIDIt.next();
				Film filmToAddTo = films.get(filmID);
				if (!filmToAddTo.actorIDs.contains(actor.id))
					filmToAddTo.actorIDs.add(actor.id);
			}
		}
	}
	
	public static void saveActorsFile()
	{
		try
		{
			PrintWriter writer = new PrintWriter("AllActorsData.txt");
			Iterator<Map.Entry<String, Actor>> it = actors.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String,Actor> entry = it.next();
				Actor actor = entry.getValue();
				String text = 
				actor.id + "|"
				+	actor.lastName + "|"
				+	actor.firstName;
				Iterator<String> it2 = actor.filmIDs.iterator();
				while(it2.hasNext())
				{
					String film = it2.next();
					text += "|" + film;
				}
				writer.println(text);
			}
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveFilmsFile()
	{
		try
		{
			PrintWriter writer = new PrintWriter("AllFilmsData.txt");
			Iterator<Map.Entry<String, Film>> it = films.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<String, Film> entry = it.next();
				Film film = entry.getValue();
				String text = film.id + "|"
				+	film.title + "|"
				+	film.year;
				Iterator<String> it2 = film.actorIDs.iterator();
				while(it2.hasNext())
				{
					String actor = it2.next();
					text += "|" + actor;
				}
				writer.println(text);
			}
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
}
