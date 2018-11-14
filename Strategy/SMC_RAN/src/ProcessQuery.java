import java.util.StringTokenizer;

public class ProcessQuery 
{
	
	private int[] query_data = new int [15];
	
 	private String query ="(TRUE) Untill(<=9) (numInfected(2)>=420)";	

		//"(isSuscepted(6445)) Untill(<=20) (numInfected(4)>=270)";

	/*parse the whole query */
	public void parse_sub_query(String query2, int portion)
	{
				
		if(query2.contains("is"))
		{
				if(portion == 1)
					query_data[4] = 0;
				else 
					query_data[9] = 0;
			
				if(query2.contains("isSuscepted"))
				{
					if(portion == 1)
					{
						query_data[6] = 0;
						query_data[7] = 0;
						query_data[8] = 0;
					}
					else 
					{
						query_data[11] = 0;
						query_data[12] = 0;
						query_data[13] = 0;
					}	
					query2 = query2.replace("isSuscepted", "");
				}
				else if(query2.contains("isInfected"))
				{
					if(portion == 1)
					{
						query_data[6] = 0;
						query_data[7] = 1;
					}
					else 
					{	
						query_data[11] = 0;
						query_data[12] = 1;				
					}
					query2 = query2.replace("isInfected", "");
				}
				else if(query2.contains("isRecovered"))
				{
					if(portion == 1)
					{
						query_data[6] = 0;
						query_data[7] = 2;
					}
					else 
					{
						query_data[11] = 0;
						query_data[12] = 2;					
					}
					query2 = query2.replace("isRecovered", "");
				}
				else
				{
					System.out.println("\nQuery Is Not in the Expected Format. It must contain (is or num) prefix");
				}			
				parse_agent_data(query2, portion);			
		}
		else if(query2.contains("num"))
		{
				if(portion == 1)
					query_data[4] = 1;
				else 
					query_data[9] = 1;
			
				if(query2.contains("numSuscepted"))
				{
					if(portion == 1)
					{
						query_data[6] = 1;				
					}
					else
					{
						query_data[11] = 1;					
					}
					query2 = query2.replace("numSuscepted", "");
				}
				else if(query2.contains("numInfected"))
				{
					if(portion == 1)
					{	
						query_data[6] = 2;				
					}
					else 
					{ 
						query_data[11] = 2;
					}
					query2 = query2.replace("numInfected", "");
				}
				else if(query2.contains("numRecovered"))
				{
					if(portion == 1)
					{
						query_data[6] = 3;
					}
					else 
					{ 
						query_data[11] = 3;
					}
					query2 = query2.replace("numRecovered", "");
				}
				else
				{
					System.out.println("\nQuery Is Not in the Expected Format. It must contain (is or num) prefix");
				}
				parse_city_data(query2, portion);
		}
		else
		{
			System.out.println("\nQuery Is Not in the Expected Format. It must contain (is or num) prefix");
		}
	}

	
	public void parse_agent_data(String query2, int portion) 
	{
		System.out.println("1. Portion " + portion +" : "+ query2);		
		query2 = query2.replace("(","");
		query2 = query2.replace(")","");		
		query2 = query2.replace(" ","");
		
		if(portion == 1)
		{	
			query_data[5] = Integer.parseInt(query2);
			System.out.println("2. Portion " + portion +" : "+ query_data[5]);
		}
		else
		{	
			query_data[10] = Integer.parseInt(query2);
			System.out.println("2. Portion " + portion +" : "+ query_data[10]);
		}

	}
	
	public void parse_city_data(String query2, int portion) 
	{
		System.out.println("3. Portion " + portion +" : "+ query2);
		
		query2 = query2.replace("(", "");
		query2 = query2.replace(")", "");
		
		System.out.println("4. Portion " + portion +" : "+ query2);
		
		String[] split_second;
		
		if(query2.contains("<="))
		{
			split_second = query2.split("<=", 2);	
			
			if(portion == 1)
				query_data[8] = 1;
			else
				query_data[13] = 1;
		}
		else
		{
			split_second = query2.split(">=", 2);
			
			if(portion == 1)
				query_data[8] = 2;
			else
				query_data[13] = 2;
		}
		split_second[0] = split_second[0].replace(" ", "");
		split_second[1] = split_second[1].replace(" ", "");

		System.out.println("Portion " + portion +" : "+ split_second[0] + "  " + split_second[1]);
		
		if(portion == 1)		
		{	
				query_data[5] = Integer.parseInt(split_second[0]);
				query_data[7] = Integer.parseInt(split_second[1]);
				System.out.println("7. Portion " + portion +" : "+ query_data[5]+ "  " + query_data[7]);
		}
		else
		{
				query_data[10] = Integer.parseInt(split_second[0]);
				query_data[12] = Integer.parseInt(split_second[1]);
				System.out.println("12.Portion " + portion +" : "+ query_data[10]+ "  " + query_data[12]);
		} 
		
		//System.out.println("Portion " + portion +" : "+ split_second[0] + "  " + split_second[1]);
		
	}

