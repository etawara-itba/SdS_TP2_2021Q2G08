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

    public double distanceTo(Position p){
        return Math.sqrt(Math.pow(p.getX()-this.x, 2.0) + Math.pow(p.getY()-this.y, 2.0) + Math.pow(p.getZ()-this.z, 2.0));
    }

}
