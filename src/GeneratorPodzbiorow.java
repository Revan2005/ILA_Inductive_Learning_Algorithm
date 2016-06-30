
public abstract class GeneratorPodzbiorow {
	
	public static int[][] generujPodzbiory(int n, int k){
		int liczbaKombinacji = kombinacje(n, k);
		int[][] wynik = new int[liczbaKombinacji][k];
		for(int i=0; i<k; i++)
			wynik[0][i] = i;
		for(int i=1; i<liczbaKombinacji; i++){
			wynik[i] = nastepnyPodzbior(n, k, wynik[i-1]);
		}
		return wynik;
	}
	
	public static int[] nastepnyPodzbior(int n, int k, int[] poprzedniPodzbior){
		//int liczbaKombinacji = kombinacje(n, k);
		//System.out.println("Liczba kombinacji = "+liczbaKombinacji);
		int[] wynik = new int[k];
		for(int i=0; i<k; i++){
			if( (poprzedniPodzbior[i]+1<n)&&( !nalezyDo((poprzedniPodzbior[i]+1), poprzedniPodzbior) ) ){
				wynik[i]=poprzedniPodzbior[i]+1;
				for(int j=0; j<i; j++){
					wynik[j] = j;
				}

				for(int j=(i+1); j<k; j++){
					wynik[j] = poprzedniPodzbior[j];
				}
				return wynik;
			}
		} return wynik;
	}
	
	public static boolean nalezyDo(int a, int[] set){
		for(int i=0; i<set.length; i++){
			if(a==set[i])
				return true;
		}
		return false;
	}
	
	public static int kombinacje(int n, int k){
		//long licznik = 1;
		double result = 1;
		for(int i=1; i<=k; i++){
			result*=(double)(n-i+1);
			result/=(double)i;
		}
		//System.out.println("n="+n+" k="+k);
		return (int)Math.round(result);
	}

}
