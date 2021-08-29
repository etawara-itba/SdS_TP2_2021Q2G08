import java.util.ArrayList;
import java.util.List;

public class ParticlesGenerator2D {

    private int size;
    private int initialArea;
    private double ratio;

    public ParticlesGenerator2D(int M, int initialArea, double ratio){
        this.size = M;
        this.initialArea = initialArea;
        this.ratio = ratio;
    }

    public List<Particle> generator(){
        List<Particle> particleList = new ArrayList<>();
        int center = this.size / 2;
        for(int i = 0; i < this.size; i++){
            for(int j = 0; j < this.size; j++){
                if(i >= center - (initialArea / 2) && i <= center + (initialArea / 2)){
                    if(j >= center - (initialArea / 2) && j <= center + (initialArea / 2)){
                        if(Math.random() < ratio){
                            particleList.add(new Particle(i,j,0,true));
                        }
                    }
                }
            }
        }
        return particleList;
    }
}
