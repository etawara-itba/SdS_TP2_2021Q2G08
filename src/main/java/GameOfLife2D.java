import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameOfLife2D {
    private boolean[][] grid;
    int size;
    int timestep;

    public GameOfLife2D(int M) {
        this.timestep = 0;
        this.size = M;
        this.grid = new boolean[M][M];
        for(int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                this.grid[i][j] = false;
            }
        }
    }

    public void fillGrid(List<Particle> particles) {
        for(int i = 0; i < particles.size(); i++) {
            Particle particle = particles.get(i);
            this.grid[particle.getPosition().getX()][particle.getPosition().getY()] = particle.isAlive();
        }
    }

    // GETTERS
    public boolean[][] getGrid(){
        return this.grid;
    }
    public int getSize(){
        return this.size;
    }


    //SETTERS
    public void setAlive(int x, int y){
        this.grid[x][y] = true;
    }
    public void setDead(int x, int y){
        this.grid[x][y] = false;
    }
    public void setState(int x, int y, boolean state){
        this.grid[x][y] = state;
    }

    public boolean isAlive(int x, int y){
        return this.grid[x][y];
    }

    public void nextRound(GameOfLife2D game){
        this.timestep++;
        for(int i = 0; i < game.getSize(); i++){
            for(int j = 0; j < game.getSize(); j++){
                int neighborsAlive = getNeighborsAlive(i,j);
                boolean oldState = this.grid[i][j];
                boolean newState = shouldBeAlive(oldState, neighborsAlive);
                setState(i,j,newState);
            }
        }
    }

    public int getNeighborsAlive(int x, int y){
        int neighborsAlive = 0;

        // TOP ROW
        if(x > 0){
            // left column
            if(y > 0 && isAlive(x-1, y-1)){
                neighborsAlive++;
            }
            // middle column
            if(isAlive(x-1, y)){
                neighborsAlive++;
            }
            // right column
            if(y+1 < this.getSize() && isAlive(x-1, y+1)){
                neighborsAlive++;
            }
        }

        // MIDDLE ROW
        if(y > 0 && isAlive(x, y-1)){
            neighborsAlive++;
        }
        if(y+1 < this.getSize() && isAlive(x, y+1)){
            neighborsAlive++;
        }

        // DOWN ROW
        if(x+1 < this.getSize()){
            // left column
            if(y > 0 && isAlive(x+1, y-1)){
                neighborsAlive++;
            }
            // middle column
            if(isAlive(x+1, y)){
                neighborsAlive++;
            }
            // right column
            if(y+1 < this.getSize() && isAlive(x+1, y+1)){
                neighborsAlive++;
            }
        }

        return neighborsAlive;

    }

    public boolean shouldBeAlive(boolean oldState, int neighborsAlive){
        if(oldState){
            if(neighborsAlive == 2 || neighborsAlive == 3){
                return true;
            } else{
                return false;
            }
        } else{
            if(neighborsAlive == 3){
                return true;
            } else{
                return false;
            }
        }
    }

    public boolean borderWithAliveParticle(){
        for(int i = 0; i < this.getSize(); i++){
            for(int j = 0; j < this.getSize(); j++){
                if(i == 0 || j == 0 || i == this.getSize() -1 || j == this.getSize() - 1){
                    if(this.grid[i][j]){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int aliveParticles(){
        int aliveParticles = 0;
        for(int i = 0; i < this.getSize(); i++){
            for(int j = 0; j < this.getSize(); j++){
                if(this.grid[i][j]){
                    aliveParticles++;
                }
            }
        }
        return aliveParticles;
    }

    public void printBoard() {
        for(int j = this.getSize() - 1; j >= 0; j--) {
            for(int i = 0; i < this.getSize(); i++) {
                if(this.grid[i][j])
                    System.out.print("+");
                else
                    System.out.print("-");
            }
            System.out.println();
        }
    }

    public void dumpToFile(String fileName) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("");

        List<Position> alivePositions = new ArrayList<>();
        for(int j = this.getSize() - 1; j >= 0; j--) {
            for(int i = 0; i < this.getSize(); i++) {
                if(this.grid[i][j])
                    alivePositions.add(new Position(i, j, 0));
            }
        }

        writer.append("ITEM: TIMESTEP\n");
        writer.append(this.timestep + "\n" );
        writer.append("ITEM: NUMBER OF ATOMS\n");
        writer.append(alivePositions.size() + "\n" );
        writer.append("ITEM: BOX BOUNDS\n");
        writer.append("0\t" + this.size + "\n" );
        writer.append("0\t" + this.size + "\n" );
        writer.append("0\t0\n" );
        writer.append("ITEM: ATOMS id type xs ys zs rc gc bc\n");

        String id, type, xs, ys, zs, rc, bc, gc;
        ColorRGB colorRGB;
        for(Position p : alivePositions){

            colorRGB = getColor(p);

            id = String.valueOf(p.getX() + this.size * p.getY());
            type = "1";
            xs = String.valueOf((float) p.getX() / this.size);
            ys = String.valueOf((float) p.getY() / this.size);
            zs = String.valueOf((float) p.getZ() / this.size);
            rc = String.format("%.3f", colorRGB.getR());
            gc = String.format("%.3f", colorRGB.getG());
            bc = String.format("%.3f", colorRGB.getB());

            writer.append(id + "\t" + type + "\t" + xs + "\t" + ys + "\t" + zs + "\t" + rc + "\t" + gc + "\t" + bc + "\n");
        }

        writer.close();
    }

    private ColorRGB getColor(Position position){
        Position center  = getCenter();
        float maxDistance = 1F * this.size;
        float distance = position.distanceTo(center);

        float r = 0.2F;
        float g = (0F + distance/maxDistance);
        float b = (0F + distance/maxDistance);

        return new ColorRGB(r, g, b);
    }

    private Position getCenter(){
        return new Position(this.size/2, this.size/2, 0);
    }

}
