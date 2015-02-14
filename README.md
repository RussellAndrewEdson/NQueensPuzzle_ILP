# NQueensPuzzle_ILP
Formulates the N-Queens Puzzle as a 0-1 Integer Linear Program and solves it using the SCPSolver + GLPK tools.

![Solving the 8-queens puzzle.](https://raw.githubusercontent.com/RussellAndrewEdson/NQueensPuzzle_ILP/master/screenshot.png "Solving the 8-queens puzzle.")

I designed this program in conjunction with the blog post I made on the [N-Queens Puzzle and 0-1 
Integer Linear Programming](http://www.thejavamathematician.blogspot.com.au/2015/02/the-n-queens-puzzle-and-0-1-integer.html). Most of the documentation for how the program works is there in the blog post (I've included a PDF version in this repository, too.)


This program uses the following open source libraries:
- the SCPSolver developed by Hannes Planatscher and Michael Schober (http://scpsolver.org/),
- the optimization backend chosen for the SCPSolver is the GNU Linear Programming Kit (https://www.gnu.org/software/glpk/) originally developed by Andrew Makhorin.

Those software libraries are licensed under the GNU GPLv3, and so is this program.
