import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{

    public static void main(String[] args) throws IOException
    {
        Main main = new Main();
        ArrayList<String> navnListe = main.importNavn(); //Henter og formaterer alle navnene fra fila

        HashtabellRestDiv hashtabellRestDiv = new HashtabellRestDiv(navnListe.size());
        HashtabellHeltallMulti hashtabellHeltallMulti = new HashtabellHeltallMulti(navnListe.size());

        for (String s : navnListe) //Legger inn alle navnene i begge hashtabellene
        {
            hashtabellRestDiv.leggInn(s);
            hashtabellHeltallMulti.leggInn(s);
        }
        System.out.println("Størelsen på listen med navn sendt inn: " + navnListe.size());
        System.out.println("Valgt størelse i restDiv hash'en: " + hashtabellRestDiv.getSize());
        System.out.println("Valgt størelse i heltallMulti hash'en: " + hashtabellHeltallMulti.getSize());
        System.out.println("\nInnhold i restDiv hash'en:\n\n" + hashtabellRestDiv.printListeInnhold());
        System.out.println("Innhold i heltallMulti hash'en:\n\n" + hashtabellHeltallMulti.printListeInnhold());

        Scanner input = new Scanner(System.in);
        System.out.println("Hashtabell med restdivisjon\nHvem leter du etter? (Skriv inn fult navn, fornavn så etternavn)\nSkriv '5' for å slutte\n");
        String search = input.nextLine();
        while (!search.equals("5"))
        {
            if (hashtabellRestDiv.finnElement(search))
            {
                System.out.println("'" + search + "' er i hastabellen\n");
            }
            else
            {
                System.out.println("'" + search + "' er ikke i hastabellen\n");
            }
            System.out.println("Hvem leter du etter? (Skriv inn fult navn, fornavn så etternavn)\nSkriv '5' for å slutte\n");
            search = input.nextLine();
        }

        while (!search.equals("6"))
        {
            System.out.println("Hashtabell med heltallsmultiplikasjon\nHvem leter du etter? (Skriv inn fult navn, fornavn så etternavn)\nSkriv '6' for å slutte\n");
            search = input.nextLine();
            if (hashtabellHeltallMulti.finnElement(search))
            {
                System.out.println("'" + search + "' er i hastabellen\n");
            }
            else
            {
                System.out.println("'" + search + "' er ikke i hastabellen\n");
            }
        }
        System.out.println("Ferdig...\n");

        System.out.println("Lastfaktor (restDiv): " + hashtabellRestDiv.getLastFaktor());
        System.out.println("Kollisjoner restDiv (under innsetting): " + hashtabellRestDiv.getKollisjonerUnderInnsetting().size());
        System.out.println("Kollisjoner per person restDiv (under innsetting): " + (double)hashtabellRestDiv.getKollisjonerUnderInnsetting().size() / (double)hashtabellRestDiv.getAntallElementer() + "\n");
        System.out.println("Lastfaktor (heltallMulti): " + hashtabellHeltallMulti.getLastFaktor());
        System.out.println("Kollisjoner heltallMulti (under innsetting): " + hashtabellHeltallMulti.getKollisjonerUnderInnsetting().size());
        System.out.println("Kollisjoner per person heltallMulti (under innsetting): " + (double)hashtabellHeltallMulti.getKollisjonerUnderInnsetting().size() / (double)hashtabellHeltallMulti.getAntallElementer() + "\n");

        System.out.println("\nKollisjoner under innsetting i restDiv (kolidert med | innsettings verdi):\n" + hashtabellRestDiv.getKollisjonerUnderInnsetting());
        System.out.println("\nKollisjoner under innsetting i heltallMulti (kolidert med | innsettings verdi):\n" + hashtabellHeltallMulti.getKollisjonerUnderInnsetting());
        System.out.println("\nKollisjoner under søk i restDiv (kolidert med | søke verdi):\n" + hashtabellRestDiv.getKollisjonerUnderSok() + "\n");
        System.out.println("Kollisjoner under søk i heltallMulti (kolidert med | søke verdi):\n" + hashtabellHeltallMulti.getKollisjonerUnderSok() + "\n");


    }

    public static ArrayList<String> importNavn() throws IOException
    {
        ArrayList<String> navnListe = new ArrayList<>();
        File file = new File("src/Klasseliste.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String navn;
        while ((navn = br.readLine()) != null)
        {
            navnListe.add(navn);
        }
        return navnListe;
    }

}

class HashtabellRestDiv extends Hashtabell
{
    public HashtabellRestDiv(int size)
    {
        super(0, (int)(size * 1.25), new EnkelLenke[(int)(size * 1.25)]);
        int sjekk = findPrimeBigger(size, 1.25); //Sikrer at vi ikke får overhead på mer enn 25%
        if (sjekk != -1)
        {
            super.setSize(sjekk);
            super.setTabell(new EnkelLenke[sjekk]);
        }
        super.opprettTabell();
    }

    /**
     * Sjekker om den insendte verdien ligger i hashtabellen
     * ser bortifra mellomrom og kommategn
     * Returnerer 'true' hvis den finner noe, eller 'false'
     * @param navn
     * @return
     */
    public boolean finnElement(String navn)
    {
        ArrayList<String> nyeKollisjoner = new ArrayList<>();
        int index = this.divHash(this.stringToInt(navn), super.getSize());
        String sokNavn = navn.replace(",", "").replace(" ", "");
        for (int i = 0; super.getTabell()[index].finnNodeVedIndex(i) != null; i++)
        {
            if (super.getTabell()[index].finnNodeVedIndex(i).getVerdi().replace(",", "").replace(" ", "").equals(sokNavn))
            {
                super.leggTilKollisjonerUnderSok(nyeKollisjoner);
                return true;
            }
            nyeKollisjoner.add(super.getTabell()[index].finnNodeVedIndex(i).getVerdi() + " | " + navn);
        }
        super.leggTilKollisjonerUnderSok(nyeKollisjoner);
        return false;
    }



    /**
     * Legger inn en ny String-verdi i hashtabbelen
     * @param nyVerdi
     * @return
     */
    public int leggInn(String nyVerdi)
    {
        int index = this.divHash(this.stringToInt(nyVerdi), super.getSize());
        super.leggTilKollisjonerUnderInnsetting(super.getTabell()[index].settInnBakerst(nyVerdi));
        super.setAntallElementer(super.getAntallElementer() + 1);
        return index;
    }

    /**
     * Hash-metode basert på restdivisjons, returnerer kalkulertverdi
     * hvis size er null eller mindre vil den returnere -1
     * Basert på boka, side 157
     * @param key
     * @param size
     * @return
     */
    public int divHash(int key, int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size can't be less than 1");
        }
        return key % size;
    }

    /**
     * Finner første primtall større enn (eller lik) innsendt verdi,
     * som gir ønsket overhead
     * Sjekker verdier opp til grensen av en int (nesten), hvis ikke noen passende verdier er funnet,
     * returneres -1
     * @param verdi
     * @param overheadMax
     * @return
     */
    private int findPrimeBigger(int verdi, double overheadMax)
    {
        // Starter letingen etter et primtall ved verdien som gir oss en minimum ønsket overhead vi skal overskride
        // Hvis innsendt verdi er et partall legger vi til 1, så start alltid er et oddetall
        int start = ((int)(verdi / overheadMax) % 2 == 0) ? (int)(verdi / overheadMax) + 1: (int)(verdi / overheadMax);
        int prime;
        int forrgiePrime = -1;
        for (int i = start; i < 2147483000; i += 2) // Skjekker nesten til slutten av int spektere
        {
            prime = i;
            for (int j = 3; j < (i / 2) + 1; j += 2)
            {
                if (i % j == 0)
                {
                    prime = -1;
                }
            }
            if (prime != -1)
            {
                if ((double)prime / (double) verdi > overheadMax)
                {
                    return forrgiePrime;
                }
                forrgiePrime = prime;
            }
        }
        return -1;
    }
}

