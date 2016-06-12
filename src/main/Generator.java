package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import util.RandomUtils;
import model.Vector2;

public class Generator {
	
	private static double Radius = 0.25;
	private static double Mass = 75;
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		generateFile(20, 1.2, 5, 200, Radius, Mass);
	}
	
	public static void generateFile(int L, double D, int R, int N, double radius, double mass) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter dynamicWriter = new PrintWriter("doc/examples/Dynamic_L" + L + "-R" + R + "-N" + N + ".txt", "UTF-8");
		PrintWriter staticWriter = new PrintWriter("doc/examples/Static_L" + L + "-R" + R + "-N" + N + ".txt", "UTF-8");

		List<Vector2> positions = generatePositions(L, N, radius);

		staticWriter.println(positions.size());
		staticWriter.println((L + R) + " " + L);
		staticWriter.println(L + " " + D + " " + R);

		for (Vector2 position : positions) {
			String staticEntry = String.format(Locale.US, "%f %f", mass, radius);
			String dynamicEntry = String.format(Locale.US, "%f %f", position.getX(), position.getY());
			dynamicWriter.println(dynamicEntry);
			staticWriter.println(staticEntry);
		}

		staticWriter.close();
		dynamicWriter.close();
	}
	
	private static List<Vector2> generatePositions(double L, int N, double radius) {
		List<Vector2> positions = new ArrayList<>();
		
		int i = 0;
		while (i < N) {
			boolean validPosition = false;
			while (!validPosition) {
				double x = RandomUtils.randomBetween(radius, L - radius);
				double y = RandomUtils.randomBetween(radius, L - radius);
				Vector2 position = new Vector2(x, y);
				boolean found = false;
				for (Vector2 other : positions) {
					if (other.distanceTo(position) < radius * 2) {
						found = true;
						break;
					}
				}
				if (!found) {
					positions.add(position);
					validPosition = true;
					i++;
				}
			}
		}
		return positions;
	}
	
}
