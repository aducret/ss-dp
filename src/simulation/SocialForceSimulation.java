package simulation;

import java.util.List;

import main.Main;
import model.Particle;
import model.SimulationData;
import model.Vector2;

public class SocialForceSimulation {
	
	private int L;
	private int R;
	private double D;
	private double KN;
	private double KT; 
	private double A; 
	private double B; 
	private double T;
	private double desiredVelocity;
	private Vector2 desiredTarget = new Vector2(L, L / 2); 
	
	public SocialForceSimulation(int L, int R, double D, double KN, double KT, double A, double B, double T, double desiredVelocity) {
		this.L = L;
		this.R = R;
		this.D = D;
		this.KN = KN;
		this.KT = KT;
		this.A = A;
		this.B = B;
		this.T = T;
		this.desiredVelocity = desiredVelocity;
	}
	
	public void simulate(double dt, double dtFrame, SimulationData simulationData, SimulationListener simulationListener) {
		List<Particle> particles = simulationData.getParticles();
		
		double currentTime = 0;
		double timeToNextFrame = dtFrame;
		while(particles.size() > 0) {
			System.out.println("Particles left: " + particles.size());
			new CellIndexMethodSimulation(simulationData, Main.getOptimalValidM(simulationData), false);
			moveSystemForward(simulationData, dt);
			killOutsiders();
			
			if (timeToNextFrame < 0) {
				timeToNextFrame = dt;
				if (simulationListener != null) {
					simulationListener.onFrameAvailable(currentTime, simulationData);
				}
			}
			currentTime += dt;
			timeToNextFrame -= dt;
		}
	}
	
	private void moveSystemForward(SimulationData simulationData, double dt) {
		
		for (Particle particle: simulationData.getParticles()) {
			Vector2 resultantForce = calculateForce(particle);
			Vector2 acceleration = resultantForce.scale(1/particle.getMass());
			Vector2 position = particle.getPosition().sum(particle.getVelocity().scale(dt)).sum(acceleration.scale(2/3.0).scale(Math.pow(dt, 2))).substract(particle.getAcceleration().scale(1/6.0).scale(Math.pow(dt, 2)));
			Vector2 velocity = particle.getVelocity().sum(acceleration.scale(dt).scale(5/6.0)).substract(particle.getAcceleration().scale(dt).scale(1/6.0));
			
			particle.setPosition(position);
			particle.setVelocity(velocity);
		}
	}
	
	private Vector2 calculateForce(Particle particle) {
		
		// neighbors
		Vector2 totalForce = desireForce(particle);
		for(Particle neighbor: particle.getNeighbors()) {
			Vector2 versorN = neighbor.getPosition().substract(particle.getPosition()).normalize();
			Vector2 versorT = versorN.rotate(Math.PI / 2);
			double e = getE(particle, neighbor);
			
			if (particle.getPosition().getX() > L && neighbor.getPosition().getX() > L) {
				if (e > 0) {
					double vt = particle.getVelocity().substract(neighbor.getVelocity()).multiply(versorT);
					double fN = -KN * e;
					double fT = -KT * e * vt;
					
					Vector2 force = versorN.scale(fN).sum(versorT.scale(fT));
					totalForce = totalForce.sum(force);
				} else if (e < 0) {
					totalForce = totalForce.sum(socialForce(e, versorN));
				}
			}
		}
		
		// Walls
		totalForce = totalForce.sum(bottomVerticalWall(particle));
		totalForce = totalForce.sum(bottomHorizontalWall(particle));
		totalForce = totalForce.sum(topVerticalWall(particle));
		totalForce = totalForce.sum(topHorizontalWall(particle));
		
		return totalForce;
	}
	
	private Vector2 socialForce(double e, Vector2 versorN) {
		Vector2 socialForce = versorN.scale(A * Math.exp(e / B));
		return socialForce.scale(-1);
	}

	private Vector2 desireForce(Particle particle) {
		Vector2 desiredDirection = new Vector2(getDestinationX(particle), getDestinationY(particle));
		Vector2 desiredVersor = desiredDirection.substract(particle.getPosition()).normalize();
		Vector2 desiredForce = desiredVersor.scale(desiredVelocity).substract(particle.getVelocity()).scale(particle.getMass() / T);
		
		return desiredForce.scale(-1);
	}

