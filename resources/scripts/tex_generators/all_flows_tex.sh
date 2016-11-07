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
width=10cm,
height=10cm,
xmin=0,
xmax=200,
ymin=0,
xlabel = Cantidad de partículas,
ylabel = Tiempo de salida (s),
mark size=0.3,
grid=both,
major grid style={line width=.2pt,draw=gray!100},
cycle list name=color list
]

\foreach \column in {1,...,8}{
  \addplot+[clip marker paths=true,mark=*] table[x index=0, y index=\column, col sep=comma] {all_flows.csv};
}
\end{axis}
\end{tikzpicture}

\end{document}
EOF
