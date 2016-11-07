#!/bin/bash

OUTPUT_FOLDER=$1

TEX_GENERATORS_FOLDER=tex_generators

./${TEX_GENERATORS_FOLDER}/all_evacuation_times_avg_tex.sh > ${OUTPUT_FOLDER}/all_evacuation_times_avg.tex
./${TEX_GENERATORS_FOLDER}/all_flow_media_tex.sh > ${OUTPUT_FOLDER}/all_flow_media.tex
