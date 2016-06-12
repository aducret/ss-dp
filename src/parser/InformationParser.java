package parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

import model.Particle;
import model.SimulationData;
import model.Vector2;

public class InformationParser {
	
	public static SimulationData readSimulationData(String dynamicFilePath, String staticFilePath) throws FileNotFoundException {
		SimulationData.Builder builder = SimulationData.Builder.create();

		InputStream dynamicIS = new FileInputStream(dynamicFilePath);
		Scanner dynamicScanner = new Scanner(dynamicIS);
		dynamicScanner.useLocale(Locale.US);

		InputStream staticIS = new FileInputStream(staticFilePath);
		Scanner staticScanner = new Scanner(staticIS);
		staticScanner.useLocale(Locale.US);

		int N = staticScanner.nextInt();
		double width = staticScanner.nextDouble();
		double height = staticScanner.nextDouble();
		double L = staticScanner.nextDouble();
		double D = staticScanner.nextDouble();
		double R = staticScanner.nextDouble();
		builder = builder.withParticlesAmount(N)
				.withSpaceDimension(width, height)
				.withParameters(L, D, R);
		
		double radiusX = 0;
		for (int i = 1; i <= N; i++) {
			double mass = staticScanner.nextDouble();
			double radius = staticScanner.nextDouble();
			radiusX = radius;
			double x = dynamicScanner.nextDouble();
			double y = dynamicScanner.nextDouble();
			Particle particle = new Particle(i, new Vector2(x, y), mass);
			particle.setVelocity(new Vector2(0, 0));
			particle.setRadius(radius);
			builder = builder.withParticle(particle);
		}
		builder.withRadius(radiusX);
		dynamicScanner.close();
		staticScanner.close();

		return builder.withInteractionRadius(radiusX).build();
	}
	
}