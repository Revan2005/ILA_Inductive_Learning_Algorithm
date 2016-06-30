
public class Przeslanka {
	int numerAtrybutu;
	String wartosc;
	
	public Przeslanka(int nrAtrybutu, String w){
		numerAtrybutu = nrAtrybutu;
		wartosc = w;
	}
	
	public Przeslanka(int nrAtrybutu, Przedzial w){
		numerAtrybutu = nrAtrybutu;
		wartosc = new String(w.dol+" "+w.gora);
	}
}
