import javax.swing.*;
import java.util.HashMap;
import java.util.Random;

public class Main
{

    public static void main(String[] args)
    {
        long start, slutt, avg = 0, kol = 0;
        Hashtabell hashtabell = new Hashtabell();
        HashMap hashMap = new HashMap();
        int[] data = getRandomList();
        System.out.println("Liste generert");
        start = System.nanoTime();
        hashtabell = new Hashtabell();
        data = getRandomList();
        start = System.nanoTime();
        for (int i : data)
        {
            hashtabell.leggInn(i);
        }
        slutt = System.nanoTime();
        avg = slutt - start;
        System.out.println("Vår hashtabell tid: " + ((double)(avg) / (double)1000000000) + "sec");
        //System.out.println("Antall kollisjoner: " + hashtabell.getAntallKollisjoner());
        //System.out.println("Lastfaktor: " + hashtabell.getLastFaktor());


        hashMap = new HashMap();
        data = getRandomList();
        start = System.nanoTime();
        for (int i : data)
        {
            hashMap.put(Integer.hashCode(i), i);
        }
        slutt = System.nanoTime();
        avg = (slutt - start);

        System.out.println("Java sin HashMap tid: " + ((double)(avg) / (double)1000000000) + "sec");
    }

    public static int[] getRandomList()
    {
        Random random = new Random();
        int[] returListe = new int[10000000];
        for (int i = 0; i < returListe.length; i++)
        {
            int newValue = random.nextInt();
            if (newValue != Integer.MIN_VALUE)
            {
                returListe[i] = newValue;
            }
            else
            {
                i--;
            }
        }
        return returListe;
    }
}

/**
 * Store deler av klassen er basert på eksempler fra læreboka, kapittel 8
 */
class Hashtabell
{
    private int _antallElementer = 0;
    private int _size = 13333339; // Setter 'this._size' til et 13333339 (primtall) som gir overhead på ~0.25%
    private Integer[] _tabell = new Integer[this._size];
    private int _antallKollisjoner = 0;

    public int getSize()
    {
        return this._size;
    }

    public void setSize(int newSize)
    {
        this._size = newSize;
    }

    public Integer[] getTabell()
    {
        return this._tabell;
    }

    public void setTabell(Integer[] nyTabell)
    {
        this._tabell = nyTabell;
    }

    public int getAntallElementer()
    {
        return this._antallElementer;
    }

    public void nyttElement()
    {
        this._antallElementer++;
    }

    public int getAntallKollisjoner()
    {
        return this._antallKollisjoner;
    }

    private void leggTilKollisjon(int antall)
    {
        this._antallKollisjoner += antall;
    }

    /**
     * Legger inn en verdi.
     * Om ledig på posisjon hash1(verdi), sett inn der og returner posisjonen
     * ellers start probing, og returner pos hvis den finnes,
     * ellers returneres -1 hvis hashtabellen er full
     * @param verdi
     * @return
     */
    public int leggInn(int verdi)
    {
        int hash1 = hash1(verdi);
        //Sjekker om hash1 gir en ledig plass
        if (this._tabell[hash1] == null)
        {
            this._tabell[hash1] = verdi;
            this.nyttElement();
            return hash1;
        }
        int hash2 = hash2(verdi), pos = hash1;
        for (int i = 0; i < this._size; i++)
        {
            pos = this.probe(pos, hash2);
            if (this._tabell[pos] == null)
            {
                this._tabell[pos] = verdi;
                this.leggTilKollisjon(i + 1);
                this.nyttElement();
                return pos;
            }
        }
        this.leggTilKollisjon(this._size);
        return -1; //Hvis tabellen er full
    }

    /**
     * Søker etter en verdi.
     * Om funnet på posisjon hash1(verdi), returneres posisjonen hash1(verdi)
     * ellers start probing, og returner pos hvis den finnes,
     * ellers returneres -1 hvis verdien ikke finnes i hashtabellen
     * @param verdi
     * @return
     */
    public int findPos(int verdi)
    {
        int hash1 = hash1(verdi);
        if (this._tabell[hash1] == verdi)
        {
            return hash1;
        }
        int hash2 = hash2(verdi), pos = hash1;
        for (int i = 0; i < this._size; i++)
        {
            pos = this.probe(pos, hash2);
            if (this._tabell[pos] == verdi)
            {
                return pos;
            }
        }
        return -1; //Hvis elemente ikke finnes
    }

    /**
     * Primær hash-funksjon
     * En restDiv hash
     * @param verdi
     * @return
     */
    private int hash1(int verdi)
    {
        return verdi < 0 ? -verdi % this._size : verdi % this._size;
    }

    /**
     * Sekundær hash-funksjon
     * En restDiv hash
     * @param verdi
     * @return
     */
    private int hash2(int verdi)
    {
        return verdi < 0 ? -(verdi % 1370051) + 1 : (verdi % 1370051) + 1;
        //return 1174681 - (verdi % 1174681);
    }


    /**
     * Dobble hash probing
     * Basert på eksempel i boka, side 163
     * @param pos
     * @param hash2
     * @return
     */
    private int probe(int pos, int hash2)
    {
        return (pos + hash2) % this._size;
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
            returString += i + ": " + this._tabell[i] + "\n";
        }
        return returString;
    }
}
