import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class Main
{
    //Noder: nodenr breddegrad lengdegrad - Starter med antall noder
    //Kanter: franode  tilnode kjøretid (hundredels sekunder) lengde (meter) fartsgrense (km/t) - Starter med antall kanter
    //Stedsnavn: nodenr kode "Navn på stedet" - Starter med antall linjer
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starter...");

        File[] testfiler = new File[3];
        testfiler[0] = new File("src/IslandNoder.txt");
        testfiler[1] = new File("src/IslandKanter.txt");
        testfiler[2] = new File("src/IslandStedsnavn.txt");

        File[] nordenFiler = new File[3];
        nordenFiler[0] = new File("src/Noder.txt");
        nordenFiler[1] = new File("src/Kanter.txt");
        nordenFiler[2] = new File("src/Stedsnavn.txt");

        //True gir testfiler, False gir Norden filer
        File[] valgteFiler;
        if (false)
        {
            valgteFiler = testfiler;
        }
        else
        {
            valgteFiler = nordenFiler;
        }

        int trondheim = 2399829, helsinki = 1221382, trondheimLufhavn = 6198111, rorosHotell = 1117256, karvag = 6013683, gjemnes = 6225195, oslo = 2353304, stockholm = 5916504;
        int startNode = stockholm, sluttNode = oslo;
        System.out.println("Prep...");
        Graf graf = new Graf(valgteFiler[0], valgteFiler[1], valgteFiler[2]);
        PrintWriter outputStream;

        //Tester om tilfeldige ruter kjøres på under 10 sekunder
        /*
        double avgTimeDijkstra = 0, avgTimeAStar = 0;
        Random random = new Random();
        int runder = 100;
        for (int i = 0; i < runder; i++)
        {
            startNode = random.nextInt(graf.get_noder().length);
            sluttNode = random.nextInt(graf.get_noder().length);

            graf.prepGrafIgjen();
            long startTime = System.nanoTime();
            graf.dijkstra(startNode, sluttNode);
            long endTime = System.nanoTime();
            long total = endTime - startTime;
            if ((double)total / 1000000000 > 10.0)
            {
                System.out.println("Tok mer enn 10 sekunder,,,");
            }
            avgTimeDijkstra += ((double)total / 1000000000) / runder;

            graf.prepGrafIgjen();
            startTime = System.nanoTime();
            graf.aStar(startNode, sluttNode);
            endTime = System.nanoTime();
            total = endTime - startTime;
            if ((double)total / 1000000000 > 10.0)
            {
                System.out.println("Tok mer enn 10 sekunder,,,");
            }
            avgTimeAStar += ((double)total / 1000000000) / runder;
            System.out.println(i);
        }
        System.out.println("AVG Dijkstra: " + avgTimeDijkstra + "\nAVG AStar: " + avgTimeAStar);
         */


        //Kjører Dijkstra på grafen
        graf.prepGrafIgjen();
        System.out.println("Kjører...");
        long startTime = System.nanoTime();
        graf.dijkstra(startNode, sluttNode);
        long endTime = System.nanoTime();
        long total = endTime - startTime;
        System.out.println((double)total / 1000000000 + " sekunder");
        System.out.println("Ferdig");
        System.out.println(graf.finnInfoSluttNode(sluttNode));

        Node[] dijkstra = graf.getPathFromSlutt(sluttNode);
        graf.skrivTilFil(dijkstra, "Dijkstra");


        //Kjører AStar på grafen
        System.out.println("Prep...");
        graf.prepGrafIgjen();
        System.out.println("Kjører...");
        startTime = System.nanoTime();
        graf.aStar(startNode, sluttNode);
        endTime = System.nanoTime();
        total = endTime - startTime;
        System.out.println((double)total / 1000000000 + " sekunder");
        System.out.println("Ferdig");
        System.out.println(graf.finnInfoSluttNode(sluttNode));

        Node[] astar = graf.getPathFromSlutt(sluttNode);
        graf.skrivTilFil(astar, "AStar");

        //Sjekker om AStar og Dijkstra har forskjellige resultater
        for (int i = 0, count = 0; i < dijkstra.length && i < astar.length; i++)
        {
            if (dijkstra[i] != astar[i] && count < 100)
            {
                System.out.println("Dik: " + dijkstra[i] + " Ast: " + astar[i] + " index: " + i);
                count++;
            }
        }


        //Finner de 10 nærmeste bensinstasjonene. PS: antallet (10) er hardcode'et
        outputStream = new PrintWriter("src/Bensinsstajoner.txt");
        outputStream.println(((Forgjenger)graf.get_noder()[trondheimLufhavn].getForgjenger()).get_breddegrad() + "," + ((Forgjenger)graf.get_noder()[trondheimLufhavn].getForgjenger()).get_lengdegrad() + ",trondheimLufhavn" + ",#F00000");
        for (Node n : graf.finnBensinstasjoner(trondheimLufhavn)) //Trondheim lufthavn, Værnes(6198111)
        {
            double breddegrad = ((Forgjenger)n.getForgjenger()).get_breddegrad();
            double lengdegrad = ((Forgjenger)n.getForgjenger()).get_lengdegrad();
            String navn = n.toString();
            String farge = "#FFFF00";
            outputStream.println(breddegrad + "," + lengdegrad + "," + navn + "," + farge);
        }
        outputStream.flush();
        outputStream.close();

        //Finner de 10 nærmeste ladestasjonene. PS: antallet (10) er hardcode'et
        outputStream = new PrintWriter("src/Ladestasjoner.txt");
        outputStream.println(((Forgjenger)graf.get_noder()[rorosHotell].getForgjenger()).get_breddegrad() + "," + ((Forgjenger)graf.get_noder()[rorosHotell].getForgjenger()).get_lengdegrad() + ",rorosHotell" + ",#F00000");
        for (Node n : graf.finnLadestasjoner(rorosHotell)) //Røros hotell(1117256)
        {
            double breddegrad = ((Forgjenger)n.getForgjenger()).get_breddegrad();
            double lengdegrad = ((Forgjenger)n.getForgjenger()).get_lengdegrad();
            String navn = n.toString();
            String farge = "#FFFF00";
            outputStream.println(breddegrad + "," + lengdegrad + "," + navn + "," + farge);
        }
        outputStream.flush();
        outputStream.close();
    }
}


