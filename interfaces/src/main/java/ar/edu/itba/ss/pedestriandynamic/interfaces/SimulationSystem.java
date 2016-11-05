package ar.edu.itba.ss.pedestriandynamic.interfaces;

/* package-private */ interface SimulationSystem<E extends SystemData> {
  /**
   *
   * @return the being simulated system's data
   */
  E getSystemData();
}
