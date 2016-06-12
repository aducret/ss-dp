package main;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import parser.InformationParser;
import simulation.SimulationListener;
import simulation.SocialForceSimulation;
import writer.OvitoFileInputGenerator;
import model.SimulationData;

public class Main {
	
	private static int L = 20;
	private static double D = 1.2;
	private static int R = 5;
	private static int N = 200;

	private static double KN = 1.2 * Math.pow(10, 5);
	private static double KT =  2 * KN;

	private static double A = 2000;
	private static double B = 0.08;
	private static double T = -0.5;
	
	private static double DT = 0.01;
	private static double DT_FRAME = 1;
	
	private static double[] desiredVelocities = {
		0.8, 1.0, 1.2, 1.4, 1.6, 1.8, 
		2, 2.2, 2.4, 2.6, 2.8,
		3, 3.2, 3.4, 3.6, 3.8,
		4, 4.2, 4.4, 4.6, 4.8,
		5, 5.2, 5.4, 5.6, 5.8, 6
	};
		
	private static String DYNAMIC_FILE_PATH = "doc/examples/Dynamic_L" + L + "-R" + R + "-N" + N + ".txt";
	private static String STATIC_FILE_PATH = "doc/examples/Static_L" + L + "-R" + R + "-N" + N + ".txt";
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		SimulationData simulationData = InformationParser.readSimulationData(DYNAMIC_FILE_PATH, STATIC_FILE_PATH);
		OvitoFileInputGenerator ovitoWriter = new OvitoFileInputGenerator("doc/examples/result.txt");
		
		SocialForceSimulation simulation = new SocialForceSimulation(L, R, D, KN, KT, A, B, T, 1.5);
		simulation.simulate(DT, DT_FRAME, simulationData, new SimulationListener() {
			
			@Override
			public void onFrameAvailable(double time, SimulationData frame) {
				ovitoWriter.printSimulationFrame(frame);
			}
		});
		
		ovitoWriter.endSimulation();
	}
	
	public static int getOptimalValidM(SimulationData simulationData) {
		double L = simulationData.getL() + 1;
		double r = simulationData.getInteractionRadius();
		int M = (int) Math.floor(L / (r + 1));
		return M;
	}
	
}
