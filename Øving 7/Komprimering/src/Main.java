import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        LempelZiv lz = new LempelZiv();
        Huffmann hm = new Huffmann();
        File oPDF = new File("../Filer/Oppgaveteskst.pdf");
        File fPDF = new File("../Filer/Forelesningen.pdf");
        File fTXT = new File("../Filer/Forelesningen.txt");
        File fLYX = new File("../Filer/Forelesningen.lyx");
        File wiki = new File("../Filer/enwik8");
        File test = new File("../Filer/test.txt");

        File valg = test;
        lz.komprimer(valg);
        hm.komprimer(new File(valg.getAbsolutePath() + ".lz"));
    }
}


class LempelZiv
{
    private byte[] lesFil(File file) throws IOException
    {
        byte[] input = Files.readAllBytes(Paths.get(file.getPath())); // Leser inn hele fila
        return input;
    }

    private void skrivFil(byte[] output, String name) throws IOException
    {

        String newName = "../Filer/" + name + ".lz";
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newName)));
        outputStream.write(output);
        outputStream.flush();
        outputStream.close();
    }

    public void komprimer(File file) throws IOException
    {
        byte[] input = this.lesFil(file);
        byte[] output = input.clone(); //Dypkopierer input så input forblir uendret ved endringer i output

        int tempIndex = 0, steps = 4, antallMindreTegn = 0;
        int index;
        for (index = 4; index < input.length - 4; )
        {
            //System.out.println((double)index / (double)input.length);
            int search = this.textSearch(this.getSection(index, index + 3, input), this.getSection(index + Short.MIN_VALUE, index - 1, input));
            if (search < 0) //Negativt tall indikerer at ingen match er funnet tidligere i teksten
            {
                if (steps - search > Short.MAX_VALUE) //Kan maks lagre dette tallet
                {
                    byte[] newData = new byte[2];
                    newData[1] = (byte)(steps & 0b11111111);
                    steps >>= 8;
                    newData[0] = (byte)(steps & 0b11111111);
                    output = this.insertSection(tempIndex - antallMindreTegn, newData, output);
                    //Setter telle verdiene våre tilbake til startverdiene
                    tempIndex = -1;
                    steps = 0;
                    antallMindreTegn -= 2; //Vi la til en byte med hvor mange tegn vi har ukomprimert
                }
                tempIndex = tempIndex == -1 ? index : tempIndex; //Settes bare om den ikke er satt fra før
                steps -= search;
                index -= search; //Øker indeksen med antall steg vi tar videre i teksten
            }
            else if (steps != 0 && tempIndex != -1)
            {
                byte[] newData = new byte[2];
                newData[1] = (byte)(steps & 0b11111111);
                steps >>= 8;
                newData[0] = (byte)(steps & 0b11111111);
                output = this.insertSection(tempIndex - antallMindreTegn, newData, output);
                //Setter telle verdiene våre tilbake til startverdiene
                tempIndex = -1;
                steps = 0;
                antallMindreTegn -= 2; //Vi la til en byte med hvor mange tegn vi har ukomprimert

                //Kommer hit om forrgie ikke var refererbar
                output = this.findLargestWord(index, input, output, antallMindreTegn);

                byte high = output[index + 2 - antallMindreTegn];
                byte low = output[index + 3 - antallMindreTegn];
                int index1 = (((high & 0b11111111) << 8) | (low & 0b11111111));
                index += index1;

                antallMindreTegn += (index1 - 4);
            }
            else //Går inn hvis forrgie vi fant også var refererbar
            {
                output = this.findLargestWord(index, input, output, antallMindreTegn);

                byte high = output[index + 2 - antallMindreTegn];
                byte low = output[index + 3 - antallMindreTegn];
                int index1 = (((high & 0b11111111) << 8) | (low & 0b11111111));
                index += index1;

                antallMindreTegn += (index1 - 4);
            }
        }
        //Håndterer slutten av teksten
        if (index == input.length - 4 && tempIndex != -1)
        {
            steps += 4;
            byte[] newData = new byte[2];
            newData[1] = (byte)(steps & 0b11111111);
            steps >>= 8;
            newData[0] = (byte)(steps & 0b11111111);

            output = this.insertSection(tempIndex - antallMindreTegn, newData, output);
        }
        else if (index != input.length)
        {
            steps = (4 - (index - (input.length - 4)));
            int lengde = steps;
            byte[] newData = new byte[2];
            newData[1] = (byte)(steps & 0b11111111);
            steps >>= 8;
            newData[0] = (byte)(steps & 0b11111111);

            output = this.insertSection(output.length - (lengde), newData, output);
        }

        //Lager komprimert fil
        this.skrivFil(output, file.getName());
    }

    private byte[] findLargestWord(int startIndex, byte[] input, byte[] output, int antallMindreTegn)
    {
        int tempStart = -1;
        int lengthRef = 0;
        int index;
        for (index = 3; index < Short.MAX_VALUE && index < input.length - startIndex; index++) //i er antall byte med i word, starter med 3 (gir 4 byte fra startindex), da dette er minste størrelse for å tjene på å bytte ut word med referanser
        {
            //Sjekker om ordet er skrevet tidligere i fila
            int startValue = this.textSearch(this.getSection(startIndex, startIndex + index, input), this.getSection(startIndex + Short.MIN_VALUE, startIndex - 1, input));
            if (startValue != -1)
            {
                //Setter evt. hvor denne referasnen starter
                tempStart = startIndex < Short.MAX_VALUE ? startValue : startIndex + Short.MIN_VALUE + startValue;
                lengthRef = index + 1;
            }
            else if (tempStart != -1)
            {
                byte[] newData = new byte[4];
                int mid = tempStart - startIndex;
                newData[1] = (byte)(mid & 0b11111111);
                mid >>= 8;
                newData[0] = (byte)(mid & 0b11111111);
                mid = lengthRef;
                newData[3] = (byte)(mid & 0b11111111);
                mid >>= 8;
                newData[2] = (byte)(mid & 0b11111111);
                output = this.removeSection(startIndex - antallMindreTegn, startIndex + index - 1 - antallMindreTegn, output); //Fjerner tegnene som er overfladiske
                output = this.insertSection(startIndex - antallMindreTegn, newData, output); //Setter inn komprisjonsverdiene
                return output; //Returnerer output endret
            }
        }
        //Returnerer output uendret
        if (tempStart != -1) //Kommer hit hvis vi fulfører byte[]'et
        {
            byte[] newData = new byte[4];
            int mid = tempStart - startIndex;
            newData[1] = (byte)(mid & 0b11111111);
            mid >>= 8;
            newData[0] = (byte)(mid & 0b11111111);
            mid = lengthRef;
            newData[3] = (byte)(mid & 0b11111111);
            mid >>= 8;
            newData[2] = (byte)(mid & 0b11111111);
            output = this.removeSection(startIndex - antallMindreTegn, startIndex + index - 1 - antallMindreTegn, output); //Fjerner tegnene som er overfladiske
            output = this.insertSection(startIndex - antallMindreTegn, newData, output); //Setter inn komprisjonsverdiene
        }
        return output;
    }

    private int textSearch(byte[] word, byte[] searchArea)
    {
        //TODO: implementer regler for mer effektiv søking (flytting) etter ordet (ikke et krav, men vil gjøre programmet betraktlig raskere)
        for (int i = 0; i < searchArea.length - word.length; i++)
        {
            for (int j = word.length - 1; j >= 0; j--)
            {
                if (word[j] != searchArea[i + j])
                {
                    j = -1;
                }
                if (j == 0)
                {
                    return i; //Match på i-index i area
                }
            }
        }
        return -1; //Ingen match
    }

    /**
     * Henter en seksjon av en byte[] og returnerer denne i en byte[].
     * Returnerer fra og med 'startIndex' og til og med 'endIndex'.
     *
     * @param startIndex
     * @param endIndex
     * @param data
     * @return
     */
    private byte[] getSection(int startIndex, int endIndex, byte[] data)
    {
        if (startIndex < 0)
        {
            startIndex = 0;
        }
        if (endIndex < startIndex || endIndex >= data.length)
        {
            throw new IllegalArgumentException("Start index må være mindre enn slutt index, og slutt index må være innenfor arrayen sin lengde");
        }
        byte[] returData = new byte[endIndex - startIndex + 1];

        for (int i = startIndex; i <= endIndex; i++)
        {
            returData[i - startIndex] = data[i];
        }
        return returData;
    }

    /**
     * Fjerner en seksjon fra en byte[] og returnerer en byte[].
     * Fjerner alt fra og med 'startIndex' og til og med 'endIndex'.
     *
     * @param startIndex
     * @param endIndex
     * @param data
     * @return
     */
    private byte[] removeSection(int startIndex, int endIndex, byte[] data)
    {
        if (endIndex < startIndex || endIndex > data.length || startIndex < 0)
        {
            throw new IllegalArgumentException("Start index må være mindre enn slutt index, og begge må være innenfor arrayen sin lengde, og større enn eller lik 0");
        }
        byte[] returData = new byte[data.length - (endIndex - startIndex) - 1];

        for (int i = 0; i < startIndex; i++)
        {
            returData[i] = data[i];
        }
        for (int i = startIndex; i < returData.length; i++)
        {
            returData[i] = data[i + endIndex - startIndex + 1];
        }
        return returData;
    }

    /**
     * Setter inn en byte[] i en byte[] og returnerer en byte[].
     * 'newData' starter på 'startIndex' i 'returData'
     *
     * @param startIndex
     * @param newData
     * @param data
     * @return
     */
    private byte[] insertSection(int startIndex, byte[] newData, byte[] data)
    {
        if (startIndex > data.length || startIndex < 0)
        {
            throw new IllegalArgumentException("Start index må større eller lik 0 og må være innenfor arrayen sin lengde + 1");
        }
        byte[] returData = new byte[data.length + newData.length];

        for (int i = 0; i < startIndex; i++)
        {
            returData[i] = data[i];
        }
        for (int i = startIndex; i < newData.length + startIndex; i++)
        {
            returData[i] = newData[i - startIndex];
        }
        for (int i = startIndex + newData.length; i < returData.length; i++)
        {
            returData[i] = data[i - newData.length];
        }
        return returData;
    }

    private void diff(byte[] before, byte[] after)
    {
        for (int i = 0; i < before.length && i < after.length; i++)
        {
            if (before[i] != after[i])
            {
                System.out.println(i + ": Before: " + before[i] + " After: " + after[i]);
            }
        }
    }
}


