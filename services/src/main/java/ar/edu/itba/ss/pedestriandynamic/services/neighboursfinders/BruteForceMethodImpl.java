package ar.edu.itba.ss.pedestriandynamic.services.neighboursfinders;

import ar.edu.itba.ss.pedestriandynamic.interfaces.NeighboursFinder;
import ar.edu.itba.ss.pedestriandynamic.models.Particle;
import ar.edu.itba.ss.pedestriandynamic.services.apis.Space2DMaths;

import java.util.*;

public class BruteForceMethodImpl implements NeighboursFinder {

  private final boolean periodicLimit;
  private final double rc;

  public BruteForceMethodImpl(final boolean periodicLimit, final double rc) {
    this.periodicLimit = periodicLimit;
    this.rc = rc;
  }

  @Override
  public Map<Particle, Collection<Particle>> run(Collection<Particle> particles) {

    final List<Particle> pointsAsList = new ArrayList<>(particles);
    final Map<Particle, Collection<Particle>> collisionPerParticle = new HashMap<>(particles.size());

    particles.forEach(point -> {
      // add the point to the map to be returned, with a new empty set
      collisionPerParticle.put(point, new HashSet<>());
    });

    if (!periodicLimit) {
      calculateCollisions(collisionPerParticle, pointsAsList, rc);
    }

    return collisionPerParticle;
  }

  private void calculateCollisions(final Map<Particle, Collection<Particle>> collisionPerParticle,
                                   final List<Particle> pointsAsList, final double rc) {
    double distance;

    for (int i = 0; i < pointsAsList.size(); i++) {
      for (int j = i+1; j < pointsAsList.size(); j++) {
        distance = Space2DMaths.distanceBetween(pointsAsList.get(i),
                pointsAsList.get(j));
        if (distance <= rc) {
          collisionPerParticle.get(pointsAsList.get(i)).add(pointsAsList.get(j));
          collisionPerParticle.get(pointsAsList.get(j)).add(pointsAsList.get(i));
        }
      }
    }
  }
}
