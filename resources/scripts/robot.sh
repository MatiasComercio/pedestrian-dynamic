#!/bin/bash
RESULTS_FOLDER=results
FLOW_CSV=flow.csv
ALL_FLOWS_CSV=all_flows.csv
TMP_CSV=tmp.csv
AUX_CSV=aux.csv
ALL_FLOWS_AVG_CSV=all_flows_avg.csv
EACH_FLOW_MEDIA_CSV=each_flow_media.csv
ALL_FLOW_MEDIA_CSV=all_flow_media.csv
EVACUATION_TIME_AVG_CSV=evacuation_time_avg.csv
ALL_EVACUATION_TIMES_AVG_CSV=all_evacuation_times_avg.csv
EACH_FLOW_EVOLUTION_CSV=each_flow_evolution.csv
ALL_FLOWS_AVG_FLOW_EVOLUTION_CSV=all_flows_avg_flow_evolution.csv
TEX_GENERATORS_FOLDER=tex_generators
echo 'Collecting data for each driving speed value...'
for driving_speed in ./DRIVING_SPEED_* ; do
  if [[ -d ${driving_speed} ]] ; then
    echo '  ' ${driving_speed}

    RESULTS_PATH=${driving_speed}/${RESULTS_FOLDER}
    # clear content
    rm -rf ${RESULTS_PATH}
    # create new vile
    mkdir -p ${RESULTS_PATH}

    ALL_FLOWS_FILE=${RESULTS_PATH}/${ALL_FLOWS_CSV}; # all flows files resumed in one table, with index
    TMP_ALL_FLOWS_FILE=${RESULTS_PATH}/${TMP_CSV}; # all flows files resumed in one table, without index
    AUX=${RESULTS_PATH}/${AUX_CSV}; # aux file for tmp_all table generation
    ALL_FLOWS_AVG_FILE=${RESULTS_PATH}/${ALL_FLOWS_AVG_CSV};  # avg & sd of each row for all columns of 'all' file
    EACH_FLOW_MEDIA_FILE=${RESULTS_PATH}/${EACH_FLOW_MEDIA_CSV}; # flow avg for each simulation (each col of the tmp_all table) resumed in one table
    ALL_FLOW_MEDIA_FILE=${RESULTS_PATH}/${ALL_FLOW_MEDIA_CSV}; # flow average & sd for all simulations (average & sd of the each_flow_media table)
    EVACUATION_TIME_AVG_FILE=${RESULTS_PATH}/${EVACUATION_TIME_AVG_CSV}; # evacuation time avg & sd for all simulations
    EACH_FLOW_EVOLUTION_FILE=${RESULTS_PATH}/${EACH_FLOW_EVOLUTION_CSV}; # flow evolution with moving average concept of each simulation
    ALL_FLOW_EVOLUTION_FILE=${RESULTS_PATH}/${ALL_FLOWS_AVG_FLOW_EVOLUTION_CSV}; # flow evolution of the averaged simulation times

    for f_iteration in ${driving_speed}/* ; do
      if [[ -d ${f_iteration} ]] ; then
        echo '    ' + ${f_iteration}
        for file in ${f_iteration}/${FLOW_CSV} ; do
          if [[ -f ${file} ]]; then
            echo '      ' - ${file}
            # This needs to be done using this aux file because, if not, it does not append the output to the existing one
            # I assume it is because the '>' is deleting and creating a new file, but I'm not sure
            #
            # Translated: get each line of the ALL_FLOWS_FILE variable (must be a file) and print it, appending
            # it the second column of the input '${file}', separating each column with a ','.
            # Save the output to de AUX file, so as not to delete the TMP_ALL_FLOWS_FILE contents before reading it.
            # Then, rename the AUX file to be the TMP_ALL_FLOWS_FILE file, and start again if you need to append a new column
            awk -F, -v TMP_ALL_FLOWS_FILE="$TMP_ALL_FLOWS_FILE" '{ getline all <TMP_ALL_FLOWS_FILE ;print all, $2}' \
              OFS=, OFMT="%.14g" ${file} > ${AUX}; mv ${AUX} ${TMP_ALL_FLOWS_FILE}
          fi
        done
      fi
    done

    # resume all consolidated data & extract results; index the TMP_ALL_FLOWS_FILE generated file
    awk -F, -v ALL_FLOWS_FILE="$ALL_FLOWS_FILE" \
      -v ALL_FLOWS_AVG_FILE="$ALL_FLOWS_AVG_FILE" \
      -v EACH_FLOW_MEDIA_FILE="$EACH_FLOW_MEDIA_FILE" \
      -v ALL_FLOW_MEDIA_FILE="$ALL_FLOW_MEDIA_FILE" \
      -v EACH_FLOW_EVOLUTION_FILE="${EACH_FLOW_EVOLUTION_FILE}" \
      -v ALL_FLOW_EVOLUTION_FILE="${ALL_FLOW_EVOLUTION_FILE}" \
      -v EVACUATION_TIME_AVG_FILE="$EVACUATION_TIME_AVG_FILE" \
      -v TMP_ALL_FLOWS_FILE="$TMP_ALL_FLOWS_FILE" \
      -f media.awk ${TMP_ALL_FLOWS_FILE}
    rm -f ${TMP_ALL_FLOWS_FILE}

    # generate all latex files
    ./${TEX_GENERATORS_FOLDER}/generate_each_tex.sh ${RESULTS_PATH}
  fi
done
echo '[DONE]'

# collect all generated data from different driving_speed folders
RESULTS_PATH=${RESULTS_FOLDER}
ALL_EVACUATION_TIMES_AVG_FILE=${RESULTS_PATH}/${ALL_EVACUATION_TIMES_AVG_CSV}
ALL_FLOW_MEDIA_FILE=${RESULTS_PATH}/${ALL_FLOW_MEDIA_CSV}

rm -rf ${RESULTS_PATH}
mkdir -p ${RESULTS_PATH}

echo 'Resuming data for all driving speed values...'
for driving_speed in ./DRIVING_SPEED_* ; do
  if [[ -d ${driving_speed} ]] ; then
    echo '  ' ${driving_speed}

    # get driving speed value
    filename=$(basename "${driving_speed}")
    speed="${filename#DRIVING_SPEED_}";

    for f_iteration in ${driving_speed}/${RESULTS_FOLDER} ; do
      if [[ -d ${f_iteration} ]] ; then
        echo '    ' + ${f_iteration}
        # evacuation time
        for file in ${f_iteration}/${EVACUATION_TIME_AVG_CSV} ; do
          if [[ -f ${file} ]]; then
            echo '      ' - ${file}
            awk -F, -v ALL_EVACUATION_TIMES_AVG_FILE="$ALL_EVACUATION_TIMES_AVG_FILE" -v speed="$speed" \
              '{ getline all < ALL_EVACUATION_TIMES_AVG_FILE; print speed, $1, $2 >> ALL_EVACUATION_TIMES_AVG_FILE;}' \
              OFS=, OFMT="%.14g" ${file}
          fi
        done

        # flow media
        for file in ${f_iteration}/${ALL_FLOW_MEDIA_CSV} ; do
          if [[ -f ${file} ]]; then
            echo '      ' - ${file}
            awk -F, -v ALL_FLOW_MEDIA_FILE="$ALL_FLOW_MEDIA_FILE" -v speed="$speed" \
              '{ getline all < ALL_FLOW_MEDIA_FILE; print speed, $1, $2 >> ALL_FLOW_MEDIA_FILE;}' \
              OFS=, OFMT="%.14g" ${file}
          fi
        done
      fi
    done
  fi
done

# sort the generated file so as the graphic is well interpretated by latex
export LC_NUMERIC=en_US.utf-8 # this enables the dot interpretation of decimals
sort -t, -g ${ALL_EVACUATION_TIMES_AVG_FILE} > ${AUX};
mv ${AUX} ${ALL_EVACUATION_TIMES_AVG_FILE}

sort -t, -g ${ALL_FLOW_MEDIA_FILE} > ${AUX};
mv ${AUX} ${ALL_FLOW_MEDIA_FILE}

# generate all latex files
./${TEX_GENERATORS_FOLDER}/generate_results_tex.sh ${RESULTS_PATH}

echo '[DONE]'