/**
 * Basert på boka, side 177
 */
class Forgjenger
{
    private long _distanse;
    private long _totalDistanse;
    private Node _forgjengerNode;
    static long uendelig = 4500000000000000000l; // MAX = 9,223,372,036,854,775,807
    private double _breddegrad;
    private double _lengdegrad;
    private double _breddeRadian;
    private double _lengdeRadian;
    private double _cosBredde;


    public Forgjenger()
    {
        this._distanse = uendelig;
        this._totalDistanse = uendelig;
    }

    public void set_breddegrad(double _breddegrad)
    {
        this._breddegrad = _breddegrad;
        this._breddeRadian = this._breddegrad * Math.PI / 180.0;
        this._cosBredde = Math.cos(this._breddeRadian);
    }

    public double get_breddeRadian()
    {
        return _breddeRadian;
    }

    public double get_lengdeRadian()
    {
        return _lengdeRadian;
    }

    public double get_cosBredde()
    {
        return _cosBredde;
    }

    public void set_lengdegrad(double _lengdegrad)
    {
        this._lengdegrad = _lengdegrad;
        this._lengdeRadian = this._lengdegrad * Math.PI / 180.0;
    }

    public double get_breddegrad()
    {
        return _breddegrad;
    }

    public double get_lengdegrad()
    {
        return _lengdegrad;
    }

    public Node get_forgjengerNode()
    {
        return _forgjengerNode;
    }

    public void set_forgjengerNode(Node _forgjengerNode)
    {
        this._forgjengerNode = _forgjengerNode;
    }

    public long getDistanse()
    {
        return this._distanse;
    }

    public void setDistanse(long nyDistanse)
    {
        this._distanse = nyDistanse;
    }

    public long get_totalDistanse()
    {
        return _totalDistanse;
    }

