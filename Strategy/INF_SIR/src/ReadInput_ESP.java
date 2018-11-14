import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;  

/* class defines Epidemic Spreading Process  */
public class ReadInput_ESP
{

  private File    config_File; 

  private int     num_Nodes;
  private int     sim_Time;
  private int     num_People;
  private int     num_HCUs;
  private int     num_Infected;
  private int 	  sample_Size;
  private double  local_prob;
  private double  inf_threshold;
  private double  beta;
  private double  delta;
  
  private String  path_init_prob_dist_of_people;
  private String  path_init_prob_dist_of_HCUs;
  private String  path_adj_matrix_of_graph;
  private String  path_trans_prob_matrix_of_people;
  private String  path_trans_prob_matrix_of_HCUs;
  private String  path_init_infected_people;
  private String  path_init_pop_at_each_node;
  
  private String  path_BA_folder;
  private String  path_position_folder;
  private String  path_state_folder;
  private String  path_mapping_folder;

  private String  path_output_folder;
  
  protected double  init_prob_of_people[][];
  protected double  init_prob_of_HCUs[][]; 
  protected int     adj_matrix_of_graph[][];
  protected int     init_infected_people[][];
  protected double  trans_prob_of_people[][];
  protected double  trans_prob_of_HCUs[][]; 
  protected int     degree_node[];  // stores the degree of each node in graph
  protected int     init_pop_at_node[][]; // stores initial population at each node
  public HashMap<Integer, ArrayList<Integer>> hm; //stores each agent location & neighbors information


  /* constructor */
  ReadInput_ESP(File _configFile)
  {
    config_File = _configFile;
  }/* End of ESP() */

  /* reading the configuration file and assign values to input variables */
  public void read_configFile() throws Exception 
  {
         
     FileReader fr = null;
	
	 try 
	 {
		             fr  = new FileReader(config_File);
		Properties props = new Properties();
		props.load(fr);

		num_Nodes 		                  = Integer.parseInt(props.getProperty("num_nodes"));
		sim_Time 		                  = Integer.parseInt(props.getProperty("time_units"));
		num_People			          = Integer.parseInt(props.getProperty("num_people"));
		num_HCUs			          = Integer.parseInt(props.getProperty("num_HCUs"));
		num_Infected				  = Integer.parseInt(props.getProperty("num_infected"));
		sample_Size					  = Integer.parseInt(props.getProperty("sample_size"));
		local_prob					  = Double.parseDouble(props.getProperty("local_prob"));
		inf_threshold				  = Double.parseDouble(props.getProperty("inf_threshold"));;
		beta					  = Double.parseDouble(props.getProperty("beta"));
		delta					  = Double.parseDouble(props.getProperty("delta"));
		 //System.getProperty("user.dir")+ "/../../../
		path_init_prob_dist_of_people     = System.getProperty("user.dir")+ "/../../../" + props.getProperty("init_prob_dist_of_people");
		path_init_prob_dist_of_HCUs       = System.getProperty("user.dir")+ "/../../../" + props.getProperty("init_prob_dist_of_HCUs");		
		path_adj_matrix_of_graph          = System.getProperty("user.dir")+ "/../../../" + props.getProperty("adj_matrix_of_graph");
		path_trans_prob_matrix_of_people  = System.getProperty("user.dir")+ "/../../../" + props.getProperty("trans_prob_matrix_of_people");
		path_trans_prob_matrix_of_HCUs    = System.getProperty("user.dir")+ "/../../../" + props.getProperty("trans_prob_matrix_of_HCUs");
        	path_init_infected_people         = System.getProperty("user.dir")+ "/../../../" + props.getProperty("init_infected_people");
        	path_init_pop_at_each_node	  = System.getProperty("user.dir")+ "/../../../" + props.getProperty("init_pop_at_each_node");
        	
        	path_BA_folder              = System.getProperty("user.dir")+ "/../../../"+props.getProperty("location_of_BA_folder");
        	path_position_folder              = System.getProperty("user.dir")+ "/../../../"+props.getProperty("location_of_position_folder");
        	path_state_folder                 = System.getProperty("user.dir")+ "/../../../"+props.getProperty("location_of_state_folder");
        	path_mapping_folder				= System.getProperty("user.dir")+ "/../../../" + props.getProperty("location_of_mapping_folder");
        	path_output_folder		  = System.getProperty("user.dir")+ "/../../../"+props.getProperty("location_of_output_folder"); 
        
               System.out.println("Num of Nodes             :" + num_Nodes);		
               System.out.println("Simulation Time Units    :" + sim_Time);		
               System.out.println("Total Population         :" + num_People);		
               System.out.println("Num of Health Care Units :" + num_HCUs);	
               System.out.println("Num of initial Infected  :" + num_Infected);
               System.out.println("HCU Sample Size          :" + sample_Size);
               System.out.println("local probability        :" + local_prob);
               System.out.println("Infected  Threshold      :" + inf_threshold);               
               System.out.println("Infection Rate           :" + beta);
               System.out.println("Recovery Rate            :" + delta);
               System.out.println(path_init_prob_dist_of_people);		
               System.out.println(path_init_prob_dist_of_HCUs);		
               System.out.println(path_adj_matrix_of_graph);		
               System.out.println(path_init_infected_people);	
               System.out.println(path_init_pop_at_each_node);
               System.out.println(path_trans_prob_matrix_of_people);		
               System.out.println(path_trans_prob_matrix_of_HCUs);	
             	//if(true)return;
		
	   }
	   catch(FileNotFoundException ex)
       {
           System.out.println(" \n Config File is not found, please pass it through constructor!! \n");
	   }
   	   catch(IOException ex)
       {
            ex.printStackTrace();
  	   }
       catch(Exception e)
       {
	      System.out.println(" \n Error occured, please try again!! \n ");
		  return;
	   }
       finally
       {
              fr.close();                
       }   
	 
       read_all_input_files();
       
       simulation();

   }/* End Of read_configFile() */

