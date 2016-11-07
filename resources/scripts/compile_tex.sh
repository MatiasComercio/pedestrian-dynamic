#!/bin/bash
RESULTS_FOLDER=results
echo 'Compiling all .tex files...'
for driving_speed in ./DRIVING_SPEED_* ; do
  if [[ -d ${driving_speed} ]] ; then
    echo '  '${driving_speed}
    RESULTS_PATH=${driving_speed}/${RESULTS_FOLDER}
    for file in ${RESULTS_PATH}/*.tex ; do
      if [[ -f ${file} ]]; then
        ( # different environment
          echo '    Compiling '${file} '...';
          cd ${RESULTS_PATH};
          filename=$(basename "${file}")
          pdflatex ${filename} 2>&1 >/dev/null;
          rm -rf latex_tmp
          mkdir latex_tmp
          mv *.log latex_tmp/
          mv *.aux latex_tmp/
          echo '    [DONE]'
        )
      fi
    done
  fi
done
for file in ${RESULTS_FOLDER}/*.tex ; do
  if [[ -f ${file} ]]; then
    ( # different environment
      echo '  Compiling '${file} '...';
      cd ${RESULTS_FOLDER};
      filename=$(basename "${file}")
      pdflatex ${filename} 2>&1 >/dev/null;
      rm -rf latex_tmp
      mkdir latex_tmp
      mv *.log latex_tmp/
      mv *.aux latex_tmp/
      echo '  [DONE]'
    )
  fi
done
echo "[DONE]"
