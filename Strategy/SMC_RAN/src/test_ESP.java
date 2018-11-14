import java.io.*;

public class test_ESP
{
	public static void main(String args[]) throws Exception
	{
		 System.setOut(new PrintStream(new OutputStream() {
			  public void write(int b) {
			    // NO-OP
			  }
			}));		 
		     
		 // System.setOut(new PrintStream(new FileOutputStream("output2.txt")));
		  //System.out.println("Itereation Id : " + i);
           File config_File =  new File(System.getProperty("user.dir")+ "/../../../Input/ESP_Resources/resources/config.properties");
           ReadInput_ESP es = new ReadInput_ESP(config_File);
           es.read_configFile();  
	}
}