class HashtabellHeltallMulti extends Hashtabell
{
    private int _closestValueIndex;
    private final int _multiVerdi = 1327217885; //Hentet fra forelesning

    public HashtabellHeltallMulti(int size)
    {
        super(0, size, new EnkelLenke[size]);
        int bigger = findBiggerTwoPow(size), smaller = bigger;
        smaller >>= 1; // Finner forrige toerpotensverdi
        int sjekk;

        if (bigger == -1)
        {
            sjekk = (int)this.potens(30, 2);
            // Enste tilfelle hvor bigger er -1 og smaller ikke er -1
            // er når smaller er 2^30 og da vil dette vil være den siste brukbare størelsen
            // vi kan ha på hashtabellen, da neste verdi er grenesen til en int
        }
        else if (((double)size / (double)bigger) >= 0.6)
        {
            sjekk = bigger; // hvis potensen under verdien er nærmest verdien
        }
        else
        {
            sjekk = smaller; // motsatt
        }
        super.setSize(sjekk);
        super.setTabell(new EnkelLenke[sjekk]);

        super.opprettTabell();
        findLog2(sjekk);
    }

    public boolean finnElement(String navn)
    {
        ArrayList<String> nyeKollisjoner = new ArrayList<>();
        int index = this.multiHash(this.stringToInt(navn), this._closestValueIndex);
        String sokNavn = navn.replace(",", "").replace(" ", "");
        for (int i = 0; super.getTabell()[index].finnNodeVedIndex(i) != null; i++)
        {
            if (super.getTabell()[index].finnNodeVedIndex(i).getVerdi().replace(",", "").replace(" ", "").equals(sokNavn))
            {
                super.leggTilKollisjonerUnderSok(nyeKollisjoner);
                return true;
            }
            nyeKollisjoner.add(super.getTabell()[index].finnNodeVedIndex(i).getVerdi() + " | " + navn);
        }
        super.leggTilKollisjonerUnderSok(nyeKollisjoner);
        return false;
    }

