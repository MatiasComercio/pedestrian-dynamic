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

\pgfplotscreateplotcyclelist{MyCyclelist}{%
  {darkgray, mark = none, thick},
  {Green, mark = none, thick},
  {MidnightBlue, mark = none, thick},
  {Mahogany, mark = none, thick},
  {RedOrange, mark = none, thick},
  {RoyalPurple, mark = none, thick},
}

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
mark size=.5,
grid=both,
major grid style={line width=.2pt,draw=gray!100},
cycle list name=color list
]

\addplot[clip marker paths=true,color = blue,mark=*, error bars/.cd,y dir=both, y explicit] table [x index=0, y index=1, y error index=2, col sep=comma] {all_flows_avg.csv};

\end{axis}
\end{tikzpicture}

\end{document}
EOF
