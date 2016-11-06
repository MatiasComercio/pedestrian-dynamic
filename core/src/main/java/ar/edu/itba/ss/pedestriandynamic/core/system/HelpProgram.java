package ar.edu.itba.ss.pedestriandynamic.core.system;

import ar.edu.itba.ss.pedestriandynamic.interfaces.MainProgram;

public class HelpProgram implements MainProgram {
  private static final String HELP_TEXT =
          "Granular Media Simulator.\n" +
                  "Arguments: \n" +
                  "* gen static <n_particles> <width> <length> <diameter_opening> <min_diameter> <max_diameter> <mass> <kn> <kt> <A> <B> <tau> <driving_speed> : \n" +
                  "     generates an output/static.dat file with the desired parameters.\n" +
                  "* gen dynamic <path/to/static.dat> : \n" +
                  "     generates an output/dynamic.dat file with the information specified at the " +
                  "given static.dat file.\n" +
                  "* sim <path/to/static.dat> <path/to/dynamic.dat> <simulationTime> <dt> <dt2> <print_ovito> <dated_file>\n" +
                  "     runs the pedestrian-dynamic simulation and saves snapshots of the system in output/ovito.xyz.\n" +
                  "     - <simulationTime>: total time to simulate.\n" +
                  "     - <dt>: time step of the simulation.\n" +
                  "     - <dt2>: time step to save snapshots of the system.\n" +
                  "     - <print_ovito>: true if ovito output is desired; false otherwise.\n" +
                  "     - <dated_file>: true if output should be saved in a folder with date appended; false otherwise.\n";

  @Override
  public void run(final String[] args) {
    System.out.println(HELP_TEXT);
  }
}
