import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Scanner;
import java.lang.Integer;

public class Main
{

    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Skriv et stort tall for addering: (xxx + xxx)");
        String inputString = input.nextLine();
        String[] deling = inputString.split(" "); //Deler opp input String'en i to tall og en operator
        DobbeltLenke liste1 = genererLenke(deling[0]);
        DobbeltLenke liste2 = genererLenke(deling[2]);
        System.out.println(deling[0] + "\n+");
        System.out.println(deling[2]);
        DobbeltLenke addert = adderLister(liste1, liste2);
        System.out.println(addert.toString());



        System.out.println("Skriv et stort tall for subtrahering: (xxx - xxx)");
        inputString = input.nextLine();
        deling = inputString.split(" "); //Deler opp input String'en i to tall og en operator
        liste1 = genererLenke(deling[0]);
        liste2 = genererLenke(deling[2]);
        System.out.println(deling[0] + "\n-");
        System.out.println(deling[2]);
        DobbeltLenke subtrahert = subtraherLister(liste1, liste2);
        System.out.println(subtrahert.toString());

    }

    public static DobbeltLenke subtraherLister(DobbeltLenke liste1, DobbeltLenke liste2) throws IllegalArgumentException
    {
        DobbeltLenke returListe = new DobbeltLenke();
        returListe.settInnFremst((byte)0);

        for (int i = 0; i < liste1.getAntallElementer() || i < liste2.getAntallElementer(); i++)
        {
            returListe.settInnFremst((byte)0);

            if (liste1.finnNodeVedIndex(i) != null && liste2.finnNodeVedIndex(i) != null)
            {
                byte verdi1 = liste1.finnNodeVedIndex(i).getVerdi();
                byte verdi2 = liste2.finnNodeVedIndex(i).getVerdi();
                byte returListeVerdi = returListe.finnNodeVedIndex(0).getVerdi();
                if (verdi1 >= verdi2)
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(verdi1 - verdi2));
                }
                else
                {
                    boolean fantLaaneVerdi = false;
                    for (int j = i + 1; j < liste1.getAntallElementer(); j++)
                    {
                        if (liste1.finnNodeVedIndex(j).getVerdi() != 0)
                        {
                            fantLaaneVerdi = true;
                            liste1.finnNodeVedIndex(j).setVerdi((byte)(liste1.finnNodeVedIndex(j).getVerdi() - 1));
                            for (int k = 0; k < j - i - 1; k++)
                            {
                                liste1.finnNodeVedIndex(j - 1 - k).setVerdi((byte)9);
                            }
                            j = liste1.getAntallElementer();
                        }
                    }
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(verdi1 - verdi2 + 10));
                    if (!fantLaaneVerdi)
                    {
                        throw new IllegalArgumentException();
                    }
                }
            }


            if (liste1.finnNodeVedIndex(i) == null && liste2.finnNodeVedIndex(i) != null)
            {
                throw new IllegalArgumentException();
            }


            if (liste1.finnNodeVedIndex(i) != null && liste2.finnNodeVedIndex(i) == null)
            {
                byte verdi1 = liste1.finnNodeVedIndex(i).getVerdi();
                returListe.finnNodeVedIndex(1).setVerdi(verdi1);
            }
        }
        boolean sjekker = true;
        while (sjekker && returListe.getAntallElementer() > 1)
        {
            if (returListe.finnNodeVedIndex(0).getVerdi() == 0)
            {
                returListe.fjernNode(returListe.finnNodeVedIndex(0));
            }
            else
            {
                sjekker = false;
            }
        }

        return returListe;
    }


    public static DobbeltLenke adderLister(DobbeltLenke liste1, DobbeltLenke liste2)
    {
        DobbeltLenke returListe = new DobbeltLenke();
        returListe.settInnFremst((byte)0);
        for (int i = 0; i < liste1.getAntallElementer() || i < liste2.getAntallElementer(); i++)
        {
            if (liste1.finnNodeVedIndex(i) != null && liste2.finnNodeVedIndex(i) != null)
            {
                returListe.settInnFremst((byte)0);

                int nyVerdi = liste1.finnNodeVedIndex(i).getVerdi() + liste2.finnNodeVedIndex(i).getVerdi();
                int returListeVerdi = returListe.finnNodeVedIndex(1).getVerdi();
                if (nyVerdi + returListeVerdi > 9)
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(nyVerdi + returListeVerdi - 10));
                    returListe.finnNodeVedIndex(0).setVerdi((byte)(1));
                }
                else
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(nyVerdi + returListeVerdi));
                }
            }


            if (liste1.finnNodeVedIndex(i) == null && liste2.finnNodeVedIndex(i) != null)
            {
                returListe.settInnFremst((byte)0);
                int nyVerdi = liste2.finnNodeVedIndex(i).getVerdi();
                int returListeVerdi = returListe.finnNodeVedIndex(1).getVerdi();
                if (nyVerdi + returListeVerdi > 9)
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(nyVerdi + returListeVerdi - 10));
                    returListe.finnNodeVedIndex(0).setVerdi((byte)(1));
                }
                else
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(nyVerdi + returListeVerdi));
                }
            }


            if (liste1.finnNodeVedIndex(i) != null && liste2.finnNodeVedIndex(i) == null)
            {
                returListe.settInnFremst((byte)0);
                int nyVerdi = liste1.finnNodeVedIndex(i).getVerdi();
                int returListeVerdi = returListe.finnNodeVedIndex(1).getVerdi();
                if (nyVerdi + returListeVerdi > 9)
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(nyVerdi + returListeVerdi - 10));
                    returListe.finnNodeVedIndex(0).setVerdi((byte)(1));
                }
                else
                {
                    returListe.finnNodeVedIndex(1).setVerdi((byte)(nyVerdi + returListeVerdi));
                }
            }
        }
        if (returListe.finnNodeVedIndex(0).getVerdi() == 0)
        {
            returListe.fjernNode(returListe.finnNodeVedIndex(0));
        }
        return returListe;
    }

    public static DobbeltLenke genererLenke(String elementer)
    {
        DobbeltLenke returLenke = new DobbeltLenke();
        String[] enkeltElementer = elementer.split("");
        for (String s : enkeltElementer)
        {
            try
            {
                returLenke.settInnFremst((byte)Integer.parseInt(s));
            }
            catch (NumberFormatException nfe)
            {
                System.out.println("Du må skrive inn på riktig format");
                System.exit(0);
            }
        }
        return returLenke;
    }
}