    public void set_totalDistanse(long _totalDistanse)
    {
        this._totalDistanse = _totalDistanse;
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
 * Basert på boka, side 172
 */
class Graf
{
    private int _antallNoder;
    private int _antallKanter;
    private Node[] _noder;
    private Heap _heap = new Heap();

    public Graf(File noder, File kanter, File navn) throws IOException
    {
        this.prepGraf(noder, kanter, navn);
    }

    public Node[] get_noder()
    {
        return _noder;
    }

    public void skrivTilFil(Node[] noder, String navn) throws FileNotFoundException
    {
        PrintWriter outputStream = new PrintWriter("src/" + navn + ".txt");
        for (Node n : noder)
        {
            double breddegrad = ((Forgjenger)n.getForgjenger()).get_breddegrad();
            double lengdegrad = ((Forgjenger)n.getForgjenger()).get_lengdegrad();
            outputStream.println(breddegrad + "  |  " + lengdegrad);
        }
        outputStream.flush();
        outputStream.close();
    }

    public void initForgjenger()
    {
        for (int i = 0; i < this._antallNoder; i++)
        {
            if (this._noder[i].getForgjenger() == null)
            {
                this._noder[i].setForgjenger(new Forgjenger());
            }
            else
            {
                ((Forgjenger)this._noder[i].getForgjenger()).set_forgjengerNode(null);
                ((Forgjenger)this._noder[i].getForgjenger()).setDistanse(Forgjenger.uendelig);
                ((Forgjenger)this._noder[i].getForgjenger()).set_totalDistanse(Forgjenger.uendelig);
            }
        }
    }

    public void initNoder()
    {
        for (int i = 0; i < this._antallNoder; i++)
        {
            this._noder[i] = new Node(i);
        }
    }

    private void prepGraf(File noder, File kanter, File stedsnavn) throws IOException
    {
        this.lesFileNoder(noder);
        this.lesFileKanter(kanter);
        this.lesFileNavn(stedsnavn);
    }

    public void prepGrafIgjen()
    {
        this.initForgjenger();
        this._heap = new Heap();

        for (Node n : this._noder)
        {
            n.setHeapIndex(-1);
        }
    }

    public void setStartNode(int start)
    {
        ((Forgjenger)(this._noder[start].getForgjenger())).setDistanse(0);
        ((Forgjenger)(this._noder[start].getForgjenger())).set_totalDistanse(0);
        this._heap.setStartNode(this._noder[start]);
    }

    private void lesFileNoder(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringTokenizer st = new StringTokenizer(br.readLine());
        this._antallNoder = Integer.parseInt(st.nextToken());
        this._noder = new Node[this._antallNoder];


        this.initNoder();
        this.initForgjenger();
        for (int i = 0; i < this._antallNoder; i++)
        {
            st = new StringTokenizer(br.readLine());
            int index = Integer.parseInt(st.nextToken());
            ((Forgjenger)this._noder[index].getForgjenger()).set_breddegrad(Double.parseDouble(st.nextToken()));
            ((Forgjenger)this._noder[index].getForgjenger()).set_lengdegrad(Double.parseDouble(st.nextToken()));
        }
    }

    private void lesFileKanter(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringTokenizer st = new StringTokenizer(br.readLine());
        this._antallKanter = Integer.parseInt(st.nextToken());

        for (int i = 0; i < this._antallKanter; i++)
        {
            st = new StringTokenizer(br.readLine());
            int fraNode = Integer.parseInt(st.nextToken());
            int tilNode = Integer.parseInt(st.nextToken());
            int kjoretid = Integer.parseInt(st.nextToken());
            int lengde = Integer.parseInt(st.nextToken());
            int fartsgrense = Integer.parseInt(st.nextToken());
            Kant kant = new Kant(this._noder[fraNode].getKantHode(), this._noder[tilNode], kjoretid, lengde, fartsgrense);
            this._noder[fraNode].setKantHode(kant);
        }
    }

    private void lesFileNavn(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int antallNavn = Integer.parseInt(st.nextToken());

        for (int i = 0; i < antallNavn; i++)
        {
            st = new StringTokenizer(br.readLine());
            int index = Integer.parseInt(st.nextToken());
            int type = Integer.parseInt(st.nextToken());
            st.nextToken();

            switch (type)
            {
                //Noden er en bensinstasjon
                case 2:
                    this._noder[index].set_bensinS(true);
                    break;
                //Noden er en ladestasjon
                case 4:
                    this._noder[index].set_ladeS(true);
                    break;
                //Noden er en bensin- og ladestasjon
                case 6:
                    this._noder[index].set_bensinS(true);
                    this._noder[index].set_ladeS(true);
                    break;
            }
        }
    }

    public void dijkstra(int startNode, int sluttNode)
    {
        this.setStartNode(startNode);
        boolean funnetSlutt = false;
        while (this._heap.getLengde() > 0 && !funnetSlutt) //Går gjennom alle alle elementene i heap'en, stopper om vi har funnet slutt noden
        {
            Node node = hentMin();
            if (node.getIndex() == sluttNode)
            {
                funnetSlutt = true;
            }
            for (Kant kant = node.getKantHode(); kant != null; kant = kant.get_neste())
            {
                Node kantNode = kant.get_tilNode();
                if (kantNode.getHeapIndex() == -1)
                {
                    this._heap.leggInn(kantNode);
                }
                this.forkortDijkstra(node, kant);
            }
        }
    }

    /**
     * Basert på boka, side 194
     *
     * @param node
     * @param kant
     */
    private void forkortDijkstra(Node node, Kant kant)
    {
        Forgjenger forgjenger1 = (Forgjenger)node.getForgjenger(), forgjenger2 = (Forgjenger)kant.get_tilNode().getForgjenger();
        long nyVerdi = forgjenger1.getDistanse() + kant.get_kjoretid();
        if (forgjenger2.getDistanse() > nyVerdi)
        {
            forgjenger2.setDistanse(nyVerdi);
            forgjenger2.set_forgjengerNode(node);
            this._heap.prioritetOpp(kant.get_tilNode().getHeapIndex(), nyVerdi);
        }
    }

    /**
     * Henter noden som har lavest verdi i heap'en
     *
     * @return
     */
    private Node hentMin()
    {
        return this._heap.hentMin();
    }

    public void aStar(int startNode, int sluttNode)
    {
        this.setStartNode(startNode);
        boolean funnetSlutt = false;
        while (this._heap.getLengde() > 0 && !funnetSlutt) //Går gjennom alle alle elementene i heap'en, stopper om vi har funnet slutt noden
        {
            Node node = hentMin();
            if (node.getIndex() == sluttNode)
            {
                funnetSlutt = true;
            }
            for (Kant kant = node.getKantHode(); kant != null; kant = kant.get_neste())
            {
                Node kantNode = kant.get_tilNode();
                if (kantNode.getHeapIndex() == -1)
                {
                    this._heap.leggInn(kantNode);
                }
                this.forkortAStar(node, kant, this._noder[sluttNode]);
            }
        }
    }

    private void forkortAStar(Node node, Kant kant, Node sluttNode)
    {
        Forgjenger forgjenger1 = (Forgjenger)node.getForgjenger(), forgjenger2 = (Forgjenger)kant.get_tilNode().getForgjenger();
        long nyVerdi = forgjenger1.getDistanse() + kant.get_kjoretid();
        if (forgjenger2.getDistanse() > nyVerdi)
        {
            forgjenger2.setDistanse(nyVerdi);
            forgjenger2.set_totalDistanse(nyVerdi + avstand(kant.get_tilNode(), sluttNode));
            forgjenger2.set_forgjengerNode(node);
            this._heap.prioritetOpp(kant.get_tilNode().getHeapIndex(), forgjenger2.get_totalDistanse());
        }
    }

    private int avstand(Node node1, Node node2)
    {
        //Jordens radius er 6371 km, høyeste fartsgrense 130km/t, 3600 sek/time
        //For å få hundredels sekunder: 2*6371/130*3600*100 = 35285538.46153846153846153846
        double sinBredde = Math.sin((((Forgjenger)node1.getForgjenger()).get_breddeRadian() - ((Forgjenger)node2.getForgjenger()).get_breddeRadian()) / 2);
        double sinLengde = Math.sin((((Forgjenger)node1.getForgjenger()).get_lengdeRadian() - ((Forgjenger)node2.getForgjenger()).get_lengdeRadian()) / 2);
        return (int)(35285538.46153846153846153846 * Math.asin(Math.sqrt(sinBredde * sinBredde + ((Forgjenger)node1.getForgjenger()).get_cosBredde() * ((Forgjenger)node2.getForgjenger()).get_cosBredde() * sinLengde * sinLengde)));
    }

    public Node[] getPathFromSlutt(int slutt)
    {
        ArrayList<Node> noder = new ArrayList<>();
        Node node = this._noder[slutt];
        while (node != null)
        {
            noder.add(node);
            node = ((Forgjenger)node.getForgjenger()).get_forgjengerNode();
        }
        Node[] path = new Node[noder.size()];

        for (int i = 0; i < noder.size(); i++)
        {
            path[i] = noder.get(i);
        }
        return path;
    }

    public String finnInfoSluttNode(int node)
    {
        Node slutt = this._noder[node];
        long distanse = ((Forgjenger)slutt.getForgjenger()).getDistanse();
        int totalNoder = 0;
        for (Node n : this._noder)
        {
            if (((Forgjenger)n.getForgjenger()).get_forgjengerNode() != null)
            {
                totalNoder++;
            }
        }

        int antallNoder = 0;
        while (((Forgjenger)slutt.getForgjenger()).get_forgjengerNode() != null)
        {
            antallNoder++;
            slutt = ((Forgjenger)slutt.getForgjenger()).get_forgjengerNode();
        }
        long timer = distanse / (60 * 60 * 100); //Hundredels sekunder til timer
        distanse %= (60 * 60 * 100);
        long minutter = distanse / (60 * 100); //Hundredels sekunder til minutter
        distanse %= (60 * 100);
        long sekunder = distanse % (100); //Hundredels sekunder til sekunder

        return "Antall noder (Path): " + antallNoder + "\nAntall noder (totalt): " + totalNoder + "\nTid: Timer: " + timer + ", Minutter: " + minutter + ", Sekunder: " + sekunder;
    }

    public Node[] finnBensinstasjoner(int startNode)
    {
        this.prepGrafIgjen();
        Node[] stastjoner = new Node[10];
        this.setStartNode(startNode);
        int funnetStasjoner = 0;
        while (this._heap.getLengde() > 0 && funnetStasjoner < 10) //Går gjennom alle alle elementene i heap'en, stopper om vi har funnet slutt noden
        {
            Node node = hentMin();
            if (node.is_bensinS())
            {
                stastjoner[funnetStasjoner++] = node;
            }
            for (Kant kant = node.getKantHode(); kant != null; kant = kant.get_neste())
            {
                if (kant.get_tilNode().getHeapIndex() == -1)
                {
                    this._heap.leggInn(kant.get_tilNode());
                }
                this.forkortDijkstra(node, kant);
            }
        }
        return stastjoner;
    }

    public Node[] finnLadestasjoner(int startNode)
    {
        this.prepGrafIgjen();
        Node[] stastjoner = new Node[10];
        this.setStartNode(startNode);
        int funnetStasjoner = 0;
        while (this._heap.getLengde() > 0 && funnetStasjoner < 10) //Går gjennom alle alle elementene i heap'en, stopper om vi har funnet slutt noden
        {
            Node node = hentMin();
            if (node.is_ladeS())
            {
                stastjoner[funnetStasjoner++] = node;
            }
            for (Kant kant = node.getKantHode(); kant != null; kant = kant.get_neste())
            {
                if (kant.get_tilNode().getHeapIndex() == -1)
                {
                    this._heap.leggInn(kant.get_tilNode());
                }
                this.forkortDijkstra(node, kant);
            }
        }
        return stastjoner;
    }
}

/**
 * Basert på eksempler i boka
 */
class Heap
{
    private int _lengde;
    private Node[] _noder;

    public Heap()
    {
        this._lengde = 0;
        this._noder = new Node[512];
    }

    public int getLengde()
    {
        return this._lengde;
    }

    public Node[] getNoder()
    {
        return this._noder;
    }

    private int over(int utgangsPosisjon)
    {
        return (utgangsPosisjon - 1) >> 1;
    }

    private int venstre(int utgangsPosisjon)
    {
        return (utgangsPosisjon << 1) + 1;
    }

    public void leggInn(Node node)
    {
        if (this._lengde >= this._noder.length)
        {
            this.utvidHeap();
        }
        this._noder[this._lengde] = node;
        node.setHeapIndex(this._lengde);
        this.prioritetOpp(this._lengde++, ((Forgjenger)node.getForgjenger()).get_totalDistanse());
    }

    private void utvidHeap()
    {
        Node[] newHeap = new Node[this._noder.length << 1];

        for (int i = 0; i < this._noder.length; i++)
        {
            newHeap[i] = this._noder[i];
        }
        this._noder = newHeap;
    }

    public void setStartNode(Node startNode)
    {
        this._noder[this._lengde] = startNode;
        startNode.setHeapIndex(this._lengde++);
    }

    public Node hentMin()
    {
        if (this._lengde > 0)
        {
            Node min = this.getNoder()[0];
            this.bytt(this._noder, 0, --this._lengde);
            this.fiksHeap(0);
            return min;
        }
        else
        {
            return null;
        }
    }

    public void prioritetOpp(int posisjon, long nyVerdi)
    {
        int foreldrenode;
        ((Forgjenger)this.getNoder()[posisjon].getForgjenger()).set_totalDistanse(nyVerdi); //Setter den nye korteste veien til noden fra start
        while (posisjon > 1 && ((Forgjenger)this.getNoder()[posisjon].getForgjenger()).get_totalDistanse() < ((Forgjenger)this.getNoder()[foreldrenode = over(posisjon)].getForgjenger()).get_totalDistanse()) //Hvis veien er kortere en forelderen
        {
            bytt(this.getNoder(), posisjon, foreldrenode);
            posisjon = foreldrenode;
        }
    }

    public void fiksHeap(int posisjon)
    {
        int venstre = this.venstre(posisjon);
        if (venstre < this.getLengde())
        {
            int hoyre = venstre + 1;
            if (hoyre < this.getLengde() && ((Forgjenger)this.getNoder()[hoyre].getForgjenger()).get_totalDistanse() < ((Forgjenger)this.getNoder()[venstre].getForgjenger()).get_totalDistanse())
            {
                venstre = hoyre;
            }
            if (((Forgjenger)this.getNoder()[venstre].getForgjenger()).get_totalDistanse() < ((Forgjenger)this.getNoder()[posisjon].getForgjenger()).get_totalDistanse())
            {
                this.bytt(this.getNoder(), posisjon, venstre);
                this.fiksHeap(venstre);
            }
        }
    }

    private void bytt(Node[] noder, int i, int j)
    {
        if (i != j)
        {
            Node mellom = noder[i];
            noder[i] = noder[j];
            noder[i].setHeapIndex(i);
            noder[j] = mellom;
            noder[j].setHeapIndex(j);
        }
    }
}

/**
 * Basert på boka, side 172
 */
class Node
{
    private Kant _kantHode; //Starten av en enkellenka liste med 'kanter'
    private Object _forgjenger;
    private int _index;
    private int _heapIndex = -1;
    private boolean _bensinS = false;
    private boolean _ladeS = false;

    public Node(int index)
    {
        this._index = index;
    }

    public boolean is_bensinS()
    {
        return _bensinS;
    }

    public boolean is_ladeS()
    {
        return _ladeS;
    }

    public void set_bensinS(boolean _bensinS)
    {
        this._bensinS = _bensinS;
    }

    public void set_ladeS(boolean _ladeS)
    {
        this._ladeS = _ladeS;
    }

    public Kant getKantHode()
    {
        return this._kantHode;
    }

    public void setKantHode(Kant nyKantHode)
    {
        this._kantHode = nyKantHode;
    }

    public Object getForgjenger()
    {
        return this._forgjenger;
    }

    public void setForgjenger(Object nyForgjenger)
    {
        this._forgjenger = nyForgjenger;
    }

    public int getIndex()
    {
        return this._index;
    }

    public int getHeapIndex()
    {
        return this._heapIndex;
    }

    public void setHeapIndex(int nyHeapIndex)
    {
        this._heapIndex = nyHeapIndex;
    }

    @Override
    public String toString()
    {
        return "Node: " + this.getIndex();
    }
}

/**
 * Basert på boka, side 172
 */
class Kant
{
    private Kant _neste; //Neste kant fra noden kanten kommer fra
    private Node _tilNode;
    private int _kjoretid;
    private int _lengde;
    private int _fartsgrense;


    public Kant(Kant neste, Node tilNode, int driveTime, int lengde, int fartsgrense)
    {
        this._neste = neste;
        this._tilNode = tilNode;
        this._kjoretid = driveTime;
        this._lengde = lengde;
        this._fartsgrense = fartsgrense;
    }

    public Kant get_neste()
    {
        return _neste;
    }

    public Node get_tilNode()
    {
        return _tilNode;
    }

    public int get_kjoretid()
    {
        return _kjoretid;
    }

    public int get_lengde()
    {
        return _lengde;
    }

    public int get_fartsgrense()
    {
        return _fartsgrense;
    }

    public void set_neste(Kant _neste)
    {
        this._neste = _neste;
    }

    public void set_tilNode(Node _tilNode)
    {
        this._tilNode = _tilNode;
    }

    public void set_kjoretid(int _kjoretid)
    {
        this._kjoretid = _kjoretid;
    }

    public void set_lengde(int _lengde)
    {
        this._lengde = _lengde;
    }

    public void set_fartsgrense(int _fartsgrense)
    {
        this._fartsgrense = _fartsgrense;
    }
}