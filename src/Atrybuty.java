import java.util.ArrayList;


public abstract class Atrybuty {
	
	static ArrayList<Atrybut> atrybuty = new ArrayList<Atrybut>();
	
	public static void add(Atrybut a){
		atrybuty.add(a);
	}
	
	public static Atrybut get(int i){
		return atrybuty.get(i);
	}
	
	public static int getLiczbaAtrybutowZKlasa(){
		return atrybuty.size();
	}
	
	public static Atrybut getAtrybutKlasowy(){
		return atrybuty.get(atrybuty.size()-1);
	}
	
	public static int getLiczbaKlas(){
		Atrybut aK = getAtrybutKlasowy();
		return aK.getLiczbaMozliwychWartosciLubPrzedzialow();
	}
	
	public static void dyskretyzuj(int liczbaPrzedzialow){
		//dla kazdego atrybutu oprocz ostatniego - ostatni to etykieta klasy
		Atrybut a;
		Obiekt o;
		double minValue, maxValue;
		for(int i=0; i<getLiczbaAtrybutowZKlasa()-1; i++){
			a = atrybuty.get(i);
			if(a.czyNumeryczny()){
				o = Main.dane.get(0);
				minValue = o.wartosciAtrybutowNumerycznych[i];
				maxValue = o.wartosciAtrybutowNumerycznych[i];
				for(int j=0; j<Main.dane.size(); j++){
					o = Main.dane.get(j);
					if(o.wartosciAtrybutowNumerycznych[i] <= minValue)
						minValue = o.wartosciAtrybutowNumerycznych[i];
					if(o.wartosciAtrybutowNumerycznych[i] >= maxValue)
						maxValue = o.wartosciAtrybutowNumerycznych[i];
				}
				a.dyskretyzuj(minValue, maxValue, liczbaPrzedzialow);
			}
		}
	}

}
