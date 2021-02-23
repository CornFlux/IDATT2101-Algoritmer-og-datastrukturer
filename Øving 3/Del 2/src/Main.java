import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main
{
    //h d l b f j n a c e g i k m o
    public static void main(String[] args)
    {

        Utskrift utskrift = new Utskrift();
        Scanner input = new Scanner(System.in);
        String[] ord = input.nextLine().split(" "); //Tar inn ordene
        BinaryTree tre = new BinaryTree();

        for (String s : ord) //Setter inn ordene iden rekkefløgen de kom inn i
        {
            tre.settInn(s);
        }
        tre.print(utskrift); //Printer treets fire øverste lag


    }
}

class BinaryTree
{
    private TreNode _rot;

    public BinaryTree()
    {
        this._rot = null;
    }

    public TreNode getRot()
    {
        return this._rot;
    }

    public void setRot(TreNode nyRot)
    {
        this._rot = nyRot;
    }

    public boolean tomt()
    {
        return this._rot == null;
    }

    /**
     * Basert på kode i boka, side 123
     * @param nyNode
     */
    public void settInn(String nyNode)
    {
        //nyNode er sorteringsnøkkelen
        TreNode rot = this._rot;
        if (this.tomt())
        {
            this._rot = new TreNode(nyNode, null, null, null);
            return;
        }
        String sammenligningsKey = "";
        TreNode foreldreNode = null;
        while (rot != null)
        {
            foreldreNode = rot;
            sammenligningsKey = rot.getOrd();

            if (nyNode.compareToIgnoreCase(sammenligningsKey) < 0)
            {
                rot = rot.getVenstre();
            }
            else
            {
                rot = rot.getHoyre();
            }

        }

        if (nyNode.compareToIgnoreCase(sammenligningsKey) < 0)
        {
            foreldreNode.setVenstre(new TreNode(nyNode, null, null, foreldreNode));
        }
        else
        {
            foreldreNode.setHoyre(new TreNode(nyNode, null, null, foreldreNode));
        }

    }

    /**
     * Henter alle verdiene i treet ved hjelp av nivåtraversering
     * @return
     */
    public String[] getVerdier()
    {
        String[] output = new String[15];
        Queue queue = new Queue(15); //Det kan maksimalt være 15 objekter i de fire første lagene i et binærtre
        queue.leggIQueue(this.getRot());
        int i = -1;
        while (!queue.tom())
        {
            i++;
            TreNode denne = queue.getNesteIQueue();
            if (denne != null)
            {
                if (i < 15 && denne.finnDybde() < 5)
                {
                    output[i] = denne.getOrd();
                }
                queue.leggIQueue(denne.getVenstre());
                queue.leggIQueue(denne.getHoyre());
            }
        }
        return output;
    }

    /**
     * Kaller en behandlingsmetode for å printe verdiene i treet på riktig format
     * @param behandler
     */
    public void print(IBehandler behandler)
    {
        behandler.behandle(this);
    }
}

/**
 * Basert på kode i boka, side 116
 */
class TreNode
{
    private String _ord;
    private TreNode _venstre;
    private TreNode _hoyre;
    private TreNode _forelder;

    public TreNode(String ord, TreNode venstre, TreNode hoyre, TreNode forelder)
    {
        this._ord = ord;
        this._venstre = venstre;
        this._hoyre = hoyre;
        this._forelder = forelder;
    }

    //Getter-metoder
    public String getOrd()
    {
        return this._ord;
    }

    public TreNode getVenstre()
    {
        return this._venstre;
    }

    public TreNode getHoyre()
    {
        return this._hoyre;
    }

    public TreNode getForelder()
    {
        return this._forelder;
    }

    //Setter-metoder
    public void setOrd(String nyttOrd)
    {
        this._ord = nyttOrd;
    }

    public void setVenstre(TreNode nyVenstre)
    {
        this._venstre = nyVenstre;
    }

    public void setHoyre(TreNode nyHoyre)
    {
        this._hoyre = nyHoyre;
    }

    public void setForelder(TreNode nyForelder)
    {
        this._forelder = nyForelder;
    }

    /**
     * Basert på kode i boka, side 118
     * @return
     */
    public int finnDybde()
    {
        TreNode node = this;
        int dybde = -1;
        while (node != null)
        {
            dybde++;
            node = node.getForelder();
        }
        return dybde;
    }

}

/**
 * Basert på kode i boka, side 99
 */
class Queue
{
    private TreNode[] _tabell;
    private int _start;
    private int _slutt;
    private int _antall;

    public Queue(int size)
    {
        this._tabell = new TreNode[size];
    }

    public boolean tom()
    {
        return this._antall == 0;
    }

    public boolean full()
    {
        return this._antall == this._tabell.length;
    }

    public void leggIQueue(TreNode nyttElement)
    {
        if (this.full())
        {
            return;
        }
        this._tabell[this._slutt] = nyttElement;
        this._slutt = (this._slutt + 1) % this._tabell.length;
        this._antall++;
    }

    public TreNode getNesteIQueue()
    {
        if(!this.tom())
        {
            TreNode element = this._tabell[this._start];
            this._start = (this._start + 1) % this._tabell.length;
            this._antall--;
            return element;
        }
        return null;
    }

    public TreNode sjekkQueue()
    {
        if (!this.tom())
        {
            return this._tabell[this._start];
        }
        return null;
    }
}

/**
 * Basert på eksempler i boka (nivåordentraversering), side 120
 */
