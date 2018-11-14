import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Simulation_ESP 
{
	
	private int nodes;
	private int sim_time;
	private int people;
	private int hcus;
	private int num_infected;
	private int sample_size;
	private double local_prob;
	private double inf_threshold;	
	private double beta;
	private double delta;
	private int curr_time;
	
	private int num_HCU_recover = 0;
	
	private int [] curr_pos_people;                  //positions of people at current instance of time
	private int [] curr_pos_HCUs;                   //positions of HCUs at current instance of time
	
	private int [][] prev_pos_HCUs;                   //positions of HCUs at previous instance of time
	
	private int [][] num_pop_node;                   //stores number of people at each node at current instance of time
	
	private double [][] map_prob_person;               //stores probability of mapping with neighbors at current instance of time

	private int [] num_infect_node;               //stores number of infected people at each node at current instance of time
	private int [] num_infect_sample;			 //stores number of infected people within sample at each node at current instance of time
	
    private int [] curr_state_people;            //keeps track of state of individuals whether infected or suspected at current time step
    private int [] next_state_people;			//keeps track of state of individuals whether infected or suspected at next time step
    
    private int [] time_stamp_HCUs;				// stores the arrival time of HCU at node
    private int [] time_stamp_people;				// stores the arrival time of HCU at node
    
    private boolean [] scan_people;		       // keeps track of status of scan of person, whether scanned at this node or not for particular duration of period

    private double [] infect_prob_people;     // stores the infection probability of each individual at current instance of time
    
    private double [] healthy_prob_node;     //  stores the probability of not getting infection from its neighbors at current instance of time 
	
    private int[][] agent_hm; 
    private HashMap<Integer, ArrayList<Integer>> org_hm;

    
    private String outputFile     = "output/output.txt";  //output file to write the data after each iteration
    private String outputFile1     = "output/output1.txt"; 
	private String stateFolder    = "state/";
	private String positionFolder = "position/";
	private String mappingFolder  = "mapping/";
	private String outputFolder   = "output/";
	
	
    /* constructor */
	Simulation_ESP(int _nodes, int _simTime, int _people, int _hcus, int _infected, int _sampleSize, double _localProb, double _infThreshold, double _beta, double _delta, String _positionFolder, String _stateFolder, String _mappingFolder, String _outputFolder)
	{
		nodes          = _nodes;
		sim_time       = _simTime;
		people         = _people;
		hcus           = _hcus;
		num_infected   = _infected;
		sample_size	   = _sampleSize;
		local_prob     = _localProb;
		inf_threshold  = _infThreshold;
		beta           = _beta;
		delta          = _delta;
		outputFolder   = _outputFolder;
		outputFile     = _outputFolder +"POP/" + (hcus)+"_hcus.txt";
		outputFile1    =  _outputFolder + "SIR_"+(local_prob)+"_"+(inf_threshold)+"_"+(delta)+"_"+(sample_size)+"_pop.txt";
		stateFolder    = _stateFolder;
		positionFolder = _positionFolder;
		mappingFolder  = _mappingFolder;
		
	}/* End of SimulationESP() */
	
	/* finds the infected people after recovery through HCUs at each node at current instance of time, Function Parameters: (num_infect_node, curr_pos_people) */
	public void find_infected_after_recovery_at_each_node(int[] num_infect_node, int[] curr_pos_people) throws IOException  
	{				
		System.out.print("\n");		
		for(int i=0; i<nodes; i++)
		{
			System.out.print("infected people at node " + (i)+ " : ");			
			int count=0;						
			for(int j=0; j<people;j++)
			{
				if((curr_pos_people[j] == i) && (curr_state_people[j] == 1))
				{
					System.out.print((j) + "  ");
					count = count+1;
				}					
			}			
			num_infect_node[i] = count;
			System.out.print( " -- " + num_infect_node[i] + "\n");
		 }		
		
	}/* End of find_infected_at_each_node() */
	
	/* finds and writes the HCUs position at current instance of time to output file, Function Parameters: (outputFile, num_HCUs_node, curr_pos_HCUs) */
	public void find_HCUs_at_each_node(String outputFile, int[] curr_pos_HCUs) throws IOException
	{
		FileWriter fw =  new FileWriter(outputFile, true);		
		fw.write("\n\n");
				
		for(int i=0; i< nodes; i++) 
		{
			fw.write("HCUs at node " + (i)+ " : ");			 			
			int count =0 ;					
			for(int j=0; j<hcus; j++)
			{
				if(curr_pos_HCUs[j] == i) 
				{
					fw.write( (j) + "; ");
					count = count + 1;
				} 
			}						
			fw.write(" -- " + count +"\n");
		}		
		fw.close();
		
	}/* End of find_positions_of_HCUs()*/		
	
	/* finds and writes the infected people at each node at current instance of time to output file, Function Parameters: (outputFile, num_infect_node, curr_pos_people) */
	public void find_infected_at_each_node(String outputFile, int[] num_infect_node, int[] curr_pos_people) throws IOException  
	{
		FileWriter fw =  new FileWriter(outputFile, true);		
		fw.write("\n");
		
		int num_recovered = 0;
		int num_infected  = 0;
		int num_suscepted = 0;
		
		for(int i=0; i<nodes; i++)
		{
			fw.write("infected people at node " + (i)+ " : ");
			
			int count_node = 0; // counts the total number of infected people at each node			
			int count_sample = 0; // counts the number of infected people with in sample 
			int sample_size  = 0; // for sample size;
			
			for(int j=1; j<=num_pop_node[i][0];j++)
			{
				int k = num_pop_node[i][j];									
				
				double rn = Math.random();				
				if(rn < 0.5)
					sample_size = sample_size + 1;
					
				if(curr_state_people[k] == 1)
				{
					fw.write((k) + "  ");
					count_node = count_node+1;				
					
					if(sample_size < 100)
						 count_sample = count_sample + 1;
					
				}	
				else
				{
					if(curr_state_people[k] == 2)
						num_recovered = num_recovered + 1;
			    }
			}			
			
			num_infect_node[i] = count_node;
			num_infect_sample[i] = count_sample;
			num_infected = num_infected + count_node;			
			fw.write( " -- " + num_infect_node[i] + " sample infected: " +num_infect_sample[i] + "\n");
		 }		
		fw.close();
		
		num_suscepted = people - (num_infected + num_recovered);
		
		FileWriter fw1 =  new FileWriter(outputFile1, true);		
		fw1.write("\nHCU Id : "+hcus+" At time t=" + curr_time + " Suscepted : " + num_suscepted + " Infected : " + num_infected  + " Recovered : " + num_recovered);
		fw1.close();
		
	}/* End of find_infected_at_each_node() */
	
	
	/*finds and write the number of people at each node at current instance of time to output file, Function Parameters: (outputFile, num_pop_node,curr_pos_people)*/// cd
	public void find_people_at_each_node(String outputFile, int [][] num_pop_node, int [] curr_pos_people) throws IOException 
	{
		FileWriter fw =  new FileWriter(outputFile, true);		
		fw.write("\n");
		
		int count=1; 
		int total=0;
		
		for(int i=0; i<nodes; i++)
		{
			count = 1;
			fw.write("people at node " + (i)+ " : ");
			
			for(int j=0; j<people;j++)
			{
				if(curr_pos_people[j] == i)
				{
					fw.write((j) + "  ");					
					num_pop_node[i][(count)] = j;
					count = count+1;
				}					
			}
			
			num_pop_node[i][0] = count-1;
			total = total+num_pop_node[i][0];
			//System.out.println(num_pop_node[i][0]);
			fw.write(" -- " + num_pop_node[i][0] +"\n");
		 }			
		fw.close();
		
		if(total != people)
		{
			System.out.println("\nError! In the total population and Sum Of the People at each node");;
			System.exit(1);
		}
		
	}/*End of find_people_at_each_node() */

	/* selects position of individuals and HCUs randomly based on probability distributions, Function Parameters: (init_pop_dist, i)*/
	public int random_position_selection(double [][] prob_dist, int pos) 
	{
		double rn = Math.random();
	
		double sum = 0.0;
		
		for(int i=0; i<nodes; i++) 
		{
		   sum = sum +  prob_dist[(pos)][i];		   
		   if(rn <= sum)
		   {
			   System.out.print("Random :" + (rn) + "; Matching Prob : " + (sum));
		       return i;
		   }
		}
		System.out.println("Error!! Check it once  : " + (sum) + " " + (rn) );
		 return 0;
	}/* End of random_selection() */
	
		
	/* parse the each line into id and it's position */
	public void parse_positions_of_people(String readline) 
	{
		String[] token = readline.split(" ");		
		int id  = Integer.parseInt(token[0]);
		int pos = Integer.parseInt(token[1]);		
		curr_pos_people[id] = pos;	
	}/* End of parse_positions_people() */
	
	/* selects position of individuals randomly based on initial probability distributions, Function Parameters: (init_pop_dist) */
	public void initial_positions_of_people(double [][] init_pop_dist) throws IOException 
	{
		curr_pos_people = new int[people];	
		num_pop_node    = new int[nodes][(int) (0.2*people)];		
		map_prob_person = new double[1][(int) (0.2*people)];		
		scan_people  = new boolean[people];
				
		String fileName = positionFolder + "0.txt";		
			
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);
		
		String readline  = br.readLine();
		if(Integer.parseInt(readline) != people)
			System.out.println("\n Mismatching with people :" + readline);
		
		int i = 0;
		int  count[] = new int[nodes];
		
		for(int j=0; j<nodes; j++)
			count[j] = 1;
		
		while((readline = br.readLine()) != null)
		{
			System.out.print("\nperson Id: " + (i) + "; ");
			parse_positions_of_people(readline);
			int pos = curr_pos_people[i];
			num_pop_node[pos][(count[pos])]  = i; 	
			System.out.print("; position : "+ (pos));			
			scan_people[i] = false;
			count[pos] = count[pos]+1;
			i=i+1;
		}	
		fr.close();
		
		for(int j=0; j<nodes; j++)
			num_pop_node[j][0] = count[j]-1;
		
		
	}/* End of initial_positions_people() */
	
	/* checks whether HCU is exist at neighbor before it is moving for next time unit, Function Parameters :(hcu_id, pos) */
	public boolean check_HCU_exist_initially(int hcu_id, int pos) 
	{
		for(int i=0; i< hcu_id; i++)
		{
		   if(curr_pos_HCUs[i] == pos)
			   return true;
		}
		return false;
		
	}/* End Of check_HCU_exist_at_neighbor() */	
	
	/* selects position of HCUs randomly based on initial probability distributions, Function Parameters: (init_HCUs_dist) */
	public void initial_positions_of_HCUs(double [][] init_HCUs_dist) 
	{
		curr_pos_HCUs = new int[(hcus)];
		prev_pos_HCUs = new int[2][(hcus)];
		time_stamp_HCUs = new int[(hcus)];
		boolean first = true;
		
		//System.out.println("\nInitial positions of HCUs");		
		System.out.println();
		for(int i=0; i<hcus; i++) 
		{
			System.out.print("\nHCU Id: " + (i+1) + "; ");
			
			int pos = random_position_selection(init_HCUs_dist, i);
			
			if( (pos == 0) && first )
			{
				first = false;
			}			
			else
			{
				while( check_HCU_exist_initially(i, pos))
				{
					pos = random_position_selection(init_HCUs_dist, i);
				}
			}
			curr_pos_HCUs[i] = pos;
			prev_pos_HCUs[0][i] = pos;
			prev_pos_HCUs[1][i] = pos;
			System.out.print("; position : "+ (pos+1));
			time_stamp_HCUs[i] = 0;
		}
		
	}/* End of initial_positions_HCUs() */
	
	/* finds the healthy probability of a person at each node,  Function Parameters: (curr_state_people, healthy_prob_node) */
	public void healthy_prob_at_each_node(boolean [] curr_state_people, double [] healthy_prob_node) 
	{
		for(int i=0; i<nodes; i++)
		{
			double prod = 1.0 ;
			
			for(int j=0; j<people; j++)
			{
				if( (curr_pos_people[j] == i) && (curr_state_people[j] == true) )
				{
					prod = prod *( 1.0 - beta);
				}
			}						
			healthy_prob_node[i] = prod;			
		}
		
	}/* End of find_healthy_prob_at_each_node() */
	
	/* finds the initial probability  of overall population, Function Parameters: (curr_state_people) */
	public void infect_prob_of_people_initially(int [] curr_state_people)
	{
		infect_prob_people = new double[people];
		healthy_prob_node   = new double[nodes];
		
		for(int i=0; i<people; i++)
		{
			if(curr_state_people[i] == 1)
				infect_prob_people[i] = 1.0;
			else
				infect_prob_people[i] = 0.0;
		}		
		
	}/* End of initial_prob_of_people() */
	
	/* finds the initial state of overall population, Function Parameters: (init_infected);	 */
	public void initial_state_of_people(int [][] init_infected) 
	{
		num_infect_node       = new int[nodes]; 
		num_infect_sample     = new int[nodes];
		curr_state_people     = new int[people];
		next_state_people	  = new int[people];
		time_stamp_people     = new int[people];	
		
		for(int i=0; i<num_infected; i++)
		{
			curr_state_people[(init_infected[0][i])] = 1;			
			
			for(int j=0; j<nodes; j++) 
			{
				if(curr_pos_people[(init_infected[0][i])] == j)
					num_infect_node[j] = num_infect_node[j] + 1;				
			}
		}
		
		infect_prob_of_people_initially(curr_state_people);
		
	}/* End of initial_state_of_people() */
	
	public void copy_org_HM_to_agent_HM()
	{
		for(int i=0; i<people; i++) 
		{
			for(int j=0; j<(0.2*people); j++)
				agent_hm[i][j] = -1;			
		}
		
		for(int i=0; i<org_hm.size(); i++)
		 {			
			for(int j=0; j<org_hm.get(i).size(); j++)
			{
				agent_hm[i][j] =  org_hm.get(i).get(j);
				//System.out.print("i : "+ i + "  j : " + j);
			}		
		 }		
	}
	
	/* starting point of simulation,  Function Parameters: (init_prob_of_people, init_prob_of_HCUs, adj_matrix_of_graph, init_infected_people, trans_prob_of_people, trans_prob_of_HCUs);	  */
	public void start_simulation(double [][] init_pop_dist, double [][] init_HCUs_dist, int [][] adj_matrix, int [][] init_infected, double [][] trans_pop_dist, double [][] trans_HCUs_dist, int [] degree_node, HashMap<Integer, ArrayList<Integer>> hm) throws IOException 
	{
		org_hm   = new HashMap<Integer, ArrayList<Integer>>(hm);
		agent_hm = new int[people][(int)(0.2*people)];	
		
		copy_org_HM_to_agent_HM();	
		
	 	//open file for writing simulation output
		File fe = new File(outputFile);	    
	    
		if(fe.exists())
	       fe.delete();
	    
    	System.out.println("\nAt time t = " + (0)+ "\n");

		FileWriter fw =  new FileWriter(outputFile, true);
    	fw.write("\nAt time t = " + (0)+ "\n");
    	fw.close();
		

    	FileWriter fw1 =  new FileWriter(outputFile1, true);		
		fw1.write("\n\nNum HCUs: "+ (hcus) +" People : " + (people) + "  Maximum Sim Time: " + sim_time);
		fw1.close();
		    	
    	initial_positions_of_people(init_pop_dist);
		initial_positions_of_HCUs(init_HCUs_dist);
		initial_state_of_people(init_infected);		
		
		find_people_at_each_node(outputFile, num_pop_node,curr_pos_people);
	    find_infected_at_each_node(outputFile, num_infect_node, curr_pos_people);
    	find_HCUs_at_each_node(outputFile, curr_pos_HCUs);
    	
      	
    	run_simulation(init_pop_dist, init_HCUs_dist, adj_matrix, trans_pop_dist, trans_HCUs_dist, degree_node);
    	
	}/* End of start_simulation() */
	
	/* checks whether HCU is exist at neighbor before it is moving for next time unit, Function Parameters : */
	public boolean check_HCU_exist_at_neighbor(int hcu_id, int pos) 
	{
		for(int i=0; i< hcus; i++)
		{
		   if( (curr_pos_HCUs[i] == pos) || (prev_pos_HCUs[0][i] == pos) || (prev_pos_HCUs[1][i] == pos) )
			   return true;
		}
		return false;
		
	}/* End Of check_HCU_exist_at_neighbor() */
	
	/* selects the position of HCU for next time unit/step randomly based on connectivity, Function Parameters : (time, time_stamp_HCUs, curr_pos_HCUs, num_HCUs_node, init_HCUs_dist, adj_matrix, trans_HCUs_dist); */
	public int random_based_HCU_selection(int time, int hcu_id, int pos, double[][] trans_HCUs_dist) 
	{
		int count = 0;		
		int pos1 = pos;
		
		pos1 = random_position_selection(trans_HCUs_dist, pos);
		
		while(check_HCU_exist_at_neighbor(hcu_id, pos1))
		{
			pos1 = random_position_selection(trans_HCUs_dist, pos);
			count = count +1;
			
			if(count == 3*nodes)
			{
				pos1 = pos;
				break;
			}
		}		
		
		time_stamp_HCUs[(hcu_id)] = time;
		prev_pos_HCUs[0][(hcu_id)] =  prev_pos_HCUs[1][(hcu_id)];
 		prev_pos_HCUs[1][(hcu_id)] =  pos1;//curr_pos_HCUs[(hcu_id)];
		
		/* Make all people at this position is not scanned*/
		for(int j=0; j<people; j++)
		{
			if( (curr_pos_people[j] == pos1) && (curr_state_people[j] != 2) )
				scan_people[j] = false;
		}
		
		return pos1;
				
	}/* End of random_based_HCU_selection() */
	
	public int population_based_HCU_selection(int time, int hcu_id, int pos, double[][] trans_HCUs_dist, int[][] num_pop_node) throws IOException 
	{
		int total_population = 0;
		
		FileWriter fw =  new FileWriter(outputFile, true);
		
		fw.write("\nSample Infected List : ");
		
		for(int i=0; i<nodes; i++)
		{
			fw.write(num_pop_node[i][0] + "  ");
			total_population = total_population +num_pop_node[i][0];			
		}
		//fw.write("\nId :" + id + " random:"+ rn + " inf prob: " + inf_prob + " rec prob : " + rec_prob +" next state : " + next_state_people[(id)] );
		
		for(int i=0; i<nodes; i++)
		{			
				fw.write("\nProbablity : ");//System.out.println("Probablity : " );
			
				for(int j=0; j<nodes; j++)
				{
					trans_HCUs_dist[i][j] = (double) (num_pop_node[j][0])/total_population;
					//System.out.print(trans_HCUs_dist[i][j] + " ");
					fw.write(trans_HCUs_dist[i][j] + " ");
				} 
		}	
		
		fw.close();
		//System.exit(1);		
		return random_based_HCU_selection(time, hcu_id, pos, trans_HCUs_dist) ;
	}
	
			
	 /* update the previous position of HCUs or copies the current position of HCUs to previous position of HCUs, Function parameters : (prev_pos_HCUs, curr_pos_HCUS)*/
	 public void copy_HCUs_positions_previous_to_current(int[][] prev_pos_HCUs, int[] curr_pos_HCUs)
	 {
			
		 System.out.println("\nPrev and current position of HCUS:\n");			
		    
		 	for(int i=0; i<hcus; i++)
		 	{	
		 		System.out.println("prev_prev :" + (prev_pos_HCUs[0][i] +1 ) + "  ; prev_curr : " + ( prev_pos_HCUs[1][i] + 1) );
	 		   // prev_pos_HCUs[0][i] =  prev_pos_HCUs[1][i];
	 		  //  prev_pos_HCUs[1][i] =  curr_pos_HCUs[i];
		 	}
		 	
			System.out.println();

	 }/*End Of copy_new_state_to_current_state */
	 
	 	 
	/* selects the position of HCU for next time unit/step randomly based on connectivity, Function Parameters : (time, time_stamp_HCUs, curr_pos_HCUs, num_HCUs_node, init_HCUs_dist, adj_matrix, trans_HCUs_dist); */
	public void next_positions_of_HCUs(int time, int [] curr_pos_HCUs, double[][] init_HCUs_dist, int[][] adj_matrix, double[][] trans_HCUs_dist, int [] degree_node) throws IOException 
	{
			
		    System.out.println();
		    
			for(int i=0; i<hcus; i++)
			{	
				int pos  = curr_pos_HCUs[i];						
				System.out.print("\nHCU Id : " + (i+1) + "; prev position : " + (pos+1) + "; " );
				
				if((time_stamp_HCUs[i]+1) == time)
				{	
					//curr_pos_HCUs[i] = infected_based_HCU_selection(time, i, pos, adj_matrix, num_infect_node);
					curr_pos_HCUs[i] = population_based_HCU_selection(time, i, pos, trans_HCUs_dist, num_pop_node);
				}
				
				pos = curr_pos_HCUs[i];
				System.out.print("; curr position : " + (pos+1) + "; time stamp : " + time_stamp_HCUs[i]);
			}		
			
			copy_HCUs_positions_previous_to_current(prev_pos_HCUs, curr_pos_HCUs);
			
	}/* End of next_positions_of_HCUs() */
	
	

	public int getDataIndex(int id, int time)
	{
		for( int i=3; i< (0.2*people); i++)
		{
			if(agent_hm[id][i] == -1)
				return i; 			
		}		
		return 3;
	}/* END of getDataIndex()*/
	
	public void map_according_to_neighbor_existance(int id,  int time, int pos)
	{
		//System.out.print("original nodes : " + org_hm.get(id) + " list of nodes reconnected : ");
		 
		int count = agent_hm[id][2];
		
		for(int i=3; i<org_hm.get(id).size(); i++)
		{
			int nbr_id = org_hm.get(id).get(i); 	
			
			if(agent_hm[nbr_id][1] == pos)
			{
				int index = count+3; 
					agent_hm[id][index] = nbr_id;
				count = count +1;
			}
		}		
		agent_hm[id][2] = count;
		
	}/* End of map_according_to_neighbor_existance()*/
	
	public boolean check_neighbor_already_exist(int id, int nbr_id)
	{
		for(int i=3; i<agent_hm[id][2]+3; i++)
		{
			if(agent_hm[id][i] == nbr_id)
			{	
				//System.out.println("Yes Entered :" + id + "  "+nbr_id);
				return true;
			}
		}
		return false;
	}
	
	
	public double find_mapping_prob_of_person(int nbr_id, int pos) 
	{   
		return (double) org_hm.get(nbr_id).get(2)/num_pop_node[pos][0];
	}
	
	
	public void map_with_people_in_other_node(int id, int time, int pos, int [] list_of_people)
	{
		
		double prob  = 0.0;		
		boolean nbr_exist = false;
		int nbr_id = -1, count = 0;
		
		for(int i=1; i<=list_of_people[0]; i++)
		{				
			nbr_id = list_of_people[i];			
			
			double rn = map_prob_person[0][i-1];
			
			if(id == nbr_id)
				continue;
			
			if(agent_hm[nbr_id][0] == agent_hm[nbr_id][1] )
			{
				prob = find_mapping_prob_of_person(nbr_id, pos);
				System.out.println("Id:" + id + " nbr_id:" + nbr_id +" pos:" + pos + " #nbrs:" + org_hm.get(nbr_id).get(2) + " #possible:" + num_pop_node[pos][0] + " prob:" + prob + " random:" + rn);
			}
			else
			{
				continue;
			}
				
			if((rn < prob) && (!check_neighbor_already_exist(id, nbr_id)))
			{
				int index = agent_hm[id][2];
				agent_hm[id][index+3] = nbr_id;
				index = agent_hm[nbr_id][2];
				agent_hm[nbr_id][index+3] = id;
				agent_hm[id][2] = agent_hm[id][2] +1;
				agent_hm[nbr_id][2] = agent_hm[nbr_id][2] +1;				
				nbr_exist = true;
				
				count = count + 1;
			    if(count >= 2)
			    	return;
			}	   					
		}
		
		if((nbr_exist != true)&&(id != nbr_id)&&(nbr_id != -1))
		{		
			int index = agent_hm[id][2]+3;
			agent_hm[id][index] = nbr_id;
			index = agent_hm[nbr_id][2]+3;
			agent_hm[nbr_id][index] = id;
			agent_hm[id][2] = agent_hm[id][2] +1;
			agent_hm[nbr_id][2] = agent_hm[nbr_id][2] +1;			
		}
				
	}/* End of map_with_people_in_other_node() */
	

	public void clearAllNeighbors(int time) 
	{
		for(int i=0; i<people; i++)
		{					
			int j=3;
			
			while(agent_hm[i][j] != -1)
			{  
				 agent_hm[i][j] = -1;
			     j = j+1;
			     if(j > (0.2*people))
						break;
			}
			agent_hm[i][2] = 0;	
		}
		
	}/* End of clearAllNeighbors() */
	
	public void store_Neighbors_Information(int time) throws IOException
	{
		//FileWriter fw =  new FileWriter(outputFile, true);	
		
		for(int i=0; i<people; i++)
		{	
			int count = 0, j=3;	
			
			//fw.write("\nId : " + i+ " org pos: " + agent_hm[i][0] + " curr pos : " + agent_hm[i][1] + " num neighbors :" + agent_hm[i][2] + " Their List :");
			System.out.print("Id : " + i+ " org pos: " + agent_hm[i][0] + " curr pos : " + agent_hm[i][1] + " num neighbors :" + agent_hm[i][2] + " Their List :");			
			
			while(agent_hm[i][j] != -1)
			{
			//	fw.write(agent_hm[i][j] + " ");
				System.out.print(agent_hm[i][j] + " ");
				count++;
				j=j+1;
				
				if(j > (0.2*people))
					break;
			}
			System.out.println();
			
			if(agent_hm[i][2] != count)
			{ 
				System.out.println("Error in the Mapping");
			    System.exit(1);
			} 
		}
		
		//fw.close();
		
	 }/* End of storeNeighborsInformation()*/
	
	public void add_or_remove_people_from_neighbors_list(int time) throws IOException 
	{
		 clearAllNeighbors(time);
		
		for(int j=0; j<people; j++)
		{ 
			int org_pos  = agent_hm[j][0];
			int curr_pos = agent_hm[j][1];
			
			if(org_pos != curr_pos)
			{
				map_with_people_in_other_node(j, time, curr_pos, num_pop_node[curr_pos]);
			}
			else
			{
				map_according_to_neighbor_existance(j, time, org_pos);
			}
		} 		
		store_Neighbors_Information(time);
		
	}/* End of add_or_remove_people_from_neighbors_list()*/
	
	public void parse_mapping_prob_of_person(int id, String data)
	{
		//System.out.print("\nPerson Id : " + id + " ");
		StringTokenizer st = new StringTokenizer(data,", ");       
	    int count = 0;
	    while (st.hasMoreTokens()) 
	    {  
	         map_prob_person[(id)][count] = Double.parseDouble(st.nextToken());   
	        // System.out.print(map_prob_person[(id)][count] + "  ");
	         count = count+1;
	    }     	
	}
	
	/* reads the position of people for next time unit from file, Function Parameters : (time); */
	public void next_mapping_prob_of_people(int time) throws IOException   
	{
		
		String fileName = mappingFolder + time + ".txt";
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);
		
		String readline  = br.readLine();
		
		if(Integer.parseInt(readline) != people)
		{
			System.out.println("\n Mismatching with people :" + readline);
			System.exit(1);
		}	
		
		int i = 0;		
		while((readline = br.readLine()) != null)
		{
			
			parse_mapping_prob_of_person(i, readline);
			i=i+1;
		}		
		fr.close();
		
		//System.out.println();
			    
    }/* End of next_positions_of_people() */
	
	/* reads the position of people for next time unit from file, Function Parameters : (time); */
	public void next_positions_of_people(int time, int[] curr_pos_people) throws IOException   
	{
		
		String fileName = positionFolder + time + ".txt";
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);
		
		String readline  = br.readLine();
		
		if(Integer.parseInt(readline) != people)
			System.out.println("\n Mismatching with people :" + readline);
		
		int i = 0;		
		while((readline = br.readLine()) != null)
		{
			int prev_pos  = curr_pos_people[i];
			
			parse_positions_of_people(readline);
			int curr_pos  = curr_pos_people[i];				
			agent_hm[i][1] = curr_pos;					
			if( (prev_pos != curr_pos) && (curr_state_people[i] != 2) )
				scan_people[i] = false;			
			i=i+1;
		}		
		fr.close();
			    
    }/* End of next_positions_of_people() */
	
		
	/* finds the healthy probability of person w.r.t to his neighbors, Function Parameters :(id, pos, infect_prob_people) */
	public double healthy_prob_wrt_neighbors(int id, int pos, double [] infect_prob_people) 
	{
		    int in = num_infect_node[pos]; //total number of people infected at this node

			double healthy_prob = 1.0;
			
			if( curr_state_people[id] == 1 )
				in = in-1;
			
			for(int j=0; j<in; j++)
			{			
				healthy_prob = healthy_prob *( 1.0 - beta);			
			}				
			return healthy_prob;		
		
	}/* End of healthy_prob_wrt_neighbors() */
	
	
	/* finds the probability of a person recovery from infection, Function Parameters : (id, infect_prob_people, zeta)  */
	public double recovery_prob_of_people(int id, double [] infect_prob_people, double  zeta)
	{
		double recover_prob = 0.0; // healthy probability of an individual at current instance of time
		
		recover_prob = ( delta * infect_prob_people[id] * zeta) + 1/2 * delta * (infect_prob_people[id]) * ( 1.0 - zeta);
		
		return recover_prob;
	}/*  End of recovery_prob_of_people() */
	
	
	public double find_recovery_prob_of_person(int id)
	{
		int days = curr_time - time_stamp_people[id];		
			return (1 - Math.pow((1-delta), (days)));
				
	}/* END of find_recovery_prob_of_person() */
	
    public double find_infection_prob_of_person(int id)
	{
    	int num_inf_nbrs = 0;  
    	
    	for(int i=3; i<agent_hm[id][2]+3; i++)
    	{
    			int nbr_id = agent_hm[id][i];		   
    			if(curr_state_people[nbr_id] == 1)
    				num_inf_nbrs = num_inf_nbrs + 1;		
    	}
		
    		if(agent_hm[id][2] == 0)
    			return 0.0;
    		else
    			return ((double)num_inf_nbrs/agent_hm[id][2]);
		   
		
	}/*End of find_infection_prob_of_person()*/
    
	/* selects the state of a person randomly based on infection and recovery probability, Function Parameters : (id, randum, rec_prob) */
	public void random_state_selection(int id, double randum) throws IOException
	{
		double rn = randum;	
		double inf_prob = 0.0;
		double rec_prob = 0.0;
		
		if(curr_state_people[(id)] == 0)
		{
			inf_prob = find_infection_prob_of_person(id);
			
			if(rn <= inf_prob)
			{
				next_state_people[(id)] = 1;
				infect_prob_people[id] =  inf_prob;
				time_stamp_people[id]  =  curr_time;
			}				
		}
		else
		{
			if(curr_state_people[(id)] == 1)
			{
				rec_prob = find_recovery_prob_of_person(id);
				next_state_people[(id)] = 1;
			}	 
							
			if( (rn <= rec_prob) || (next_state_people[(id)] == 2) )
			{
				next_state_people[(id)] = 2;
				infect_prob_people[id] =  0.0;
			}	 			
				
		}		
		System.out.println("Id :" + id + " random:"+ rn + " inf prob: " + inf_prob + " rec prob : " + rec_prob +" next state : " + next_state_people[(id)] );		
		/*
		 FileWriter fw =  new FileWriter(outputFile, true);
		fw.write("\nId :" + id + " random:"+ rn + " inf prob: " + inf_prob + " rec prob : " + rec_prob +" next state : " + next_state_people[(id)] );
		fw.close();
		*/
		
	}/* End of random_state_selection() */
	
	/* selects the state of a person randomly based on infection and recovery probability, Function Parameters : (id, num_pop_node, num_infect_node, curr_state_people) */
	 public void random_state_selection(int id, double randum, int [][] num_pop_node, int [] num_infect_node, int [] curr_state_people, double [] infect_prob_people, double [] healthy_prob_node) 
	 {
		int pos = curr_pos_people[(id)];   //current position of individual( id ) 
		int in = num_infect_node[pos];     //total number of people infected at that node
				
		if(in == 0)
		{
			System.out.println("Person Id: " + (id+1) + "; prev state : " + curr_state_people[(id)] + "; curr state : " + curr_state_people[(id)] );
			return;		
		}
		
		double inf_prob  = 0.0;  // probability of getting infection
		double rec_prob  = 0.0;  // probability of recovery from infection 
		double zeta      = 0.0; // probability of not getting infection from neighbors
		
		zeta     = healthy_prob_wrt_neighbors(id, pos, infect_prob_people);		
		inf_prob = (1.0 - zeta );
		rec_prob = recovery_prob_of_people(id, infect_prob_people, zeta);
				
		if( (inf_prob > 1.0) || (inf_prob < 0.0) ||  (rec_prob > 1.0) || (rec_prob < 0.0) )
		{
			System.out.println("healthy prob : " + (healthy_prob_node[pos]) + " (1-beta)" + (1-beta) + "rec prob : "  + rec_prob + "\n Error In probility Calculation") ;
			System.exit(1);
		}
		
		double rn = randum;
		
		System.out.print("Person Id: " + (id+1) + "; prev state : " + curr_state_people[(id)] + "; Random : " + rn + "; inf prob : " + inf_prob + "; rec prob : " + rec_prob + "; ");
		
		if(curr_state_people[(id)] == 0)
		{
			if(rn <= inf_prob)
			{
				curr_state_people[(id)] = 1;
				infect_prob_people[id] =  inf_prob;
				scan_people[(id)] = false;
			}				
		}
		else
		{
			if(rn <= rec_prob)
			{
				curr_state_people[(id)] = 2;
				infect_prob_people[id] =  0.0;
				scan_people[(id)] = true;
			}	 
		}		
		System.out.print(" curr state : " + curr_state_people[(id)] + "\n");
		
	}/* End of random_state_selection() */
	
	 /* parse the each line into id and it's position */
	 public double parse_state_of_people(String readline) 
	 {
			String[] token = readline.split(" ");		
			//int id  = Integer.parseInt(token[0]);
			double randum = Double.parseDouble(token[1]);		
			return randum;	
	  }/* End of parse_state_people() */
		
	 public void copy_next_state_to_current_state(int[] _next_state_people, int[] _curr_state_people)
	 {
			for(int i=0; i<people; i++)
				curr_state_people[i] = next_state_people[i];		
	 }

	 
	 /* finds the next state of people by reading data from file, Function Parameters : (num_pop_node, num_infect_node, curr_state_people); */
	public void next_state_of_people(int time, int [][] num_pop_node, int [] num_infect_node, int [] curr_state_people) throws IOException
	{
		System.out.println();		
		String fileName = stateFolder + time + ".txt";
		//read_positions_of_people(fileName, curr_pos_people);
			
		File fe = new File(fileName);
		FileReader fr = new FileReader(fe);		
		BufferedReader br = new BufferedReader(fr);
		
		String readline  = br.readLine();
		if(Integer.parseInt(readline) != people)
			System.out.println("\n Mismatching with people :" + readline);
		
		int i = 0;
		
		while((readline = br.readLine()) != null)
		{
			double randum = parse_state_of_people(readline);
			//random_state_selection(i, randum, num_pop_node, num_infect_node, curr_state_people, infect_prob_people, healthy_prob_node);	
			random_state_selection(i, randum);
			i=i+1;
		}		
		fr.close();				
		//System.out.println();	
		
	}/* End of next_state_of_people() */
	
	
	/*	 clear_infected_at_node_with_HCU, Function Parameters : (id, pos, time, curr_pos_people, curr_state_people, infect_prob_people, trans_HCUs_dist); */		    	   
	public void clear_infected_at_node_with_HCU(int id, int pos, int time, int[] curr_pos_people, int[] curr_state_people, double[] infect_prob_people, int[][] adj_matrix, int[] degree_node, double [][] trans_HCUs_dist) throws IOException
	{
		FileWriter fw =  new FileWriter(outputFile, true);			
		ArrayList<Integer> sample = new ArrayList<Integer>();
		
		int scan_count = 0;
		
		for(int j=1; j<=num_pop_node[pos][0]; j++)
		{
			int i = num_pop_node[pos][j];
								
			if(scan_people[i] == false)
			{
				sample.add(i);
				scan_count = scan_count+1;
			}				
			
			if(scan_count >= sample_size)
				break;  	
		}
		
		if(sample.isEmpty())
		{
			System.out.println();
		}
		else
		{
			int infect_count=0;
			fw.write("\npeople recovered at node " + (pos) + " : ");
			System.out.print("\npeople recovered at node " + (pos) + " :");			
					
			for(int j=0; j < scan_count; j++) 
			{
				int i = sample.get(j);
				scan_people[i] = true;
			
				if(curr_state_people[i] == 1)
				{
					System.out.print( (i) + " ");
					fw.write((i) + " " );
					curr_state_people[i] = 2;
					next_state_people[i] = 2;
					infect_prob_people[i] = 0.0;
					infect_count++;
					num_HCU_recover = num_HCU_recover + 1;
				}			
			}				
			System.out.println();		
			fw.write("\n");
			
			if((infect_count >= (inf_threshold * sample_size)) && (scan_count >= sample_size))
			{
				fw.write("Yes Entered : HUCs " + hcus + " time :" + time + "\n");
				System.out.println();
				curr_pos_HCUs[id] = pos; 
				time_stamp_HCUs[id] = curr_time+1; 
				//random_based_HCU_selection(time, id, pos, trans_HCUs_dist);
			}			
		}		
		fw.close();
		
	}/* End of clear_all_infected_with_HCU() */
	
	/*	 clear_infected_at_node_with_HCU, Function Parameters : (pos, curr_pos_people, curr_state_people, infect_prob_people);	*/		    	   
	public void clear_infected_at_node_with_HCU(int pos, int[] curr_pos_people, int[] curr_state_people, double[] infect_prob_people) throws IOException
	{
		FileWriter fw =  new FileWriter(outputFile, true);	
		
		fw.write("\npeople recovered at node " + (pos+1) + " : ");
		System.out.print("\npeople recovered at node " + (pos+1) + " :");			
		
		
		for(int j=0; j < people; j++) 
		{
			if((curr_pos_people[j] == pos) && (curr_state_people[j] == 1))
			{
				System.out.print( (j+1) + " ");
				fw.write((j+1) + " " );
				curr_state_people[j] = 2;
				infect_prob_people[j] = 0.0;
			}				
		}
				
		System.out.println();		
		fw.write("\n");
		fw.close();
	}/* End of clear_all_infected_with_HCU() */

	// 	recover_infected_with_HCUs(num_HCUs_node);
	public void recover_infected_with_HCUs(int time, int [][] adj_matrix, int [] degree_node, double [][] trans_HCUs_dist) throws IOException 
	{		
		System.out.println();
		
		for(int i=0; i<hcus; i++)
		{
			int pos = curr_pos_HCUs[i];  		   
			//clear_infected_at_node_with_HCU(pos, curr_pos_people, curr_state_people, infect_prob_people);
			clear_infected_at_node_with_HCU(i, pos, time, curr_pos_people, curr_state_people, infect_prob_people, adj_matrix, degree_node, trans_HCUs_dist);
		}
		//copy_HCUs_positions_previous_to_current(prev_pos_HCUs, curr_pos_HCUs);
		System.out.println();
	}/* End of clear_infected_at_node_with_HCU() */
	
	// next_stage_evolution(j, init_pop_dist, init_HCUs_dist, adj_matrix, trans_pop_dist, trans_HCUs_dist); 
	public void next_stage_evolution(int time, double[][] init_pop_dist, double[][] init_HCUs_dist, int[][] adj_matrix, double[][] trans_pop_dist, double[][] trans_HCUs_dist, int [] degree_node) throws IOException 
	{
		next_state_of_people(time, num_pop_node, num_infect_node, curr_state_people);
		copy_next_state_to_current_state(next_state_people, curr_state_people);
		System.out.println("\nAt time t = " + time);
		next_positions_of_people(time, curr_pos_people);
		find_people_at_each_node(outputFile, num_pop_node, curr_pos_people);
		next_mapping_prob_of_people(time);
		add_or_remove_people_from_neighbors_list(time);		
		next_positions_of_HCUs(time, curr_pos_HCUs, init_HCUs_dist, adj_matrix, trans_HCUs_dist, degree_node);	
		find_infected_at_each_node(outputFile, num_infect_node, curr_pos_people);
    	find_HCUs_at_each_node(outputFile, curr_pos_HCUs);
    	//copy_next_state_to_current_state(next_state_people, curr_state_people);
		
	}/* End of next_stage_evolution()*/
	
	
	/* storing time need to complete the simulation in a file */
	public void store_final_results(int time) throws IOException 
	{
		   int count=0;
		   for(int i=0; i<people; i++)
		   {
			  if(curr_state_people[i] == 2)
			  {   count++;
			  }
		   }
		String fileName = outputFolder + "time-pop.txt";
		FileWriter fw =  new FileWriter(fileName, true);		  
		fw.write("\nPeople:" + people +  "; Nodes:" + nodes + "; Time:" + time + "; HCUs:" + hcus + " Total People Infected : " +count + " People Recovered By HCU : " + num_HCU_recover);
    	fw.close();    
	}/* End of store_final_results() */
	   
	/* checks whether number of infected people are zero or not, Function Parameters: (num_infect_node)  */
	public boolean check_termination_of_simulation(int[] num_infect_node) 
	{
	
	   for(int i=0; i<nodes; i++)
	   {
		  if(num_infect_node[i] != 0)
		  {   return false;
		  }
	   }   
	    return true;	    
	 
		
	}/* End of check_termination_of_simulation */

	/* run simulation for next time step evolution of infected, recovered,  movement of population and HCUs over graph, Function parameters : (init_pop_dist, init_HCUs_dist, adj_matrix, trans_pop_dist, trans_HCUs_dist); */
	public void run_simulation(double [][] init_pop_dist, double [][] init_HCUs_dist, int [][] adj_matrix, double [][] trans_pop_dist, double [][] trans_HCUs_dist, int [] degree_node) throws IOException 
	{
         //sim_time = 99;

        for(int j=1; j < sim_time; j++)		
	  	{ 
        	curr_time = j;
        	
        	recover_infected_with_HCUs(j, adj_matrix, degree_node, trans_HCUs_dist);
        	find_infected_after_recovery_at_each_node(num_infect_node, curr_pos_people);
        	
        	FileWriter fw =  new FileWriter(outputFile, true);
        	fw.write("\n At time t = " + (j)+ "\n");
        	fw.close();
		   	
        	next_stage_evolution(j, init_pop_dist, init_HCUs_dist, adj_matrix, trans_pop_dist, trans_HCUs_dist, degree_node);
		   	
        	if(check_termination_of_simulation(num_infect_node))
		   	{	
		   		store_final_results(j);
		   		break;
		   	}
        	
		}        
		    		    
	}/* End Of run_simulation() */
}
