import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.*;


public class App {

	private static final String OUTPUT_DIR = "output";
	private static final String LOGGING_DIR = "logging";

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


		// VALIDATORS
		if(gridSize <= 0){
			System.out.println("Invalid grid size argument! It must be greater than 0");
			return;
		} else if(!rule.equals("CLASSIC") && !rule.equals("HIGHLIFE") && !rule.equals("EVEN") && !rule.equals("L2333") && !rule.equals("L5766") && !rule.equals("L4555")){
			System.out.println("Invalid rule argument! The rule you introduce does not exist");
			return;
		} else if(initialArea <= 0){
			System.out.println("Invalid initial area argument! It must be greater than 0");
			return;
		} else if(particlesAlivePercentageRatio < 0 || particlesAlivePercentageRatio > 1){
			System.out.println("Invalid alive particles ratio argument! It must be between 0 and 1");
			return;
		} else if(maxIterations <= 0){
			System.out.println("Invalid max iterations argument! It must be greater than 0");
			return;
		}

		createDirectory(OUTPUT_DIR, true);

		if(gridDimension == 2){
			GameOfLife2D game = new GameOfLife2D(gridSize, GameModes.valueOf(rule));
			if(randomParticles){
				ParticlesGenerator particles2DGenerator = new ParticlesGenerator(gridSize, initialArea, particlesAlivePercentageRatio);
				List<Particle> particleListGenerated = particles2DGenerator.generator2D();
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

			String baseFilename = OUTPUT_DIR + "/dynamic_";
			String currentFilename;
			String fileSuffix = ".dump";
			for(int iteration = 0; iteration < maxIterations && !game.borderWithAliveParticle() && game.aliveParticles() > 0; iteration++){
				System.out.println("Iteration number: " + iteration);
				// game.printBoard();

				currentFilename = baseFilename + String.format("%05d", iteration) + fileSuffix;
				try{
					game.dumpToFile(currentFilename);
				}catch (IOException e){
					System.out.println("error writing to " + currentFilename);
				}

				game.nextRound(game);
			}

			createDirectory(LOGGING_DIR, false);
			String logFilename = LOGGING_DIR + "/log_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".log";
			try{
				game.writeDistanceFile(logFilename);
			}catch (IOException e){
				System.out.println("error writing to " + logFilename);
			}


		} else if(gridDimension == 3){
			GameOfLife3D game = new GameOfLife3D(gridSize, GameModes.valueOf(rule));
			if(randomParticles){
				ParticlesGenerator particles3DGenerator = new ParticlesGenerator(gridSize, initialArea, particlesAlivePercentageRatio);
				List<Particle> particleListGenerated = particles3DGenerator.generator3D();
				game.fillSpace(particleListGenerated);
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
				game.fillSpace(particleList);
			}

			String baseFilename = OUTPUT_DIR + "/dynamic_";
			String currentFilename;
			String fileSuffix = ".dump";
			for(int iteration = 0; iteration < maxIterations && !game.borderWithAliveParticle() && game.aliveParticles() > 0; iteration++){
				System.out.println("Iteration number: " + iteration);
				// game.printBoard();

				currentFilename = baseFilename + String.format("%05d", iteration) + fileSuffix;
				try{
					game.dumpToFile(currentFilename);
				}catch (IOException e){
					System.out.println("error writing to " + currentFilename);
				}

				game.nextRound();
			}

			createDirectory(LOGGING_DIR, false);
			String logFilename = LOGGING_DIR + "/log_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".log";
			try{
				game.writeDistanceFile(logFilename);
			}catch (IOException e){
				System.out.println("error writing to " + logFilename);
			}

		} else {
			System.out.println("Wrong gridDimension argument! This value must be 2 or 3.");
			return;
		}





		// System.out.println(particleList);

	}

	private static File createDirectory(String directoryName, boolean deleteIfExist) throws IOException {
		File dir = new File(String.valueOf(FileSystems.getDefault().getPath("./" + directoryName)));
		if (dir.exists()) {
			if(deleteIfExist){
				for (File file: dir.listFiles()) {
					if (!file.isDirectory())
						file.delete();
				}
			}
			return dir;
		}
		if (dir.mkdirs()) {
			return dir;
		}
		throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "' for an unknown reason.");
	}
}
