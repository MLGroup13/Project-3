
public class PerceptronLearning 
{
	private float [][] examples;
	private Network network;
	private INode[] inputN;
	
	public PerceptronLearning(float [][] ex, Network net)
	{
		network = net;
		examples = ex;
		inputN = network.getINodes();
	}
	
	public Network Learn(float learn_rate)
	{
		for (int i = 0; i < examples.length; i++)
		{	
			for (int j = 0; j < examples[i].length-1; j++)
			{
				float w[] = inputN[j].getWeight();
				float in = 0;
				for (int k = 0; k < w.length; k++)
				{
					in = in + (w[k]*examples[i][j]);
				}
				float Err = examples[i][examples[i].length-1] - sigActivate(in);
				for (int k = 0; k < w.length; k++)
				{
					w[k] = w[k] + learn_rate*Err*sigDerive(in)*examples[i][j];
				}
				network.setInputWeights(j, w);
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
	
	public void printExamples()
	{
		for (int i = 0; i < examples.length; i++)
		{
			for(int j = 0; j < examples[i].length; j++)
			{
				System.out.print(examples[i][j]);
			}
			System.out.println();
		}
	}
}
