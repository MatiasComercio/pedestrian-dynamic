#!/bin/awk -f

BEGIN {
  MAX_PRESSURE_FILE="max_pressure.csv";
  max_pressure = 0;
}
{
  # pressure is in the last column == NF; type is in the column NF-1
  # type is needed for not NaN values
  if ($(NF-1) == "PEDESTRIAN" && $NF > max_pressure) {
    max_pressure = $NF;
  }
}
END {
  print max_pressure;
}
