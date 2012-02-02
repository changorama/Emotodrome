package emotodrome.mesh;

public class Physics
{
	public static final float DECAY = 0.9f;
	
	/**
	 * Returns a Vec3 containing the force applied by the spring's
	 * spring from the end to or away from the anchor.
	 */
	public static Vec3 anchoredSpringForce(AnchoredSpring spring)
	{
		return spring.end.sub(spring.anchor).normalize().scale(spring.equilibrium).add(spring.anchor).sub(spring.end).scale(spring.k);
	}
	
	/**
	 * AnchoredRubberBand's answer to anchoredSpringForce().
	 */
	public static Vec3 anchoredRubberBandForce(AnchoredRubberBand rb)
	{
		if(rb.anchor.distance(rb.end) <= rb.equilibrium)
			return new Vec3(0, 0, 0);
		else
			return rb.end.sub(rb.anchor).normalize().scale(rb.equilibrium).add(rb.anchor).sub(rb.end).scale(rb.k);
	}
	
	/**
	 * Returns the position of the physics point after t time has 
	 * passed.  Does not consider any acceleration.  THIS SHOULD
	 * BE THE ONLY METHOD USED TO EFFECT VELOCITY-BASED MOVEMENT
	 * IN PHYSICS POINTS.
	 */
	public static void translatePhysicsPoint(PhysicsPoint p, float t)
	{
		p.velocity.setToScale(DECAY);
		p.pos.setToAdd(p.velocity.scale(t));
	}
	
	/**
	 * This accepts a force vector and a physics point and 
	 * modifies the stored velocity of the physics point to reflect
	 * the acceleration.  Force is assumed to be instantaneous.  
	 * THIS METHOD DOES NOT APPLY ANY MOTION.
	 */
	public static void acceleratePoint(PhysicsPoint p, Vec3 force)
	{
		// Pretty straightforward at the moment.
		p.velocity.setToAdd(force.scale(1 / p.mass));
	}
	
	/**
	 * Simulates the effect of a rope tie - a non-elastic omni-
	 * directional distance limiter.
	 * 
	 * @param t: the relevant RopeTie object
	 * @return the new position for the end of the RopeTie.
	 */
	public static Vec3 ropePull(RopeTie t)
	{
		if(t.anchor.distance(t.end) > t.length)
			return t.end.sub(t.anchor).normalize().scale(t.length).add(t.anchor);
		return t.end;
	}
	
	/**
	 * Returns where the end of the bone should be to comply
	 * with the bone's length.
	 */
	public static Vec3 bonePull(AnchoredBone b)
	{
		return b.end.sub(b.anchor).normalize().scale(b.length).add(b.anchor);
	}
	
	/**
	 * An spring anchored at one point that applies force to another point.
	 * Does not assume any directionality on the spring - in other words, 
	 * the simulated spring may pivot freely on the anchor.
	 */
	public static class AnchoredSpring
	{
		public Vec3 anchor, end;
		public float equilibrium, k;
	}
	
	/**
	 * This is basically an AnchoredSpring that doesn't push if the points 
	 * get too close together.  This will prevent weird jiggling effects.
	 */
	public static class AnchoredRubberBand
	{
		public Vec3 anchor, end;
		public float equilibrium, k;
	}
	
	public static class Spring extends AnchoredSpring
	{
		/*
		 * Shell class.
		 */
	}
	
	/**
	 * A class to represent a reactive (not self-motivated)
	 * physics point.
	 */
	public static class PhysicsPoint
	{
		/**
		 * Convention: velocity will be stored as a Vec3, with direction indicated
		 * by the normalization of the vector and magnitude, in DISTUNITS per
		 * TIMEUNITS, will be represented by the length of the vector.
		 */
		public Vec3 pos, velocity;
		public float mass;
		
		public PhysicsPoint(Vec3 position, Vec3 vel, float m)
		{
			pos = new Vec3();
			if(position != null)
				pos.set(position);
			velocity = new Vec3();
			if(vel != null)
				velocity.set(vel);
			mass = m;
		}
	}
	
	/**
	 * A class to represent a rope, which can be used to prevent a physics point
	 * from being farther than a particular distance from another physics point.
	 * Not, at the moment, built to utilize velocity.
	 */
	public static class RopeTie
	{
		public Vec3 anchor, end;
		public float length;
	}
	
	/**
	 * A class to represent a rigid bond from one physics point to another.
	 * Essentially identical to a RopeTie, this is instead used to keep a given
	 * physics point ON a particular sphere of distance, as opposed to WITHIN a
	 * particular sphere of distance.
	 */
	public static class AnchoredBone
	{
		public Vec3 anchor, end;
		public float length;
	}
}
