import java.util.ArrayList;

/*
 * 
 * teraz ucze sie na danych!! trzeba zmienic na podzbio
 * r uczacy zamiast calych danych, teraz jest tak lepiej bo latwiej weryfikowac 
 * poprawnosci, ale zeby zastosowac kroswalidacje trzeba podzielic na 
 * zbiory uczacy i testowy mam to zrobione trzeba tylko przelaczyc w 
 * funkcji kilka linijek nizej
 */


public class ILA {
	ArrayList<Obiekt> dane;
	int liczbaKlas;
	int liczbaFoldow;
	ArrayList<Obiekt> zbiorUczacy;
	ArrayList<Obiekt> zbiorTestowy;
	ArrayList<Regula> reguly;
	//ZbiorRegul reguly;
	double tP, tN, fP, fN;
	double accuracy, precision, recall, fmeasure;
	double[][] wynikiCzesciowe;
	double[] wynikiKoncowe;
	
	
	public ILA(ArrayList<Obiekt> data, int lK, int lF){
		dane = data;
		liczbaKlas = lK;
		liczbaFoldow = lF;
		
		zbiorUczacy = new ArrayList<Obiekt>();
		zbiorTestowy = new ArrayList<Obiekt>();
		wynikiKoncowe = new double[4];
		wynikiCzesciowe = new double[lF][4];
		
//======== aby uzyskac reguly wygenerowane dla calego zbioru danych nalezy: ponizsze odkomentowac, a zakomentowac petle for bez wiersza 44 (Kroswalidacja ...)
		/*
		reguly = generujReguly(dane).reguly;
		wynikiCzesciowe[0] = testuj(dane, reguly);
		piszReguly(0);
		piszWynikiCzesciowe(0);
		*/
		Kroswalidacja kroswalidacja = new Kroswalidacja(lF, data);
		
		for(int i=0; i<lF; i++){
			zbiorUczacy = kroswalidacja.getZbiorUczacy(i);
			zbiorTestowy = kroswalidacja.getZbiorTestowy(i);
			if((zbiorUczacy.isEmpty())||(zbiorTestowy.isEmpty()))
				continue;
			
			reguly = generujReguly(zbiorUczacy).reguly;
			wynikiCzesciowe[i] = testuj(zbiorTestowy, reguly);
			piszReguly(i);
			piszWynikiCzesciowe(i);
		}

		int[] licznosciFoldow = kroswalidacja.getLicznosciFoldow();
		//licznosci foldow beda stanowily wagi suma tych licznosci = liczbie obiektow na liscie dane:
		//int suma=0;
		//for(int i=0; i<licznosciFoldow.length; i++){
		//	suma+=licznosciFoldow[i];
		//}
		//System.out.println("a                                    suma = "+suma+"  dane.size="+dane.size());
		double[] wagi = new double[liczbaFoldow];
		for(int i=0; i<liczbaFoldow; i++){
			wagi[i] = (double)licznosciFoldow[i]/dane.size();
			//System.out.println("==========================================================================wagi["+i+"]="+wagi[i]);
		}
		
		for(int i=0; i<4; i++)
			wynikiKoncowe[i] = 0;
		for(int i=0; i<4; i++){
			for(int j=0; j<liczbaFoldow; j++){
				wynikiKoncowe[i] = wynikiKoncowe[i] + wynikiCzesciowe[j][i] * wagi[j];
			}
		}
		
	}
	
	public ZbiorRegul generujReguly(ArrayList<Obiekt> zU){
		return new ZbiorRegul(zU);
		//return new ZbiorRegul(dane);
	}
	
	public void piszReguly(int i){
		System.out.println("\n\n\n\nFold: "+i);
		String nazwaAtrybutu;
		for(int j=0; j<reguly.size(); j++){
			Regula r = reguly.get(j);
			for(int k=0;k<r.przeslanki.size(); k++){
				Przeslanka p = r.przeslanki.get(k);
				//atrybuty powinny byc klasa abstrakcyjna mam tak w ver 2 tu balem sie ze jak przerobie to przestanie dzialac
				nazwaAtrybutu = Atrybuty.atrybuty.get(p.numerAtrybutu).nazwa;
				System.out.print("AND "+nazwaAtrybutu+"=("+p.wartosc+"] ");
			}
			System.out.print(" => "+r.konkluzja+"\n");
		}
	}
	
