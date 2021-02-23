import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        LempelZiv lz = new LempelZiv();
        Huffmann hm = new Huffmann();
        File oPDF = new File("../Filer/Oppgaveteskst.pdf.lz.hm");
        File fPDF = new File("../Filer/Forelesningen.pdf.lz.hm");
        File fTXT = new File("../Filer/Forelesningen.txt.lz.hm");
        File fLYX = new File("../Filer/Forelesningen.lyx.lz.hm");
        File wiki = new File("../Filer/enwik8.lz.hm");
        File test = new File("../Filer/test.txt.lz.hm");

        File valg = new File("../Filer/AlgDat.pdf.lz.hm");
        hm.dekomprimer(valg);
        String name = "../Filer/DeKomp";
        String[] parts = valg.getName().split("\\.");
        for (int i = 0; i < parts.length - 1; i++)
        {
            name += "." + parts[i];
        }
        lz.dekomprimer(new File(name));
        //lz.dekomprimer(new File("../Filer/test.txt.lz"));
    }
}

class LempelZiv
{
    public void dekomprimer(File file) throws IOException
    {
        String name = "../Filer/";
        String[] parts = file.getName().split("\\.");
        for (int i = 0; i < parts.length - 1; i++)
        {
            if (i > 0)
            {
                name += "." + parts[i];
            }
            else
            {
                name += parts[i];
            }
        }

        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
        DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(name)));
        DataInputStream outputWritten;

        int read, secondRead;
        byte[] written;
        boolean run = true;
        do
        {
            try
            {
                read = inputStream.readShort();
                if (read > 0)
                {
                    written = new byte[read];
                    inputStream.read(written, 0, read);
                    output.write(written, 0, read);
                    output.flush();
                }
                else
                {
                    outputWritten = new DataInputStream(new BufferedInputStream(new FileInputStream(name)));
                    secondRead = inputStream.readShort();
                    written = new byte[secondRead];
                    outputWritten.skipBytes(output.size() + read);
                    outputWritten.read(written, 0, secondRead);
                    outputWritten.close();
                    output.write(written);
                    output.flush();
                }
            }
            catch (EOFException eofe)
            {
                run = false;
            }
        } while (run);

        inputStream.close();
        output.flush();
        output.close();
    }
}

class Huffmann
{
    private int[][] frekvenstabell = new int[256][3]; //frekvens | bit-rep | antall bit i rep --- index er lik byte representasjonen

    public void dekomprimer(File file) throws IOException
    {
        String name = "../Filer/DeKomp";
        String[] parts = file.getName().split("\\.");
        for (int i = 0; i < parts.length - 1; i++)
        {
            name += "." + parts[i];
        }

        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(name)));

        for (int i = 0; i < this.frekvenstabell.length; i++)
        {
            this.frekvenstabell[i][0] = inputStream.readInt(); //Leser inn frekvenstabell verdiene
        }
        Node rotNode = this.lagTre();
        this.genererOppslagstabell(rotNode, 0, 0);

        boolean run = true;
        int read = 0;
        int[] verdier = {0b100000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

        do
        {
            try
            {
                read = inputStream.readShort();
                if (read > 0)
                {
                    outputStream.writeShort(read); //Skriver fremoverreferansen
                    int added = 0;
                    int count = 0;
                    Node currentNode = rotNode;
                    byte byteData;
                    while (added < read)
                    {
                        byteData = inputStream.readByte();
                        for (int i = 0; i < 8; i++)
                        {
                            if ((byteData & verdier[i]) == 0)
                            {
                                currentNode = currentNode.getLeftChild();
                                count++;
                            }
                            else
                            {
                                currentNode = currentNode.getRightChild();
                                count++;
                            }
                            if (currentNode.getLeftChild() == null) //Hvis stemmer, er blad node
                            {
                                outputStream.writeByte((currentNode.getValue() & 0b11111111));
                                //System.out.println("Byte value: " + (byte)(currentNode.getValue() & 0b11111111) + "\nCount: " + count);
                                currentNode = rotNode;
                                added++;
                                count = 0;
                                if (added == read)
                                {
                                    i = 8;
                                }
                            }
                        }
                    }
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