	private double getDestinationX(Particle p) {
		double x = p.getPosition().getX();
		
		if (x <= desiredTarget.getX()) {
			return desiredTarget.getX();
		} else {
			return L + R + 1;
		}
	}
	
	private double getDestinationY(Particle p) {
		double y = p.getPosition().getY();
		double r = p.getRadius();
		
		if (y >= desiredTarget.getY() + D/2 - r || y <= desiredTarget.getY() - D/2 + r) {
			return desiredTarget.getY();
		} else {
			return y;
		}
	}
	
	private double getE(Particle particle, Particle neighbour) {
		return particle.getRadius() + neighbour.getRadius() - neighbour.getPosition().distanceTo(particle.getPosition());
}
	
	private void killOutsiders() {
		
	}
	
	private Vector2 bottomVerticalWall(Particle particle) {
		double px = particle.getPosition().getX();
		double py = particle.getPosition().getY();
		double r = particle.getRadius();
		
		if (px < r || px > L-r) {
			return new Vector2(0, 0);
		}
		
		if (py > r || py < 0) {
			return new Vector2(0, 0);
		}

		// Colision
		Vector2 versorT = new Vector2(0, 1);
		Vector2 versorN = new Vector2(1, 0);
		Vector2 wallPosition = new Vector2(L, 0);
		Particle wall = new Particle(0, wallPosition, particle.getMass());
		
		return getForceByWall(particle, wall, versorN, versorT);
	}
	
	private Vector2 topVerticalWall(Particle particle) {
		double px = particle.getPosition().getX();
		double py = particle.getPosition().getY();
		double r = particle.getRadius();
		
		if (px < r || px > L-r) {
			return new Vector2(0, 0);
		}
		
		if (py < L-r || py > L) {
			return new Vector2(0, 0);
		}
		
		// Colision
		Vector2 versorT = new Vector2(0, 1);
		Vector2 versorN = new Vector2(1, 0);
		Vector2 wallPosition = new Vector2(L, D + (L - D) / 2);
		Particle wall = new Particle(0, wallPosition, particle.getMass());
		
		return getForceByWall(particle, wall, versorN, versorT);
	}
	
	private Vector2 bottomHorizontalWall(Particle particle) {
		double px = particle.getPosition().getX();
		double py = particle.getPosition().getY();
		double r = particle.getRadius();
		
		if (px < L-r || px > L) {
			return new Vector2(0, 0);
		}
		
		if (py > L-r || py < r + D + (L - D) / 2) {
			return new Vector2(0, 0);
		}
		
		// Colision
		Vector2 versorT = new Vector2(-1, 0);
		Vector2 versorN = new Vector2(0, -1);
		Vector2 wallPosition = new Vector2(0, 0);
		Particle wall = new Particle(0, wallPosition, particle.getMass());
		
		return getForceByWall(particle, wall, versorN, versorT);
	}
	
	private Vector2 topHorizontalWall(Particle particle) {
		double px = particle.getPosition().getX();
		double py = particle.getPosition().getY();
		double r = particle.getRadius();
		
		if (px < L-r || px > L) {
			return new Vector2(0, 0);
		}
		
		if (py > r + (L - D) / 2 || py < r) {
			return new Vector2(0, 0);
		}
		
		// Colision
		Vector2 versorT = new Vector2(-1, 0);
		Vector2 versorN = new Vector2(0, 1);
		Vector2 wallPosition = new Vector2(0, L);
		Particle wall = new Particle(0, wallPosition, particle.getMass());
		
		return getForceByWall(particle, wall, versorN, versorT);
	}
	
	private Vector2 getForceByWall(Particle particle, Particle wall, Vector2 versorN, Vector2 versorT) {
		double e = particle.getRadius() - Math.abs(Math.abs(particle.getPosition().multiply(versorN))
				- Math.abs(wall.getPosition().multiply(versorN)));
		double vt = particle.getVelocity().substract(wall.getVelocity()).multiply(versorT);
		double fN = -KN * e;
		double fT = -KT * e * vt;
		
		return versorN.scale(fN).sum(versorT.scale(fT));
	}
	
	private void lastWall(Particle p) {
		double px = p.getPosition().getX();
		
		if (px > L + R) {
			return;
		}
	}
	
}