   /* read all input files and store data into arrays */
   public void read_all_input_files() throws Exception 
   {
	    read_init_prob_of_people(path_init_prob_dist_of_people);
        read_init_prob_of_HCUs(path_init_prob_dist_of_HCUs);
        read_adj_matrix_of_graph(path_adj_matrix_of_graph);
        read_init_infected__people(path_init_infected_people);
        read_trans_prob_of_people(path_trans_prob_matrix_of_people);
        read_trans_prob_of_HCUs(path_trans_prob_matrix_of_HCUs);
        find_degree_of_each_node(adj_matrix_of_graph);
        read_init_pop_at_each_node(path_init_pop_at_each_node);        
        read_BANode_Information(path_BA_folder);
   }/* End Of read_all_input_files() */

   public void read_init_prob_of_people(String _path_init_prob_dist_of_people) throws Exception 
   {
        init_prob_of_people = new double[(num_People)][(num_Nodes)];
        System.out.println("\n read_init_prob_of_people \n");
        read_input_double_data(_path_init_prob_dist_of_people, init_prob_of_people);

   }/* End Of read_init_prob_of_people() */
   
   public void read_init_prob_of_HCUs(String _path_init_prob_dist_of_HCUs) throws Exception 
   {
        init_prob_of_HCUs = new double[(num_HCUs)][(num_Nodes)];
        System.out.println("\n read_init_prob_of_HCUs \n");
        read_input_double_data(_path_init_prob_dist_of_HCUs, init_prob_of_HCUs); 

   }/* End Of read_init_prob_of_HCUs() */

   public void read_adj_matrix_of_graph(String _path_adj_matrix_of_graph) throws Exception 
   {
        adj_matrix_of_graph = new int[(num_Nodes)][(num_Nodes)];
        System.out.println("\n read_adj_matrix_of_graph \n");
        read_input_int_data(_path_adj_matrix_of_graph, adj_matrix_of_graph); 

   }/* End Of read_adj_matrix_of_graph() */
   
   public void read_init_infected__people(String _path_init_infected_people) throws Exception 
   {

	    init_infected_people = new int[1][num_Infected];                
        System.out.println("\n read_init_infected__people \n");
        read_input_int_data(_path_init_infected_people, init_infected_people);

   }/* End Of read_init_infected__people() */

   public void read_trans_prob_of_people(String _path_trans_prob_matrix_of_people) throws Exception 
   {

        trans_prob_of_people = new double[(num_Nodes)][(num_Nodes)];                
        System.out.println("\n read_trans_prob_of_people \n");
        read_input_double_data(_path_trans_prob_matrix_of_people, trans_prob_of_people);

   }/* End Of read_trans_prob_of_people() */
   
   public void read_trans_prob_of_HCUs(String _path_trans_prob_matrix_of_HCUs) throws Exception 
   {
        trans_prob_of_HCUs  = new double[(num_Nodes)][(num_Nodes)];
        System.out.println("\n read_trans_prob_of_HCUs \n");
        read_input_double_data(_path_trans_prob_matrix_of_HCUs, trans_prob_of_HCUs); 
             
   }/* End Of read_trans_prob_of_HCUs() */
   
   public void read_init_pop_at_each_node(String _path_init_pop_at_each_node) throws Exception
   {
	   init_pop_at_node = new int[1][num_Nodes];
	   System.out.println("\n read_init_pop_at_each_node \n");
       read_input_int_data(_path_init_pop_at_each_node, init_pop_at_node);
	
   }/* End Of read_init_pop_at_each_node() */   
   
