import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) throws IOException
    {
        Graf graf = new Graf();
        //1: L7g1, 2: L7g2, 3: L7g5, 4: L7g6, 5: L7Skandinavia(funker ikke enda, StackOverflowError)
        System.out.println(graf.finnSterktSammenhengende(4));
    }
}

/**
 * Basert på boka, side 177
 */
class Forgjenger
{
    private int _distanse;
    private Node _forgjenger;
    static int uendelig = 1000000000;

    public Forgjenger() {
        this._distanse = uendelig;
    }

    public void setForgjenger(Node nyForgjenger) {
        this._forgjenger = nyForgjenger;
    }

    public int getDistanse() {
        return this._distanse;
    }

    public void setDistanse(int nyDistanse) {
        this._distanse = nyDistanse;
    }

    @Override
    public String toString()
    {
        String returString = "";
        returString += "Distanse: " + this.getDistanse();
        return returString;
    }
}

/**
 * Basert på boka, side 181
 */
class DFS_Forgjenger extends Forgjenger
{
    private int _funnetTid, _ferdigTid;
    static int tid; //Teller

    static void nullTid()
    {
        tid = 0;
    }

    static int lesTid()
    {
        return ++tid;
    }

    public int getFunnetTid()
    {
        return this._funnetTid;
    }

    public int getFerdigTid()
    {
        return this._ferdigTid;
    }

    public void setFunnetTid(int nyFunnetTid)
    {
        this._funnetTid = nyFunnetTid;
    }

    public void setFerdigTid(int nyFerdigTid)
    {
        this._ferdigTid = nyFerdigTid;
    }
}

/**
 * Basert på boka, side 172
 */
class Graf
{
    private int _antallNoder;
    private int _antallKanter;
    private Node[] _noder;
    private ArrayList<Integer> _ferdigListe = new ArrayList<>();
    private ArrayList<Integer> _komponentListe = new ArrayList<>();

    public int getAntallNoder()
    {
        return _antallNoder;
    }

    public int getAntallKanter()
    {
        return _antallKanter;
    }

    public Node[] getNoder()
    {
        return _noder;
    }

    private ArrayList<Integer> getFerdigListe()
    {
        return this._ferdigListe;
    }

    public ArrayList<Integer> getKomponentListe()
    {
        return this._komponentListe;
    }

    /**
     * Kaller dfs() på alle nodene som ikke er i ferdiglisten enda
     * og returnerer ferdig listen i synkede rekkefølge på ferdig-tid
     * @return
     */
    private ArrayList<Integer> fetchFerdigListe()
    {
        for (int i = 0; i < this.getAntallNoder(); i++)
        {
            if (!this.getFerdigListe().contains(i))
            {
                this.dfs(this.getNoder()[i]);
            }
        }

        //Snur listen så den er i synkede rekkefølge (ferdig-tid)
        ArrayList<Integer> mid = new ArrayList<>();
        for (int i = this._ferdigListe.size(); --i >= 0;)
        {
            mid.add(this._ferdigListe.get(i));
        }
        return mid;
    }

    /**
     * Lager den omvendte grafen og setter
     * 'this' sine noder lik den sine og 'this' er
     * da blitt omvendt
     */
    private void transposeGraf()
    {
        Graf graf = new Graf();
        graf._antallNoder = this.getAntallNoder();
        graf._antallKanter = this.getAntallKanter();
        graf._noder = new Node[graf._antallNoder];
        for (int i = 0; i < graf.getAntallNoder(); i++)
        {
            graf._noder[i] = new Node(i);
        }
        graf.dfsInit();
        for (int i = 0; i < this.getAntallNoder(); i++) //Går gjennom nodene i 'this'
        {
            Node node = this.getNoder()[i];
            for (Kant k = node.getKantHode(); k != null; k = k.getNesteKant()) //Går gjennom alle kantene til noden i 'this'
            {
                int fra = k.getNode().getIndex(); //Setter graf sin 'fra' lik 'this' sin 'til' node
                int til = node.getIndex(); //Setter graf sin 'til' lik 'this' sin 'fra' node
                Kant kant = new Kant(graf._noder[til], graf._noder[fra].getKantHode()); //Lager en kant motsatt av den i 'this', og lenker kanten til kanthode i 'fra'-noden(til graf)
                graf._noder[fra].setKantHode(kant); //Setter den nye kanten lik kanthode i 'fra'-noden (graf)
            }
        }
        this._noder = graf.getNoder(); //Setter 'this' sine noder lik graf sine noder
    }

