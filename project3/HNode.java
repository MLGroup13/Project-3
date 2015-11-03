import java.util.Random;

public class HNode 
{
	private float output;
	private float initW;
	private float[] weight;
	
	// create random generator object
		Random randomN = new Random();
		
		public HNode(float w)
		{
			initW = w;
		}
		
		// initialize the weights to a random real between 0.0 and 1.0
		public void initWeight(int w)
		{
			weight = new float[w];
			
			for(int i = 0; i < w; i++)
			{
				weight[i] = initW;
				//System.out.print("w" + i + "=" + weight[i]);
			}
		}
		
		public float[] getWeight()
		{
			return weight;
		}
		
		public void setWeight(float[] w)
		{
			weight = w;
		}
		
		public float getOutput()
		{
			return output;
		}	
		
		public void setOutput(float a)
		{
			output = a;
		}
}
