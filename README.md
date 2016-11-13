# Pedestrian Dynamics
TP6 for Systems Simulation course from ITBA.

Java Implementation of a pedestrian dynamics simulation
## Build
To build the project, it is necessary to have Maven and Java 1.8 installed.
Then, run

    $ mvn clean package

## Execution
To run the program, from the root folder

    $ java -jar core/target/pedestrian-dynamic.jar <arguments>

## Simulation
`help` argument is a highly detailed help menu that show possible usages of the current program.
So, we highly recommend that for using this jar, you may run

    $ java -jar core/target/pedestrian-dynamic.jar help

### Usage examples

Generate static data file

    $ java -jar core/target/pedestrian-dynamic.jar gen static 100 20 20 1.2 .5 .7 80 1.2e5 2.4e5 2e3 .08 .5 1.5

Generate dynamic data file

    $ java -jar core/target/pedestrian-dynamic.jar gen dynamic output/static.dat

Run granular system's simulation

    $ java -jar core/target/pedestrian-dynamic.jar sim output/static.dat output/dynamic.dat 100 1e-4 1e-2 true true false

## Provided scripts
For replaying a bunch of simulations to make statistics analysis, run the following from the root's directory

    $ ./tp6_simulations_200.sh

You can configure the parameters for the simulations from that file directly.
A `statistics` folder will be created, and all the necessary scripts to process data will be inside it.

All you have to do to process data and generate tables is run the following from the `statistics` folder

    $ ./robot.sh

To compile latex documents with graphics, just do the following from the `statistics` folder,
 after having called the `robot.sh` script

    $ ./compile_tex.sh

## Ovito color relative pressure
To obtain an ovito output with pressure colors relative to the maximum of the given simulation, just run from the project's root

    $ ./color_ovito.sh

After executing the above code, a new ovito file will be generated, with the name `new_ovito.xyz`.
This file will have the new colors appended to each original row as R G B.
