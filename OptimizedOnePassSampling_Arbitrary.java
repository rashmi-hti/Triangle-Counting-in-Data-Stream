import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class InstanceAverageTriangle {
	static int avgT3 = 0;
	public void addTrianglesCount(int t3) {
		
		avgT3 += t3;

	}
	public int getTriangleCount() {
		return avgT3;
		
	}
}
public class OptimizedOnePassSampling_Arbitrary extends Thread {
	
	ArrayList<Integer> nodes;
	InstanceAverageTriangle avg;
	public OptimizedOnePassSampling_Arbitrary(ArrayList<Integer> nodes, InstanceAverageTriangle avg) {
		this.nodes = nodes;
		this.avg = avg;
	}
	

	public void run(){
		int r=1000;
		int r1 = ThreadLocalRandom.current().nextInt(1, r + 1);
		int r2 = ThreadLocalRandom.current().nextInt(1, r + 1);
		@SuppressWarnings("unchecked")
		HashMap<String,Integer>[] hashTable = new HashMap[2*r];
		
		ArrayList<HashMap<String,Integer>> samples = new ArrayList<HashMap<String,Integer>>();
		int A=0;
		int x=1;
		int M=r;
		int m=1;
		int B=0;
		int T3 = 0;
		BufferedReader br = null;
		FileReader fr = null;
		int s=0;
		
		
		try {
			int a,b,v;
			String sCurrentLine;
			br = new BufferedReader(new FileReader("facebook_combined.txt"));
			
			while ((sCurrentLine = br.readLine()) != null) {
				
				String[] e = sCurrentLine.split(" ");
				
				a = Integer.parseInt(e[0]);
				b = Integer.parseInt(e[1]);
				
				A=A+1;
				if(A==x){
					HashMap<String, Integer> sample = new HashMap<String, Integer>();
					s=s+1;
					Random random = new Random();
					while(true){
						v = nodes.get(random.nextInt(nodes.size()));
						if(v != a && v!=b){
							break;
						}
					}
					sample.put("s", s);
					sample.put("a", a);
					sample.put("b", b);
					sample.put("v", v);
					sample.put("count", 0);
					samples.add(sample);
					InsertIntoHashTable(a,v,s,sample,hashTable,r1,r2,r);
					InsertIntoHashTable(b,v,s,sample,hashTable,r1,r2,r);
					m=m+1;
					
					x = NextSample(1.0/m,A);
					
				}
				if(A==M){
					M=2*M;
					m=2*m;
					s = CleanHalfSampleSet(samples,hashTable,r1,r2,r);
					
				}
				CheckTriangles(a,b,hashTable,r1,r2,r);
			}
			Iterator<HashMap<String, Integer>> iter = samples.iterator();
			while (iter.hasNext()) { 
				HashMap<String, Integer> sample = iter.next();
				//System.out.println(sample.get("s")+" "+sample.get("a")+" "+sample.get("b") +" "+sample.get("v")+" "+ sample.get("count"));
				if(sample.get("count") >= 2){
					B=B+1;
				}
			}
//			System.out.println("ss"+samples.size());
//			System.out.println("B" + B);
//			System.out.println("a" + A);
//			System.out.println("nod" + nodes.size());
			T3 = (int)(((double)B/samples.size())*nodes.size()*A);
			System.out.println("triangle count inside"+T3);
			synchronized (avg) {
				avg.addTrianglesCount(T3);
			}
			
			

		} 
		catch (IOException e) {

			e.printStackTrace();

		} 
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		ArrayList<Integer> nodes = new GetNodes().getnodes();
		InstanceAverageTriangle avg = new InstanceAverageTriangle();
		int instance = 100;
		int tmp = instance;
		long startTime = System.currentTimeMillis();
		while(tmp>0){
			OptimizedOnePassSampling_Arbitrary t1=new OptimizedOnePassSampling_Arbitrary(nodes,avg); 
			t1.start();
			t1.join();
			tmp--;
		}
		
		int T3 = avg.getTriangleCount()/instance;
		System.out.println("triangle count" + T3);
		long endTime = System.currentTimeMillis();
		System.out.println("Runtime:" + (int)(endTime-startTime));
	}

	private static void CheckTriangles(int a, int b,HashMap<String, Integer>[] hashTable, int r1, int r2, int r) {
		int h = (r1*a + r2*b)%(2*r);
		if(hashTable[h] != null){
			if((hashTable[h].get("a") == a && (hashTable[h].get("v") == b)) ||
					(hashTable[h].get("a") == b && (hashTable[h].get("v") == a)) ||
						(hashTable[h].get("b") == a && (hashTable[h].get("v") == b)) ||
							(hashTable[h].get("b") == b && (hashTable[h].get("v") == a))){
				hashTable[h].put("count", hashTable[h].get("count")+1);
			}
			
		}
	}

	private static int CleanHalfSampleSet(ArrayList<HashMap<String, Integer>> samples, HashMap<String, Integer>[] hashTable, int r1, int r2, int r) {
		int s = 1;
		Iterator<HashMap<String, Integer>> iter = samples.iterator();
		while (iter.hasNext()) { 
			HashMap<String, Integer> sample = iter.next();
			if (ThreadLocalRandom.current().nextInt(1, 11) % 2 == 0) {
					iter.remove();
			}
			else{
				sample.put("s", s);
				s=s+1;
				int h_a = (r1*sample.get("a") + r2*sample.get("v"))%(2*r);
				int h_b = (r1*sample.get("b") + r2*sample.get("v"))%(2*r);
				hashTable[h_a].put("s", s);
				hashTable[h_b].put("s", s);
			}
		}
		
		return samples.size();
	}

	private static int NextSample(double p, int k) {
		
		double alpha = Math.random();
		
		int kNew = (int) (k + (Math.ceil((Math.log((alpha-1)/(p-1))/Math.log(1-p))+1)));
		return kNew;
	}

	private static void InsertIntoHashTable(int a, int v, int s, HashMap<String, Integer> sample, HashMap<String, Integer>[] hashTable, int r1, int r2, int r) {
		int h = (r1*a + r2*v)%(2*r);
		System.out.println("H" + h);
		hashTable[h] = sample;
	}
	
	
}
