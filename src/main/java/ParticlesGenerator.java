import java.util.ArrayList;
import java.util.List;

public class ParticlesGenerator {

    private final int size;
    private final int initialArea;
    private final double ratio;

    public ParticlesGenerator(int M, int initialArea, double ratio){
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

    public List<Particle> generator3D() {
        List<Particle> particleList = new ArrayList<>();

        int center = this.size / 2;
        int radius = this.initialArea / 2;

        for (int i = 0; i < this.size; i++) {
            if (i >= center - radius && i <= center + radius) {

                for (int j = 0; j < this.size; j++) {
                    if (j >= center - radius && j <= center + radius) {

                        for (int k = 0; k < this.size; k++) {
                            if (k >= center - radius && k <= center + radius) {
                                if (Math.random() < this.ratio)
                                    particleList.add(new Particle(i, j, k, true));
                            }
                        }
                    }
                }
            }
        }

        return particleList;
    }
}
