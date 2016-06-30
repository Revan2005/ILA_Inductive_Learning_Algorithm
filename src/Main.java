import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
//moge modyfikowac liczbe przedzialow prz ydyskretyzacji w tej chwili podaje tam liczbe klas

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static ArrayList<Obiekt> dane;
	static int liczbaFoldow;
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Wybierz zbiór danych:\n1 - iris\n2 - iris2D\n3 - weather\n4 - weather.nominal\n5 - weather.numeric\n6 - wine\n7 - glass\n8 - wyklad\n");
		Scanner in = new Scanner(System.in);
		int choose = in.nextInt();
		String fileName = "";
		switch(choose){
			case 1:
				fileName = "iris.arff";
				break;
			case 2: 
				fileName = "iris.2D.arff";
				break;
			case 3: 
				fileName = "weather.arff";
				break;
			case 4:
				fileName = "weather.nominal.arff";
				break;
			case 5: 
				fileName = "weather.numeric.arff";
				break;
			case 6:
				fileName = "wine.arff";
				break;
			case 7:
				fileName = "glass.arff";
				break;
			case 8:
				fileName = "wyklad.arff";
				break;
			default:
				break;
		}
		in.close();
		String filePath = "/home/tomek/WORKSPACES!!/SystemyUczaceSie/AlgorytmILA/data/"+fileName;
		FileReader fileReader;
		fileReader = new FileReader(filePath);	
		Scanner scanner = new Scanner(fileReader);
		//BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String textLine;
		String[] splittedLine;
		String firstWord, nazwaAtrybutu, typAtybutu;
		do {
			textLine = scanner.nextLine();
			splittedLine = textLine.split(" ");
			firstWord = splittedLine[0];
			if((firstWord.equals("@attribute"))||(firstWord.equals("@ATTRIBUTE"))){
				nazwaAtrybutu = splittedLine[1];
				typAtybutu = splittedLine[2];
				Atrybuty.atrybuty.add(new Atrybut(nazwaAtrybutu, typAtybutu));
			}	  
		} while((!firstWord.equals("@data"))&&(!firstWord.equals("@DATA")));
		
		
		dane = new ArrayList<Obiekt>();
		do {
			textLine = scanner.nextLine();
			dane.add(new Obiekt(textLine));	  
		} while(scanner.hasNext());
		scanner.close();
		
		/*//test poprawnosci dzialania operacji wczytywania danych z pliku
		Obiekt o;
		for(int i=0; i< dane.size(); i++){
			o = dane.get(i);
			o.pisz(i);
		}*/
		
//dyskretyzacja =========================================================================================
		//ustalam liczbe klas - na tyle przedzialow bede dzielil atrybuty numeryczne ile jest klas
		Atrybut etykietaKlasy = Atrybuty.atrybuty.get(Atrybuty.atrybuty.size()-1);
		//System.out.println(etykietaKlasy.dziedzina);
		int liczbaKlas = etykietaKlasy.dziedzina.size();
		int liczbaPrzedzialow = 5;
		Atrybuty.dyskretyzuj(liczbaPrzedzialow);
//koniec procedury dyskretyzacji metoda dyskretyzacji jest zawarta w klasie atrybut=======================
//zeby pokazac reguly dla calego zbioru danych ustawic liczbe foldow na 1 i przejsc do ILA.java - wiersz 37		
		liczbaFoldow = 3;
		ILA ila = new ILA(dane, liczbaKlas, liczbaFoldow);
		ila.piszWynikiKoncowe();
	}

}
