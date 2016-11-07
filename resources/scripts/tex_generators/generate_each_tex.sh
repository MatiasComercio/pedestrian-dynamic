#!/bin/bash

OUTPUT_FOLDER=$1

TEX_GENERATORS_FOLDER=tex_generators

./${TEX_GENERATORS_FOLDER}/all_flow_avg_flow_evolution_tex.sh > ${OUTPUT_FOLDER}/all_flow_avg_flow_evolution.tex
./${TEX_GENERATORS_FOLDER}/all_flows_avg_tex.sh > ${OUTPUT_FOLDER}/all_flows_avg.tex
./${TEX_GENERATORS_FOLDER}/all_flows_tex.sh > ${OUTPUT_FOLDER}/all_flows.tex
./${TEX_GENERATORS_FOLDER}/each_flow_evolution_tex.sh > ${OUTPUT_FOLDER}/each_flow_evolution.tex
