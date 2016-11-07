#!/bin/bash

N=200
W=20
L=20
D=1.2
MIN_DIAMETER=.5
MAX_DIAMETER=.7
MASS=80
KN=1.2e5
KT=2.4e5
A=2e3
B=.08
TAU=.5

DRIVING_SPEED_ARRAY=(.8 1 1.5 2 2.5 3 4 5 6)
DRIVING_SPEED_ARRAY_LENGTH=${#DRIVING_SPEED_ARRAY[*]}

T_ITER=8
SIM_TIME=500
DT_1=1e-4
DT_2=1e-2
PRINT_OVITO="false"
DATED_FILE="true"
CONSIDER_DELTA_1="true"

mkdir statistics

echo -e "####################################"
for (( a = 0; a < ${DRIVING_SPEED_ARRAY_LENGTH}; a++ )); do
  DRIVING_SPEED=${DRIVING_SPEED_ARRAY[${a}]}
  mkdir statistics/DRIVING_SPEED_${DRIVING_SPEED}

  echo ${a}
  echo ${DRIVING_SPEED_ARRAY}

  echo -e "Running analyser with driving_speed = $DRIVING_SPEED..."
  java -jar ./core/target/pedestrian-dynamic.jar gen static ${N} ${W} ${L} ${D} ${MIN_DIAMETER} ${MAX_DIAMETER} ${MASS} ${KN} ${KT} ${A} ${B} ${TAU} ${DRIVING_SPEED}
  for (( j = 0; j < ${T_ITER}; j++ )); do
    java -jar ./core/target/pedestrian-dynamic.jar gen dynamic output/static.dat
    java -jar ./core/target/pedestrian-dynamic.jar sim output/static.dat output/dynamic.dat ${SIM_TIME} ${DT_1} ${DT_2} ${PRINT_OVITO} ${DATED_FILE} ${CONSIDER_DELTA_1}
    mv output/dynamic.dat output/dynamic_${j}.dat
  done
  mv output/* statistics/DRIVING_SPEED_${DRIVING_SPEED}
  # move data processors and graphics generators
  cp -r resources/scripts/* statistics/
done
