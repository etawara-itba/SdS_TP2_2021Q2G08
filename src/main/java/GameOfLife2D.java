import java.util.List;

public class GameOfLife2D {
    private boolean[][] grid;
    int size;

    public GameOfLife2D(int M) {
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

}
