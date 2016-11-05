package ar.edu.itba.ss.pedestriandynamic.interfaces;

public interface TimeDrivenSimulationSystem<E extends SystemData> extends SimulationSystem<E> {
  /**
   * Evolves the system a {@code dt} time interval.
   *
   * @param dt the time step used to evolve the system
   */
  void evolveSystem(double dt);
}
