import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import static java.util.Arrays.sort;

public class Main
{
    public static void main(String[] args)
    {
        long startTid = 0, sluttTid = 0, totalTid1 = 0, totalTid2 = 0;

        for (int i = 0; i < 10; i++) {
            int[] testListe = genererListe(2, 100000000); //Liste for referanse
            double testListeSum = hentSum(testListe); //Summen av referanse listen

            int[] quick1liste = deepCopy(testListe); //Liste for quicksort med et delingstall
            double sumAvQuicksort1 = hentSum(quick1liste); //Summen av listen til quicksort1 før sortering
            startTid = System.nanoTime(); //Starter tidtaking
            quicksort(quick1liste, 0, quick1liste.length - 1); //Sorterer listen med quicksort1
            sluttTid = System.nanoTime(); //Slutter tidtaking
            totalTid1 = (sluttTid - startTid) / 1000000; //Regner ut tid brukt i millisekunder
            double sumAvQuicksort1etter = hentSum(quick1liste); //Summen av listen til quicksort1 etter sortering

            int[] quick2liste = deepCopy(testListe); //Liste for quicksort med to delingstall
            double sumAvQuicksort2 = hentSum(quick2liste); //Summen av listen til quicksort2
            startTid = System.nanoTime(); //Starter tidtaking
            dualPivotQuickSort(quick2liste, 0, quick2liste.length - 1);
            sluttTid = System.nanoTime(); //Slutter tidtaking
            totalTid2 = (sluttTid - startTid) / 1000000; //Regner ut tid brukt i millisekunder
            double sumAvQuicksort2etter = hentSum(quick1liste); //Summen av listen til quicksort2 etter sortering


            //Tester sum av quicksort1
            boolean sumtest1 = false;
            if (sumAvQuicksort1 == sumAvQuicksort1etter) {
                sumtest1 = true;
            }
            System.out.println("Test sum av quicksort1: " + sumtest1);
            System.out.println("Test rekkefølge av quicksort1: " + testRekkefolge(quick1liste));
            System.out.println("Tid quicksort1: " + totalTid1 + " millisekunder\n");

            //Tester sum av quicksort2
            boolean sumtest2 = false;
            if (sumAvQuicksort2 == sumAvQuicksort2etter) {
                sumtest2 = true;
            }
            System.out.println("Test sum av quicksort2: " + sumtest2);
            System.out.println("Test rekkefølge av quicksort2: " + testRekkefolge(quick2liste));
            System.out.println("Tid quicksort2: " + totalTid2 + " millisekunder\n");
        }
    }


    /**
     * int valg: 1 = tilfeldige tall, 2 = mange duplikater, 3 = sortert
     * @param valg
     * @return
     */
    public static int[] genererListe(int valg, int antallElementer) throws IllegalArgumentException
    {
        Random random = new Random();
        int[] returListe = new int[antallElementer];

        if ((valg == 1 || valg == 2 || valg == 3) && antallElementer > 0)
        {
            switch (valg)
            {
                case 1:
                    for (int i = 0; i < returListe.length; i++)
                    {
                        returListe[i] = random.nextInt(antallElementer);
                        //Det og ikke sette en verdi inn her i nextInt() ga så store verdier at sum testen ikke
                        //ble bestått, dette kommer nok av at verdiene da kommer fra hele 'int'-spekteret som er ganske stort,
                        //men som kan gjøre at den mister noe nøyaktighet og det er alt som skal til for å feile sum testen.
                        //Men å sette nextInt(antallElementer) så vil hvert tall gjennomsnittlig forekomme 1 gang,
                        //så dette skal ikke ha så mye å si for tidstestingen.
                    }
                    break;

                case 2:
                    for (int i = 0; i < returListe.length; i++)
                    {
                        //returListe[i] = random.nextInt(antallElementer / 10); //Gjør at hver verdi gjennomsnittlig forekommer 10 ganger
                        returListe[i] = i % 2; //Her vil vi få en liste som er 1,0,1,0,1,0,1,0, .... også videre
                    }
                    break;

                case 3:
                    returListe = genererListe(1, antallElementer);
                    Arrays.sort(returListe); //Sorterer listen med java sin innebygde sorteringsmetode
                    break;
            }
            return returListe;
        }
        else
        {
            throw new IllegalArgumentException("Parametere var ikke gyldig");
        }
    }

    /**
     * Implementeringen av quicksort med et delingstall
     */
    public static void quicksort(int[] t, int v, int h)
    {
        if (h - v > 2)
        {
            int delepos = splitt(t, v, h);
            quicksort(t, v, delepos - 1);
            quicksort(t, delepos + 1, h);
        }
        else
        {
            median3sort(t, v, h);
        }
    }

