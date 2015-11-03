import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class DataPreprocessor 
{
	public static void main(String[] args) throws Exception
	{
		// create Scanner instance to receive info from user 
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter file to process");
		String dataset = input.nextLine(); 
		
		System.out.println("Delimiter");
		String delimiter = input.nextLine();
		
		System.out.println("No. of attributes");
		int attr = input.nextInt();
		
		System.out.println("No. of instances");
		int instance = input.nextInt();
		
		float [][] data = new float [attr][instance];
		
		System.out.println("No. of attributes to delete");
		int delete = input.nextInt();
		int [] deletes = new int [delete];
		for(int i = 0; i < delete; i++)
		{
			System.out.println("Index of delete" + i);
			deletes[i] = input.nextInt();
		}
		
		System.out.println("No. of attributes to skip");
		int skip = input.nextInt();
		int [] skips = new int [skip];
		for(int i = 0; i < skip; i++)
		{
			System.out.println("Index of skip" + i);
			skips[i] = input.nextInt();
		}
		
		// create file reader for dataset file and wrap it in a buffer
		FileReader fileReader = new FileReader(dataset);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
					
		// create file writer for result file and wrap in buffer  
		PrintWriter fileWriter = new PrintWriter("pro_" + dataset);
		
		float min = 0;
		float max = 0;
		
		for (int i = 0; i < instance; i++)
		{
			String line = bufferedReader.readLine();
			String[] data_line = line.split(delimiter);
			for (int j = 0; j < data_line.length; j++)
			{
				data[j][i] = Float.parseFloat(data_line[j]);
				
				if (data[j][i] < min)
				{
					min = data[j][i];
				}
				
				if (data[j][i] > max)
				{
					max = data[j][i];
				}
			}
			
		}
		
		float range = max - min;
		
		for (int i = 0; i < instance; i++)
		{
			for (int j = 0; j < attr; j++)
			{
				data[j][i] = (data[j][i] - min)/(range);
				fileWriter.print(data[j][i] + ",");
			}
			fileWriter.println();
		}
		fileReader.close();
		fileWriter.close();
	}
}