	public void podziel(int iteracja){
		// to trzeba zrobic losowo
		//System.out.println(iteracja+"ta iteracja dzielenia na zbiory testowy i uczacy:\n");
		int licznoscZbioruTestowego = dane.size()/liczbaFoldow;
		int poczatek = iteracja*licznoscZbioruTestowego;
		int koniec = (iteracja+1)*licznoscZbioruTestowego;
		zbiorTestowy = new ArrayList<Obiekt>();
		zbiorUczacy = new ArrayList<Obiekt>();
		int licznik=0;
		for(int i=0; i<dane.size(); i++){
			if((i<poczatek)||(i>=koniec))
				zbiorUczacy.add(dane.get(i));
			else{
				zbiorTestowy.add(dane.get(i));
				licznik++;
				//System.out.print(dane.get(i).wartosciAtrybutow.get(dane.get(i).wartosciAtrybutow.size()-1)+" ");
			}
		}
		//System.out.println("\nlicznik = "+licznik+"\n");
		//piszZbiory();
	}
	
	public void piszZbiory(){
		System.out.println("\nZbior uczacy:==================================\n");
		for(int i=0; i<zbiorUczacy.size(); i++){
			zbiorUczacy.get(i).pisz(i);
		}
		System.out.println("\nZbior testowy:=================================\n");
		for(int i=0; i<zbiorTestowy.size(); i++){
			zbiorTestowy.get(i).pisz(i);
		}
	}
	
	public void piszWynikiCzesciowe(int i){
		System.out.println("\n\nWyniki Fold: "+i);
		//tp fn itd trzeba pisac w kontekscie klasy wiec tu bedzie macierz
		System.out.println("Accuracy (weighted avg.) = "+wynikiCzesciowe[i][0]);
		System.out.println("Precision (weighted avg.) = "+wynikiCzesciowe[i][1]);
		System.out.println("Recall (weighted avg.) = "+wynikiCzesciowe[i][2]);
		System.out.println("F-Measure (weighted avg.) = "+wynikiCzesciowe[i][3]);
	}
	
	public void piszWynikiKoncowe(){
		System.out.println("\n\nWyniki koncowe dla liczbyFoldow = "+liczbaFoldow+":");
		//tp fn itd trzeba pisac w kontekscie klasy wiec tu bedzie macierz
		System.out.println("Accuracy (weighted avg.) = "+wynikiKoncowe[0]);
		System.out.println("Precision (weighted avg.) = "+wynikiKoncowe[1]);
		System.out.println("Recall (weighted avg.) = "+wynikiKoncowe[2]);
		System.out.println("F-Measure (weighted avg.) = "+wynikiKoncowe[3]);
	}
	
	public double[] testuj(ArrayList<Obiekt> zT, ArrayList<Regula> reguly){
		//testowanie regul, zwracam wartosci parametrow
		zbiorTestowy = zT;
		
		double[] wyniki = new double[4];
		
		int tP=0,  tN=0, fP=0, fN=0;
		double accuracy=0, precision=0, recall=0, fmeasure=0;
		
		String przewidywanaKlasa, rzeczywistaKlasa, rozwazanaKlasa;
		Obiekt o;
		//przejedz po wszsytkich klasach
		Atrybut atrybutKlasowy = Atrybuty.getAtrybutKlasowy();
		int liczbaKlas = atrybutKlasowy.dziedzina.size();
		double[][] wynikiDlaPoszczegolnychKlas = new double[liczbaKlas][8];
		
		for(int i=0; i<liczbaKlas; i++){
			tP=0;  tN=0; fP=0; fN=0;
			accuracy=0; precision=0; recall=0; fmeasure=0;
			rozwazanaKlasa = atrybutKlasowy.dziedzina.get(i);
			//jade po obiektach w calym zbiorze testowym
			for(int j=0; j<zbiorTestowy.size(); j++){
				o = zbiorTestowy.get(j);
				rzeczywistaKlasa = o.getEtykietaKlasy();
				przewidywanaKlasa = okreslKlaseNaPodstawieRegul(o, reguly);
				if(o.getEtykietaKlasy().equals(rozwazanaKlasa)){
					if(przewidywanaKlasa.equals(rzeczywistaKlasa))
						tP++;
					else{
						fN++;
						//System.out.println("obiekt klasy "+o.getEtykietaKlasy()+" blednie niezaklasyfikowany do niej = ");
						//o.pisz(0);
					}
				} else {
					if(przewidywanaKlasa.equals(rozwazanaKlasa))
						fP++;
					else
						tN++;
				}
			}
			accuracy = (double)(tP+tN)/(tP+tN+fP+fN);
			if((tP+fP) == 0)
				precision = 1;
			else
				precision = (double)tP/(tP+fP);
			if((tP+fN) == 0)
				recall = 1;
			else
				recall = (double)tP/(tP+fN);
			if((precision+recall) == 0)
				fmeasure = 0;
			else
				fmeasure = (double)2*precision*recall/(precision+recall);
			wynikiDlaPoszczegolnychKlas[i][0] = tP;
			wynikiDlaPoszczegolnychKlas[i][1] = tN;
			wynikiDlaPoszczegolnychKlas[i][2] = fP;
			wynikiDlaPoszczegolnychKlas[i][3] = fN;
			wynikiDlaPoszczegolnychKlas[i][4] = accuracy;
			wynikiDlaPoszczegolnychKlas[i][5] = precision;
			wynikiDlaPoszczegolnychKlas[i][6] = recall;
			wynikiDlaPoszczegolnychKlas[i][7] = fmeasure;
		}
		
		//brzydko jest przez to ze wyzej accuracy zaczyna sie na indeksie 4 a nie 0
		wyniki = wyliczWynikiSrednieWazone( zbiorTestowy, wynikiDlaPoszczegolnychKlas);
		
		return wyniki;
	}
	