	/*parse the sub part of the query */
	public void parse_data(String substr)
	{
		String sub_first = "";
		String sub_second = "";
		StringTokenizer st2 = new StringTokenizer(substr, "<=");
		sub_first = st2.nextToken();
		sub_second = st2.nextToken();
		System.out.println(sub_first + "  " + sub_second);		
	}
	
	/*Reads query */
	public int read_query()
	{
		String query2 = query;

		String query_first  = "";
		String query_second = "";
		
		String dup_query = query2;
		
		if(query2.contains("Untill"))
		{
			query_data[0] = 0;
			
			dup_query = dup_query.replace("(<=", "");
			String[] split_query = dup_query.split("Untill",2);
			
			query_first  = new String(split_query[0]);
			query_second = new String(split_query[1]);			
		
			if(query_second.contains(")"))
			{		
					String[] split_second = query_second.split(" ", 2);			
					split_second[0] = split_second[0].replace(")","");		
					query_data[1] = Integer.parseInt(split_second[0]);
					System.out.println(split_second[0] + "  -- " + query_data[1]+ " ---  " + split_second[1]);
					query_second = split_second[1];
			}
			
			if(query_first.contains("TRUE"))
			{
				query_data[2] = 1;
			}
			else
			{
				System.out.println("5: " + query_first);
				parse_sub_query(query_first, 1);
			}
			
			if(query_second.contains("TRUE"))	
			{
				query_data[3] = 1;
			}		
			else
			{
				System.out.println("6: " + query_second);
				parse_sub_query(query_second, 2);
			}
			
		}
		else if(query2.contains("Next") )
		{
			query_data[0] = 1;
			query_data[1] = 1;
		}
		else
		{
			System.out.println("\nQuery Is Not in the Expected Format. It must contain (Untill or Next) Operator");
		}
		
		for(int i=0; i<=13; i++)
			System.out.println(i + " :" + query_data[i]);
		
		return query_data[1];
		//query_data[5] = agent;
		//query_data[10] = city;
	}
	
	public void evaluate_sub_query(int index, int [] curr_state_people, int [] num_suscepted_city, int [] num_infected_city, int [] num_recovered_city )
	{
		int dup_infected_city[] = new int[50];
		
		if(query_data[index+2] == 2)
		{	
			int id = (query_data[index+1]);
			dup_infected_city[(id)] = num_infected_city[(id)] + num_recovered_city[(id)];
			System.out.print("\n duplicate value : " + dup_infected_city[(id)] + " id : " + id);
		} 
			
		switch(query_data[index+2]) 
		{
			case 0 :check_sub_query(index, curr_state_people);
					break;
			case 1 :check_sub_query(index, num_suscepted_city);
					break;
			case 2 :check_sub_query(index, dup_infected_city);
					break;
			case 3 :check_sub_query(index, num_recovered_city);
					break;		 
		}	
						
	}
	
	public int check_sub_query_satisfied(int op, int id, int val, int [] array)
	{
		switch(op)
		{
			case 0  : if(array[id] == val) return 1; else return 0;
			case 1  : if(array[id] <= val) return 1; else return 0;
			case 2  : if(array[id] >= val) return 1; else return 0;
			default : return 0;
		} 				
	}
		
	
	public void check_sub_query(int index, int [] array)
	{
		int isValid = check_sub_query_satisfied(query_data[index+4], query_data[index+1], query_data[index+3], array);

		if(index == 4)
			query_data[2] = isValid;		
		else
			query_data[3] = isValid;
	}

	public void evaluate_query(int [] curr_state_people, int [] num_suscepted_city, int [] num_infected_city, int [] num_recovered_city)
	{
			evaluate_sub_query(4, curr_state_people, num_suscepted_city, num_infected_city, num_recovered_city);
			evaluate_sub_query(9, curr_state_people, num_suscepted_city, num_infected_city, num_recovered_city);
	}
	
	public boolean check_query_satisfied(int [] curr_state_people, int [] num_suscepted_city, int [] num_infected_city, int [] num_recovered_city)
	{
		evaluate_query(curr_state_people,  num_suscepted_city, num_infected_city, num_recovered_city);
		
		if((query_data[2]==1) &&(query_data[3]==1)) 
			return true;
		else
			return false;
	}
	
	/*public static void main(String args[])
	{
		String query = "(isInfected(10)) Untill(<=10) (numInfected(15)>=30)";
		ProcessQuery pq = new ProcessQuery();
		pq.read_query(query);
		
		int[] curr_state_people;
		int[] num_suscepted_city;
		int[] num_infected_city;
		int[] num_recovered_city;
		if(pq.check_query_satisfied(curr_state_people,  num_suscepted_city, num_infected_city, num_recovered_city))
			System.out.println("Yes Satisfied");
		else
			System.out.println("Not Satisfied");
	}	*/
}
