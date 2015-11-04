public class Individual {
    private float rho;
    private float x[];
    private float fitness;
    private int rank;
    
    public Individual(float[] weights_in, float strategyParameter){
        x = weights_in;
        this.rho = rho;
    }
    
    public void set_x(float x[]){
        this.x = x;
    }
    
    public void set_s(float s){
        this.rho = s;
    }
    
    public void setFitness(float fitness){
        this.fitness = fitness;
    }
    
    public float[] get_x(){
        return x;
    }
    
    public float get_s(){
        return rho;
    }
    
    public float getFitness(){
        return fitness;
    }
    
    public void setRank(int i){
        rank = i;
    }
    
    public int getRank(){
        return rank;
    }
}
