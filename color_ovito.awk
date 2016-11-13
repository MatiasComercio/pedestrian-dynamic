#!/bin/awk -f

BEGIN {
  # TMP_FILE
  # MAX_PRESSURE
}
{
  # pressure is in the last column == NF;
  # type is in the column NF-1
  # id is in the first field
  r = g = b = 0;
  if (NF < 5) {
    print $0 >> TMP_FILE;
  } else { # it is not a particle's record
    if ($(NF-1) == "PEDESTRIAN") {
      if(MAX_PRESSURE > 0) {
        pressure = $NF / MAX_PRESSURE;
        r = pressure;
        b = 1 - pressure;
        g = 0;
      } else {
        print "[ERROR CASE]";
        r = 1;
        g = 1;
        b = 1;
      }
    } else if ($(NF-1) == "SPAWN") {
      r = g = b = 0.5;
    } else {
      r = g = b = 1;
    }

    print $0 r " " g " " b >> TMP_FILE;
  }
}
