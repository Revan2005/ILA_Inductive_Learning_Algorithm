import java.util.ArrayList;


public class Atrybut {
	String nazwa;
	String typ;
	ArrayList<String> dziedzina;
	ArrayList<Przedzial> przedzialy;
	
	public Atrybut(String nazwaAtrybutu, String typAtrybutu){
		dziedzina = new ArrayList<String>();
		przedzialy = new ArrayList<Przedzial>();
		nazwa = nazwaAtrybutu;
		typ = typAtrybutu;
		//jezeli typ nie jest liczbowy to wypelnij liste zbior wartosci
		if((!typ.equals("REAL"))&&(!typ.equals("real"))){
			String wartosciEnum = typAtrybutu;
			wartosciEnum = wartosciEnum.replace('{', ' ');
			wartosciEnum = wartosciEnum.replace('}', ' ');
			wartosciEnum = wartosciEnum.trim();
			String[] split = wartosciEnum.split(",");
			for(int i=0; i<split.length; i++)
				dziedzina.add(split[i]);
		}
		//dyskretyzuj(100, 200, 10);
		//piszWszystko();
	}
	
	public String getWartosc(int i){
		if(czyNumeryczny()){
			Przedzial przedzial = przedzialy.get(i);
			String przedzialString = new String(przedzial.dol+" "+przedzial.gora);
			return przedzialString; 
		}
		return dziedzina.get(i);
	}
	
	public void dyskretyzuj(double minValue, double maxValue, int numIntervals){
		//jesli typ jest liczbowy to dyskretyzuj jesli nie to nie rob nic
		if((typ.equals("REAL"))||typ.equals("real")){
			double dlugoscPrzedzialu = (maxValue-minValue)/numIntervals;
			//2 linie ponizej zeby wartosc minValue tez wpadla do ktoregos (dokladniej - najnizszego) przedzialu
			double dol = minValue-10000000;
			double gora = minValue+dlugoscPrzedzialu;
			przedzialy = new ArrayList<Przedzial>();
			przedzialy.add(new Przedzial(dol, gora));
			for(int i=1; i<numIntervals-1; i++){
				dol = minValue+(i*dlugoscPrzedzialu);
				gora = minValue+((i+1)*dlugoscPrzedzialu);
				przedzialy.add(new Przedzial(dol, gora));
			}
			dol = minValue+((numIntervals-1)*dlugoscPrzedzialu);
			gora = minValue+((numIntervals)*dlugoscPrzedzialu)+10000000;
			przedzialy.add(new Przedzial(dol, gora));
		} else {
			;
		}
		//System.out.println("Po dyskretyzacji liczba mozliwych przedzialow = "+przedzialy.size()+" numintervals="+numIntervals);
	}
	
	public int getLiczbaMozliwychWartosciLubPrzedzialow(){
		if((typ.equals("REAL"))||(typ.equals("real")))
			return przedzialy.size();
		return dziedzina.size();
	}

	public void piszWszystko(){
		System.out.print("\nNazwa: "+nazwa+", typ: "+typ);
		if((typ.equals("REAL"))||typ.equals("real"))
			for(int i=0; i<przedzialy.size(); i++)
				przedzialy.get(i).piszPrzedzial();
		else
			for(int i=0; i<dziedzina.size(); i++)
				System.out.print(" "+dziedzina.get(i)+" ");
		System.out.println("\n\n");
	}
	
	public boolean czyNumeryczny(){
		if((typ.equals("REAL"))||(typ.equals("real")))
			return true;
		return false;
	}
}
