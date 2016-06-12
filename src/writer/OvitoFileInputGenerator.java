package writer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import model.Particle;
import model.SimulationData;

public class OvitoFileInputGenerator {
	private static final String BLUE = "0 0 255";

	private PrintWriter writer;

	public OvitoFileInputGenerator(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		this.writer = new PrintWriter(filePath, "UTF-8");
	}

	public void printSimulationFrame(SimulationData simulationData) {
		printHeaders(simulationData.getParticlesAmount());
		for (Particle particle : simulationData.getParticles()) {
			writer.println(generateLine(particle));
		}
		printBoundariesParticles(simulationData.getWidth(), simulationData.getHeight(), simulationData.getParticlesAmount());
	}

	public void endSimulation() {
		writer.close();
	}

	private void printBoundariesParticles(double width, double height, int particleAmount) {
		printBoundaryParticle(particleAmount + 1, 0, 0);
		printBoundaryParticle(particleAmount + 2, width, 0);
		printBoundaryParticle(particleAmount + 3, 0, height);
		printBoundaryParticle(particleAmount + 4, width, height);
	}

	private void printBoundaryParticle(int id, double x, double y) {
		writer.println(id + " " + x + " " + y + " 0 0 " + BLUE + " 0 " + BLUE);
	}

	private void printHeaders(int particlesAmount) {
		writer.println(particlesAmount + 4);
		writer.println("ID x y Vx Vy Cr Cg Cb r");
	}

	private String generateLine(Particle particle) {
		StringBuilder line = new StringBuilder();
		String particleColor = generateParticleColor(particle);
		line.append(particle.getId()).append(" ").append(particle.getPosition().getX()).append(" ")
				.append(particle.getPosition().getY()).append(" ").append(particle.getVelocity().getX()).append(" ")
				.append(particle.getVelocity().getY()).append(" ").append(particleColor).append(" ")
				.append(particle.getRadius());
		return line.toString();
	}

	private String generateParticleColor(Particle particle) {
		double speed = particle.getSpeed();
		double factor = 1 - Math.pow(Math.E, -0.2 * speed);
		double red = factor;
		double blue = 1 - factor;
		String color = red + " 0 " + blue;
		return color;
	}
}