import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

public class ActorFileSaver
{
    public static void main(String[] args)
    {
        ActorIDRetriever.loadAllActorIDs();
        for (String id : ActorIDRetriever.actorIDs)
        {
            String urlText = "http://www.imdb.com/name/" + id;
            try
            {
                String outLocation = "ActorDataFiles/" + id;
                
                System.out.println(urlText);
                System.out.println(outLocation);
                PrintWriter out = new PrintWriter(outLocation);
                URL url = new URL(urlText);
                InputStreamReader isr = new InputStreamReader(url.openStream());
                BufferedReader br = new BufferedReader(isr);
                String line = br.readLine();
                while (line != null)
                {
                    out.println(line);
                    line = br.readLine();
                }
                out.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