    /**
     * Splitt metode brukt i quicksort med et delingstall,
     * "Denne metoden finner en delingsverdi, og flytter deretter rundet på tallene
     * i tabellen slik at mindre tall havner på lave indekser og høyere tall på høyere indekser.
     * Til slutt returneres posisjonen hvor delingsverdien havnet." - hentet fra læreboka
     * @param t
     * @param v
     * @param h
     * @return
     */
    public static int splitt(int[] t, int v, int h)
    {
        int iv, ih;
        int m = median3sort(t, v, h);
        int dv = t[m];
        bytt(t, m, h - 1);
        for (iv = v, ih = h - 1;;)
        {
            while (t[++iv] < dv) ;
            while (t[--ih] > dv) ;
            if (iv >= ih)
            {
                break;
            }
            bytt(t, iv, ih);
        }
        bytt(t, iv, h - 1);
        return iv;
    }

    /**
     * Denne motoden sorterer tre tall og returnerer medianen.
     * I tilegg setter den det laveste tallet først i tabellen og
     * det høyeste sist.
     * @param t
     * @param v
     * @param h
     * @return
     */
    public static int median3sort(int[] t, int v, int h)
    {
        int m = (v + h) / 2;
        if (t[v] > t[m])
        {
            bytt(t, v, m);
        }
        if (t[m] > t[h])
        {
            bytt(t, m, h);
            if (t[v] > t[m])
            {
                bytt(t, v, m);
            }
        }
        return m;
    }

    /**
     * Denne metoden bytter to verdier i en tabell.
     * @param t
     * @param i
     * @param j
     */
    public static void bytt(int[] t, int i, int j)
    {
        int k = t[j];
        t[j] = t[i];
        t[i] = k;
    }

    /**
     * Tar en liste og sorterer den, sjekker om rekkefølge er riktig og om summen
     * før og etter sorteringen stemmer.
     * @param liste
     * @return
     */
    public static boolean testRekkefolge(int[] liste)
    {
        for (int i = 0; i < liste.length - 1; i++)
        {
            if (liste[i] > liste[i + 1])
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Beregner summen og returnerer den
     * @param liste
     * @return
     */
    public static double hentSum(int[] liste)
    {
        double sum = 0;
        for (int i : liste)
        {
            sum += i;
        }
        return sum;
    }

    /**
     * Metoden dypkopierer arrayen
     * @param liste
     * @return
     */
    public static int[] deepCopy(int[] liste)
    {
        int[] returListe = new int[liste.length];
        for (int i = 0; i < liste.length; i++)
        {
            returListe[i] = liste[i];
        }
        return returListe;
    }


    //alt under er hentet fra https://www.geeksforgeeks.org/dual-pivot-quicksort/
    //Jeg har kun ryddet opp i syntaks, og enderet det som var anbefalt å gjøre, ellers er det som fra nettsiden

    static void swap(int[] arr, int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void dualPivotQuickSort(int[] arr, int low, int high)
    {
        if (low < high)
        {
            // piv[] stores left pivot and right pivot.
            // piv[0] means left pivot and
            // piv[1] means right pivot
            int[] piv;
            piv = partition(arr, low, high);

            dualPivotQuickSort(arr, low, piv[0] - 1);
            dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
            dualPivotQuickSort(arr, piv[1] + 1, high);
        }
    }

    static int[] partition(int[] arr, int low, int high)
    {
        swap(arr, low, low + (high - low) / 3); //Dette for å unngå et O(n^2) tilfelle (som anbefalt i oppgaveteksen)
        swap(arr, high, high - (high - low) / 3); //Dette for å unngå et O(n^2) tilfelle (som anbefalt i oppgaveteksen)

        if (arr[low] > arr[high])
            swap(arr, low, high);

        // p is the left pivot, and q
        // is the right pivot.
        int j = low + 1;
        int g = high - 1, k = low + 1,
                p = arr[low], q = arr[high];

        while (k <= g)
        {

            // If elements are less than the left pivot
            if (arr[k] < p)
            {
                swap(arr, k, j);
                j++;
            }

            // If elements are greater than or equal
            // to the right pivot
            else if (arr[k] >= q)
            {
                while (arr[g] > q && k < g)
                    g--;

                swap(arr, k, g);
                g--;

                if (arr[k] < p)
                {
                    swap(arr, k, j);
                    j++;
                }
            }
            k++;
        }
        j--;
        g++;

        // Bring pivots to their appropriate positions.
        swap(arr, low, j);
        swap(arr, high, g);

        // Returning the indices of the pivots
        // because we cannot return two elements
        // from a function, we do that using an array.
        return new int[] { j, g };
    }

    // Driver code
    /*
    public static void main(String[] args)
    {
        int[] arr = { 24, 8, 42, 75, 29, 77, 38, 57 };

        dualPivotQuickSort(arr, 0, 7);

        System.out.print("Sorted array: ");
        for (int i = 0; i < 8; i++)
            System.out.print(arr[i] + " ");

        System.out.println();
    }

     */
}

