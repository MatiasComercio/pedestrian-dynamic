#!/bin/bash

#define the template.
cat  << EOF
\documentclass{standalone}
\usepackage[utf8]{inputenc}
\usepackage[T1]{fontenc} %previous and current for spanish characters
\usepackage{tikz}
\usepackage{float} % For table positioning
\usepackage{titling} % Title edition: http://ctan.org/pkg/titling

% Plotting packages
\usepackage{filecontents}
\usepackage{pgfplots}
\pgfplotsset{
    compat=newest, % loads newest improved settings
    width=9.5cm,
    height=5cm,
}

\usepackage{pgfplotstable}
\usepgfplotslibrary{external}
%\tikzexternalize

\begin{document}


\begin{tikzpicture}
\begin{axis}[
scale only axis,
% width=30cm,
height=7.5cm,
% xmin=0,
% xmax=200,
% ymin=0,
xlabel = $\displaystyle \Delta t_{100}$ (s),
ylabel = Caudal (partículas/s),
mark size=1,
grid=both,
major grid style={line width=.2pt,draw=gray!100},
% cycle list name=color list
only marks
]

\addplot[clip marker paths=true,color = blue,mark=*] table [x index=0, y index=1, col sep=comma] {all_flows_avg_flow_evolution.csv};

\end{axis}
\end{tikzpicture}

\end{document}
EOF
