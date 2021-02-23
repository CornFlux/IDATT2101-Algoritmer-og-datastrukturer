import java.io.*;
import java.util.StringTokenizer;

public class Main
{

    public static void main(String[] args) throws IOException
    {
        Graf graf = new Graf();
        File valgtFil = new File("src/vg1.txt");
        //valgtFil = new File("src/vg2.txt");
        //valgtFil = new File("src/vg3.txt");
        //valgtFil = new File("src/vg4.txt");
        //valgtFil = new File("src/vg5.txt");
        //valgtFil = new File("src/vgSkandinavia.txt");
        BufferedReader br = new BufferedReader(new FileReader(valgtFil));
        graf.lesFil(br);
        long start = System.nanoTime();
        graf.dijkstra(1);
        long end = System.nanoTime();
        System.out.println((double)(end - start) / (double)1000000000 + " Sekunder");
        System.out.println(graf.printDijkstra());
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

    public Forgjenger()
    {
        this._distanse = uendelig;
    }

    public void setForgjenger(Node nyForgjenger)
    {
        this._forgjenger = nyForgjenger;
    }

    public Node getForgjenger()
    {
        return this._forgjenger;
    }

    public int getDistanse()
    {
        return this._distanse;
    }

    public void setDistanse(int nyDistanse)
    {
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
 * Basert på boka, side 172
 */
class Graf
{
    private int _antallNoder;
    private int _antallKanter;
    private Node[] _noder;
    private Heap _heap;
    private String[] _utskrift;

    /**
     * Basert på boka, side 192
     *
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
            int vekt = Integer.parseInt(st.nextToken());
            VKant kant = new VKant(this._noder[til], this._noder[fra].getKantHode(), vekt);
            this._noder[fra].setKantHode(kant);
        }
        this._utskrift = new String[this._noder.length + 2];
    }

    /**
     * Basert på boka, side 177
     *
     * @param start
     */
    public void initForgjenger(int start)
    {
        Node startNode = this._noder[start];
        for (int i = this._antallNoder; i-- > 0; )
        {
            this._noder[i].setForgjenger(new Forgjenger());
        }
        ((Forgjenger)(startNode.getForgjenger())).setDistanse(0);
    }

    /**
     * Basert på boka, side 195
     *
     * @param startNode
     */
    public void dijkstra(int startNode)
    {
        this.initForgjenger(startNode);
        this.lagPrioritetsko(this._noder, startNode);
        while (this._heap.getLengde() > 0) //Går gjennom alle alle elementene i heap'en
        {
            Node node = hentMin();
            for (VKant vKant = (VKant)node.getKantHode(); vKant != null; vKant = (VKant)vKant.getNesteKant())
            {
                this.forkort(node, vKant);
            }
        }
    }

    /**
     * Lager en utskrifts String
     * @return
     */
    public String printDijkstra()
    {
        for (int i = 0; i < this._utskrift.length; i++)
        {
            this._utskrift[i] = "";
        }
        this._utskrift[0] += "Node   Forgjenger   Distanse\n";
        for (Node n : this._noder)
        {
            if (((Forgjenger)n.getForgjenger()).getDistanse() == 0)
            {
                this._utskrift[n.getIndex() + 1] += n.getIndex() + "      Start        0\n";
            }
            else if (((Forgjenger)n.getForgjenger()).getDistanse() == Forgjenger.uendelig)
            {
                this._utskrift[n.getIndex() + 1] += n.getIndex() + "                   Nåes ikke\n";
            }
            else
            {
                this._utskrift[n.getIndex() + 1] += n.getIndex() + "      " + ((Forgjenger)n.getForgjenger()).getForgjenger().getIndex() + "            " + ((Forgjenger)n.getForgjenger()).getDistanse() + "\n";
            }
        }
        String retur = "";
        for (String s : this._utskrift)
        {
            retur += s;
        }
        return retur;
    }

    /**
     * Oppretter en heap som blir brukt for å håndtere nodenes prioritet
     * @param ko
     * @param startNode
     */
    private void lagPrioritetsko(Node[] ko, int startNode)
    {
        this._heap = new Heap(ko, startNode);
        this._heap.lagHeap();
    }

    /**
     * Henter noden som har lavest verdi i heap'en
     * @return
     */
    private Node hentMin()
    {
        return this._heap.hentMin();
    }

    /**
     * Basert på boka, side 194
     *
     * @param node
     * @param vKant
     */
    private void forkort(Node node, VKant vKant)
    {
        Forgjenger forgjenger1 = (Forgjenger)node.getForgjenger(), forgjenger2 = (Forgjenger)vKant.getNode().getForgjenger();
        int nyVerdi = forgjenger1.getDistanse() + vKant.getVekt();
        if (forgjenger2.getDistanse() > nyVerdi)
        {
            forgjenger2.setDistanse(nyVerdi);
            forgjenger2.setForgjenger(node);
            this._heap.prioritetOpp(vKant.getNode().getHeapIndex(), nyVerdi);
        }
    }
}

/**
 * Basert på eksempler i boka
 */
class Heap
{
    private int _lengde;
    private Node[] _noder;

    public Heap(Node[] noder, int startNode)
    {
        this._lengde = noder.length;
        this._noder = noder;
        this.fiksHeap(startNode);
        for (int i = 0; i < this._lengde; i++)
        {
            this._noder[i].setHeapIndex(i);
        }
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

    public Node hentMin()
    {
        Node min = this.getNoder()[0];
        this.bytt(this._noder, 0, --this._lengde);
        this.fiksHeap(0);
        return min;
    }

    public void lagHeap()
    {
        int posisjon = this.getLengde() / 2;
        while (posisjon-- > 0)
        {
            fiksHeap(posisjon);
        }
    }

    public void prioritetOpp(int posisjon, int nyVerdi)
    {
        int foreldrenode;
        ((Forgjenger)this.getNoder()[posisjon].getForgjenger()).setDistanse(nyVerdi); //Setter den nye korteste veien til noden fra start
        while (posisjon > 0 && ((Forgjenger)this.getNoder()[posisjon].getForgjenger()).getDistanse() < ((Forgjenger)this.getNoder()[foreldrenode = over(posisjon)].getForgjenger()).getDistanse()) //Hvis veien er kortere en forelderen
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
            if (hoyre < this.getLengde() && ((Forgjenger)this.getNoder()[hoyre].getForgjenger()).getDistanse() < ((Forgjenger)this.getNoder()[venstre].getForgjenger()).getDistanse())
            {
                venstre = hoyre;
            }
            if (((Forgjenger)this.getNoder()[venstre].getForgjenger()).getDistanse() < ((Forgjenger)this.getNoder()[posisjon].getForgjenger()).getDistanse())
            {
                this.bytt(this.getNoder(), posisjon, venstre);
                this.fiksHeap(venstre);
            }
        }
    }

    private void bytt(Node[] noder, int i, int j)
    {
        Node mellom = noder[i];
        noder[i] = noder[j];
        noder[i].setHeapIndex(i);
        noder[j] = mellom;
        noder[j].setHeapIndex(j);
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
    private int _heapIndex;

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

/**
 * Basert på boka, side 191
 */
class VKant extends Kant
{
    private int _vekt;

    public VKant(Node pekerTil, Kant nesteKant, int vekt)
    {
        super(pekerTil, nesteKant);
        this._vekt = vekt;
    }

    public int getVekt()
    {
        return this._vekt;
    }
}