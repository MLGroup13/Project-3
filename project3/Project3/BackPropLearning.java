
public class BackPropLearning 
{
	private float [][] examples;
	private Network network;
	private INode[] inputN;
	private HNode[] layer1, layer2;
	private ONode[] outputN;
	
	public BackPropLearning(float [][] ex, Network net)
	{
		network = net;
		examples = ex;
		
		inputN = network.getINodes();
			if (network.getHLayers() == 1)
			{
				layer1 = network.getL1HNodes();
			}
			else if (network.getHLayers() == 2)
			{
				layer1 = network.getL1HNodes();
				layer2 = network.getL2HNodes();
			}
		outputN = network.getONodes();
	}
	
	public Network Learn(float learn_rate)
	{
		for (int i = 0; i < examples.length; i++)
		{
			// setup inputs
			for (int j = 0; j < inputN.length; j++)
			{
				inputN[j].setInput(examples[i][j]);
			}
			
			// setup 1st hidden layer
			for (int j = 0; j < layer1.length; j++)
			{
				float in = 0;
				for (int k = 0; k < inputN.length; k++)
				{
					float w[] = inputN[k].getWeight();
					in = in + (w[j]*inputN[k].getInput());
				}
				layer1[j].setOutput(sigActivate(in));
			}
			
			// setup output layer
			for (int j = 0; j < outputN.length; j++)
			{
				float in = 0;
				for (int k = 0; k < layer1.length; k++)
				{
					float w[] = layer1[k].getWeight();
					in = in + (w[j]*layer1[k].getOutput());
				}
				outputN[j].setOutput(sigActivate(in));
			}
			
			/* CALCULATE delta_i's (output node's percentage of network error)
			 * for output nodes to back-propagate error to previous layer
			 */
			
			// array to store delta_i of outputs so can distribute errors to previous layer
			float delta_i[] = new float[outputN.length]; 
				
			// loop to calculate delta_i's
			for (int o = 0; o < outputN.length; o++)
			{
				// if network has 1 hidden layer, delta_i's calculated using 1st hidden layer
				if (network.getHLayers() == 1)
				{
					float in = 0;
					for (int j = 0; j < layer1.length; j++)
					{
						float w[] = layer1[j].getWeight();
						in = in + layer1[j].getOutput()*w[o];
					}
					delta_i[o] = sigDerive(in)*(examples[i][examples[i].length-1] - outputN[o].getOutput());
					
					//System.out.print("delta" + 0 + delta_i[o]);
				}
					
			}
			
			
			/* CALCULATE percentage of error for rest of layers
			 * and back-propagate error to previous layers
			 */
			
			// if the network has a single hidden layer
			if (network.getHLayers() == 1)
			{
				// array to store delta_j's of hidden layer outputs so can distribute errors to previous layer 
				float[] delta_j = new float [layer1.length];
					
				// loop to backprop error from hidden layer to input layer
				for (int j = 0; j < layer1.length; j++)
				{
					float in = 0;
					float w[] = layer1[j].getWeight();
					float sum = 0;
						
					// loop through previous layer to determine input for hidden layer 
					for (int p = 0; p < inputN.length; p++)
					{
						float wI[] = inputN[p].getWeight();
						in = in + inputN[p].getInput()*wI[j];
					}
						
					// loop through hidden layer to get sum of it's weights and delta i from output layer
					for (int o = 0; o < outputN.length; o++)
					{
						sum = sum + w[o]*delta_i[o];
					}
					delta_j[j] = sigDerive(in)*sum;
				
					// loop to update weights of input layer
					for (int input = 0; input < inputN.length; input++)
					{
						float wI[] = inputN[input].getWeight();
						for (int w0 = 0; w0 < wI.length; w0++)
						{
							wI[w0] = wI[w0] + learn_rate*inputN[input].getInput()*delta_j[j];
						}
						network.setInputWeights(input, wI);
					}
					
				}
			}
		}
		
		return network;
	}
	
	// sigmoid activation function 
	public float sigActivate(float x){
		float e = (float) Math.E;
        float t = (float) Math.pow(e, -x);
        float activate = 1 / (1 + t);
		return activate;
	}
	
	// sigmoid derivative 
	public float sigDerive(float x)
	{
		float e = (float) Math.E;
		float numerator = (float) Math.pow(e, x);
		float denomenator = (float) Math.pow((1 + Math.pow(e, x)), 2);
		return numerator / denomenator;
	}

}