class Huffmann
{

    private int[][] frekvenstabell = new int[256][3]; //frekvens | bit-rep | antall bit i rep --- index er lik byte representasjonen
    private int nesteIndex = 0;

    public int[][] getFrekvenstabell()
    {
        return this.frekvenstabell;
    }

    public int getNesteIndex()
    {
        return this.nesteIndex;
    }

    public void setFrekvenstabell(int[][] nyFrekvenstabell)
    {
        this.frekvenstabell = nyFrekvenstabell;
    }

    public void setNesteIndex(int nyFrekvensIndex)
    {
        this.nesteIndex = nyFrekvensIndex;
    }

    private byte[] lesFil(File file) throws IOException
    {
        return Files.readAllBytes(Paths.get(file.getPath())); // Leser inn hele fila
    }


    public void komprimer(File file) throws IOException
    {
        this.opprettFrekvenstabell(file);
        Node rotNode = this.lagTre();

        String newName = "../Filer/" + file.getName() + ".hm";
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(newName)));
        int read = 0;
        boolean run = true;
        this.genererOppslagstabell(rotNode, 0, 0);
        for (int i = 0; i < 256; i++)
        {
            outputStream.writeInt(this.frekvenstabell[i][0]); //Skriver frekvensen som må være int, da den kan være for stor for en byte og/eller en short
        }
        do
        {
            try
            {
                read = inputStream.readShort();
                if (read > 0)
                {
                    outputStream.writeShort(read); //Skriver fremoverreferansen
                    byte[] data = new byte[read]; //Total data, ukomprimert
                    inputStream.read(data, 0, read);
                    data = this.huffmannForkorting(data); //Lager Huffmann forkortelsen, komprimert data
                    outputStream.write(data); //Skriver ut all komprimert data
                    outputStream.flush();
                }
                else
                {
                    //Skriver ut bakoverreferanse
                    outputStream.writeShort(read);
                    outputStream.writeShort(inputStream.readShort());
                    outputStream.flush();
                }
            }
            catch (EOFException eofe)
            {
                run = false;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println(read);
                System.exit(0);
            }
        } while (run);
        inputStream.close();
        outputStream.close();
    }

    private int getEnkeltVerdiBinary(int number)
    {
        return (int)Math.pow(2, number);
    }

    private byte[] huffmannForkorting(byte[] input)
    {
        ArrayList<Byte> output = new ArrayList<>();
        byte newByte = 0b00000000;
        int skrevetBits = 0;
        for (int i = 0; i < input.length; i++)
        {
            int[] bitInfo = this.getBitValue(input[i]);
            int tempByte = bitInfo[0];
            int bits = bitInfo[1];
            int antallBits = bits;

            for (int j = 0; j < antallBits; j++)
            {
                int steg = bits - 8 + skrevetBits;
                if (steg >= 0)
                {
                    newByte += ((tempByte & this.getEnkeltVerdiBinary(bits - 1)) >> (bits - 8 + skrevetBits));
                }
                else
                {
                    newByte += ((tempByte & this.getEnkeltVerdiBinary(bits - 1)) << (-1) * (bits - 8 + skrevetBits));
                }
                bits--;
                skrevetBits++;
                if (skrevetBits == 8)
                {
                    output.add(newByte);
                    newByte = 0b00000000;
                    skrevetBits = 0;
                }
            }
        }
        if (skrevetBits != 0)
        {
            output.add(newByte);
        }
        byte[] arrayOutput = new byte[output.size()];
        for (int i = 0; i < output.size(); i++)
        {
            arrayOutput[i] = output.get(i);
        }
        return arrayOutput;
    }

    private int[] getBitValue(byte verdi)
    {
        int[] bitValue = new int[2];

        bitValue[0] = this.frekvenstabell[(verdi & 0b11111111)][1];
        bitValue[1] = this.frekvenstabell[(verdi & 0b11111111)][2];

        return bitValue;
    }

    private void genererOppslagstabell(Node rotNode, int level, int bitRep)
    {

        if (rotNode.getLeftChild() == null) //Sjekker om det er et blad, har man et barn har man to
        {
            //Er blad node
            int verdi = rotNode.getValue();
            this.frekvenstabell[(verdi & 0b11111111)][1] = bitRep;
            this.frekvenstabell[(verdi & 0b11111111)][2] = level;
            //System.out.println("Nivå: " + level);
        }
        else
        {
            //Er ikke blad node
            int left = (bitRep << 1);
            this.genererOppslagstabell(rotNode.getLeftChild(), ++level, left);
            int right = (bitRep << 1) + 1;
            this.genererOppslagstabell(rotNode.getRightChild(), level, right);
        }
    }

    private Node lagTre()
    {
        ArrayList<Node> childNoder = new ArrayList<>();
        for (int i = 0; i < this.frekvenstabell.length; i++)
        {
            childNoder.add(new Node(i, this.frekvenstabell[i][0]));
        }
        while (!childNoder.isEmpty())
        {

            Node minste1 = this.getMinste(childNoder); //Venstre barn
            Node minste2 = this.getMinste(childNoder); //Høyre barn
            Node forelder = new Node(minste1.getFrekvens() + minste2.getFrekvens());
            forelder.setLeftChild(minste1);
            forelder.setRightChild(minste2);
            minste1.setParent(forelder);
            minste2.setParent(forelder);
            if (!childNoder.isEmpty())
            {
                childNoder.add(forelder);
            }
            else
            {
                return forelder; //Returnerer rot noden
            }
        }
        return null; //Skal ikke komme hit
    }

    private Node getMinste(ArrayList<Node> noder)
    {
        int index = 0, minsteFrekvens = noder.get(0).getFrekvens();
        for (int i = 1; i < noder.size(); i++)
        {
            int verdi = noder.get(i).getFrekvens();
            if (verdi < minsteFrekvens)
            {
                minsteFrekvens = verdi;
                index = i;
            }
        }
        Node returNode = noder.get(index);
        noder.remove(returNode);
        return returNode;
    }

    private void opprettFrekvenstabell(File file) throws IOException
    {
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
        int read;
        boolean run = true;
        do
        {
            try
            {
                read = inputStream.readShort();
                if (read > 0)
                {
                    for (int i = 0; i < read; i++)
                    {
                        //this.putInFrekvenstabell(inputStream.readByte());
                        byte p = inputStream.readByte();
                        this.putInFrekvenstabell(p);
                    }
                }
                else
                {
                    inputStream.skipBytes(2); //Hopper over referansen i LZ
                }
            }
            catch (EOFException eofe)
            {
                run = false;
            }
        } while (run);
        inputStream.close();
    }

    private void putInFrekvenstabell(byte tegn)
    {
        this.frekvenstabell[(tegn & 0b11111111)][0]++;
    }
}

class Node
{
    private int value;
    private int frekvens;
    private Node parent;
    private Node leftChild;
    private Node rightChild;

    public Node(int value, int frekvens)
    {
        this.value = value;
        this.frekvens = frekvens;
    }

    public Node(int frekvens)
    {
        this.value = 256;
        this.frekvens = frekvens;
    }

    public int getValue()
    {
        return value;
    }

    public int getFrekvens()
    {
        return frekvens;
    }

    public Node getParent()
    {
        return parent;
    }

    public Node getLeftChild()
    {
        return leftChild;
    }

    public Node getRightChild()
    {
        return rightChild;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public void setFrekvens(int frekvens)
    {
        this.frekvens = frekvens;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public void setLeftChild(Node leftChild)
    {
        this.leftChild = leftChild;
    }

    public void setRightChild(Node rightChild)
    {
        this.rightChild = rightChild;
    }
}