	public double[] wyliczWynikiSrednieWazone(ArrayList<Obiekt> zbiorTestowy, double[][] wynikiDlaKlas){
		//wage stanowi liczba obiektow w klasie(w zbiorze testowym) podzielona na liczbe wszystkich obiektow w zbiorze testowym
		double[] wynikiAvg = new double[4];
		double accuracyAvg=0, precisionAvg=0, recallAvg=0, fmeasureAvg=0;
		//ustalam wagi - indeks odpowiada klasie
		ArrayList<String> etykietyKlas = Atrybuty.getAtrybutKlasowy().dziedzina;
		double[] wagi = new double[etykietyKlas.size()];
		for(int i=0; i<etykietyKlas.size(); i++){
			wagi[i] = 0;
		}
		Obiekt o;
		for(int i=0; i<zbiorTestowy.size(); i++){
			o = zbiorTestowy.get(i);
			//przechodze po wszystkich mozliwych etykietach klas
			for(int j=0; j<etykietyKlas.size(); j++){
				if(o.getEtykietaKlasy().equals(etykietyKlas.get(j))){
					wagi[j]+=1;
				}
			}
		}
		for(int i=0; i<wagi.length; i++){
			wagi[i] = wagi[i]/(double)zbiorTestowy.size();
			System.out.println("Waga dla klasy: "+i+" = "+wagi[i]);
		}
		
		//iteruje po klasach
		for(int i=0; i<wynikiDlaKlas.length; i++){
			accuracyAvg += wynikiDlaKlas[i][4]*wagi[i];
			precisionAvg += wynikiDlaKlas[i][5]*wagi[i];
			recallAvg += wynikiDlaKlas[i][6]*wagi[i];
			fmeasureAvg += wynikiDlaKlas[i][7]*wagi[i];
		}
		wynikiAvg[0] = accuracyAvg;
		wynikiAvg[1] = precisionAvg;
		wynikiAvg[2] = recallAvg;
		wynikiAvg[3] = fmeasureAvg;
		return wynikiAvg;
	}

	
	public String okreslKlaseNaPodstawieRegul(Obiekt o, ArrayList<Regula> reguly){
		Regula r;
		Przeslanka p;
		int licznik;
		String klasa;
		double wartosc;
		double przedzialWynikajacyZPrzeslankiDol;
		double przedzialWynikajacyZPrzeslankiGora;
		String[] przedzialWynikajacyZPrzeslanki;
		
		for(int i=0; i<reguly.size(); i++){
			r = reguly.get(i);
			licznik = 0;
			for(int j=0; j<r.przeslanki.size(); j++){
				p = r.przeslanki.get(j);
				if(Atrybuty.atrybuty.get(p.numerAtrybutu).czyNumeryczny()){
					wartosc = o.wartosciAtrybutowNumerycznych[p.numerAtrybutu];
					przedzialWynikajacyZPrzeslanki = p.wartosc.split(" ");
					przedzialWynikajacyZPrzeslankiDol = Double.parseDouble(przedzialWynikajacyZPrzeslanki[0]);
					przedzialWynikajacyZPrzeslankiGora = Double.parseDouble(przedzialWynikajacyZPrzeslanki[1]);
					if( (przedzialWynikajacyZPrzeslankiDol<wartosc)&&(wartosc<=przedzialWynikajacyZPrzeslankiGora) )
						licznik++;
				} else {
					if(p.wartosc.equals(o.wartosciAtrybutowEnum[p.numerAtrybutu]))
						licznik++;
				}
			}
			if(licznik == r.przeslanki.size()){
				klasa = r.konkluzja;
				return klasa;
			}
		}
		return new String("Dla obiektu klasy"+ o.getEtykietaKlasy()+" zadna wygenerowana regula nie przypisala mu klasy.");
	}

	
	
	
}