class Node
{
    private byte _verdi;
    private Node _neste;
    private Node _forrige;

    public Node(byte verdi, Node neste, Node forrige)
    {
        this._verdi = verdi;
        this._neste = neste;
        this._forrige = forrige;
    }

    public byte getVerdi()
    {
        return this._verdi;
    }

    public void setVerdi(byte nyVerdi)
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

    public Node getForrige()
    {
        return this._forrige;
    }

    public void setForrige(Node forrige)
    {
        this._forrige = forrige;
    }

}

class DobbeltLenke
{
    private Node _hode = null;
    private Node _hale = null;
    private int _antallElementer = 0;

    public Node getHode()
    {
        return this._hode;
    }

    public Node gethale()
    {
        return this._hale;
    }

    public int getAntallElementer()
    {
        return this._antallElementer;
    }

    public void settInnFremst(byte verdi)
    {
        this._hode = new Node(verdi, this._hode, null);

        if (this._hale == null)
        {
            this._hale = this._hode;
        }
        else
        {
            this._hode.getNeste().setForrige(this._hode);
        }
        this._antallElementer++;
    }

    public void settInnBakerst(byte verdi)
    {
        Node ny = new Node(verdi, null, this._hale);

        if (this._hale != null)
        {
            this._hale.setNeste(ny);
        }
        else
        {
            this._hode = ny;
        }
        this._hale = ny;
        this._antallElementer++;
    }

    public Node fjernNode(Node node)
    {
        if (node.getForrige() != null)
        {
            node.getForrige().setNeste(node.getNeste());
        }
        else
        {
            this._hode = node.getNeste();
        }
        if (node.getNeste() != null)
        {
            node.getNeste().setForrige(node.getForrige());
        }
        else
        {
            this._hale = node.getForrige();
        }
        node.setNeste(null);
        node.setForrige(null);
        this._antallElementer--;
        return node;
    }

    public Node finnNodeVedIndex(int index)
    {
        Node denne = this._hode;
        if (index < this._antallElementer)
        {
            for (int i = 0; i < index; ++i)
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
        this._hale = null;
        this._antallElementer = 0;
    }

    public String toString()
    {
        String returnString = "Verdier i lenke: ";
        Node denne = this._hode;
        for (int i = 0; i < this._antallElementer; i++)
        {
            returnString += denne.getVerdi() + " ";
            denne = denne.getNeste();
        }
        return returnString;
    }
}
