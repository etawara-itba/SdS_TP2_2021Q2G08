public class Particle {
    private boolean isAlive;
    private Position position;

    public Particle(int x, int y, int z, Boolean isAlive){
        this.isAlive = isAlive;
        this.position = new Position(x,y,z);
    }

    // GETTERS
    public Position getPosition() {
        return position;
    }
    public boolean isAlive(){
        return isAlive;
    }

    @Override
    public String toString() {
        StringBuilder aux = new StringBuilder();
        aux.append(" [").append(this.getPosition().getX()).append(",").append(this.getPosition().getY()).append(",").append(this.getPosition().getZ()).append(",").append(this.isAlive()).append("] ");
        return aux.toString();
    }
}
