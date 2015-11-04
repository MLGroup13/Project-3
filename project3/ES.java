import java.util.*;

/**
 *
 * @author Angelica Davis, James Beck, Shane Bauerman
 */
public class ES {

    private int mu; // number of individuals in parent pool (size of B)
    private int p; // mixing number (size of mixing set)
    private int lambda; // number of offspring 
    private int k; // size of x-vector (weights)
    private Individual[] B; // current population
    private Individual[] P; // mixing set
    private static float tao;
    private static float tao_prime;
    private float alpha;
    private Network[] networks;

    public ES(Network[] networks, int mu, int p, int lambda, float alhpa) {
        this.mu = mu;
        this.p = p;
        this.lambda = lambda;
        B = initializeB(networks);
        this.networks = networks;
        P = new Individual[p];
        this.alpha = alpha;
        tao_prime = (float) (1 / Math.sqrt(2) * N(1)); // N?????
    }

    /*
     * Extracts weights from each input network to be used as a population starting point
     *  returns a set of individuals
     */
    public Individual[] initializeB(Network[] nets) {
        // create an array for the initial population of individuals
        Individual[] B = new Individual[nets.length];
        for (int i = 0; i < nets.length; i++) {
            // initialize strategy params to random value
            Random rand = new Random();
            float s = rand.nextFloat();
            // extract weights from the current network
            float[][] w_I = new float[nets[i].getINodes().length][];
            float[][] w_H = new float[nets[i].getL1HNodes().length][];
            for (int j = 0; j < nets[i].getINodes().length; j++) {
                w_I[j] = nets[i].getINodes()[j].getWeight();
            }
            for (int j = 0; j < nets[i].getL1HNodes().length; j++) {
                w_H[j] = nets[i].getINodes()[j].getWeight();
            }
            float[] weights_from_network = convertTo1DArray(w_I, w_H);
            // create new indivdual based on extracted weights and add to population
            Individual x = new Individual(weights_from_network, s);
            B[i] = x;
        }
        return B;
    }

    /*
     * Combines a pair of weight matrices extracted from networks
     *  and returns a single 1-D array of all network weights
     */
    public float[] convertTo1DArray(float[][] w_I, float[][] w_H) {
        int h1 = w_I.length; // "height" of first array
        int w1 = w_I[0].length; // "width" of first array
        int h2 = w_H.length; // "" of second
        int w2 = w_H[0].length; // ""
        int dimension = h1 * w1 + h2 * w2;
        float[] w = new float[dimension];
        for (int i = 0; i < w_I.length; i++) {
            for (int j = 0; j < w_I[0].length; j++) {
                w[i * w1 + j] = w_I[i][j];
            }
        }
        for (int i = 0; i < w_H.length; i++) {
            for (int j = 0; j < w_H[0].length; j++) {
                w[h1 * w1 + i * w2 + j] = w_H[i][j];
            }
        }
        return w;
    }

    // Not sure yet how to return weights to network, return type void for now
    public void Evolve() {
        int g = 0;
        Individual[] B_prime = new Individual[lambda];
        for (int i = 0; i < lambda; i++) {
            float s = recombine_Ps();
            float x[] = recombine_Px();
            float s_prime = mutate_s(s);
            Individual x_prime = mutate_x(x, s_prime);
            // fitness
            B_prime[i] = x_prime;
        }
        Individual[] BnextGen = nextGen(B_prime);
        B = BnextGen;
        g++;
    }

    /*
     * Returns a set of networks given the current population
     */
    public Network[] getNetworks(Individual[] Population) {
        Network[] nets = networks;
        int w1 = networks[0].getINodes().length; // "width" of input nodes' weight matrix
        int h1 = networks[0].getINodes()[0].getWeight().length; // "height""
        int w2 = networks[0].getL1HNodes().length; // "width" of hidden layer weights
        int h2 = networks[0].getL1HNodes()[0].getWeight().length; // "height" of hidden layer weights matrix
        for (int i = 0; i < Population.length; i++) {
            float[][] w_I = new float[w1][h1];
            float[][] w_H = new float[w2][h2];
            float[] x = Population[i].get_x();
            for (int j = 0; j < w1; j++) {
                for (int k = 0; k < h1; k++) {
                    w_I[i][j] = x[i * w1 + j];
                }
            }
            for (int j = 0; j < w2; j++) {
                for (int k = 0; k < h2; k++) {
                    w_I[i][j] = x[w1*h1 + i * w2 + j];
                }
            }
            nets[i].updateNetworkWeights(w_I, w_H);
        }
        return nets;
    }

    public float[] recombine_Px() {
        float[] x = new float[k]; // x-vector derived from recombination
        Random random = new Random();
        for (int i = 0; i < k; k++) {
            int r = random.nextInt(p); // randomly select a parent from the mixing set
            x[i] = P[r].get_x()[i];  // select the ith "gene" from the selected parent
        }
        return x;
    }

    public float recombine_Ps() {
        Random random = new Random();
        int r = random.nextInt(mu);
        return P[r].get_s();    // randomly select the s "gene" from parent pool 
    }

    public Individual mutate_x(float[] x, float s_prime) {
        float[] x_prime = new float[k];
        for (int i = 0; i < k; i++) {
            x_prime[i] = x[i] + s_prime * N(x[i]);
        }
        return new Individual(x_prime, s_prime);
    }

    public float mutate_s(float s) {
        float rho_prime = (float) 0.0;
        rho_prime = (float) (s * Math.pow(Math.E, tao * N(1)));  // Math????
        return rho_prime;
    }

    /*
     * Computes and returns the Gaussian distribution
     *  N(0,1)
     */
    public float N(float x) {
        return (float) (1 / Math.sqrt(2 * Math.PI) * Math.pow(Math.E, -x * x));
    }

    public Individual[] nextGen(Individual[] B_prime) {
        Individual[] nextGen = new Individual[p + lambda];
        for (int i = 0; i < p; i++) {
            nextGen[i] = B[i];
        }
        for (int i = p; i < lambda; i++) {
            nextGen[i] = B_prime[i];
        }
        return nextGen;
    }
}
