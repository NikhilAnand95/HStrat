import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class CreateBANodes_ESP 
{
	
	int nodeId;
	int population;
	
	/* Constructor */
	CreateBANodes_ESP(int _nodeId, int _population)
	{
		nodeId     = _nodeId;
		population = _population;
	}	

	public void read_input_of_node(String input_file_name, int start_index, HashMap<Integer, ArrayList<Integer>> hm) throws Exception 
	{
	        String lineRead   = null;
	         BufferedReader br = null;

	         try
	         {

	         	      br = new BufferedReader(new FileReader(input_file_name));
	        	lineRead = br.readLine();
	         	int firstline = Integer.parseInt(lineRead);
	         	if((firstline != population))
	         	{
	         	   System.out.println(" \nError! Miss match of nodes with in configuration file\n ");
			       return;
	        	}
	         	
	         	int agent_id = start_index;
	            while(( lineRead=br.readLine() ) != null)
	            {
	                   parse_input_int_data(hm, lineRead, agent_id);
	                   agent_id = agent_id + 1;
	            }
	          }
		      catch(Exception e) 
		      {
	         	e.printStackTrace();
	      	  }         
	          finally
	          {
	                br.close();        
	          }

	  }/* End of read_input_int_data() */	
	   
	   public void parse_input_int_data( HashMap<Integer, ArrayList<Integer>> hm, String data, int agent_id)
	   {
		   ArrayList<Integer> mylist = new ArrayList<Integer>();		   
	       StringTokenizer st = new StringTokenizer(data,", ");   
	       
	       mylist.add(nodeId);
	       mylist.add(nodeId);
	       int count = 0;
	       while (st.hasMoreTokens()) 
	       {  
	         int num = Integer.parseInt(st.nextToken()); 
	         mylist.add(num);
	         count = count+1;
	       }
	       mylist.add(2, count);	       
	       hm.put(agent_id, mylist);
	       System.out.println("Person Id :" + agent_id + " information : " + hm.get(agent_id));
	   }/* End of parse_input_int_data() */
	   
	   
}
