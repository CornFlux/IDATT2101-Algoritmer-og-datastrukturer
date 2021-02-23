import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args)
    {
        Main main = new Main();
        long startTimer;
        long endTimer;
        long totalTime = 0;
        int antallGjennomganger = 1000000;
        int n = 14;
        double x = 3;

        System.out.println("Rekursiv metode 1: " + main.potens1(n,x));

        double midlertidig = 0.0;
        for (int i = 0; i < antallGjennomganger; i++) //Kjører metoden 1000 ganger for å få en gjennomsnittlig mer nøyaktig måling av tidsbruket
        {
            startTimer = System.nanoTime();
            midlertidig = main.potens1(n,x);
            endTimer = System.nanoTime();
            totalTime += (endTimer - startTimer);
        }
        System.out.println("Gjennomsnittelig tid etter " + antallGjennomganger + " gjennomganger: " + totalTime / antallGjennomganger + " nanosekunder\n"); // deler på 1000 for å finne gjennomsnittet
        totalTime = 0;

        System.out.println("Java Math.pow() 1: " + Math.pow(x,n));

        for (int i = 0; i < antallGjennomganger; i++) //Kjører metoden 1000 ganger for å få en gjennomsnittlig mer nøyaktig måling av tidsbruket
        {
            startTimer = System.nanoTime();
            midlertidig = Math.pow(x,n);
            endTimer = System.nanoTime();
            totalTime += (endTimer - startTimer);
        }
        System.out.println("Gjennomsnittelig tid etter " + antallGjennomganger + " gjennomganger: " + totalTime / antallGjennomganger + " nanosekunder\n"); // deler på 1000 for å finne gjennomsnittet
        totalTime = 0;



        System.out.println("Rekursiv metode 2: " + main.potens2(n,x));

        for (int i = 0; i < antallGjennomganger; i++) //Kjører metoden 1000 ganger for å få en gjennomsnittlig mer nøyaktig måling av tidsbruket
        {
            startTimer = System.nanoTime();
            midlertidig = main.potens2(n,x);
            endTimer = System.nanoTime();
            totalTime += (endTimer - startTimer);
        }
        System.out.println("Gjennomsnittelig tid etter " + antallGjennomganger + " gjennomganger: " + totalTime / antallGjennomganger + " nanosekunder\n"); // deler på 1000 for å finne gjennomsnittet
        totalTime = 0;

        System.out.println("Java Math.pow() 2: " + Math.pow(x,n));

        for (int i = 0; i < antallGjennomganger; i++) //Kjører metoden 1000 ganger for å få en gjennomsnittlig mer nøyaktig måling av tidsbruket
        {
            startTimer = System.nanoTime();
            midlertidig = Math.pow(x,n);
            endTimer = System.nanoTime();
            totalTime += (endTimer - startTimer);
        }
        System.out.println("Gjennomsnittelig tid etter " + antallGjennomganger + " gjennomganger: " + totalTime / antallGjennomganger + " nanosekunder\n"); // deler på 1000 for å finne gjennomsnittet
    }

    /**
     * Dette er besvarelsen til oppgave 1 (2.1-1 i boka)
     * @param n heltall
     * @param x desimaltall
     * @return
     */
    public double potens1(int n, double x)
    {
        if (n <= 0) //bruker mindre-enn-er-lik for å sikre at negative verdier blir håndtert riktig
        {
            return 1;
        }
        return  x * this.potens1(n - 1, x);
        //Denne metoden vil skalere lineært med 'n', altså den vil ha like mange metode kall som verdien av 'n'
        //dette fordi den vil kalle f. eks. P(5, x) -> P(4, x) -> P(3, x) -> P(2, x) -> P(1, x),
        //dette siden den kaller seg selv med 'n - 1' hver gang helt ned til 'n == 0', hvor den da returnerer 1
    }

    /**
     * Dette er besvarelsen til oppgave 2 (2.2-3 i boka)
     * @param n heltall
     * @param x desimaltall
     * @return
     */
    public double potens2(int n, double x)
    {
        if (n <= 0) //bruker mindre-enn-er-lik for å sikre at negative verdier blir håndtert riktig
        {
            return 1;
        }
        return n % 2 == 0 ? potens2(n / 2, x * x) : x * potens2((n - 1) / 2, x * x); //Her er resultatet av å formulere det rett fra slik oppgaven sier det
        //Denne metoden vil skalere med log(base 2)(n) + 1, si f.eks. n = 32 så vil den kalle P(32, x) -> P(16, x) -> P(8, x) -> P(4, x) -> P(2, x) -> P(1, x) altså 6 metode kall og log(base 2)(32) + 1 = 5(avrundert) + 1 = 6
        //dette gjelder også når metoden får en 'n'-verdi som er et oddetall da den "trekker" en 'x' ut av utrykket for å få 'n - 1' for å få et partall som igjen kan halveres
        //ta f. eks. n = 35 så vil metoden kalle P(35, x) -> x * P(17, x) -> x * P(8, x) -> P(4, x) -> P(2, x) -> P(1, x) altså 6 metode kall og log(base 2)(35) + 1 = 5(avrundet) + 1 = 6
        //dette gjør at metoden må ha veldig store 'n' verdier for at den skal bli treger
        //sammmenlignet med potens1-metoden hvis 'n' er si 5000 så vil potens1 ha 5000 rekusive kall, mens denne vil ha estimert log(base 2)(5000) + 1 = 12(avrundet) + 1 = 13
    }
}
