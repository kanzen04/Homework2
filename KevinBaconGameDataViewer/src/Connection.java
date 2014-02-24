import java.util.*;

public class Connection implements Comparable, Comparator
{
	public String filmID;
	public String actor1ID;
	public String actor2ID;
	
	public Connection(	String initFilmID,
						String initActor1ID,
						String initActor2ID)
	{
		filmID = initFilmID;
		actor1ID = initActor1ID;
		actor2ID = initActor2ID;
	}
	
	public boolean hasActor(String actorID)
	{
		if (actor1ID.equals(actorID))
			return true;
		else if (actor2ID.equals(actorID))
			return true;
		else
			return false;
	}
	
	public int compareTo(Object obj)
	{
		if (equals(obj))
			return 0;
		else
		{
			Connection otherConnection = (Connection)obj;
			return (filmID + actor1ID + actor2ID)
			.compareTo(otherConnection.filmID 
				+ otherConnection.actor1ID
				+ otherConnection.actor2ID);
		}
	}
	
	public boolean equals(Object obj)
	{
		Connection otherConnection = (Connection)obj;
		boolean sameActors;
		sameActors = ((actor1ID.equals(otherConnection.actor1ID)
						&&
					   actor2ID.equals(otherConnection.actor2ID))
					  ||
					  (actor1ID.equals(otherConnection.actor2ID)
						&&
					   actor2ID.equals(otherConnection.actor1ID)));
		return sameActors && (filmID.equals(otherConnection.filmID));
	}

	public int compare(Object arg0, Object arg1) 
	{
		Connection conn0 = (Connection) arg0;
		Connection conn1 = (Connection) arg1;
		return conn0.compareTo(conn1);
	}
}