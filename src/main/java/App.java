import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;


public class App {

	public static void main(String[] args) throws Exception {
		Object obj = new JSONParser().parse(new FileReader("config.json"));
		JSONObject jo = (JSONObject) obj;


		int gridSize = (int) (long) jo.get("gridSize");
		int gridDimension = (int) (long) jo.get("gridDimension");
		String rule = (String) jo.get("rule");
		boolean randomParticles = (boolean) jo.get("randomParticles");
		int initialArea = (int) (long) jo.get("initialArea");
		double particlesAlivePercentageRatio = (double) jo.get("particlesAlivePercentageRatio");
		long maxIterations = (long) jo.get("iterations");

		GameOfLife2D game = new GameOfLife2D(gridSize);
		if(randomParticles){
			ParticlesGenerator2D particles2DGenerator = new ParticlesGenerator2D(gridSize, initialArea, particlesAlivePercentageRatio);
			List<Particle> particleListGenerated = particles2DGenerator.generator();
			game.fillGrid(particleListGenerated);
		} else{
			JSONArray particlesArray = (JSONArray) jo.get("particles");
			List<Particle> particleList = new ArrayList<>();
			Iterator<JSONObject> iterator = particlesArray.iterator();
			while(iterator.hasNext()){
				JSONObject particleJSON = iterator.next();
				int x = (int) (long) particleJSON.get("x");
				int y = (int) (long) particleJSON.get("y");
				int z = (int) (long) particleJSON.get("z");
				Boolean isAlive = (boolean) particleJSON.get("isAlive");
				Particle particle = new Particle(x, y, z, isAlive);
				particleList.add(particle);
			}
			game.fillGrid(particleList);
		}


		for(int iteration = 0; iteration < maxIterations && !game.borderWithAliveParticle() && game.aliveParticles() > 0; iteration++){
			System.out.println("Iteration number: " + iteration);
			game.printBoard();
			game.nextRound(game);
		}

		// System.out.println(particleList);

	}
}
