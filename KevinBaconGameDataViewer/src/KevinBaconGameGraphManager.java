import java.util.*;
import java.io.*;

/**
 * This class manages the graph used to map Films and Actors in the game graph.
 */
public class KevinBaconGameGraphManager
{

    // THIS WILL STORE ALL OF OUR FILM DATA
    public TreeMap<String, Film> films = new TreeMap();

    // THIS WILL STORE ALL OF OUR ACTOR DATA
    public TreeMap<String, Actor> actors = new TreeMap();

    /*
     * Default Constructor - It does nothing
     */
    public KevinBaconGameGraphManager()
    {
    }

    /*
     * loadActors - This method loads all actors
     * from the actorsFileName into the actors
     * instance variable.
     */
    public void loadActors(String actorsFileName) throws IOException
    {
        FileInputStream fis = new FileInputStream(actorsFileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(isr);
        String inputLine = reader.readLine();
        StringTokenizer st;
        while (inputLine != null)
        {
            st = new StringTokenizer(inputLine, "|");
            String actorID = st.nextToken();
            String lastName = st.nextToken();
            String firstName = st.nextToken();
            System.out.println("Loading Data for " + firstName + " " + lastName);
            Vector<String> filmsForActor = new Vector<String>();
            while (st.hasMoreTokens())
            {
                String filmToAdd = st.nextToken();
                filmsForActor.add(filmToAdd);
            }
            Collections.sort(filmsForActor);
            Actor actorToAdd = new Actor(
                    actorID,
                    firstName,
                    lastName);
            actorToAdd.filmIDs = filmsForActor;
            actors.put(actorID, actorToAdd);
            inputLine = reader.readLine();
        }
    }

    /*
     * loadFilms - This method should load all the
     * films described inside the filmsFileName file
     * into the films instance variable.
     */
    public void loadFilms(String filmsFileName) throws IOException
    {
        FileInputStream fis = new FileInputStream(filmsFileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader reader = new BufferedReader(isr);
        String inputLine = reader.readLine();
        StringTokenizer st;
        int counter = 0;
        while (inputLine != null)
        {
            st = new StringTokenizer(inputLine, "|");
            String filmID = st.nextToken();
            String title = st.nextToken();
            System.out.println("Loading Data for " + title);
            String yearText = st.nextToken();
            int year = Integer.parseInt(yearText);
            Vector<String> actorsForFilm = new Vector<String>();
            while (st.hasMoreTokens())
            {
                String actorToAdd = st.nextToken();
                actorsForFilm.add(actorToAdd);
            }
            Collections.sort(actorsForFilm);
            Film filmToAdd = new Film(
                    filmID,
                    title,
                    year);
            filmToAdd.actorIDs = actorsForFilm;

            films.put(filmID, filmToAdd);
            System.out.println(counter + ": " + filmToAdd);
            counter++;
            inputLine = reader.readLine();
        }
    }

    /*
     * getAllNeighbors - This method should examine 
     * the Actor corresponding to the provided 
     * actorID and build return a Vector with a 
     * constructed Connection object for each actor 
     * connected by one degree to the actor referenced 
     * in the argument.
     */
    public Vector<Connection> getAllNeighbors(String actorID)
    {
        Vector<Connection> connections = new Vector<Connection>();
        Actor actor = actors.get(actorID);
        for (int i = 0; i < actor.filmIDs.size(); i++)
        {
            String filmID = actor.filmIDs.get(i);
            Film film = films.get(filmID);
            for (int j = 0; j < film.actorIDs.size(); j++)
            {
                String actorID2 = film.actorIDs.get(j);
                if (!actorID2.equals(actorID))
                {
                    Connection connection;
                    connection = new Connection(filmID, actorID, actorID2);
                    connections.add(connection);
                }
            }
        }
        return connections;
    }

    private Vector<Connection> generatePath(Vector<String> actorIDs,
            Vector<String> filmIDs)
    {
        Vector<Connection> path = new Vector<Connection>();
        for (int i = 0; i < filmIDs.size(); i++)
        {
            Connection c = new Connection(filmIDs.get(i),
                    actorIDs.get(i),
                    actorIDs.get(i + 1));
            path.add(c);
        }
        return path;
    }

    /*
     * findPathToKevinBacon - This method should use
     * a Greedy-type algorithm to find a path from
     * the actor argument to Kevin Bacon. Note that it
     * need not be the shortest path, but it cannot
     * repeat any actors or films in the path.
     */
    public Vector<Connection> findPathToKevinBacon(Actor actor)
    {
        Vector<String> actorIDsInPath = new Vector<String>();
        Vector<String> filmIDsInPath = new Vector<String>();
        Actor kevinBacon = actors.get("nm0000102");
        TreeMap<String, String> closedActorIDs;
        closedActorIDs = new TreeMap<String, String>();
        TreeMap<String, String> closedFilmIDs;
        closedFilmIDs = new TreeMap<String, String>();

        actorIDsInPath.add(actor.id);
        closedActorIDs.put(actor.id, actor.id);

		// WHILE THE PATH IS NOT EMPTY
        // WHICH MEANS THERE MIGHT STILL
        // BE A PATH
        while (!actorIDsInPath.isEmpty())
        {
            String lastActorID = actorIDsInPath.get(actorIDsInPath.size() - 1);
            Actor lastActor = actors.get(lastActorID);

            // GET ALL FILMS FOR lastActor
            Vector<Connection> neighbors = getAllNeighbors(lastActorID);

			// IF KEVIN BACON HAS BEEN IN ANY
            // OF THOSE FILMS, ADD THE FILM TO 
            // THE PATH AND WE'RE DONE
            Iterator<Connection> it = neighbors.iterator();
            while (it.hasNext())
            {
                Connection c = it.next();
                if (c.hasActor(kevinBacon.id))
                {
                    Film filmForPath = films.get(c.filmID);
                    filmIDsInPath.add(c.filmID);
                    actorIDsInPath.add(kevinBacon.id);
                    Vector<Connection> path = generatePath(actorIDsInPath, filmIDsInPath);
                    return path;
                }
            }

			// REMOVE ALL CONNECTIONS FROM
            // THE CLOSED LIST OF ACTORS
            // AND THE CLOSED LIST OF FILMS
            for (int i = 0; i < neighbors.size(); i++)
            {
                Connection c = neighbors.get(i);
                String filmToTest = c.filmID;
                if (closedActorIDs.containsKey(c.actor2ID))
                {
                    neighbors.remove(i);
                    i--;
                } else
                {
                    if (closedFilmIDs.containsKey(filmToTest))
                    {
                        neighbors.remove(i);
                        i--;
                    }
                }
            }

			// IF NO MORE NEIGHBORS, THEN WE HAVE
            // A DEAD END, WHICH MEANS WE HAVE TO GO
            // ANOTHER WAY. THAT MEANS WE HAVE TO
            // REMOVE THE LAST NODE FROM THE PATH, ALSO
            // ADD IT TO THE CLOSED LIST
            if (neighbors.size() == 0)
            {
                actorIDsInPath.remove(actorIDsInPath.size() - 1);
                filmIDsInPath.remove(filmIDsInPath.size() - 1);
            } else
            {
                Connection c = neighbors.firstElement();
                actorIDsInPath.add(c.actor2ID);
                closedActorIDs.put(c.actor2ID, c.actor2ID);
                filmIDsInPath.add(c.filmID);
                closedFilmIDs.put(c.filmID, c.filmID);
            }
        }
        return new Vector<Connection>();
    }

    /*
     * findShortestPathToKevinBacon - This method does
     * the same thing as the other path finding
     * algorithm, except this one finds the optimal
     * path. The easiest way to do this is with
     * a breadth first search algorithm.
     */
    public Vector<Connection> findShortestPathToKevinBacon(Actor actor)
    {
        // THIS IS OUR DESTINATION NODE
        Actor kevinBacon = actors.get("nm0000102");

		// WE'LL MAINTAIN A SHORTEST PATH FROM THE
        // STARTING ACTOR TO EACH ACTOR WE ENCOUNTER
        TreeMap<String, Vector<Connection>> shortestPaths;
        shortestPaths = new TreeMap<String, Vector<Connection>>();

		// THIS WILL STORE THE PATH WE ARE CURRENTLY
        // BUILDING UPON
        Vector<Connection> currentPath;

		// WE ARE USING A BREADTH FIRST SEARCH, AND
        // WE'LL ONLY CHECK EACH Actor AND Film ONCE
        // WE ARE USING 2 DATA STRUCTURES FOR EACH
        // BECAUSE WE WILL USE ONE AS A LIST OF 
        // ITEMS TO CHECK IN ORDER, AND ANOTHER
        // FOR FAST SEARCHING
        Vector<Actor> actorsVisited = new Vector<Actor>();
        TreeMap<String, Actor> actorsVisitedFast = new TreeMap<String, Actor>();
        Vector<Film> filmsVisited = new Vector<Film>();
        TreeMap<String, Film> filmsVisitedFast = new TreeMap<String, Film>();

        // INDEX OF Actors AND Films TO CHECK
        int actorIndex = 0;
        int filmIndex = 0;

		// THE SHORTEST PATH FROM THE START ACTOR
        // TO THE START ACTOR IS NOTHING, SO WE'll
        // START OUT WITH AN EMPTY Vector
        actorsVisited.add(actor);
        actorsVisitedFast.put(actor.id, actor);
        shortestPaths.put(actor.id, new Vector<Connection>());

		// GO THROUGH ALL THE ACTORS WE HAVE REACHED
        // NEVER RE-VISITING AN ACTOR
        while (actorIndex < actorsVisited.size())
        {
			// FIRST GET ALL THE MOVIES FOR THE
            // ACTOR AT THE actorIndex
            Actor currentActor = actorsVisited.get(actorIndex);

			// MAKE THE SHORTEST PATH FOR THE CURRENT
            // ACTOR THE CURRENT PATH, SINCE WE WILL
            // BUILD ON IT
            currentPath = shortestPaths.get(currentActor.id);

            Iterator<String> itFilmIDs = currentActor.filmIDs.iterator();
            while (itFilmIDs.hasNext())
            {
                String filmID = itFilmIDs.next();
                Film film = films.get(filmID);
                if (!filmsVisitedFast.containsKey(filmID))
                {
                    filmsVisited.add(film);
                    filmsVisitedFast.put(film.id, film);
                }
            }

            while (filmIndex < filmsVisited.size())
            {
				// NOW GO THROUGH THE FILMS AND GET
                // ALL THE ACTORS WHO WERE IN THOSE
                // FILMS, DO NOT GET ACTORS ALREADY
                // VISITED
                Film currentFilm = filmsVisited.get(filmIndex);
                Iterator<String> itActorIDs = currentFilm.actorIDs.iterator();
                while (itActorIDs.hasNext())
                {
                    String actorID = itActorIDs.next();
                    Actor actorToTest = actors.get(actorID);
                    if (!actorsVisitedFast.containsKey(actorID))
                    {
                        actorsVisited.add(actorToTest);
                        actorsVisitedFast.put(actorID, actorToTest);
                        Vector<Connection> actorPath;
                        actorPath = (Vector<Connection>) currentPath.clone();
                        Connection c = new Connection(currentFilm.id,
                                currentActor.id,
                                actorToTest.id);
                        actorPath.add(c);
                        shortestPaths.put(actorID, actorPath);

                        // IF THIS IS KEVIN BACON WE'RE DONE
                        if (actorID.equals(kevinBacon.id))
                        {
                            return actorPath;
                        }
                    }
                }
                filmIndex++;
            }
            actorIndex++;
        }
        return new Vector<Connection>();
    }

    /*
     * filmsIterator - This method returns an
     * Iterator that can be used to access all
     * Films in this graph.
     */
    public Iterator<Map.Entry<String, Film>> filmsIterator()
    {
        return films.entrySet().iterator();
    }

    /*
     * actorsIterator - This method returns an
     * Iterator that can be used to access all
     * Actors in this graph.
     */
    public Iterator<Map.Entry<String, Actor>> actorsIterator()
    {
        return actors.entrySet().iterator();
    }
}
