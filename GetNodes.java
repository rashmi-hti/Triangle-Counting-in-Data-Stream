import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class GetNodes {
	public ArrayList<Integer>  getnodes(){
		BufferedReader br = null;
		FileReader fr = null;
		LinkedHashSet<Integer> test = new LinkedHashSet<Integer>();
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		try {
			
			String sCurrentLine;

			br = new BufferedReader(new FileReader("facebook_combined.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] fields = sCurrentLine.split(" ");
				
				int a = Integer.parseInt(fields[0]);
				int b = Integer.parseInt(fields[1]);
				if (test.add(a)) nodes.add(a);
				if (test.add(b)) nodes.add(b);
				
			}
			
		} 
		catch (IOException e) {

			e.printStackTrace();

		}
		return nodes; 
	}
}