interface IBehandler
{
    void behandle(BinaryTree tre);
}

/**
 * Tar seg av formateringen og "styling'en" av en god representasjon av
 * et binærtre i terminalen (konsolen).
 * Klassen lager et komplett binært tre med 4 lag og bytter ut verdiene
 * med verdier ffra et ordentlig tre, og deretter printer dummy-treet
 * Dette pga. lettere formatering av et komplett tre
 */
class Utskrift implements IBehandler
{
    BinaryTree dummy = new BinaryTree();


    public Utskrift()
    {
        this.fillTreeNull();
    }

    /**
     * Tar et BinaryTree og kaller fillTree() og printDummy().
     * Det er denne metoden som blir kalt fra andre klasser
     * @param tre
     */
    @Override
    public void behandle(BinaryTree tre)
    {
        this.fillTree(tre);
        this.printDummy();


    }

    /**
     * Denne metoden står for selve utformingen av treet,
     * den tar en TreNode og basert på dybde og en gitt bredde
     * finner ut hva som skal printes
     * @param node
     */
    private void print(TreNode node)
    {
        int bredde = 128;
        int dybde = node.finnDybde();
        String ord = node.getOrd();
        if (ord != null)
        {
            for (int i = 0; i < (((bredde / Math.pow(2, dybde)) - ord.length()) / 2); i++)
            {
                System.out.print(" ");
            }
            System.out.print(ord);
            for (int i = 0; i < (((bredde / Math.pow(2, dybde)) - ord.length()) / 2); i++)
            {
                System.out.print(" ");
            }
        }
        else
        {
            for (int i = 0; i < (bredde / Math.pow(2, dybde)); i++)
            {
                System.out.print(" ");
            }
        }
        if (node.getForelder() == null)
        {
            System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------");
        }
        else if(this.hoyreElement(node))
        {
            System.out.println("\n--------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     * Skjekker om TreNode'en er et element helt til høyre
     * i sin dybde
     * @param node
     * @return
     */
    private boolean hoyreElement(TreNode node)
    {
        if (node.getForelder().getHoyre() == node)
        {
            if (node.getForelder().getForelder() == null)
            {
                return true;
            }
            return this.hoyreElement(node.getForelder());
        }
        else
        {
            return false;
        }
    }

    /**
     * Ddenne metoden nivåordenstraverserer gjennom
     * "dummy"-treet og kaller print()-metoden på elementene
     */
    public void printDummy()
    {
        Queue queue = new Queue(15); //Det kan maksimalt være 15 objekter i de fire første lagene i et binærtre
        queue.leggIQueue(this.dummy.getRot());
        while (!queue.tom())
        {
            TreNode denne = queue.getNesteIQueue();
            if (denne != null)
            {
                this.print(denne);
                queue.leggIQueue(denne.getVenstre());
                queue.leggIQueue(denne.getHoyre());
            }
        }
    }

    /**
     * Setter verdiene i dummy-treet lik verdiene fra et annet tre
     * @param tre
     */
    public void fillTree(BinaryTree tre)
    {
        this.nivaaordenstraverseringFlereVerdier(tre.getRot(), tre.getVerdier());
    }

    /**
     * Lager et fult fire nivå binærtre, dummy.
     * Setter så alle verdiene i dummy lik null.
     */
    private void fillTreeNull()
    {
        String[] ord = {"h", "d", "l", "b", "f", "j", "n", "a", "c", "e", "g", "i", "k", "m", "o"};
        for (String s : ord)
        {
            this.dummy.settInn(s);
        }
        this.nivaaordenstraverseringEnVerdi(this.dummy.getRot(), null);
    }

    /**
     * Setter alle verdiene i et tre lik null
     * @param node
     * @param nyVerdi
     */
    private void nivaaordenstraverseringEnVerdi(TreNode node, String nyVerdi)
    {
        Queue queue = new Queue(15); //Det kan maksimalt være 15 objekter i de fire første lagene i et binærtre
        queue.leggIQueue(node);
        while (!queue.tom())
        {
            TreNode denne = queue.getNesteIQueue();
            if (denne != null)
            {
                denne.setOrd(nyVerdi);
                queue.leggIQueue(denne.getVenstre());
                queue.leggIQueue(denne.getHoyre());
            }
        }
    }

    /**
     * Setter verdiene i et tre lik en liste med innsedte verdier
     * @param node
     * @param nyeVerdier
     */
    private void nivaaordenstraverseringFlereVerdier(TreNode node, String[] nyeVerdier)
    {
        Queue queue = new Queue(15); //Det kan maksimalt være 15 objekter i de fire første lagene i et binærtre
        Queue queueDummy = new Queue(15);
        queue.leggIQueue(node);
        queueDummy.leggIQueue(this.dummy.getRot());
        int i = -1;
        while (!queue.tom())
        {
            i++;
            TreNode denne = queue.getNesteIQueue();
            TreNode dummyDenne = queueDummy.getNesteIQueue();
            if (denne != null)
            {
                if (i < 15 && dummyDenne != null)
                {
                    dummyDenne.setOrd(nyeVerdier[i]);
                    queue.leggIQueue(denne.getVenstre());
                    queue.leggIQueue(denne.getHoyre());
                    queueDummy.leggIQueue(dummyDenne.getVenstre());
                    queueDummy.leggIQueue(dummyDenne.getHoyre());
                }
            }
        }
    }
}