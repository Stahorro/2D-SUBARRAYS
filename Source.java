//Stanislaw Kucharski - 5 a
import java.util.Scanner;

//ogolne dzialanie programu:
//kazda dana macierz(Matrix) n na m poddaje dwom przeksztalceniom pierwsze nanosi wagi ze wzoru (3* liczba dodatnia i 2* liczba ujemna)
//drugie zamienia kazdy element na jego sume z elementami powyzej niego w macierzy
//nastepnie dla kazdej kombinacji wierszy macierzy stosuje algorytm Kadane na sumach kolumn w kombinacji ((dla a > b)wiersz a - wiersz b = suma elementow b...a)
//na koncu jezeli wynik = 0 sprawdza czy macierz zawiera elementy niezerowe aby wyswietlic empty dla odpowiednich przypadkow i wyniki dla pozostalych

public class Source {
    public static Scanner scanner = new Scanner(System.in);

    // wymiary tablicy
    private static int n = 0, m = 0;
    private static int resultTop = n, resultBottom = n, resultLeft = m, resultRight = m; //static dla metody KeepLexicalOrder

    //wczytanie tablicy z pierwszym przeksztalceniem
    public static int[][] LoadMatrix() { // program wczytujacy tablice z zastosowaniem wag (liczby dodatnie *3 liczby ujemne *2)
        scanner.nextInt();
        scanner.next();
        n = scanner.nextInt();
        m = scanner.nextInt();
        int[][] Matrix = new int[n][m];
        for(int i = 0; i <n; i++){
            for(int j = 0; j < m; j++){
                int temp = scanner.nextInt();
                if(temp >=0) temp *= 3; //waga dodatnia
                else temp *= 2; //waga ujemna
                Matrix[i][j] = temp;
            }
        }
        return Matrix;
    }

    //drugie przeksztalcenie - przygotowanie macierzy takiej ze kazdy element tej macierzy odpowiada wartosci oryginlanej powiekoszonej o sume wartosci oryginlanych pol powyzej danego pola (z zachowaniem wczesniejszew uwzgledenienia wag)
    public static int[][] ProcessMatrix(int[][] Matrix){
        int [][] preprocessedMatrix = new int[n][m];
        int[] temparray = new int [m];
        for(int i = 0; i < n; i++){
            //pierwszy wiersz jest przepisywany
            if(i == 0) {
                temparray = Matrix[i];
                preprocessedMatrix[i] = Matrix[i];
            }
            //do pol z innych wierszy dodajemy wszystko to co jest wyzej w tabeli (jezeli w2 = w1+w2 to do w3 wystarczy dodac w2 aby w3 = w1 + w2 + w3
            else{
                for(int j = 0 ; j < m; j++){
                    preprocessedMatrix[i][j] = Matrix[i][j] + temparray[j];                 }
                temparray = preprocessedMatrix[i];
            }
        }
        return preprocessedMatrix;
    }