   public void read_BANode_Information(String _path_of_BA_Folder) throws Exception
   {
	      int start_index = 0;	      
	      hm = new HashMap<Integer, ArrayList<Integer>>();
	      
	      for(int i=0; i<num_Nodes; i++)
		  {
			  CreateBANodes_ESP cban = new CreateBANodes_ESP(i, init_pop_at_node[0][i]);			  
			  String filename = _path_of_BA_Folder + "adjacency_list_of_node" + i + ".txt";			  
			  cban.read_input_of_node(filename, start_index, hm);
			  start_index = start_index + init_pop_at_node[0][i];
		  }	   
   }
   
   /*  finds the degree of each node in the graph, Function Parameters: ( adj_matrix )  */
	public void find_degree_of_each_node(int[][] adj_matrix)
	{
		degree_node = new int[(num_Nodes)];
		
		System.out.print("\n Degree of each Node  : ");
		
		for(int i=0; i < num_Nodes; i++)
		{
			int count = 0;
			
			for(int j=0; j < num_Nodes; j++)
			{
				if(adj_matrix[i][j] == 1)
				  count++;				
			}			
			degree_node[i] = count;	
			System.out.print( count + "  ");
		}		
		System.out.print("\n");
		
	}/* End of find_degree_of_each_node() */
   
   
   public void read_input_double_data(String input_file_name, double [][] prob_dist) throws Exception 
   {
         String lineRead   = null;
         BufferedReader br = null;
         try
         {
        	      br = new BufferedReader(new FileReader(input_file_name));
        	lineRead = br.readLine();

         	int first_line = Integer.parseInt(lineRead);

         	if(((input_file_name == path_init_prob_dist_of_people)||(input_file_name == path_trans_prob_matrix_of_people))&&(first_line != num_People) )
         	{
         	  	System.out.println(" \nError! Miss match of people with in configuration file\n ");
          	  	return;
        	}   
            else
            {
                if(((input_file_name == path_init_prob_dist_of_HCUs)||(input_file_name == path_trans_prob_matrix_of_HCUs))&&(first_line != num_HCUs) )
         		{
         	  		System.out.println(" \nError! Miss match of HCUs with in configuration file\n ");
          	  		return;
        		}  
                
            }            
         	
            int row_id = 0;
        	while(( lineRead=br.readLine() ) != null)
            {
                   parse_input_double_data(prob_dist, lineRead, row_id);
                   row_id = row_id + 1;
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

   }/* End of read_input_double_data() */
 
   public void read_input_int_data(String input_file_name, int [][] adj_matrix) throws Exception 
   {
         String lineRead   = null;
         BufferedReader br = null;

         try
         {

         	      br = new BufferedReader(new FileReader(input_file_name));
        	lineRead = br.readLine();
         	int firstline = Integer.parseInt(lineRead);
         	
         	if((input_file_name == path_adj_matrix_of_graph)&&(firstline != num_Nodes))
         	{
         	   System.out.println(" \nError! Miss match of nodes with in configuration file\n ");
		       return;
        	}  
         	else 
         	{
         		if((input_file_name == path_init_infected_people) && (firstline != num_Infected)) 
         		{
         			System.out.println(" \nError! Miss match of infected people with in configuration file\n ");
     		        return;
         		}
         		
         	}
         	int row_id = 0;
            while(( lineRead=br.readLine() ) != null)
            {
                   parse_input_int_data(adj_matrix, lineRead, row_id);
                   row_id = row_id + 1;
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

   public void parse_input_double_data( double [][] prob_dist, String data, int row_id)
   {
            
       StringTokenizer st = new StringTokenizer(data,", ");       
       int count = 0;
       while (st.hasMoreTokens()) 
       {  
         prob_dist[(row_id)][count] = Double.parseDouble(st.nextToken());   
         System.out.print(prob_dist[(row_id)][count] + "  ");
         count = count+1;
       }       
       System.out.println();
           
   }/* End of parse_input_double_data() */

   public void parse_input_int_data( int [][] adj_matrix, String data, int row_id)
   {
            
       StringTokenizer st = new StringTokenizer(data,", ");       
       int count = 0;
       while (st.hasMoreTokens()) 
       {  
         adj_matrix[(row_id)][count] = Integer.parseInt(st.nextToken());   
         System.out.print(adj_matrix[(row_id)][count] + "  ");
         count = count+1;
       }
       System.out.println();
        
   }/* End of parse_input_int_data() */

   
   public void simulation() throws IOException
   {
       //num_Nodes
	  for(int i=0; i<=num_HCUs; i++)
	  {
		  if(i%10==0)
		  {
			  System.out.println("\n\nNum of Health Care Units : " + i);
			  Simulation_ESP ses = new Simulation_ESP(num_Nodes, sim_Time, num_People, i, num_Infected, sample_Size, local_prob, inf_threshold, beta, delta, path_position_folder, path_state_folder, path_mapping_folder, path_output_folder);
			  ses.start_simulation(init_prob_of_people, init_prob_of_HCUs, adj_matrix_of_graph, init_infected_people, trans_prob_of_people, trans_prob_of_HCUs, degree_node, hm);
		  }
      }
   }
      
}/* End Of class ESP */
