import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameOfLife3D {
    public enum GameModes {
        L2333,
        L5766,
        L4555,
    }

    private boolean[][][] space;
    private final int size;
    private final GameModes mode;
    int timestep;
    List<Double> maxDistanceList;

    public GameOfLife3D(int size, GameModes mode) {
        this.timestep = 0;
        this.size = size;
        this.mode = mode;
        this.space = new boolean[size][size][size];
        this.maxDistanceList = new ArrayList<>();
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
        this.timestep++;
        boolean[][][] newSpace = new boolean[this.size][this.size][this.size];
        double maxDistance = -1;

        for (int x = 0; x < this.size; x++) {
            for (int y = 0; y < this.size; y++) {
                for (int z = 0; z < this.size; z++) {
                    int aliveNeighbors = getAliveNeighbors(x, y, z);
                    boolean newState = getNewState(this.space[x][y][z], aliveNeighbors);
                    newSpace[x][y][z] = newState;

                    if(newState)
                        maxDistance = Math.max(maxDistance, getDistanceToCenter(new Position(x, y, z)));
                }
            }
        }

        this.space = newSpace;
        this.maxDistanceList.add(maxDistance);
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
        switch (this.mode) {
            case L5766:
                return cellIsAlive ? (aliveNeighbors >= 5 && aliveNeighbors <= 7) : (aliveNeighbors == 6);
            case L4555:
                return cellIsAlive ? (aliveNeighbors == 4 || aliveNeighbors == 5) : (aliveNeighbors == 5);
            default:
        }
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

    public void dumpToFile(String fileName) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("");

        List<Position> alivePositions = new ArrayList<>();
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                for(int k = 0; k < this.size; k++){
                    if(this.space[i][j][k])
                        alivePositions.add(new Position(i, j, k));
                }
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

    public void writeDistanceFile(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write("");

        writer.append("Timestep\tMax Distance\n");

        for (int t = 0; t < maxDistanceList.size(); t++)
            writer.append(t + "\t" + maxDistanceList.get(t) + "\n");

        writer.close();
    }

    private ColorRGB getColor(Position position){
        Position center  = getCenter();
        float maxDistance = 1.5F * this.size;
        double distance = position.distanceTo(center);

        float r = 0.2F;
        float g = (float) (0F + distance/maxDistance);
        float b = (float) (0F + distance/maxDistance);

        return new ColorRGB(r, g, b);
    }

    private Position getCenter() {
        return new Position(this.size/2, this.size/2, this.size/2);
    }

    private double getDistanceToCenter(Position position){
        return getCenter().distanceTo(position);
    }
}
