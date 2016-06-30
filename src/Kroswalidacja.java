import java.util.ArrayList;
import java.util.Random;


public class Kroswalidacja {
	public ArrayList<ArrayList<Obiekt>> podzielonyLosowoZbiorDanych;
	public int lF;
	//int[] juzWylosowane;
	
	//czy moge sobie swobodnie modyfikowac dane przekazane w parametrze nie zmieniajac danych w ILA?
	//let's check it - odpowiedz - nie moge!!!
	public Kroswalidacja(int liczbaFoldow, ArrayList<Obiekt> dane){
		lF = liczbaFoldow;
		podzielonyLosowoZbiorDanych = new ArrayList<ArrayList<Obiekt>>();
		for(int i=0; i<liczbaFoldow; i++)
			podzielonyLosowoZbiorDanych.add(new ArrayList<Obiekt>());
		//juzWylosowane = new int[dane.size()];
		//for(int i=0; i<dane.size(); i++)
		//	juzWylosowane[i] = -1;
		int liczbaObiektowWFoldzie;
		if(dane.size()%liczbaFoldow == 0)
			liczbaObiektowWFoldzie = dane.size()/liczbaFoldow;
		else
			liczbaObiektowWFoldzie = (dane.size()/liczbaFoldow)+1; //plus 1 bo dzielenie int'ow zaokragla w dol!! a ja chce sufit
		Random rand = new Random();
		int r;
		for(int i=0; i<dane.size(); i++){
			r = rand.nextInt(liczbaFoldow);
			//dopoki nieprawda ze ...
			while(!(podzielonyLosowoZbiorDanych.get(r).size()<liczbaObiektowWFoldzie))
				r = rand.nextInt(liczbaFoldow);
			podzielonyLosowoZbiorDanych.get(r).add(dane.get(i));
		}
		
		piszPodzielonyLosowoZbiorDanych();
		
	}
	
	public void piszPodzielonyLosowoZbiorDanych(){
		for(int i=0; i<podzielonyLosowoZbiorDanych.size(); i++){
			System.out.println("podzbior: "+(i+1)+" licznosc="+podzielonyLosowoZbiorDanych.get(i).size());
			for(int j=0; j<podzielonyLosowoZbiorDanych.get(i).size(); j++){
				System.out.print(podzielonyLosowoZbiorDanych.get(i).get(j).getEtykietaKlasy()+"  ");
			}
			System.out.println("\n");
		}
	}
	
	
	public ArrayList<Obiekt> getZbiorTestowy(int numerFolda){
		return podzielonyLosowoZbiorDanych.get(numerFolda);
	}
	
	public ArrayList<Obiekt> getZbiorUczacy(int numerFolda){
		ArrayList<Obiekt> zbiorUczacy = new ArrayList<Obiekt>();
		for(int i=0; i<podzielonyLosowoZbiorDanych.size(); i++){
			if(i == numerFolda){
				; //do nothing
			} else {
				for(int j=0; j<podzielonyLosowoZbiorDanych.get(i).size(); j++){
					zbiorUczacy.add(podzielonyLosowoZbiorDanych.get(i).get(j));
				}
			}
		}
		return zbiorUczacy;
	}
	
	//w zasadzie tylko ostatni fold moze miec inna liczbe elementow niz pozostale ale zwroce cala tablice
	//a wcale ze nie ostatni tylko jeden losowy albo kilka, - dobrze ze to zrobilem!!
	public int[] getLicznosciFoldow(){
		//lF = liczba foldow nie myslic z licznoscia
		int[] licznosciFoldow = new int[lF];
		for(int i=0; i<lF; i++){
			licznosciFoldow[i] = podzielonyLosowoZbiorDanych.get(i).size();
		}
		return licznosciFoldow; 
	}

}
