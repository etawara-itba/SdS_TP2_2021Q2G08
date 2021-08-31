public class Position {
    private int x;
    private int y;
    private int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
     // GETTERS
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getZ(){
        return this.z;
    }

    public float distanceTo(Position p){
        return Math.abs(this.x - p.getX()) + Math.abs(this.y - p.getY()) + Math.abs(this.z - p.getZ());
    }

}
