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

    $ java -jar core/target/pedestrian-dynamic.jar sim output/static.dat output/dynamic.dat .5 1e-7 1e-4 true true