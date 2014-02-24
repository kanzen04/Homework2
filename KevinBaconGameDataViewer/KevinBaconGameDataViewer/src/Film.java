import java.util.Vector;

public class Film implements Comparable
{
	public String id;
	public String title;
	public int year;
	
	// ACTOR IDs OF ACTORS WHO APPEARED IN THIS FILM
	public Vector<String> actorIDs = new Vector<String>();
	
	public Film(String initId,
				String initTitle,
				int initYear)
	{
		id = initId;
		title = initTitle;
		year = initYear;
	}
	
	public int compareTo(Object otherObject)
	{
		Film otherFilm = (Film)otherObject;
		return id.compareTo(otherFilm.id);
	}
	
	public boolean equals(Object obj)
	{
		Film otherFilm = (Film)obj;
		return otherFilm.id.equals(id);
	}

	public String toString()
	{
		return title + " (" + year + ")";
	}	
}
