import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Network
{	
	PrintWriter write = new PrintWriter("errors.txt");
	PrintWriter averageOut = new PrintWriter("averages.txt");
	ArrayList averages = new ArrayList();
	
	private float[] inputVector;
	private float[] outputVector;
	private INode[] inputN;
	private int HLayers;
	private HNode[] layer1, layer2;
	private ONode[] outputN;
	private float bias;
	private float weight;
	
	public Network(int in, int hLayers, int [] hNodes, float[] inv, float[] outv, int out, float b, float w) throws FileNotFoundException
	{
		inputN = new INode[in];
		HLayers = hLayers;
		inputVector = inv;
		outputVector = outv; 
		outputN = new ONode[out];
		if (hLayers == 1)
		{
			layer1 = new HNode[hNodes[0]];
		}
		else if (hLayers == 2)
		{
			layer1 = new HNode[hNodes[0]];
			layer2 = new HNode[hNodes[1]];
		}
		bias = b;
		weight = w;
	}
	
	public void setupNetwork()
	{
		setupInputNodes();
		if (HLayers > 0)
		{
			setupHiddenNodes();
		}
		setupOutputNodes();
	}
	
	public void runNetwork(float[] inv, float[] outv)
	{
		// set output vector 
		outputVector = outv;
		
		// send new input into input nodes
		setInput(inv);
		if (HLayers > 0)
		{
			updateHiddenNodes();
		}
		updateOutputNodes();
	}
	
	private void setupInputNodes()
	{
		/* if there are no hidden nodes the number of weights 
		of each input node is equal to the number of outputs */
		if (HLayers == 0)
		{
			for(int i = 0; i < inputN.length; i++)
			{
				System.out.print("N" + i + " ");
				inputN[i] = new INode(inputVector[i], weight);
				inputN[i].initWeight(outputN.length);
			}
		}
		
		/* if there are hidden nodes the number of weights
		of each input node is equal to the number of hidden 
		nodes in the first hidden layer */
		else if (HLayers > 0)
		{
			for(int i = 0; i < inputN.length; i++)
			{
				inputN[i] = new INode(inputVector[i], weight);
				inputN[i].initWeight(layer1.length);
			}
		}
	}
	
	private void setupHiddenNodes()
	{
		for (int i = 0; i < HLayers; i++)
		{
			// if on the first hidden layer
			if(i == 0)
			{
				for (int j = 0; j < layer1.length; j++)
				{
					/* if there is a single hidden layer the number of weights
					of each node in the first hidden layer is equal to the 
					number of outputs */
					if (HLayers == 1)
					{
						layer1[j] = new HNode(weight);
						layer1[j].initWeight(outputN.length);
						calcAct(inputN, layer1[j], j);
					}
					
					/* if there is two hidden layers the number of weights
					of each node in the first hidden layer is equal to the
					number of nodes in the second hidden layer*/
					else if (HLayers == 2)
					{
						layer1[j] = new HNode(weight);
						layer1[j].initWeight(layer2.length);
						calcAct(inputN, layer1[j], j);
					}
				}
			}
			
			// if on the second hidden layer
			if(i == 1)
			{
				for(int j = 0; j < layer2.length; j++)
				{
					layer2[j] = new HNode(weight);
					layer2[j].initWeight(outputN.length);
					calcAct(layer1, layer2[j], j);
				}
			}
		}
	}
	
	// method to update hidden nodes
	private void updateHiddenNodes()
	{
		for (int i = 0; i < HLayers; i++)
		{
			// if on the first hidden layer
			if(i == 0)
			{
				for (int j = 0; j < layer1.length; j++)
				{
					calcAct(inputN, layer1[j], j);
				}
			}
			
			// if on the second hidden layer
			if(i == 1)
			{
				for(int j = 0; j < layer2.length; j++)
				{
					calcAct(layer1, layer2[j], j);
				}
			}
		}
	}
	
	private void setupOutputNodes()
	{
		// case for neural net with no hidden layers
		if (HLayers == 0)
		{
			for(int i = 0; i < outputN.length; i++)
			{
				outputN[i] = new ONode();
				calcAct(inputN, outputN[i], i);
			}
		}
		
		// case for neural net with a single layer
		else if (HLayers == 1)
		{
			for(int i = 0; i < outputN.length; i++)
			{
				outputN[i] = new ONode();
				calcAct(layer1, outputN[i], i);
			}
		}
		
		// case for neural net with two hidden layers
		else if (HLayers == 2)
		{
			for(int i = 0; i < outputN.length; i++)
			{
				outputN[i] = new ONode();
				calcAct(layer2, outputN[i], i);
			}
		}
	}
	
	// method to update output nodes
	public void updateOutputNodes()
	{
		// case for neural net with no hidden layers
				if (HLayers == 0)
				{
					for(int i = 0; i < outputN.length; i++)
					{
						calcAct(inputN, outputN[i], i);
					}
				}
				
				// case for neural net with a single layer
				else if (HLayers == 1)
				{
					for(int i = 0; i < outputN.length; i++)
					{
						calcAct(layer1, outputN[i], i);
					}
				}
				
				// case for neural net with two hidden layers
				else if (HLayers == 2)
				{
					for(int i = 0; i < outputN.length; i++)
					{
						calcAct(layer2, outputN[i], i);
					}
				}
	}
	
	/** Shane wrote original, James adjusted input parameter 
	 *	An activation function using a sigmoid function. 
	 *
	 */
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
		float denomenator = (float) Math.pow(1 + Math.pow(e, -x), 2);
		return numerator / denomenator;
	}
	
	/** Shane wrote original, James adjusted input parameter
	 *  An activation function using a hyperbolic tangent.
	 */
	public float tanhActivate(float x){
        float activate = (float) Math.tanh(x);
        return activate;
    }
	
	// hyperbolic tangent derivative 
	public float tanhDeriv(float x)
	{
		float derive = (float) (Math.sinh(x) / Math.cosh(x));
		return derive;
	}
	
	// squared error used to compare network output o(x) to actual y from Rosenbrock function 
	public float sqError(float o, float y)
	{
		float err;
		err = (float) (0.5f*Math.pow((y - o), 2));
		
		return err;
	}
	
	// calcAct used by output nodes when there are no hidden layers
	private void calcAct(INode[] preLayer, ONode curNode, int index)
	{
		float sum = 0;
		for (int i = 0; i < preLayer.length; i++)
		{
			float [] w = preLayer[i].getWeight();
			sum = sum + preLayer[i].getInput()*w[index];
		}
		curNode.setOutput(sigActivate(sum + bias));
	}
	
	// calcAct used by output nodes when there are one or more hidden layers
	private void calcAct(HNode[] preLayer, ONode curNode, int index)
	{
		float sum = 0;
		for (int i = 0; i < preLayer.length; i++)
		{
			float [] w = preLayer[i].getWeight();
			sum = sum + preLayer[i].getOutput()*w[index];
		}
		curNode.setOutput(sigActivate(sum + bias));
	}
	
	// calcAct used by first hidden layer 
	private void calcAct(INode[] preLayer, HNode curNode, int index)
	{
		float sum = 0;
		for (int i = 0; i < preLayer.length; i++)
		{
			float [] w = preLayer[i].getWeight();
			sum = sum + preLayer[i].getInput()*w[index];
		}
		curNode.setOutput(sigActivate(sum + bias));
	}
	
	// calcAct used by the second hidden layer 
	private void calcAct(HNode[] preLayer, HNode curNode, int index)
	{
		float sum = 0;
		for (int i = 0; i < preLayer.length; i++)
		{
			float [] w = preLayer[i].getWeight();
			sum = sum + preLayer[i].getOutput()*w[index];
		}
		curNode.setOutput(sigActivate(sum + bias));
	}
	
	// method to get input nodes
	public INode[] getINodes()
	{
		return inputN;
	}
	
	// method to set input node values
	public void setInput(float [] inv)
	{
		for (int i = 0; i < inputN.length; i++)
		{
			inputN[i].setInput(inv[i]);
		}
	}
	
	// method to set input weights
	public void setInputWeights(int index, float[] w)
	{
		inputN[index].setWeight(w);
	}
	
	// method to set hidden node weights
	public void setHiddenWeights(int hL, int index, float[] w)
	{
		if (hL == 1)
			layer1[index].setWeight(w);
		if (hL == 2)
			layer2[index].setWeight(w);
	}
	
	public int getHLayers()
	{
		return HLayers;
	}
	
	// method to get hidden layer 1 nodes
	public HNode[] getL1HNodes()
	{
		return layer1;
	}
	
	// method to get hidden layer 2 nodes
	public HNode[] getL2HNodes()
	{
		return layer2;
	}
	
	// method to get output nodes
	public ONode[] getONodes()
	{
		return outputN;
	}
	
	// function to convert output for classification
	public void convertOutput(int classes)
	{
		float threshold = classes / 2;
		for (int i = 0; i < outputN.length; i++)
		{
			if (outputN[i].getOutput() < threshold)
			{
				outputN[i].setOutput(0.0f);
			}
			else if (outputN[i].getOutput() > threshold)
			{
				outputN[i].setOutput(1.0f);
			}
		}
	}
	
	// method to get output
	public float getOutput()
	{
		return outputN[0].getOutput();
	}
	
	
	void printNetwork() throws FileNotFoundException
	{
		for(int i = 0; i < outputN.length; i++)
		{
			float output = outputN[i].getOutput();
			System.out.print("Output" + i + ": " + output);
			float err = sqError(outputN[i].getOutput(), outputVector[i]);
			System.out.print("Error: " +  err );
			
		}
	}
	
	void printResults()
	{
		
	}
	
	void WriteClose(){
		write.close();
	}
	void averageClose(){
		averageOut.close();
	}
	
	void overallAverage(){
		float temp = 0;
		float average = 0;
		for(int i = 0; i < averages.size(); i++){
			temp = (float) averages.get(i);
			average += temp;
		}
		average = average / averages.size();
		averageOut.println("the average is " + average);
	}
	
}
