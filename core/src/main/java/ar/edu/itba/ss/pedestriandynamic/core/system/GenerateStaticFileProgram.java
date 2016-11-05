package ar.edu.itba.ss.pedestriandynamic.core.system;

import ar.edu.itba.ss.pedestriandynamic.core.helpers.OutputSerializerHelper;
import ar.edu.itba.ss.pedestriandynamic.interfaces.MainProgram;
import ar.edu.itba.ss.pedestriandynamic.models.StaticData;
import ar.edu.itba.ss.pedestriandynamic.services.IOService;

import java.nio.file.Path;

import static ar.edu.itba.ss.pedestriandynamic.services.IOService.ExitStatus.BAD_N_ARGUMENTS;

public class GenerateStaticFileProgram implements MainProgram {
  private static final String DEFAULT_OUTPUT_FOLDER = "output";
  private static final String DEFAULT_STATIC_FILE_NAME = "static";
  private static final String DEFAULT_DAT_FILE_EXTENSION = ".dat";

  // gen static args indexes
  private static final int I_N_PARTICLES = 2;
  private static final int I_WIDTH = 3;
  private static final int I_LENGTH = 4;
  private static final int I_DIAMETER_OPENING = 5;
  private static final int I_MIN_DIAMETER = 6;
  private static final int I_MAX_DIAMETER = 7;
  private static final int I_MASS = 8;
  private static final int I_KN = 9;
  private static final int I_KT = 10;
  private static final int I_A = 11;
  private static final int I_B = 12;
  private static final int I_TAU = 13;
  private static final int I_DESIRED_SPEED = 14;


  private static final int N_ARGS_EXPECTED = 15;

  @Override
  public void run(final String[] args) {
    System.out.println("Generating static file...");
    final StaticData staticData = loadStaticData(args);
    final String serializedStaticData = OutputSerializerHelper.staticOutput(staticData);
    final Path pathToStaticFile =
            IOService.createOutputFile(DEFAULT_OUTPUT_FOLDER, DEFAULT_STATIC_FILE_NAME, DEFAULT_DAT_FILE_EXTENSION);
    IOService.openOutputFile(pathToStaticFile, true);
    IOService.appendToFile(pathToStaticFile, serializedStaticData);
    IOService.closeOutputFile(pathToStaticFile);
    System.out.println("[DONE]");
  }

  private StaticData loadStaticData(final String[] args) {
    if (args.length < N_ARGS_EXPECTED) {
      IOService.exit(BAD_N_ARGUMENTS, null);
      // should never reach here
      throw new IllegalStateException();
    }

    final int nParticles = IOService.parseAsInt(args[I_N_PARTICLES], "<n_particles>");
    final double width = IOService.parseAsDouble(args[I_WIDTH], "<width>");
    final double length = IOService.parseAsDouble(args[I_LENGTH], "<length>");
    final double diameterOpening = IOService.parseAsDouble(args[I_DIAMETER_OPENING], "<diameter_opening>");
    final double minDiameter = IOService.parseAsDouble(args[I_MIN_DIAMETER], "<min_diameter>");
    final double maxDiameter = IOService.parseAsDouble(args[I_MAX_DIAMETER], "<max_diameter>");
    final double mass = IOService.parseAsDouble(args[I_MASS], "<mass>");
    final double kn = IOService.parseAsDouble(args[I_KN], "<kn>");
    final double kt = IOService.parseAsDouble(args[I_KT], "<kt>");
    final double A = IOService.parseAsDouble(args[I_A], "<A>");
    final double B = IOService.parseAsDouble(args[I_B], "<B>");
    final double tau = IOService.parseAsDouble(args[I_TAU], "<tau>");
    final double desiredSpeed = IOService.parseAsDouble(args[I_DESIRED_SPEED], "<desired_speed>");

    return StaticData.builder(nParticles, width, length, diameterOpening, minDiameter,
            maxDiameter, mass, kn, kt, A, B, tau, desiredSpeed).build();
  }
}
