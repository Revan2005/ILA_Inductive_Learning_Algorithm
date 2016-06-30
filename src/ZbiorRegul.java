import java.lang.reflect.Array;
import java.util.ArrayList;


public class ZbiorRegul {
	ArrayList<Regula> reguly = new ArrayList<Regula>();
	Regula regula;
	ArrayList<Przeslanka> przeslanki;
	ArrayList<Obiekt> zbiorUczacy; 
	ArrayList<Obiekt> zbiorTestowy;
	double tP, tN, fP, fN;
	double accuracy, precision, recall, fmeasure;
	ArrayList<Atrybut> atrybuty;
	double[] wyniki;
	ArrayList<ArrayList<Integer>> indeksyJuzPokrytych = new ArrayList<ArrayList<Integer>>();
	
	public ZbiorRegul(ArrayList<Obiekt> zU){
		zbiorUczacy = zU;
		//generowanie regul
		//krok 1 dziele tabele danych uczacych na n podtabel n = liczba mozliwych wartosci etykiety klasy
		//w kazdej podtabeli elementy naleza do te jsamej klasy
		ArrayList<ArrayList<Obiekt>> podzielonyZbiorUczacy = new ArrayList<ArrayList<Obiekt>>();
		//Obiekt pierwszyObiekt = zbiorUczacy.get(0);
		atrybuty = Atrybuty.atrybuty;
		Atrybut atrybutKlasy = atrybuty.get(Atrybuty.atrybuty.size()-1);
		ArrayList<String> etykietyKlas = atrybutKlasy.dziedzina;
		//System.out.println(etykietyKlas.size());
		for(int i=0; i<etykietyKlas.size(); i++)
			podzielonyZbiorUczacy.add(new ArrayList<Obiekt>());
		//System.out.println(podzielonyZbiorUczacy.size());
		Obiekt o;
		for(int i=0; i<zbiorUczacy.size(); i++){
			//System.out.println("zbior uczecu sie size ="+zbiorUczacy.size());
			o = zbiorUczacy.get(i);
			//przechodze po wszystkich mozliwych etykietach klas
			for(int j=0; j<etykietyKlas.size(); j++){
				//System.out.println(etykietyKlas.get(j));
				if(o.getEtykietaKlasy().equals(etykietyKlas.get(j))){
					podzielonyZbiorUczacy.get(j).add(o);
				}
			}
		}
		//System.out.println(podzielonyZbiorUczacy.size()+" "+podzielonyZbiorUczacy.get(2).size());
		//piszPodzielonyZbiorUczacy(podzielonyZbiorUczacy);
		//chyba jest w porzadku mam zbior uczacy podzielony na podzbiory zawierajace obiekty tej samej klasy
		//pierwszy indeks w tej "macierzy z list" odpowiada indeksowi etykiety na liscie etykietyKlas
		//krok 2: dla wszystkich tabel:
		int liczbaKombinacji, liczbaAtrybutowBezKlasy = atrybuty.size()-1;
		String[][] wszystkieWartosciowaniaAtrybutowWPodtabeli;
		//String[][] wszystkieWartosciowaniaAtrybutow;

		//zbior uczacy nie musi posiadac obiektow wszystkich klas
		//wtedy podlista ma rozmiar zero i wywala blad
		//ponizej oczyszczam liste z pustych podlist
		for(int i=0;i<podzielonyZbiorUczacy.size(); i++)
			if(podzielonyZbiorUczacy.get(i).size()==0)
				podzielonyZbiorUczacy.remove(i);
		

		ArrayList<ArrayList<Atrybut>> kombinacjePodzbiorowAtrybutow;
		ArrayList<Atrybut> podzbiorAtrybutow;
		//String[] wartosciowanieKombinacjiAtrybutow;
		Przedzial[] wartosciowanieKombinacjiAtrybutowNumerycznych; 
		String[] wartosciowanieKombinacjiAtrybutowEnum;
		Przedzial[] najlepszeWartosciowanieNumerycznych = new Przedzial[1];
		String[] najlepszeWartosciowanieEnum  = new String[1];
		int pokrycieMaxDlaDanejKombinacji;
		int pokrycieMaxWsrodWszystkichJElementowychKombinacji;
		int pokrycie;
		int najlepszaKombinacja = -1;
		ArrayList<Atrybut> podzbiorAtrybutowNajlepszejKombinacji = new ArrayList<Atrybut>();
		
		
		indeksyJuzPokrytych = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<podzielonyZbiorUczacy.size(); i++)
			indeksyJuzPokrytych.add(new ArrayList<Integer>());
		
		
		for(int i=0; i<podzielonyZbiorUczacy.size(); i++){
			// i - podtabela zawierajaca obiekty jednej klasy
			for(int j=1; j<=liczbaAtrybutowBezKlasy; j++){
				ArrayList<Obiekt> podtabela = podzielonyZbiorUczacy.get(i);
				if(podtabela.size() == 0)
					continue;
				Obiekt obiektRozwazanejKlasy = podtabela.get(0);
				String rozwazanaKlasa = obiektRozwazanejKlasy.getEtykietaKlasy();
				if(indeksyJuzPokrytych.get(i).size() >= podtabela.size()){
					System.out.println("podtabela "+i+" pokryta, szybko lece po liczbach atrybutow: "+j);
					continue;
				}
				//teraz tworze wszystkie j elementowe kombinacje atrybutow
				int [][] podzbiory = GeneratorPodzbiorow.generujPodzbiory(liczbaAtrybutowBezKlasy, j);
				int[] indeksyAtrybutow;
				kombinacjePodzbiorowAtrybutow = zamienIndeksyNaAtrybuty(podzbiory);
				
				
				pokrycieMaxWsrodWszystkichJElementowychKombinacji = 0;
				pokrycieMaxDlaDanejKombinacji = 0;
				do {
					//System.out.println("a tu doszlo?  j="+j);
					pokrycieMaxWsrodWszystkichJElementowychKombinacji = 0;
					for(int k=0; k<kombinacjePodzbiorowAtrybutow.size(); k++){
						//teraz lece po obiektach podstawiam wartosci do kombinacji i sprawdzam ile takie wartosciowania pokrywaja
						podzbiorAtrybutow = kombinacjePodzbiorowAtrybutow.get(k);
						indeksyAtrybutow = podzbiory[k];
					
						pokrycieMaxDlaDanejKombinacji = 0;
						for(int l=0; l<podtabela.size(); l++){
							Obiekt rozwazanyObiekt = podtabela.get(l); 
							wartosciowanieKombinacjiAtrybutowNumerycznych = getWartosciowanieKombinacjiAtrybutowNumerycznych(indeksyAtrybutow, rozwazanyObiekt);
							wartosciowanieKombinacjiAtrybutowEnum = getWartosciowanieKombinacjiAtrybutowEnum(indeksyAtrybutow, rozwazanyObiekt);
					
							pokrycie = ilePokrywa(podzbiorAtrybutow, wartosciowanieKombinacjiAtrybutowNumerycznych, wartosciowanieKombinacjiAtrybutowEnum,  podtabela, indeksyJuzPokrytych.get(i));
							//System.out.println("pokrycie = "+pokrycie);
							if((pokrycie > pokrycieMaxDlaDanejKombinacji)&&(!czyPokrywaInneKlasy(podzbiorAtrybutow, wartosciowanieKombinacjiAtrybutowNumerycznych, wartosciowanieKombinacjiAtrybutowEnum, podzielonyZbiorUczacy, i))){
								pokrycieMaxDlaDanejKombinacji = pokrycie;
								//System.out.println("obiekt: "+l+"pokrycje Max dla danej kombinacji"+ pokrycieMaxDlaDanejKombinacji);
								najlepszeWartosciowanieNumerycznych = wartosciowanieKombinacjiAtrybutowNumerycznych;
								najlepszeWartosciowanieEnum = wartosciowanieKombinacjiAtrybutowEnum;
								najlepszaKombinacja = k;
								podzbiorAtrybutowNajlepszejKombinacji = podzbiorAtrybutow;
							}
						}	
						if(pokrycieMaxWsrodWszystkichJElementowychKombinacji < pokrycieMaxDlaDanejKombinacji){
							pokrycieMaxWsrodWszystkichJElementowychKombinacji = pokrycieMaxDlaDanejKombinacji;
						}
						
						//System.out.println("po przejsciu przez obiekty "+pokrycieMaxDlaDanejKombinacji+"   "+pokrycieMaxWsrodWszystkichJElementowychKombinacji);
						//System.out.println("PokrycieMaxWsrodWszys... "+pokrycieMaxWsrodWszystkichJElementowychKombinacji);
					}
					if(pokrycieMaxWsrodWszystkichJElementowychKombinacji>0){
						//System.out.println("regula pokrywa: "+pokrycieMaxDlaDanejKombinacji);
						dodajRegule(podzbiory[najlepszaKombinacja], najlepszeWartosciowanieNumerycznych, najlepszeWartosciowanieEnum, rozwazanaKlasa);
						zaznaczPokrytePrzezTeRegule(podzbiorAtrybutowNajlepszejKombinacji, najlepszeWartosciowanieNumerycznych, najlepszeWartosciowanieEnum, podzielonyZbiorUczacy.get(i), indeksyJuzPokrytych.get(i));
					}
					//System.out.println("pokryciemaxglobal="+pokrycieMaxWsrodWszystkichJElementowychKombinacji);
					//System.out.println("indeksyjuzpokrytych size ="+indeksyJuzPokrytych.get(i).size()+"    podtabelasize="+podtabela.size());
				} while(pokrycieMaxWsrodWszystkichJElementowychKombinacji > 0);
				//System.out.println("Wyszlo poza while");
				
			}			
		}
	}
	
	
	public void zaznaczPokrytePrzezTeRegule(ArrayList<Atrybut> podzbiorAtrybutow, Przedzial[] wartosciowanieNumerycznych, String[] wartosciowanieEnumow, ArrayList<Obiekt> podtablica, ArrayList<Integer> indeksyJuzPokrytych){		
		Obiekt o;
		Integer index;
		int licznik;
		for(int i=0; i<podtablica.size(); i++){
			if(czyIJuzPokryte(i, indeksyJuzPokrytych)){
				;//do nothing
			} else {
				o = podtablica.get(i);
				index = new Integer(i);
				if(czyPokrywaObiekt(podzbiorAtrybutow, wartosciowanieNumerycznych, wartosciowanieEnumow, o)){
					//najpierw musze sprawdzic czy index nie znajduje sie juz na liscie
					licznik = 0;
					for(int j=0; j<indeksyJuzPokrytych.size(); j++){
						if(indeksyJuzPokrytych.get(j).equals(index)){
							break;
						} else {
							licznik++;
						}
					}
					if(licznik == indeksyJuzPokrytych.size())
						indeksyJuzPokrytych.add(index);
				}
			}
		}
	}
	
	public boolean czyNalezyDoPrzedzialu(double a, Przedzial p){
		//System.out.println(wartoscDouble);
		//System.out.println(dolGora[1]);
		double dol = p.dol;
		double gora = p.gora;
		if((dol<a)&&(a<=gora))
			return true;
		return false;	
	}

	public void dodajRegule(int[] numeryAtrybutow, Przedzial[] wartosciowanieAtrybutowNumerycznych, String[] wartosciowanieAtrybutowEnum, String klasa){
		ArrayList<Przeslanka> przeslanki = new ArrayList<Przeslanka>();
		for(int i=0; i<numeryAtrybutow.length; i++){
			if(Atrybuty.atrybuty.get(numeryAtrybutow[i]).czyNumeryczny())
				przeslanki.add(new Przeslanka(numeryAtrybutow[i], wartosciowanieAtrybutowNumerycznych[i]));
			else
				przeslanki.add(new Przeslanka(numeryAtrybutow[i], wartosciowanieAtrybutowEnum[i]));
		}
		String konkluzja = klasa;
		reguly.add(new Regula(przeslanki, konkluzja));
	}
	
	public void piszPodzielonyZbiorUczacy(ArrayList<ArrayList<Obiekt>> zbior){
		for(int i=0; i<zbior.size(); i++){
			System.out.println("Tu sie zaczyna "+(i+1)+" - ta czesc tabelki\n================================================================\n");
			for(int j=0; j<zbior.get(i).size(); j++){
				zbior.get(i).get(j).pisz(0);
			}
		}
	}
	
	public int ilePokrywa(ArrayList<Atrybut> podzbiorAtrybutow, Przedzial[] wartosciowanieNumerycznych, String[] wartosciowanieEnumow, ArrayList<Obiekt> podtablica, ArrayList<Integer> indeksyJuzPokrytych){
		int licznikPokrycia = 0;		
		Obiekt o;
		for(int i=0; i<podtablica.size(); i++){
			if(czyIJuzPokryte(i, indeksyJuzPokrytych)){
				;//do nothing
			} else {
				o = podtablica.get(i);
				if(czyPokrywaObiekt(podzbiorAtrybutow, wartosciowanieNumerycznych, wartosciowanieEnumow, o)){
					licznikPokrycia++;
					//indeksyJuzPokrytych.add(new Integer(i));
				}
			}
		}
		//System.out.println("licznikpokrycia="+licznikPokrycia);
		return licznikPokrycia;
	}
	
	public boolean czyIJuzPokryte(int i, ArrayList<Integer> indeksyJuzPokrytych){
		Integer iInteger = new Integer(i);
		
		for(int j=0; j<indeksyJuzPokrytych.size(); j++){
			if(iInteger.equals(indeksyJuzPokrytych.get(j)))
				return true;
		}
		return false;
	}
	
	
	public boolean czyPokrywaObiekt(ArrayList<Atrybut> podzbiorAtrybutow, Przedzial[] wartosciowanieNumerycznych, String[] wartosciowanieEnumow, Obiekt o){
		//System.out.println("wartosciowanie.size="+wartosciowanie.length+"  podzbioratrybutow = "+podzbiorAtrybutow.size());
		//System.out.print("Wartosciowanie: ");
		//for(int i=0; i<wartosciowanie.length; i++){
		//	System.out.print(wartosciowanie[i]+" ");
		//}
		//System.out.println();
		double wartoscAtrybutuNumerycznego;
		double dol, gora;
		Atrybut a1;
		Atrybut a2;	
		int licznikAtrybutow = 0;
		for(int j=0; j<Atrybuty.atrybuty.size(); j++){
			//System.out.println("\nNowyObiekt\n");
			a1 = Atrybuty.atrybuty.get(j);
			for(int k=0;k<podzbiorAtrybutow.size();k++){
				a2=podzbiorAtrybutow.get(k);
				//System.out.println("a1 = "+a1.nazwa+" a2 = "+a2.nazwa);
				if(a2.nazwa.equals(a1.nazwa)){
					if(a1.czyNumeryczny()){
						wartoscAtrybutuNumerycznego = o.wartosciAtrybutowNumerycznych[j];
						dol = wartosciowanieNumerycznych[k].dol;
						gora = wartosciowanieNumerycznych[k].gora;
						if((dol<wartoscAtrybutuNumerycznego)&&(wartoscAtrybutuNumerycznego<=gora))
							licznikAtrybutow++;
					} else {
						//System.out.println("o.wartosciAtrybutow.get(j) = "+o.wartosciAtrybutow.get(j)+" wartosciowanie[k] = "+wartosciowanie[k]);
						if(o.wartosciAtrybutowEnum[j].equals(wartosciowanieEnumow[k])){
							licznikAtrybutow++;
						}
					}
				}
			}
		}
		//System.out.println("Licznikatrybutow="+licznikAtrybutow+" podzbiorAtrybutow.size()="+podzbiorAtrybutow.size());
		if(licznikAtrybutow == podzbiorAtrybutow.size())
			return true;
		return false;
		
	}

	public boolean czyPokrywaInneKlasy(ArrayList<Atrybut> podzbiorAtrybutow, Przedzial[] wartosciowanieNumerycznych, String[] wartosciowanieEnumow, ArrayList<ArrayList<Obiekt>> podtablice, int indexOmijanejPodtablicy){
		Obiekt o;
		ArrayList<Obiekt> podtablica;
		
		for(int i=0; i<podtablice.size(); i++){
			if(i == indexOmijanejPodtablicy){
				;//do nothing
			} else {
				podtablica = podtablice.get(i);
				for(int j=0; j<podtablica.size(); j++){
					o = podtablica.get(j);
					if(czyPokrywaObiekt(podzbiorAtrybutow, wartosciowanieNumerycznych, wartosciowanieEnumow, o)){
						//System.out.println("Wczodzi tu w ogole?");
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public ArrayList<ArrayList<Atrybut>> zamienIndeksyNaAtrybuty(int[][] podzbiory){
		ArrayList<ArrayList<Atrybut>> kombinacjePodzbiorowAtrybutow = new ArrayList<ArrayList<Atrybut>>(); 
		ArrayList<Atrybut> podzbiorAtrybutow;
		Atrybut atrybut;
		for(int k=0; k<podzbiory.length; k++){
			atrybuty = Atrybuty.atrybuty;
			//zamieniam tabele podzbiorow atrybutow z tabeli double na tabele  (arraylist) atrybutow
			podzbiorAtrybutow = new ArrayList<Atrybut>();
			for(int l=0; l<podzbiory[0].length; l++){
				atrybut = atrybuty.get(podzbiory[k][l]);
				podzbiorAtrybutow.add(atrybut);
			}
			kombinacjePodzbiorowAtrybutow.add(podzbiorAtrybutow);
		}
		return kombinacjePodzbiorowAtrybutow;
	}
	
	
	public Przedzial[] getWartosciowanieKombinacjiAtrybutowNumerycznych(int[] kombinacjaAtrybutow, Obiekt obj){
		Przedzial[] wartosciowanieKombinacjiAtrybutowNumerycznych = new Przedzial[kombinacjaAtrybutow.length];
		for(int m=0; m<wartosciowanieKombinacjiAtrybutowNumerycznych.length; m++){
			int numerRozwazanegoAtrybutu = kombinacjaAtrybutow[m];
			Atrybut rozwazanyAtrybut = Atrybuty.atrybuty.get(numerRozwazanegoAtrybutu);
			if(rozwazanyAtrybut.czyNumeryczny()){
				double wartoscAtrybutu;
				wartoscAtrybutu = obj.wartosciAtrybutowNumerycznych[numerRozwazanegoAtrybutu];
				//do ktorego przedzialu wpada ta wartosc
				for(int a=0; a<rozwazanyAtrybut.getLiczbaMozliwychWartosciLubPrzedzialow(); a++){
					Przedzial przedz = rozwazanyAtrybut.przedzialy.get(a);
					if(czyNalezyDoPrzedzialu(wartoscAtrybutu, przedz)){
						//String przedzString = new String(przedz.dol+" "+przedz.gora);
						wartosciowanieKombinacjiAtrybutowNumerycznych[m] = przedz;
					}
				}
			}
		}
		return wartosciowanieKombinacjiAtrybutowNumerycznych;
	}
	
	public String[] getWartosciowanieKombinacjiAtrybutowEnum(int[] kombinacjaAtrybutow, Obiekt obj){
		String[] wartosciowanieKombinacjiAtrybutowEnum = new String[kombinacjaAtrybutow.length];
		for(int m=0; m<wartosciowanieKombinacjiAtrybutowEnum.length; m++){
			int numerRozwazanegoAtrybutu = kombinacjaAtrybutow[m];
			Atrybut rozwazanyAtrybut = Atrybuty.atrybuty.get(numerRozwazanegoAtrybutu);
			if(!rozwazanyAtrybut.czyNumeryczny()){
				wartosciowanieKombinacjiAtrybutowEnum[m] = obj.wartosciAtrybutowEnum[numerRozwazanegoAtrybutu];
			}
		}
		return wartosciowanieKombinacjiAtrybutowEnum;
	}	
	
	
	
}
