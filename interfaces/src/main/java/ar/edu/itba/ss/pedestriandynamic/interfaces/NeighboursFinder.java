package ar.edu.itba.ss.pedestriandynamic.interfaces;

import ar.edu.itba.ss.pedestriandynamic.models.Particle;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface NeighboursFinder {

  /**
   * For each particle of the given set, this method gets the ones that are colliding with other particles
   * of the collection.
   * <p>
   * Collisions conditions depends on implementations.
   *
   * @param particles collection containing the particles for the algorithm
   * @return a map containing as key each of the particles of the set, and a list of the particles with the ones
   * each point collides
   */
  Map<Particle,Collection<Particle>> run(Collection<Particle> particles);
}
