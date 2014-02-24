import java.util.Iterator;
import java.util.Vector;

public class Actor implements Comparable
{
	public String id;
	public String firstName;
	public String lastName;
	
	// FILM IDs OF FILMS THIS ACTOR HAS BEEN IN
	public Vector<String> filmIDs = new Vector<String>();
	
	public Actor(	String initId,
					String initFirstName,
					String initLastName)
	{
		id = initId;
		firstName = initFirstName;
		lastName = initLastName;
	}
	
	public int compareTo(Object otherObject)
	{
		Actor otherActor = (Actor)otherObject;
		return id.compareTo(otherActor.id);
	}

	public boolean equals(Object obj)
	{
		Actor otherActor = (Actor)obj;
		return otherActor.id.equals(id);
	}
	
	public String toString()
	{
		return firstName + " " + lastName;
	}
}