    /**
     * Legger inn en ny String-verdi i hashtabbelen
     * @param nyVerdi
     * @return
     */
    public int leggInn(String nyVerdi)
    {
        int index = this.multiHash(this.stringToInt(nyVerdi), this._closestValueIndex);
        super.leggTilKollisjonerUnderInnsetting(super.getTabell()[index].settInnBakerst(nyVerdi));
        super.setAntallElementer(super.getAntallElementer() + 1);
        return index;
    }

    /**
     * Hash-metode basert på heltallsmultiplikasjon
     * Basert på boka, side 159 og forelesingen
     * @param key
     * @param x
     * @return
     */
    private int multiHash(int key, int x)
    {
        return key * this._multiVerdi >>> (31 - x) & (super.getTabell().length - 1);
    }

    /**
     * Finner log2 av innsendt verdi, hvis det er et helltall mindre enn 31
     * ellers returner returner den toerpotensen som var nærest
     * @param verdi
     * @return
     */
    private void findLog2(int verdi)
    {
        int toerpotens = 2;
        int closestValue = Math.abs(toerpotens - verdi);
        this._closestValueIndex = 1;
        for (int i = 1; i < 31; i++)
        {
            if (toerpotens == verdi)
            {
                this._closestValueIndex = i;
                break;
            }
            if (Math.abs(toerpotens - verdi) < closestValue)
            {
                closestValue = Math.abs(toerpotens - verdi);
                this._closestValueIndex = i;
            }
            toerpotens <<= 1;
        }
    }

    /**
     * Finner første toer potens større enn (eller lik) innsendt verdi
     * Returnerer denne verdien
     * @return
     */
    private int findBiggerTwoPow(int verdi)
    {
        int toerpotens = 2;
        for (int i = 1; i <= 30; i++) // 2^31 er grensen for hva en int kan representere og metoden bryter ned ved tall større og går i en evig loop, ergo stopper den på 30
        {
            if (toerpotens >= verdi)
            {
                return toerpotens; // returnerer potensen til toeren
            }
            toerpotens <<= 1;
        }
        return -1;
    }
}

/**
 * Store deler av klassen er basert på eksempler fra læreboka, kapittel 8
 */
abstract class Hashtabell
{
    private int _antallElementer = 0;
    private int _size;
    private EnkelLenke[] _tabell;
    private ArrayList<String> _kollisjonerUnderInnsetting = new ArrayList<>();
    private ArrayList<String> _kollisjonerUnderSok = new ArrayList<>();


