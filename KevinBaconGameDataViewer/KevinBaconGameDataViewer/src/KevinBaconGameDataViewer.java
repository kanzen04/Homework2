import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * This program lets one view the data we'll use in the 
 * Kevin Bacon game.
 */
public class KevinBaconGameDataViewer extends JFrame implements ItemListener
{
	// THIS IS THE GRAPH FOR MANAGING THE GAME DATA
	KevinBaconGameGraphManager graph = new KevinBaconGameGraphManager();

	// WE'LL HAVE 4 SEPARATE TABS
	JTabbedPane tabbedPane = new JTabbedPane();
	
	// TAB FOR PLAYING THE GAME
	JPanel gamePanel = new JPanel();
	JPanel northGamePanel = new JPanel();
	JLabel gameInputLabel = new JLabel("Select Academy Award Winning Actor: ");
	JComboBox gameComboBox;
	JLabel dfsLabel = new JLabel("Use Depth First Search");
	JRadioButton dfsRadioButton = new JRadioButton();
	JLabel bfsLabel = new JLabel("Use Breadth First Search");
	JRadioButton bfsRadioButton = new JRadioButton();
	JEditorPane gamePane = new JEditorPane();
	JScrollPane jsp = new JScrollPane(gamePane);

	// TAB FOR VIEWING AN ACTOR'S CONNECTIONS (NEIGHBORS)
	JPanel neighborsPanel = new JPanel();
	JPanel northNeighborsPanel = new JPanel();
	JLabel neighborsLabel = new JLabel("Select an Actor: ");
	JComboBox neighborsComboBox;
	JEditorPane neighborsPane = new JEditorPane();
	JScrollPane neighborsJSP = new JScrollPane(neighborsPane);

	// TAB FOR VIEWING ACTOR INFO
	JPanel actorsPanel = new JPanel();
	JPanel northActorsPanel = new JPanel();
	JLabel actorsLabel = new JLabel("Select an Actor: "); 
	JComboBox actorsComboBox;
	JEditorPane actorsPane = new JEditorPane();
	JScrollPane actorsJSP = new JScrollPane(actorsPane);

	// TAB FOR VIEWING FILM INFO
	JPanel filmsPanel = new JPanel();
	JPanel northFilmsPanel = new JPanel();
	JLabel filmsLabel = new JLabel("Select a Film: ");
	JComboBox filmsComboBox;
	JEditorPane filmsPane = new JEditorPane();
	JScrollPane filmsJSP = new JScrollPane(filmsPane);

	/*
	 * Default Constructor - This method initializes
	 * the application.
	 */
	public KevinBaconGameDataViewer()
	{
		super("Six Degrees of Kevin Bacon - Academy Awards Edition");
		loadActorsAndFilmsData();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(MAXIMIZED_BOTH);
		layoutGUI();            
	}
	
