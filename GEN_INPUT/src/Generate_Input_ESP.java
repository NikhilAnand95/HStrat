import java.io.*;

public class Generate_Input_ESP 
{
	
	private int nodes;
	private int sim_time;
	private int people;
	
	private double local_prob;
	private double inf_threshold;
	 
	 private String  path_position_folder;
	 private String  path_state_folder;
	 private String  path_mapping_folder;
	
	// private String folderLoc = "/home/ramesh/ESP/ESP_Input/";
    
    /* constructor */
	Generate_Input_ESP(int _nodes, int _simTime, int _people, double _localProb, double _infThreshold, String _position_folder, String _state_folder, String _mapping_folder)
	{
		nodes        = _nodes;
		sim_time     = _simTime;
		people       = _people;
		//avg_pop		 =  people/nodes;
		local_prob	 = _localProb;
		inf_threshold = _infThreshold;
		path_position_folder = _position_folder;
		path_state_folder    = _state_folder;
		path_mapping_folder  = _mapping_folder;	
		
		if( (path_mapping_folder == null) || (path_state_folder == null) || (path_position_folder == null) )
 		{	
			System.out.println("Error in the mapping or state or position folder's path");
			System.exit(1);
		}
	}/* End of Generate_Input_ESP() */
	
	/* write generated random position of people into separate file for each time unit*/
	public void write_positions_of_people(int time, int [] curr_pos_person) throws IOException
	{
		//String file_name = folderLoc + "position/" + time + ".txt";
		String file_name = System.getProperty("user.dir") + "/../../" + path_position_folder + time + ".txt";
		File fe = new File(file_name);
		
		if(fe.exists())
			fe.delete();	
		
		//fe.createNewFile();
		
		FileWriter fw = new FileWriter(file_name);
		
		fw.write("" + (people) );
		
		for(int i=0; i<people; i++)
		{
			fw.write("\n" + (i) + " " + (curr_pos_person[i]));
		}		
		
		fw.close();
		
	}/* End Of write_positions_of_people()*/
	
