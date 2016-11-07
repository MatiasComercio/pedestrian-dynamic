#!/bin/awk -f
BEGIN {
  # format for printing numbers
  OFMT="%.14g"
  OFS=",";
  t_index=1;
  i_flow_evolution = 1;
  W = 50; # window
  i_s_w = 1; # index of the starting point for window calculation
  i_e_w = W; # index of the ending point for window calculation
  # This variables should be passed as arguments
  # ALL_FLOWS_FILE
  # TMP_ALL_FLOWS_FILE
  # ALL_FLOWS_AVG_FILE
  # EACH_FLOW_MEDIA_FILE
  # ALL_FLOW_MEDIA_FILE
  # EVACUATION_TIME_AVG_FILE
  # EACH_FLOW_EVOLUTION_FILE
  # ALL_FLOW_EVOLUTION_FILE
}
{
  each_flow_evolution_value[""] = 0;
  all_flow_evolution_value[""] = 0;
  media = 0;
  sd = 0;
  getline all <TMP_ALL_FLOWS_FILE;
  flow_row = i_flow_evolution;
  for(i = 2; i <= NF; i++) {
    # NR ==> in this context, it is the row index; i is the column index; $i is the value of the columned indexed by i ath the row NR
    each_flow_evolution_value[NR "," i] = $i
    media += $i;
    sd += $i^2;

    # for each simualtion flow evolution
    if (NR >= W) {
      flow = W/(each_flow_evolution_value[i_e_w "," i] - each_flow_evolution_value[i_s_w "," i]);
      flow_row = flow_row "," flow
      # i_s_w ++; i_e_w ++; i_flow_evolution ++; this indexes MUST be updated at the end only
    }
  }
  N = NF-1; # skip the first column, the empty index
  media /= N;
  sd = sqrt(sd/N - media^2);
  # index the all file
  print t_index all >> ALL_FLOWS_FILE; # 'all' file should already include a ',' at the beggining of each line
  # write media and sd to new file
  print t_index, media, sd >> ALL_FLOWS_AVG_FILE;


  # for each simulation & average of simualtions flow evolution
  all_flow_evolution_value[NR] = media;
  if (NR >= W) {
    # for each
    print flow_row >> EACH_FLOW_EVOLUTION_FILE;

    # for all
    flow = W/(all_flow_evolution_value[i_e_w] - all_flow_evolution_value[i_s_w]);
    print i_flow_evolution, flow >> ALL_FLOW_EVOLUTION_FILE
    i_s_w ++; i_e_w ++; i_flow_evolution ++; # here they must be updated ; not in the for cycle
  }

  t_index ++;
}
END {
  # get the avg evacuation time for all the simulations &
  # get the avg flow for each simulation | | | |                                     avg  sd
  #                --> avg               · · · ·  at EACH_FLOW_MEDIA_FILE ==> (avg & sd) ==> ·   · at ALL_FLOW_MEDIA_FILE
  t_index = 1;
  all_avg_flow = 0;
  all_sd_flow = 0;
  avg_evacuation_time = 0;
  sd_evacuation_time = 0;
  for(i = 2; i <= NF; i++) {
    # avg evacuation time
    evacuation_time = $i;
    avg_evacuation_time += evacuation_time;
    sd_evacuation_time += evacuation_time^2;

    # avg flow
    each_avg_flow = NR/$i;
    all_avg_flow += each_avg_flow;
    all_sd_flow += each_avg_flow^2;
    print t_index, each_avg_flow >> EACH_FLOW_MEDIA_FILE;

    t_index ++;
  }
  N = NF-1; # skip the first column, the empty index
  all_avg_flow /= N;
  all_sd_flow = sqrt(all_sd_flow/N - all_avg_flow^2);

  print all_avg_flow, all_sd_flow >> ALL_FLOW_MEDIA_FILE;

  avg_evacuation_time /= N;
  sd_evacuation_time = sqrt(sd_evacuation_time/N - avg_evacuation_time^2);
  print avg_evacuation_time, sd_evacuation_time >> EVACUATION_TIME_AVG_FILE;
}