    /**
     * Tar et filvalg, velger fil basert på det. Leser filen inn og lager grafen.
     * Kjører DFS og lager en liste (brukes som en stack) med synkende ferdigtid.
     * Transponerer grafen (lager den omvendt).
     * Kjører nytt DFS på den omvendte grafen på nodene i rekkefølgen deres i listen (ferdigtid),
     * og finner for hvert søk sterkt sammenhengende komponenter, lagrer dem i utskrifts
     * format (i en «String» som metoden returnerer), og søker etter neste node som ikke er i ferdiglisten.
     * Legger også til slutt til hvilken fil som er lest og hvor mange sterkt sammenhengende komponenter den har.
     * @param filValg
     * @return Sterkt sammenhengende komponenter i en graf
     * @throws IOException
     */
    public String finnSterktSammenhengende(int filValg) throws IOException
    {
        File valgtFil;
        String filnavn;
        switch (filValg)
        {
            case 1:
                filnavn = "L7g1";
                valgtFil = new File("src/L7g1.txt");
                break;
            case 2:
                filnavn = "L7g2";
                valgtFil = new File("src/L7g2.txt");
                break;

            case 3:
                filnavn = "L7g5";
                valgtFil = new File("src/L7g5.txt");
                break;

            case 4:
                filnavn = "L7g6";
                valgtFil = new File("src/L7g6.txt");
                break;

            case 5:
                filnavn = "L7Skandinavia";
                valgtFil = new File("src/L7Skandinavia.txt");
                break;

            default:
                throw new IllegalArgumentException("Må sende inn et gyldig valg");
        }

        this.lesFil(new BufferedReader(new FileReader(valgtFil))); //Leser inn og oppretter grafen, basert på fila
        ArrayList<Integer> stack = this.fetchFerdigListe(); //Henter ut listen over nodene etter DFS i synkende ferdig-tid
        String resultat = "";
        this.transposeGraf(); //Lager den omvendte grafen
        getFerdigListe().clear(); //Tømmer ferdig listen
        int komponent = 0; //Teller hvilken komponent vi er på
        for (int i : stack) //Går gjennom listen med noder i synkende ferdig-tid
        {
            if (!this.getFerdigListe().contains(i)) //Sjekker om noden alt er registret (ligger i ferdig listen)
            {
                komponent++; //Ny komponent, inkrementerer
                this.getKomponentListe().clear(); //Klargjør komponent listen
                this.dfs(this.getNoder()[i]); //Kjører DFS på noden
                resultat += "\n" + komponent + "           "; //Format oppsett
                if (this.getKomponentListe().size() < 100) //Skriver kun ut de spesifike nodene hvis det er under 100
                {
                    for (int in : this.getKomponentListe()) //Går gjennom alle nodene i komponent listen
                    {
                        resultat += in + ", "; //Legger inn noden i resultatet
                    }
                }
                else
                {
                    resultat += this.getKomponentListe().size() + "(Antall noder)";
                }
            }
        }
        return "Grafen " + filnavn + " har " + komponent + " sterkt\nsammenhengdene komponenter.\nKomponent   Noder i komponent\n" + resultat;
    }

