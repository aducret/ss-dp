package model;

import java.util.ArrayList;
import java.util.List;


public class Particle {
	private int id;
	private Vector2 position;
	private Vector2 oldPosition; 
	private Vector2 velocity = new Vector2(0, 0);
	private Vector2 oldVelocity;
	private Vector2 resultantForce;
	private double mass;
	private List<Particle> neighbors;
	private double radius;
	private Vector2 acceleration = new Vector2(0, 0);
	public Vector2 getOldPosition() {
		return oldPosition;
	}

	public void setOldPosition(Vector2 oldPosition) {
		this.oldPosition = oldPosition;
	}

	public Vector2 getOldVelocity() {
		return oldVelocity;
	}

	public void setOldVelocity(Vector2 oldVelocity) {
		this.oldVelocity = oldVelocity;
	}

	public Vector2 getResultantForce() {
		return resultantForce;
	}

	public void setResultantForce(Vector2 resultantForce) {
		this.resultantForce = resultantForce;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public void setNeighbors(List<Particle> neighbors) {
		this.neighbors = neighbors;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	
	public Particle(int id, Vector2 position, double mass) {
		if (id < 0)
			throw new IllegalArgumentException("particle wrong");
		this.id = id;
		this.position = position;
		this.neighbors = new ArrayList<Particle>();
		this.mass = mass;
	}
	
	public double getKineticEnergy() {
		double speed = getVelocity().norm();
		return (getMass()*speed*speed)/2.0;
	}
	
	public double getMomentum(Particle sun) {
		return getSpeed() * getMass() * getPosition().distanceTo(sun.getPosition()); 
	}
	
	public double getSpeed() {
		return getVelocity().norm();
	}
	
	public int getId() {
		return id;
	}

	public Vector2 getPosition() {
		return position;
	}

	public double getRadius() {
		return radius;
	}

	public double getMass() {
		return mass;
	}

	public Vector2 getVelocity() {
		return velocity;
	}
	
	public double getAngle() {
		return Math.atan2(velocity.getY(), velocity.getX());
	}

	public List<Particle> getNeighbors() {
		return neighbors;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setX(double x) {
		this.position.setX(x);
	}

	public void setY(double y) {
		this.position.setY(y);
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public String toString() {
		return new StringBuilder()
				.append("------ PARTICLE ------\n")
				.append("id: " + id + "\n")
				.append("position: " + position + "\n")
				.append("velocity: " + velocity + "\n")
				.append("mass: " + mass + "\n")
				.append("radius: " + getRadius() + "\n")
				.append("----------------------\n")
				.toString();
	}
}
