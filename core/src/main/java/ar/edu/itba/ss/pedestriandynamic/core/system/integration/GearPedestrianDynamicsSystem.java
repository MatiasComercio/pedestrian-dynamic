package ar.edu.itba.ss.pedestriandynamic.core.system.integration;

import ar.edu.itba.ss.pedestriandynamic.interfaces.NumericIntegrationMethod;
import ar.edu.itba.ss.pedestriandynamic.interfaces.TimeDrivenSimulationSystem;
import ar.edu.itba.ss.pedestriandynamic.models.Particle;
import ar.edu.itba.ss.pedestriandynamic.models.StaticData;
import ar.edu.itba.ss.pedestriandynamic.models.Wall;
import ar.edu.itba.ss.pedestriandynamic.services.gear.Gear5SystemData;
import ar.edu.itba.ss.pedestriandynamic.services.gear.GearPredictorCorrector;

import java.util.*;

public class GearPedestrianDynamicsSystem
        implements TimeDrivenSimulationSystem<Gear5PedestrianDynamicsSystemData> {

  private final NumericIntegrationMethod<Gear5SystemData> integrationMethod;
  private final Gear5PedestrianDynamicsSystemData systemData;

  public GearPedestrianDynamicsSystem(final Collection<Particle> systemParticles,
                                      final Collection<Wall> systemWalls, final StaticData staticData) {
    this.systemData = new Gear5PedestrianDynamicsSystemData(systemParticles, systemWalls, staticData);
    this.integrationMethod = new GearPredictorCorrector<>();
  }

  @Override
  public Gear5PedestrianDynamicsSystemData getSystemData() {
    return systemData;
  }

  @Override
  public void evolveSystem(final double dt) {
    integrationMethod.evolveSystem(systemData, dt);
  }
}
