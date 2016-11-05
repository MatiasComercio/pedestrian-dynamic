package ar.edu.itba.ss.pedestriandynamic.core.system.integration;

import ar.edu.itba.ss.pedestriandynamic.interfaces.NeighboursFinder;
import ar.edu.itba.ss.pedestriandynamic.models.*;
import ar.edu.itba.ss.pedestriandynamic.services.IOService;
import ar.edu.itba.ss.pedestriandynamic.services.apis.Space2DMaths;
import ar.edu.itba.ss.pedestriandynamic.services.gear.Gear5SystemData;
import ar.edu.itba.ss.pedestriandynamic.services.neighboursfinders.BruteForceMethodImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Gear5PedestrianDynamicsSystemData extends Gear5SystemData {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(Gear5PedestrianDynamicsSystemData.class);

  private static final double RC = 0;
  private static final boolean PERIODIC_LIMIT = false;

  private static final int NORMAL = 0;
  private static final int TANGENTIAL = 1;

  private static final int VELOCITY_DERIVED_ORDER = 1;

  private static final double ZERO = 0;

  private final StaticData staticData;
  private final Collection<Wall> walls;
  private final NeighboursFinder neighboursFinder;
  private final double fallLength;

  private Map<Particle, Collection<Particle>> currentNeighbours;
  private double kineticEnergy;
  private long nParticlesFlowed;
  private long nParticlesJustFlowed;

  /* package-private */ Gear5PedestrianDynamicsSystemData(final Collection<Particle> particles,
                                                          final Collection<Wall> walls,
                                                          final StaticData staticData) {
    super(particles);
    this.staticData = staticData;

    this.walls = Collections.unmodifiableCollection(walls);
    this.currentNeighbours = new HashMap<>(); // initialize so as not to be null
    this.fallLength = staticData.fallLength();

    this.neighboursFinder = new BruteForceMethodImpl(PERIODIC_LIMIT, RC);

    // update particles to meet the force of a Social Force Model pedestrian dynamics system
    initWithSystemConditions(particles);
  }

  public Collection<Wall> walls() {
    return walls;
  }

  public double kineticEnergy() {
    return kineticEnergy;
  }


  @Override
  protected Map<Integer, Vector2D> setInitialDerivativeValues(final Particle particle) {
    // it is considered that there is no interaction between any pair of particles, and
    final Map<Integer, Vector2D> initialDerivativeValues = new HashMap<>(sVectors());

    final Vector2D r0 = particle.r0();
    final Vector2D r1 = particle.r1();
    final Vector2D r2 = particle.r2();
    final Vector2D r3 = Space2DMaths.nullVector();
    final Vector2D r4 = Space2DMaths.nullVector();
    final Vector2D r5 = Space2DMaths.nullVector();

    initialDerivativeValues.put(0, r0);
    initialDerivativeValues.put(1, r1);
    initialDerivativeValues.put(2, r2);
    initialDerivativeValues.put(3, r3);
    initialDerivativeValues.put(4, r4);
    initialDerivativeValues.put(5, r5);

    return initialDerivativeValues;
  }

  @Override
  protected Vector2D getForceWithPredicted(final Particle particle) {
    particle.normalForce(0); // reset forces for this iteration

    // granular force
    final Vector2D totalGranularForce = totalGranularForce(particle);
    // social force
    final Vector2D totalSocialForce = totalSocialForce(particle);
    // driving force
    final Vector2D totalDrivingForce = totalDrivingForce(particle);

    return totalGranularForce.add(totalSocialForce).add(totalDrivingForce);
  }

  @Override
  protected void prePredict() {
    // reset kinetic energy
    kineticEnergy = 0;

    // reset maxPressure
    Particle.setMaxPressure(0);

    // reset nParticlesJustFlowed
    nParticlesJustFlowed = 0;
  }

  @Override
  protected void predicted(final Particle predictedParticle) {
    // it is assumed that if the predicted particle.y() is < ZERO => the particle will be out soon =>
    // => we remove that particle before evaluation for simplification on neighbours finder method usage
    removeIfOut(predictedParticle);

    super.predicted(predictedParticle);
  }

  @Override
  protected void preEvaluate() {
    // calculate neighbours with the system's particles updated with the predicted values
    this.currentNeighbours = neighboursFinder.run(predictedParticles());
    super.preEvaluate();
  }

  public long nParticlesFlowed() {
    return nParticlesFlowed;
  }

  public long nParticlesJustFlowed() {
    return nParticlesJustFlowed;
  }

  @Override
  public void fixed(final Particle particle) {
    if (flowedOut(particle)) {
      nParticlesJustFlowed ++;
      nParticlesFlowed ++;
    }
    if (!removeIfOut(particle)) {
      kineticEnergy += particle.kineticEnergy();
    }

    super.fixed(particle);
  }

  /**
   *
   * @param particle -
   * @return true if removed; false otherwise
   */
  private boolean removeIfOut(final Particle particle) {
    if(particle.y() < ZERO){
      removeWhenFinish(particle);
      return true;
    }
    return false;
  }

  private void initWithSystemConditions(final Collection<Particle> particles) {
    final Collection<Particle> updatedParticles = new HashSet<>();
    particles.forEach(particle -> {
      final Particle updatedParticle =
              particleWithInitialForce(particle).withTau(staticData.tau()).withDrivingSpeed(staticData.drivingSpeed());
      updatedParticles.add(updatedParticle);
      initParticle(updatedParticle);
    });

    // update system data particles
    super.particles(updatedParticles);
  }

  private Particle particleWithInitialForce(final Particle particle) {
    // calculate social force and driving force
    final Vector2D totalSocialForce = totalSocialForce(particle);
    final Vector2D totalDrivingForce = totalDrivingForce(particle);

    final Vector2D totalInitialForce = totalSocialForce.add(totalDrivingForce);
    return particle.withForceX(totalInitialForce.x()).withForceY(totalInitialForce.y());
  }

  private Vector2D totalGranularForce(final Particle particle) {
    // granular (particles) force
    // neighbours are supposed to be correctly updated
    final Vector2D totalParticlesForce = totalParticlesForce(particle, currentNeighbours.get(particle));
    // granular (walls) force
    final Vector2D totalWallsForce = totalWallsForce(particle);
    return totalParticlesForce.add(totalWallsForce);
  }

  private Vector2D totalDrivingForce(final Particle particle) {
    final double drivingForceModule = particle.mass() / particle.tau();
    final Vector2D[] normalAndTangentialVersors =
            Space2DMaths.normalAndTangentialVersors(particle.r0(), particle.currentTarget()); // +++ xtodo

    if (normalAndTangentialVersors == null) {
      // both particles are at the exactly same position => something is wrong...
      // abort program
      IOService.exit(IOService.ExitStatus.PARTICLES_AT_SAME_POSITION,
              new Object[] {particle, particle.currentTarget()}); // +++ xtodo

      // should not reach here; written so as validators don't complain about possible null's access
      return Space2DMaths.nullVector();
    }
    final Vector2D normalVersor = normalAndTangentialVersors[NORMAL];

    final Vector2D drivingVelocity = normalVersor.times(particle.drivingSpeed());

    return drivingVelocity.sub(particle.r1()).times(drivingForceModule);
  }

  private Vector2D totalSocialForce(final Particle particle) {
    Vector2D totalSocialForce = Space2DMaths.nullVector();
    for (final Particle systemParticle : particles()) {
      if (!systemParticle.equals(particle)) {
        totalSocialForce = totalSocialForce.add(socialForce(particle, systemParticle));
      }
    }
    return totalSocialForce;
  }

  private Vector2DAbs socialForce(final Particle particle, final Particle neighbour) {
    final Vector2D[] normalAndTangentialVersors
            = Space2DMaths.normalAndTangentialVersors(particle.r0(), neighbour.r0());

    if (normalAndTangentialVersors == null) {
      // both particles are at the exactly same position => something is wrong...
      // abort program
      IOService.exit(IOService.ExitStatus.PARTICLES_AT_SAME_POSITION, new Object[] {particle, neighbour});

      // should not reach here; written so as validators don't complain about possible null's access
      return Space2DMaths.nullVector();
    }

    final Vector2D normalVersor = normalAndTangentialVersors[NORMAL];

    // border-to-border distance
    final double distanceBetween = Space2DMaths.distanceBetween(particle, neighbour);
    final double socialForceModule = staticData.A() * Math.exp(-distanceBetween / staticData.B());

    return normalVersor.times(socialForceModule);
  }

  private boolean flowedOut(final Particle particle) {
    // professor told us to use only the particle's center point, not including its radio
    if (particle.y() < fallLength && !particle.hasFlowedOut()) {
      particle.hasFlowedOut(true);
      return true;
    }
    return false;
  }

  // Particle's total force
  private Vector2D totalParticlesForce(final Particle particle, final Collection<Particle> neighbours) {
    Vector2D totalParticlesForce = Space2DMaths.nullVector();
    if (neighbours != null) {
      for (final Particle neighbour : neighbours) {
        final Vector2D neighbourForce = neighbourForce(particle, neighbour);
        totalParticlesForce = totalParticlesForce.add(neighbourForce);
      }
    }
    return totalParticlesForce;
  }

  private Vector2D neighbourForce(final Particle particle, final Particle neighbour) {
    final double superposition = Space2DMaths.superpositionBetween(particle, neighbour);
    if (superposition < 0) {
      return Space2DMaths.nullVector();
    }

    final Vector2D[] normalAndTangentialVersors =
            Space2DMaths.normalAndTangentialVersors(particle.r0(), neighbour.r0());

    if (normalAndTangentialVersors == null) {
      // both particles are at the exactly same position => something is wrong...
      // Abort program
      IOService.exit(IOService.ExitStatus.PARTICLES_AT_SAME_POSITION, new Object[] {particle, neighbour});

      // should not reach here; written so as validators don't complain about possible null's access
      return Space2DMaths.nullVector();
    }

    final Vector2D normalVersor = normalAndTangentialVersors[NORMAL];
    final Vector2D tangentialVersor = normalAndTangentialVersors[TANGENTIAL];

    final Vector2D particlePredictedVelocity = getPredictedR(particle, VELOCITY_DERIVED_ORDER);
    final Vector2D neighbourPredictedVelocity = getPredictedR(neighbour, VELOCITY_DERIVED_ORDER);
    final Vector2D relativeVelocity = Space2DMaths.relativeVector(neighbourPredictedVelocity, particlePredictedVelocity);

    final Vector2D normalNeighbourForce = normalForce(superposition, normalVersor);
    final Vector2D tangentialNeighbourForce = tangentialForce(superposition, relativeVelocity, tangentialVersor);

    particle.increaseNormalForce(normalNeighbourForce.norm2());

    return normalNeighbourForce.add(tangentialNeighbourForce);
  }

  // Walls total force
  private Vector2D totalWallsForce(final Particle particle) {
    Vector2D totalWallsForce = Space2DMaths.nullVector();
    for (final Wall wall : walls) {
      final Vector2D wallForce = wallForce(particle, wall);
      totalWallsForce = totalWallsForce.add(wallForce);
    }

    return totalWallsForce;
  }

  private Vector2D wallForce(final Particle particle, final Wall wall) {
    final double superposition = Space2DMaths.superpositionBetween(particle, wall);
    if (superposition <= 0) { // not colliding => no force
      return Space2DMaths.nullVector();
    }

    final Vector2D[] normalAndTangentialVersors =
            Space2DMaths.normalAndTangentialVersors(particle, wall);

    if (normalAndTangentialVersors == null) {
      // both particles are at the exactly same position => something is wrong...
      // Abort program
      IOService.exit(IOService.ExitStatus.PARTICLES_AT_SAME_POSITION, new Object[] {particle, wall});

      // should not reach here; written so as validators don't complain about possible null's access
      return Space2DMaths.nullVector();
    }

    final Vector2D normalVersor = normalAndTangentialVersors[NORMAL];
    final Vector2D tangentialVersor = normalAndTangentialVersors[TANGENTIAL];

    final Vector2D relativeVelocity = getPredictedR(particle, VELOCITY_DERIVED_ORDER);

    final Vector2D normalForce = normalForce(superposition, normalVersor);
    final Vector2D tangentialForce = tangentialForce(superposition, relativeVelocity, tangentialVersor);

    particle.increaseNormalForce(normalForce.norm2()); // increase normal force

    return normalForce.add(tangentialForce);
  }

  // system's force calculation for both normal and tangential components

  private Vector2D normalForce(final double superposition, final Vector2D normalVersor) {
    final double normalNeighbourForceModule = - staticData.kn() * superposition;
    return normalVersor.times(normalNeighbourForceModule);
  }

  private Vector2D tangentialForce(final double superposition,
                                   final Vector2D relativeVelocity,
                                   final Vector2D tangentialVersor) {
    final double tangentialRelativeVelocity = Space2DMaths.dotProduct(relativeVelocity, tangentialVersor);
    final double tangentialNeighbourForceModule = - staticData.kt() * superposition *tangentialRelativeVelocity;

    return tangentialVersor.times(tangentialNeighbourForceModule);
  }
}