    /**
     * Basert på boka, side 173
     * @param fil
     * @throws IOException
     */
    public void lesFil(BufferedReader fil) throws IOException
    {
        StringTokenizer st = new StringTokenizer(fil.readLine());
        this._antallNoder = Integer.parseInt(st.nextToken());
        this._noder = new Node[this._antallNoder];

        for (int i = 0; i < this._antallNoder; i++)
        {
            this._noder[i] = new Node(i);
        }

        this._antallKanter = Integer.parseInt(st.nextToken());

        for (int i = 0; i < this._antallKanter; i++)
        {
            st = new StringTokenizer(fil.readLine());
            int fra = Integer.parseInt(st.nextToken());
            int til = Integer.parseInt(st.nextToken());
            Kant kant = new Kant(this._noder[til], this._noder[fra].getKantHode());
            this._noder[fra].setKantHode(kant);
        }
    }

    /**
     * Basert på boka, side 177
     * @param start
     */
    public void initForgjenger(Node start)
    {
        for (int i = this._antallNoder;  i-- > 0;)
        {
            this._noder[i].setD(new Forgjenger());
        }
        ((Forgjenger)(start.getD())).setDistanse(0);
    }

    /**
     * Basert på boka, side 181
     */
    private void dfsInit()
    {
        for (int i = this._antallNoder;  i-- > 0;)
        {
            this._noder[i].setD(new DFS_Forgjenger());
        }
        DFS_Forgjenger.nullTid();
    }

    /**
     * Basert på boka, side 181
     * @param node
     */
    private void dfSok(Node node)
    {
        DFS_Forgjenger fg = (DFS_Forgjenger)node.getD();
        fg.setFunnetTid(DFS_Forgjenger.lesTid());

        for (Kant kant = node.getKantHode(); kant != null; kant = kant.getNesteKant())
        {
            DFS_Forgjenger fg1 = (DFS_Forgjenger)kant.getNode().getD();
            if (fg1.getFunnetTid() == 0)
            {
                fg1.setForgjenger(node);
                fg1.setDistanse(fg.getDistanse() + 1);
                this.dfSok(kant.getNode());
            }
        }
        fg.setFerdigTid(DFS_Forgjenger.lesTid());
        if (!this._ferdigListe.contains(node.getIndex()))
        {
            this._ferdigListe.add(node.getIndex());
            this._komponentListe.add(node.getIndex());
        }
    }

    /**
     * Basert på boka, side 181
     * @param start
     */
    public void dfs(Node start)
    {
        this.dfsInit();
        ((DFS_Forgjenger)start.getD()).setDistanse(0);
        this.dfSok(start);
    }
}

/**
 * Basert på boka, side 172
 */
class Node
{
    private Kant _kantHode; //Starten av en enkellenka liste med 'kanter'
    private Object _d;
    private int _index;

    public Node(int index)
    {
        this._index = index;
    }

    public Kant getKantHode()
    {
        return this._kantHode;
    }

    public void setKantHode(Kant nyKantHode)
    {
        this._kantHode = nyKantHode;
    }

    public Object getD()
    {
        return this._d;
    }

    public void setD(Object nyD)
    {
        this._d = nyD;
    }

    public int getIndex()
    {
        return this._index;
    }



    @Override
    public String toString()
    {
        String returString = "";
        returString += "\nNode: " + this._index + "\n" + ((DFS_Forgjenger)this._d).toString() + "\nFerdig: " + ((DFS_Forgjenger)this._d).getFerdigTid();
        return returString;
    }
}

/**
 * Basert på boka, side 172
 */
class Kant
{
    private Kant _neste; //Neste kant fra noden kanten kommer fra
    private Node _pekerTil; //Hvilken node kanten går til

    public Kant(Node pekerTil, Kant nesteKant)
    {
        this._neste = nesteKant;
        this._pekerTil = pekerTil;
    }

    public Kant getNesteKant()
    {
        return _neste;
    }

    public Node getNode()
    {
        return _pekerTil;
    }
}