    //zwraca True jezeli istnieje nieujemne wartosci w tablicy
    public static boolean Emptiness(int[][]Matrix){
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if (Matrix[i][j] >= 0) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    //ustawia wartosci z kat result na pierwsze 4 arg jezeli jest to korzystniejsze dla porzadku leksykograficznego
    public static void KeepLexicalOrder(int i, int j, int tempLeft, int q) {
        if(i < resultBottom) {
            resultLeft = tempLeft;
            resultRight = q;
            resultBottom = i;
            resultTop = j;
        }
        else if (i == resultBottom){
            if(j < resultTop){
                resultLeft = tempLeft;
                resultRight = q;
                resultTop = j;
            } else if (j == resultTop) {
                if(tempLeft < resultLeft){
                    resultLeft = tempLeft;
                    resultRight = q;
                }
                else if(tempLeft == resultLeft){
                    if(q <= resultRight){
                        resultRight = q;
                    }
                }
            }
        }
    }



        public static void main(String[] args) {
        // wczytanie ilosci zestawow danych
        int dataSets = scanner.nextInt();
        // obsluga kazdego zestawu danych z osobna
        for(int z = 0; z< dataSets; z++){
            //wczytanie tablicy
            int[][] Matrix = LoadMatrix();

            //sprawdzenie pustosci zanim dokonamy przeksztalcenia
            boolean controlPrint = Emptiness(Matrix);

            //przygotowanie macierzy takiej ze kazdy element tej macierzy odpowiada wartosci oryginlanej powiekoszonej o sume wartosci oryginlanych pol powyzej danego pola (z zachowaniem wczesniejszew uwzgledenienia wag)
            Matrix = ProcessMatrix(Matrix);

            //główna część programu
            int[] temparray = new int[m];
            int tempLeft, current;
            int currentMax = 0;
            resultTop = n; resultBottom = n; resultLeft = m; resultRight = m;

            //iteracja po wszystkim mozliwosciach wyboru wierszy (wiedzac ze [0:1] == [1:0] a standardem jest pierwszy zapis)
            for(int i = 0; i < n; i++){
                for(int j = i; j < n; j++){
                    // iteracja po  j-tym wierszu do ustawienie temparray na sumy kolumn w wierszach od i do j
                    for(int k = 0; k < m; k++){
                        temparray[k] = Matrix[j][k];//suma od poczatku do j
                        if (i != 0) {
                            temparray[k] -= Matrix[i - 1][k]; //odjecie wszystkiego przed i
                        }
                    }
                    //co kombinacje wierszy robimy Kadane (dodawanie kolejnych wartosci wiersza i analizowanie sum czesciowych)
                    current = 0;
                    tempLeft = 0;

                    for(int q = 0; q < m; q++){
                        current += temparray[q];
                        //pomijanie 0 i ujemnych na poczatku ciagu bez utraty szukania wspolrzednych 0
                        if(current <= 0){
                            if(current == currentMax){
                                KeepLexicalOrder(i,j,tempLeft,q);
                            }
                            current = 0;
                            tempLeft = q+1;
                        }
                        //sprawdzanie czy nasza najlepsza suma moze byc uzyskana mniejsza iloscia elementow lub mniejszym ciagiem (leksykograficznie)
                        else if(current == currentMax){
                            int currentArea = (q - tempLeft + 1) * (j - i + 1);
                            int resultArea = (resultRight - resultLeft + 1) * (resultTop - resultBottom + 1);
                            if(currentArea < resultArea){
                                    resultLeft = tempLeft;
                                    resultRight = q;
                                    resultBottom = i;
                                    resultTop = j;
                                }
                            //czy ciag o rownej ilosc elemntow jest leksykograficznie mniejszy
                            else if(currentArea == resultArea){
                                KeepLexicalOrder(i,j,tempLeft,q);
                            }
                        }
                        //aktualizacja najlepszej sumy
                        else if(current > currentMax){
                            currentMax = current;
                            resultLeft = tempLeft;
                            resultRight = q;
                            resultBottom = i;
                            resultTop = j;
                        }

                    }
                }
            }
            //wyswietlanie wynikow
            if(controlPrint)
                System.out.println((z+1) + ": ms_tab = a["+resultBottom+".."+resultTop+"]["+resultLeft+".."+resultRight+"], msum="+currentMax);
            else  System.out.println((z+1) + ": ms_tab is empty");
        }

    }

}


//dane testowe
/*
input:
20
1 : 1 6
-2 7 -4 8 -5 4
2 : 2 5
1 1 -1 -1 0
1 1 -1 -1 4
3 : 2 5
0 -1 -1 1 1
4 -2 -2 1 1
4 : 2 5
0 -1 -1 4 0
4 -2 -2 0 0
5 : 2 5
-1 -2 -3 -1 -2
-1 -1 -1 -1 -5
6 : 2 5
0 0 0 0 0
0 0 0 0 0
7 : 3 1
1
-6
8
8 : 4 2
8 -5
-3 4
-1 2
0 1
9 : 5 5
1 1 1 1 1
1 1 1 1 1
1 1 1 1 1
1 1 1 1 1
1 1 1 1 1
10 : 3 3
1 0 -1
0 1 -1
0 0 1
11 : 3 3
1 0 -1
0 1 0
-1 0 1
12 : 4 4
3 -10 3 -10
-10 3 -10 3
3 -10 3 -10
-10 3 -10 3
13 : 4 4
0 0 0 1
1 0 0 0
0 0 0 0
0 0 0 0
14 : 4 4
0 0 0 0
1 0 0 1
0 0 1 0
0 1 0 0
15 : 4 4
-5 -5 -5 1
1 -5 -5 -5
-5 -5 -5 -5
0 0 0 0
16 : 4 4
-5 -5 -5 -5
1 -5 -5 1
-5 -5 1 -5
-5 1 -5 -5
17 : 3 2
0 -1
-1 -2
-1 0
18 : 1 3
-3 0 -1
19 : 3 1
-3
0
-2
20 : 1 7
0 2 -2 2 -2 1 0


output:
1: ms_tab = a[0..0][1..5], msum=39
2: ms_tab = a[0..1][0..4], msum=16
3: ms_tab = a[1..1][0..0], msum=12
4: ms_tab = a[0..0][3..3], msum=12
5: ms_tab is empty
6: ms_tab = a[0..0][0..0], msum=0
7: ms_tab = a[2..2][0..0], msum=24
8: ms_tab = a[0..3][0..1], msum=27
9: ms_tab = a[0..4][0..4], msum=75
10: ms_tab = a[0..1][0..1], msum=6
11: ms_tab = a[0..1][0..1], msum=6
12: ms_tab = a[0..0][0..0], msum=9
13: ms_tab = a[0..1][0..3], msum=6
14: ms_tab = a[1..3][0..3], msum=12
15: ms_tab = a[0..0][3..3], msum=3
16: ms_tab = a[1..1][0..0], msum=3
17: ms_tab = a[0..0][0..0], msum=0
18: ms_tab = a[0..0][1..1], msum=0
19: ms_tab = a[1..1][0..0], msum=0
20: ms_tab = a[0..0][1..3], msum=8



 */