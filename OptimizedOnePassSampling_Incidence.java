import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

class InstanceAverageTriangle_Incidence {
	static int avgT3 = 0;
	public void addTrianglesCount(int t3) {
		
		avgT3 += t3;

	}
	public int getTriangleCount() {
		return avgT3;
		
	}
}
public class OptimizedOnePassSampling_Incidence extends Thread {
	
	InstanceAverageTriangle_Incidence avg;
	public OptimizedOnePassSampling_Incidence(InstanceAverageTriangle_Incidence avg) {
		
		this.avg = avg;
	}
	public void run(){
		int r=1000;
		//r=1000 (1,50 *r +1)
		//r = 10000 (1, 10*r+1)
		//r= 100000 (1,r/2+1)
		int r1 = ThreadLocalRandom.current().nextInt(1, 50*r+1);
		int r2 = ThreadLocalRandom.current().nextInt(1, 50*r+1);
		@SuppressWarnings("unchecked")
		HashMap<String,Integer>[] hashTable = new HashMap[2*r];
		
		ArrayList<HashMap<String,Integer>> samples = new ArrayList<HashMap<String,Integer>>();
		int P=0;
		int x=1;
		int M=r;
		int m=1;
		int B=0;
		int T3 = 0;
		int u=0;
		
		ArrayList<Integer> D = new ArrayList<Integer>();
		BufferedReader br = null;
		FileReader fr = null;
		int s=0;
		try {
			int a=0,b = 0,w,flag=0;
			String sCurrentLine;
			br = new BufferedReader(new FileReader("facebook_combined.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				
				String[] e = sCurrentLine.split(" ");
				
				while(P<x){
					if(flag==1){
						
						if((sCurrentLine = br.readLine()) != null){
							
							e = sCurrentLine.split(" ");
							
						}
					}
					a = Integer.parseInt(e[0]);
					b = Integer.parseInt(e[1]);
					if(a != u){
						D = new ArrayList<Integer>();
						u = a;
					}
					D.add(b);
					CheckTriangles(a,b,hashTable,r1,r2,r);
					P = P + D.size() - 1;
					
					flag = 1;
					
				}
				flag=0;	
				
				while(P>=x){
					w = D.get(D.size() + x - P-1);
					
					HashMap<String, Integer> sample = new HashMap<String, Integer>();
					sample.put("s", s);
					sample.put("w", w);
					sample.put("b", b);
					sample.put("a", a);
					
					samples.add(sample);
					
					HashMap<String, Integer> hashValue = new HashMap<String, Integer>();
					hashValue.put("c1", 0);
					hashValue.put("c2", 0);
					hashValue.put("c3", 0);
					hashValue.put("w", w);
					hashValue.put("b", b);
					hashValue.put("count", 1);
					
					if(b!=w){
						InsertIntoHashTable(w,b,hashValue,hashTable,r1,r2,r);
						
						s=s+1;
						m = m +1 ;
						
					}
					
					x = NextSample(1.0/m,P);
					
				}
				while(x>=M){
					M=M*2;
					m=m*2;
					s = CleanHalfSampleSet(samples,hashTable,r1,r2,r);
				}
			}
			System.out.println("P : "+P);
			B = calculateT(hashTable,B);
			T3 = B*P/(3*samples.size());
			
			synchronized (avg) {
				avg.addTrianglesCount(T3);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			
		}
				
	}
	
	private int calculateT(HashMap<String, Integer>[] hashTable,int B) {
		for(int i = 0; i<hashTable.length;i++){
			HashMap<String, Integer> arc = hashTable[i];
			if(arc!=null){
				System.out.println("arc : "+arc);
				B = B + (arc.get("c2")+(2*arc.get("c1")));
				System.out.println("B : "+B);
			}
			
		}
		
		return B;
	}

	private static int CleanHalfSampleSet(ArrayList<HashMap<String, Integer>> samples, HashMap<String, Integer>[] hashTable, int r1, int r2, int r) {
		int s = 1;
		int h_a ;
		int h;
		Iterator<HashMap<String, Integer>> iter = samples.iterator();
		while (iter.hasNext()) { 
			HashMap<String, Integer> sample = iter.next();
			if (ThreadLocalRandom.current().nextInt(1, 11) % 2 == 0) {
				if(sample.get("w")<sample.get("b")){
					h = (r1*sample.get("w") + r2*sample.get("b"))%(2*r);
				}
				else{
					h = (r1*sample.get("b") + r2*sample.get("w"))%(2*r);
				}
				if(hashTable[h]!=null){
					if(hashTable[h].get("c1")==0 && hashTable[h].get("c2")==0){
						iter.remove();
					}
				}
				
			}
			else{
				sample.put("s", s);
				s=s+1;
				if(sample.get("w")<sample.get("b")){
					h_a= (r1*sample.get("w") + r2*sample.get("b"))%(2*r);
				}
				else{
					h_a= (r1*sample.get("w") + r2*sample.get("b"))%(2*r);
				}
				if(hashTable[h_a]!=null){
					hashTable[h_a].put("s", s);
				}
			}
		}
		
		return samples.size();
	}
	private static int NextSample(double p, int k) {
		
		double alpha = Math.random();
		
		int kNew = (int) (k + (Math.ceil((Math.log((alpha-1)/(p-1))/Math.log(1-p))+1)));
		return kNew;
	}
	private static void CheckTriangles(int a, int b,HashMap<String, Integer>[] hashTable, int r1, int r2, int r) {
		int h;
		if(a<b){
			h = (r1*a + r2*b)%(2*r);
			if(h<0){
				h = r1*a + r2*b;
				System.out.println("h : " +h + "r1 : " +r1+ "r2 : " +r2+  "a:  " +a +"b: " +b );
			}
		}
		else{
			h = (r1*b + r2*a)%(2*r);
			if(h<0){
				h = r1*b + r2*a;
				//System.out.println(h + " " +r1+ " " +r2+  " " +a +" " +b );
				System.out.println("h : " +h + "r1 : " +r1+ "r2 : " +r2+  "a:  " +a +"b: " +b );
			}
		}
		if(hashTable[h] != null){
			if(a<=b){
				hashTable[h].put("c1", hashTable[h].get("count"));
				
			}
			else{
				hashTable[h].put("c2", hashTable[h].get("count"));
			}
			
		}
	}
	private static void InsertIntoHashTable(int w, int b, HashMap<String, Integer> hashValue, HashMap<String, Integer>[] hashTable, int r1, int r2, int r) {
		int h;
		
		if(w<b){
			h = (r1*w + r2*b)%(2*r);
			if(h<0 || r1<0 || r2<0){
				//System.out.println(h + r1 + r2);
				h = r1*w + r2*b;
			}
		}
		else{
			h = (r1*b + r2*w)%(2*r);
			if(h<0){
				h = r1*b + r2*w;
			}
		}
		
		if(hashTable[h]!=null){
			hashTable[h].put("count", hashTable[h].get("count")+1);
		}
		else{
			hashTable[h]=hashValue;
		}
			
	}
	
	public static void main(String[] args) throws InterruptedException {
		ArrayList<Integer> nodes = new GetNodes().getnodes();
		InstanceAverageTriangle_Incidence avg = new InstanceAverageTriangle_Incidence();
		int instance = 100;
		int tmp = instance;
		while(tmp>0){
			OptimizedOnePassSampling_Incidence t1=new OptimizedOnePassSampling_Incidence(avg); 
			t1.start();
			t1.join();
			tmp--;
		}
		
		int T3 = avg.getTriangleCount()/instance;
		System.out.println("triangle count" + T3);
	}
	

}