	/* selects position of individuals and HCUs randomly based on probability distributions, Function Parameters: (init_pop_dist, i)*/
	public int random_position_selection(int org_pos, int curr_pos) 
	{
		double rn = Math.random();
		double sum = 0.0;

		for(int i=0; i<nodes; i++) 
		{
		   if(org_pos == i)
			   sum = sum + local_prob;
		   else
			   sum = sum + (1-local_prob)/(nodes-1);
  
		   if(rn <= sum)
		   {
			  // System.out.print("Random :" + (rn) + "; Matching Prob : " + (sum));
		       return i;
		   }		   
		}
		
		System.out.println("Error!! Check it once  : " + (rn) + " " + (sum) );
		 return 0;
		 
	}/* End of random_selection() */
	
		
	/* selects position of individuals randomly based on initial probability distributions, Function Parameters: (init_pop_dist) */
	public void initial_positions_of_people(int [] org_pos_person, int [] curr_pos_person) throws IOException 
	{
		//curr_pos_people = new int[people];	
		
		for(int i=0; i<people; i++) 
		{
			System.out.print("\nperson Id: " + (i+1) + "; ");
			int pos = org_pos_person[i]; //random_position_selection(init_pop_dist, i);			
			curr_pos_person[i] = org_pos_person[i];			 	
			System.out.print("; position : "+ (pos+1));						
		}		
		
		write_positions_of_people(0, curr_pos_person);
		
	}/* End of initial_positions_people() */
	
		
	/* starting point of simulation,  Function Parameters: (init_prob_of_people, init_prob_of_HCUs, adj_matrix_of_graph, init_infected_people, trans_prob_of_people, trans_prob_of_HCUs);	  */
	public void start_simulation(int [] org_pos_person, int [] curr_pos_person) throws IOException 
	{
	 	System.out.println("\nAt time t = " + (0)+ "\n");
		initial_positions_of_people(org_pos_person, curr_pos_person);	
		next_state_of_people(0);	
		next_mapping_of_people(0);
		run_simulation(org_pos_person, curr_pos_person);
    	
	}/* End of start_simulation() */
	
	
	/* selects the position of people for next time unit/step randomly based on connectivity, Function Parameters : (curr_pos_people, init_pop_dist, adj_matrix, trans_pop_dist); */
	public void next_positions_of_people(int time, int [] org_pos_person, int [] curr_pos_person) throws IOException 
	{
		    for(int i=0; i<people; i++)
			{
				int pos  = curr_pos_person[i];
				System.out.print("\nPerson Id : " + (i+1) + "; curr position : " + (pos+1) + "; " );
				curr_pos_person[i] = random_position_selection(org_pos_person[i], pos);
				pos = curr_pos_person[i];
				System.out.print("; next position : " + (pos+1) +"; original position : " + (org_pos_person[i]+1));				
			}		    
			write_positions_of_people(time, curr_pos_person);

    }/* End of next_positions_of_people() */
   
  
	/* finds the next state of people , Function Parameters : (time); */
	public void next_mapping_of_people(int time) throws IOException
	{
		
		//String file_name  = folderLoc + "mapping/"+time+".txt";
		String file_name  = System.getProperty("user.dir") + "/../../" +path_mapping_folder+time+".txt";
		
		File fe = new File(file_name);
		
		if(fe.exists())
			fe.delete();
		
		FileWriter fw = new FileWriter(file_name);
		
		fw.write("" + (people));
		
		System.out.println();	
		
		for(int i=0; i<1; i++)
		{
			fw.write("\n");
			System.out.print("\nperson Id : " + i + " ");
			
			for(int j=0; j<(0.2*people)-1; j++)
			{
			   double rn = Math.random();		    		
			   fw.write(Double.toString(rn) + " ");
			   System.out.print(rn + " ");
			}		
			
			double rn = Math.random();		    		
			fw.write(Double.toString(rn));	
			System.out.print(rn);

		}			
		fw.close();
		
	}/* End of next_state_of_people() */
	
	/* finds the next state of people , Function Parameters : (time); */
	public void next_state_of_people(int time) throws IOException
	{
		
		//String file_name  = folderLoc + "state/"+time+".txt";
		String file_name  = System.getProperty("user.dir") + "/../../" +path_state_folder+time+".txt";
		
		File fe = new File(file_name);
		
		if(fe.exists())
			fe.delete();
		
		FileWriter fw = new FileWriter(file_name);
		
		fw.write("" + (people));
		
		System.out.println();	
		
		for(int i=0; i<people; i++)
		{
			double rn = Math.random();
			
			//System.out.println(rn);	
			
			fw.write("\n" + (i) + " " + Double.toString(rn));						
		}		
		
		//System.out.println();	
		
		fw.close();
		
	}/* End of next_state_of_people() */
	
	/* next_stage_evolution(time, adj_matrix, trans_pop_dist); */
	public void next_stage_evolution(int time, int [] org_pos_person, int [] curr_pos_person) throws IOException 
	{
		System.out.println("\n\nAt time t = " + time);
		next_state_of_people(time);	
		next_mapping_of_people(time);
		next_positions_of_people(time, org_pos_person, curr_pos_person);				
		
	}/* End of next_stage_evolution()*/
   
	
	/* run simulation for next time step evolution of infected, recovered,  movement of population and HCUs over graph, Function parameters : (init_pop_dist, init_HCUs_dist, adj_matrix, trans_pop_dist, trans_HCUs_dist); */
	public void run_simulation(int [] org_pos_person, int [] curr_pos_person) throws IOException 
	{
        for(int j=1; j < sim_time; j++)		
	  	{ 
        	next_stage_evolution(j, org_pos_person, curr_pos_person);	  
		}        
		    		    
	}/* End Of run_simulation() */
}
