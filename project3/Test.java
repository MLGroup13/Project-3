import java.util.Scanner;
import java.io.*;

public class Test {

	public static void main(String[] args) throws Exception
	{
		// create Scanner instance to receive info from user 
		Scanner input = new Scanner(System.in);
		
		String line = null;
		String dataset;
		String result;
		String ptype;
		int classes = 0;
		String delim;
		
		System.out.println("Enter name of dataset file with extension");
		dataset = input.nextLine();
		
		System.out.println("Enter name of file to save results. (Extension will be added automatically)");
		result = input.nextLine();
		
		System.out.println("Problem Type (c or r)");
		ptype = input.nextLine();
		
		System.out.println("Enter delimiter used by dataset");
		delim = input.nextLine();
		
		System.out.println("How many classes");
		classes = input.nextInt();
		
		// create file reader for dataset file and wrap it in a buffer
		FileReader fileReader = new FileReader(dataset);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
			
		// create file writer for training result file  
		PrintWriter fileWriter = new PrintWriter(new FileOutputStream(result + ".train.txt"), true);
		
		// create file writer for testing result file
		PrintWriter fileWriterO = new PrintWriter(new FileOutputStream(result + ".test.txt"), true);
			
		// write name of dataset based on file name entered
		
		/* FIRST PHASE initializes a network with random weights
		 */
		System.out.println("INITIALIZATION PHASE");
		
		int iNodes;
		System.out.println("# of input nodes?");
		iNodes = input.nextInt();
		
		float[] inputVector = new float[iNodes];
		for (int i = 0; i < iNodes; i++)
		{
			System.out.println("enter input" + i);
			inputVector[i] = input.nextFloat();
		}
		
		int hLayers;
		int [] hNodes = new int[2];
		System.out.println("# of hidden layers?");
		hLayers = input.nextInt();
		for (int i = 0; i < hLayers; i++)
		{
			System.out.print("# of nodes in layer" + i);
			hNodes[i] = input.nextInt();
		}
		
		int oNodes;
		System.out.println("# of output nodes");
		oNodes = input.nextInt();
		
		float[] outputVector = new float[oNodes];
		for (int i = 0; i < oNodes; i++)
		{
			System.out.print("enter vector" + i);
			outputVector[i] = input.nextFloat();
		}
		
		float bias;
		System.out.println("Enter Bias");
		bias = input.nextFloat();
		
		float weight;
		System.out.println("Enter initialize weights");
		weight = input.nextFloat();
		
		// code to setup a a new Network 
		Network network = new Network(iNodes, hLayers, hNodes, inputVector, outputVector, oNodes, bias, weight);
		network.setupNetwork();
		network.printNetwork();
		
		
		/* LEARNING PHASE which uses training examples 
		 */
		System.out.println("LEARNING PHASE");
		
		// prompt for number of examples for training 
		int example;
		System.out.println("How many examples for learning?");
		example = input.nextInt();
		
		float [][] examples = new float[example][iNodes+1];
		for(int i = 0; i < example; i++)
		{
			line = bufferedReader.readLine();
			String[] data = line.split(delim);
			for (int j = 0; j < iNodes; j++)
			{
				examples[i][j] = Float.parseFloat(data[j]);
			}
			examples[i][iNodes] = Float.parseFloat(data[iNodes]);
		}
		
		Network networkLearn = network;
		
		float learn_rate;
		System.out.println("Enter Learning Rate");
		learn_rate = input.nextFloat();
		
		if (hLayers == 0)
		{
			PerceptronLearning learner = new PerceptronLearning(examples, networkLearn);
			learner.printExamples();
			
			System.out.println("Epochs | Average Error");
			
			float averageErr;
			int epochs = 0;
			do
			{
				averageErr = 0;
				
				System.out.print(epochs + " ");
				
				networkLearn = learner.Learn(learn_rate);
		
				float totalErr = 0;
				for (int i = 0; i < example; i++)
				{
					float inputV[] = new float [examples[i].length-1];
					float outputV[] = new float [oNodes];
				
					for (int j = 0; j < inputV.length; j++)
					{
						inputV[j] = examples[i][j];
					}
					
					for (int j = 0; j < outputV.length; j++)
					{
						outputV[j] = examples[i][iNodes];
					}
			
					networkLearn.runNetwork(inputV, outputV);
					
					if (ptype.equals("r"))
					{
						totalErr = totalErr + networkLearn.sqError(networkLearn.getOutput(),outputV[0]);
					}
					else if (ptype.equals("c"))
					{
						System.out.print(networkLearn.getOutput());
						networkLearn.convertOutput(classes);
						if (networkLearn.getOutput() == outputV[0])
						{
							totalErr = totalErr + 1.0f;
						}
						else
							totalErr = totalErr + 0.0f;
					}
				}
				
				averageErr = totalErr / example;
				System.out.println(averageErr);
			
				epochs++;
			}	while ( epochs < 1000 );
		}
		else if (hLayers > 0)
		{
			BackPropLearning learner = new BackPropLearning(examples, networkLearn);
			float averageErr;
			int epochs = 0;
			//System.out.println("Epochs | Average Error");
			fileWriter.println("Epochs | Average Error");
			
			do
			{
				averageErr = 0;
				//System.out.print(epochs + " ");
				fileWriter.print(epochs + " ");
				networkLearn = learner.Learn(learn_rate);
		
				float totalErr = 0;
				for (int i = 0; i < example; i++)
				{
					float inputV[] = new float [examples[i].length-1];
					float outputV[] = new float [oNodes];
				
					for (int j = 0; j < inputV.length; j++)
					{
						inputV[j] = examples[i][j];
					}
					
					for (int j = 0; j < outputV.length; j++)
					{
						outputV[j] = examples[i][iNodes];
					}
			
					networkLearn.runNetwork(inputV, outputV);
					
					totalErr = totalErr + outputV[0] - networkLearn.getOutput();
				}
				
				averageErr = totalErr / example;
				//System.out.println(averageErr);
				fileWriter.println(averageErr);
			
				epochs++;
			}	while(epochs < 10000);
		}
		
		
		
		/* TESTING PHASE which tests the accuracy of the approximation 
		 */
		System.out.println("TESTING PHASE");
		
		System.out.println("How many test Examples");
		int test = input.nextInt();
		
		float averageErr = 0;
		float totalErr = 0;
		
		fileWriterO.println("Example NetworkOutput ActualOutput Error");
		for (int i = 0; i < test; i++)
		{
			fileWriterO.print(i + " ");
			line = bufferedReader.readLine();
			String[] data = line.split(delim);
			
			for (int j = 0; j < iNodes; j++)
			{
				
				inputVector[j] = Float.parseFloat(data[j]);
				//System.out.print(inputVector[j] + " ");
			}
		
		outputVector[0] = Float.parseFloat(data[iNodes]);
		networkLearn.runNetwork(inputVector, outputVector);
		float exampleErr = 0;
			if (ptype.equals("r"))
			{
				fileWriterO.print(networkLearn.getOutput() + " " + outputVector[0] + " ");
				exampleErr = outputVector[0] - networkLearn.getOutput();
				fileWriterO.print(exampleErr);
		
				totalErr = totalErr + exampleErr;
			}
		
			else if (ptype.equals("c"))
			{
				networkLearn.convertOutput(classes);
				System.out.print(outputVector[0] + " " + networkLearn.getOutput() + " ");
				if (networkLearn.getOutput() == outputVector[0])
				{
					totalErr = totalErr + 1.0f;
				}
				else
					totalErr = totalErr + 0.0f;
			}
			fileWriterO.println();
		}
		
		averageErr = totalErr / example;
		fileWriterO.println("Average Error: " + averageErr);
		input.close();
		bufferedReader.close();
		fileWriter.close();
		fileWriterO.close();
		//bufferedWriter.close();
	}

}
