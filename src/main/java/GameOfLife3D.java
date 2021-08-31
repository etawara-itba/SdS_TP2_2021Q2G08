import java.util.List;

public class GameOfLife3D {
    private final boolean[][][] space;
    private final int size;

    public GameOfLife3D(int size) {
        this.size = size;
        this.space = new boolean[size][size][size];
    }

    private boolean isAlive(int x, int y, int z) {
        if (x < 0 || x > this.size)
            return false;
        if (y < 0 || y > this.size)
            return false;
        if (z < 0 || z > this.size)
            return false;
        return this.space[x][y][z];
    }

    public void fillSpace(List<Particle> particles) {
        for (Particle p: particles) {
            Position pos = p.getPosition();
            this.space[pos.getX()][pos.getY()][pos.getZ()] = p.isAlive();
        }
    }

    public void nextRound() {
        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                for (int z = 0; z < this.size; z++) {
                    int aliveNeighbors = getAliveNeighbors(x, y, z);
                    boolean newState = getNewState(this.space[x][y][z], aliveNeighbors);
                    this.space[x][y][z] = newState;
                }
            }
        }
    }

    private int getAliveNeighbors(int x, int y, int z) {
        int liveCells = 0;

        for (int i = -1; i <= 1; i++) {
            int newZ = z + i;
            for (int j = -1; j <= 1; j++) {
                int newY = y + j;
                for (int k = -1; k <= 1; k++) {
                    int newX = x + k;
                    if (newZ == z && newY == y && newX == x)
                        continue;
                    if (isAlive(newX, newY, newZ))
                        liveCells++;
                }
            }
        }

        return liveCells;
    }

    private boolean getNewState(boolean cellIsAlive, int aliveNeighbors) {
        return cellIsAlive ? (aliveNeighbors == 2 || aliveNeighbors == 3) : (aliveNeighbors == 3);
    }

    public int aliveParticles() {
        int aliveParticles = 0;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                for (int k = 0; k < this.size; k++) {
                    if (this.space[i][j][k])
                        aliveParticles++;
                }
            }

        }
        return  aliveParticles;
    }
}