	/*
	 * loadActorsAndFilmsData - This method triggers
	 * the loading of all data from text files into
	 * the graph.
	 */
	public void loadActorsAndFilmsData()
	{
		try
		{
			graph.loadActors("AllActorsData.txt");
			graph.loadFilms("AllFilmsData.txt");
			loadComboBoxes();
			computeAllPaths();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * computeAllPaths - This method tests
	 * all paths to see how long they are, to
	 * make sure none exceed 6 degrees.
	 */
	public void computeAllPaths()
	{
		int[] histogram = new int[7];
		for (int i = 0; i < histogram.length; i++)
			histogram[i] = 0;

		for (int j = 0; j < gameComboBox.getItemCount(); j++)
		{
			Actor actor = (Actor)gameComboBox.getItemAt(j);
			System.out.print("Pathing for " + actor);
			Vector<Connection> path = graph.findShortestPathToKevinBacon(actor);
			int size = path.size();
			System.out.println(" - " + size + " degrees");
			histogram[size]++;
		}
		System.out.println("Shortest Path Histogram:");
		for (int i = 0; i < histogram.length; i++)
			System.out.println(i + " Degrees: " + histogram[i]);
	}
	
	/*
	 * loadComboBoxes - This method loads all appropriate
	 * data, already loaded from text files, into their
	 * respective text files in order to provide the
	 * appropriate controls to the user.
	 */
	public void loadComboBoxes()
	{
		// FIRST GET ALL THE ACTORS
		Vector<Actor> actors = new Vector<Actor>();
		Iterator<Map.Entry<String, Actor>> it = graph.actorsIterator();
		while (it.hasNext())
		{
			Map.Entry<String, Actor> entry = it.next();
			Actor actor = entry.getValue();
			actors.add(actor);
		}
		// NOW SORT THEM USING A DIFFERENT CRITERIA
		// NOTE THAT THIS WILL NOT CHANGE HOW THEY 
		// ARE STORED IN THE GRAPH, WE ARE JUST SORTING
		// COPIES OF POINTERS TO THE OBJECTS
		NameSorter nameSorter = new NameSorter();
		Collections.sort(actors, nameSorter);
		
		// AND NOW LOAD THE BOXES
		gameComboBox = new JComboBox(actors);
		neighborsComboBox = new JComboBox(actors);
		actorsComboBox = new JComboBox(actors);
		
		// DO THE SAME THING, THIS TIME FOR FILMS
		Vector<Film> films = new Vector<Film>();
		Iterator<Map.Entry<String, Film>> it2 = graph.filmsIterator();
		while (it2.hasNext())
		{
			Map.Entry<String, Film> entry = it2.next();
			Film film = entry.getValue();
			films.add(film);
		}
		TitleSorter titleSorter = new TitleSorter();
		Collections.sort(films, titleSorter);
		
		// ONLY ONE COMBO BOX HAS THE FILMS
		filmsComboBox = new JComboBox(films);
	}

	/*
	 * NameSorter - This class is used for sorting
	 * actors by name in our combo boxes. We have to
	 * provide a custom sorting criteria class because
	 * Actors are normally sorted by ID.
	 */
	class NameSorter implements Comparator
	{
		public int compare(Object arg0, Object arg1) 
		{
			Actor a0 = (Actor)arg0;
			Actor a1 = (Actor)arg1;
			return (a0.firstName + a0.lastName)
			.compareTo(a1.firstName + a1.lastName);
		}
	}
	
	/*
	 * TitleSorter - This class is used for sorting
	 * films by title in our combo box. We have to
	 * provide a custom sorting criteria class because
	 * Films are normally sorted by ID.
	 */
	class TitleSorter implements Comparator
	{
		public int compare(Object arg0, Object arg1)
		{
			Film f0 = (Film)arg0;
			Film f1 = (Film)arg1;
			return (f0.title.compareTo(f1.title));
		}
	}
	
	/*
	 * layoutGUI - This method places all the GUI
	 * components in their appropriate places.
	 */
	public void layoutGUI()
	{
		Font f = new Font("Serif", Font.BOLD, 20);
		// GAME TAB
		gamePane.setContentType("text/html");
		gamePane.setFont(f);
		gamePane.setEditable(false);
		gamePanel.setLayout(new BorderLayout());
		gamePanel.add(northGamePanel, BorderLayout.NORTH);
		gamePanel.add(jsp, BorderLayout.CENTER);
		northGamePanel.add(gameInputLabel);
		northGamePanel.add(gameComboBox);
		northGamePanel.add(dfsLabel);
		northGamePanel.add(dfsRadioButton);
		northGamePanel.add(bfsLabel);
		northGamePanel.add(bfsRadioButton);
		ButtonGroup bg = new ButtonGroup();
		bg.add(dfsRadioButton);
		bg.add(bfsRadioButton);
		dfsRadioButton.addItemListener(this);
		bfsRadioButton.addItemListener(this);
		dfsRadioButton.setSelected(true);
		gameComboBox.addItemListener(this);
		tabbedPane.add("Kevin Bacon Game", gamePanel);

		// NEIGHBORS TAB
		neighborsPane.setContentType("text/html");
		neighborsPane.setFont(f);
		neighborsPane.setEditable(false);
		neighborsPanel.setLayout(new BorderLayout());
		neighborsPanel.add(northNeighborsPanel, BorderLayout.NORTH);
		neighborsPanel.add(neighborsJSP, BorderLayout.CENTER);
		northNeighborsPanel.add(neighborsLabel);
		northNeighborsPanel.add(neighborsComboBox);
		neighborsComboBox.addItemListener(this);
		tabbedPane.add("View Connected Neighbors", neighborsPanel);

		// ACTORS TAB
		actorsPane.setContentType("text/html");
		actorsPane.setFont(f);
		actorsPane.setEditable(false);
		actorsPanel.setLayout(new BorderLayout());
		actorsPanel.add(northActorsPanel, BorderLayout.NORTH);
		actorsPanel.add(actorsJSP, BorderLayout.CENTER);
		northActorsPanel.add(actorsLabel);
		northActorsPanel.add(actorsComboBox);
		actorsComboBox.addItemListener(this);
		tabbedPane.add("View Loaded Actors", actorsPanel);

		// FILMS TAB
		filmsPane.setContentType("text/html");
		filmsPane.setFont(f);
		filmsPane.setEditable(false);
		filmsPanel.setLayout(new BorderLayout());
		filmsPanel.add(northFilmsPanel, BorderLayout.NORTH);
		filmsPanel.add(filmsJSP, BorderLayout.CENTER);
		northFilmsPanel.add(filmsLabel);
		northFilmsPanel.add(filmsComboBox);
		filmsComboBox.addItemListener(this);
		tabbedPane.add("View Loaded Films", filmsPanel);

		// LOAD THE TABBED PANE
		add(tabbedPane);
	}
	
	/*
	 * updateGame - called whenever the app state changes.
	 */
	public void updateGame(Vector<Connection> path)
	{
		String text = "<html>\n"
			+ " <body style=\"font-size:20\">\n"
			+ "  <ol>\n";
		Iterator<Connection> it = path.iterator();
		while(it.hasNext())
		{
			Connection c = it.next();
			Actor a1 = graph.actors.get(c.actor1ID);
			Actor a2 = graph.actors.get(c.actor2ID);
			Film f = graph.films.get(c.filmID);
			String desc = a1.firstName + " " + a1.lastName
			+ " was in " + f.title
			+ " with " + a2.firstName + " " + a2.lastName
			+ " in " + f.year;
			text += "   <li>" + desc + "</li>\n";
		}
		text += "  </ol>\n";
		text += " </body>\n";
		text += "</html>";
		gamePane.setText(text);
		gamePane.setCaretPosition(0);
	}

	/*
	 * updateNeighborsPane - 
	 */
	public void updateNeighborsPane(Actor actor)
	{
		String text = "<html>\n"
			+ " <body style=\"font-size:20\">\n"
			+ "  <ol>\n";
		Vector<Connection> neighbors = graph.getAllNeighbors(actor.id);
		Iterator<Connection> it = neighbors.iterator();
		while(it.hasNext())
		{
			Connection c = it.next();
			Actor a1 = graph.actors.get(c.actor1ID);
			Actor a2 = graph.actors.get(c.actor2ID);
			Film f = graph.films.get(c.filmID);
			String desc = a1.firstName + " " + a1.lastName
			+ " was in " + f.title
			+ " with " + a2.firstName + " " + a2.lastName
			+ " in " + f.year;
			text += "   <li>" + desc + "</li>\n";
		}
		text += "  </ol>\n";
		text += " </body>\n";
		text += "</html>";
		neighborsPane.setText(text);
		neighborsPane.setCaretPosition(0);
	}
	
	/*
	 * updateActorsPane - updates content for actors display
	 */
	public void updateActorsPane(Actor actor)
	{
		String html = "<html>\n"
				+ " <body style=\"font-size:20\">\n"
				+ "  <ol>\n";
		Iterator<String> it = actor.filmIDs.iterator();
		while (it.hasNext())
		{
			String filmID = it.next();
			Film film = graph.films.get(filmID);
			html += "   <li>" + film.title + " (" + film.year + ")" + "</li>\n";
		}
		html += "  </ol>\n"
			+   " </body>\n"
			+	"</html>";
		actorsPane.setText(html);
		actorsPane.setCaretPosition(0);
	}

	/*
	 * updateFilmsPane - updates content for films display
	 */
	public void updateFilmsPane(Film film)
	{
		String html = "<html>\n"
				+ " <body style=\"font-size:20\">\n"
				+ "  <ol>\n";
		Iterator<String> it = film.actorIDs.iterator();
		while (it.hasNext())
		{
			String actorID = it.next();
			Actor actor = graph.actors.get(actorID);
			html += "   <li>" + actor.toString() + "</li>\n";
		}
		html += "  </ol>\n"
			+   " </body>\n"
			+	"</html>";
		filmsPane.setText(html);
		filmsPane.setCaretPosition(0);
	}	

        /**
         * Called when the user makes a selection in a combo box.
         */
	public void itemStateChanged(ItemEvent arg) 
	{
		Object source = arg.getSource();
		int stateChange = arg.getStateChange();
		if (stateChange != ItemEvent.SELECTED)
			return;
		if (source == gameComboBox)
		{						
			Actor selectedActor = (Actor)arg.getItem();
			Vector<Connection> path;
			if (dfsRadioButton.isSelected())
				path = graph.findPathToKevinBacon(selectedActor);
			else
				path = graph.findShortestPathToKevinBacon(selectedActor);
			updateGame(path);
		}
		else if (source == dfsRadioButton)
		{
			Actor selectedActor = (Actor)gameComboBox.getSelectedItem();
			Vector<Connection> path = graph.findPathToKevinBacon(selectedActor);
			updateGame(path);
		}
		else if (source == bfsRadioButton)
		{
			Actor selectedActor = (Actor)gameComboBox.getSelectedItem();
			Vector<Connection> path = graph.findShortestPathToKevinBacon(selectedActor);
			updateGame(path);
		}
		else if (source == neighborsComboBox)
		{
			Actor selectedActor = (Actor)arg.getItem();
			updateNeighborsPane(selectedActor);
		}
		else if (source == actorsComboBox)
		{
			Actor selectedActor = (Actor)arg.getItem();
			updateActorsPane(selectedActor);
		}
		else if (source == filmsComboBox)
		{
			Film selectedFilm = (Film)arg.getItem();
			updateFilmsPane(selectedFilm);
		}
	}
	
        // STARTS THE PROGRAM IN EVENT HANDLING MODE
	public static void main(String[] args) 
	{
		JFrame frame = new KevinBaconGameDataViewer();
		frame.setVisible(true);
	}
}