    public Hashtabell(int antallElementer, int size, EnkelLenke[] tabell)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("Size can't be less than 1");
        }
        this._antallElementer = antallElementer;
        this._size = size;
        this._tabell = tabell;
    }

    public int getSize()
    {
        return this._size;
    }

    public void setSize(int newSize)
    {
        this._size = newSize;
    }

    public EnkelLenke[] getTabell()
    {
        return this._tabell;
    }

    public void setTabell(EnkelLenke[] nyTabell)
    {
        this._tabell = nyTabell;
    }

    public int getAntallElementer()
    {
        return this._antallElementer;
    }

    public void setAntallElementer(int nyAntallElementer)
    {
        this._antallElementer = nyAntallElementer;
    }

    public ArrayList<String> getKollisjonerUnderInnsetting()
    {
        return this._kollisjonerUnderInnsetting;
    }

    public void leggTilKollisjonerUnderInnsetting(ArrayList<String> nyKollisjonerUnderInnsetting)
    {
        if (nyKollisjonerUnderInnsetting != null)
        {
            this._kollisjonerUnderInnsetting.addAll(nyKollisjonerUnderInnsetting);
        }
    }

    public ArrayList<String> getKollisjonerUnderSok()
    {
        return this._kollisjonerUnderSok;
    }

    public void leggTilKollisjonerUnderSok(ArrayList<String> nyKollisjonerUnderSok)
    {
        if (nyKollisjonerUnderSok != null)
        {
            this._kollisjonerUnderSok.addAll(nyKollisjonerUnderSok);
        }
    }

    /**
     * Setter opp tabellen så alle Enkellenkene er initialisert
     */
    public void opprettTabell()
    {
        for (int i = 0; i < this._tabell.length; i++)
        {
            this._tabell[i] = new EnkelLenke();
        }
    }

    /**
     * Beregner og returnerer lastfaktoren
     * Basert på boka, side 155
     * @return
     */
    public double getLastFaktor()
    {
        return (double)this._antallElementer / (double)this._size;
    }

    /**
     * Gjør en string verdi om til en int verdi,
     * basert på bokstavenes plassering og int verdi når parse'et
     * @return
     */
    public int stringToInt(String verdi)
    {
        verdi = verdi.replace(",", "").replace(" ", "");
        char[] bokstaver = verdi.toCharArray();
        int sum = 0;
        int moduloFaktor = 6;
        for (int i = 0; i < bokstaver.length; i++)
        {
            sum += (potens(i % moduloFaktor,7)) * (int)bokstaver[i];
            // i % 3 er for å sikre at int verdien ikke blir for stor og der av at
            // alle verdiene havner på samme sted, da sum vil være uendret når den blir for stor og ikke kan legges mer til på
        }
        return sum;
    }

    /**
     * Beste potensmetoden fra øving 1
     * @param n
     * @param x
     * @return
     */
    public double potens(int n, double x)
    {
        if (n <= 0)
        {
            return 1;
        }
        return n % 2 == 0 ? potens(n / 2, x * x) : x * potens((n - 1) / 2, x * x);
    }


    public String printListeInnhold()
    {
        String returString = "";

        for (int i = 0; i < this._tabell.length; i++)
        {
            returString += "(" + i + ") " + this._tabell[i].toString() + "\n";
        }
        return returString;
    }
}


/**
 * En node representasjon i en enkellenkaliste, med verdi "String"
 * Basert på læreboka, side 84
 */
class Node
{
    private String _verdi;
    private Node _neste;

    public Node(String verdi, Node neste)
    {
        this._verdi = verdi;
        this._neste = neste;
    }

    public String getVerdi()
    {
        return this._verdi;
    }

    public void setVerdi(String nyVerdi)
    {
        this._verdi = nyVerdi;
    }

    public Node getNeste()
    {
        return this._neste;
    }

    public void setNeste(Node neste)
    {
        this._neste = neste;
    }

}

/**
 * Basert på læreboka, side 86
 */
class EnkelLenke
{
    private Node _hode = null;
    private int _antallElementer = 0;

    public Node getHode()
    {
        return this._hode;
    }

    public int getAntallElementer()
    {
        return this._antallElementer;
    }

    public void settInnFremst(String verdi)
    {
        this._hode = new Node(verdi, this._hode);
        this._antallElementer++;
    }

    public ArrayList<String> settInnBakerst(String verdi)
    {
        ArrayList<String> kollisjoner = new ArrayList<>();
        if (this._hode != null)
        {
            Node denne = this._hode;
            kollisjoner.add(denne.getVerdi() + " | " + verdi);
            while (denne.getNeste() != null)
            {
                denne = denne.getNeste();
                kollisjoner.add(denne.getVerdi() + " | " + verdi);
            }
            denne.setNeste(new Node(verdi, null));
            this._antallElementer++;
            return kollisjoner;
        }
        else
        {
            this._hode = new Node(verdi, null);
            this._antallElementer++;
            return null;
        }
    }

    public Node fjernNode(Node node)
    {
        Node forrige = null;
        Node denne = this._hode;

        while (denne != null && denne != node)
        {
            forrige = denne;
            denne = denne.getNeste();
        }
        if (denne != null)
        {
            if (forrige != null)
            {
                forrige.setNeste(denne.getNeste());
            }
            else
            {
                this._hode = denne.getNeste();
            }
            denne.setNeste(null);
            this._antallElementer--;
            return denne;
        }
        else
        {
            return null;
        }
    }

    public Node finnNodeVedIndex(int index)
    {
        Node denne = this._hode;
        if (index < this._antallElementer)
        {
            for (int i = 0; i < index; i++)
            {
                denne = denne.getNeste();
            }
            return denne;
        }
        else
        {
            return null;
        }
    }

    public void slettAlle()
    {
        this._hode = null;
        this._antallElementer = 0;
    }

    public String toString()
    {
        String returnString = "Verdier i lenke: ";
        Node denne = this._hode;
        for (int i = 0; denne != null; i++)
        {
            returnString += denne.getVerdi() + " || ";
            denne = denne.getNeste();
        }
        return returnString;
    }
}
