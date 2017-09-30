# Game of Life

A desktop simulation of John Conway's Game of Life written in Java, created by Dominic Zirbel [djynth@gmail.com]. This software is open-source and free to use under the GNU General Public License, Version 3 (http://www.gnu.org/licenses/gpl.html).

Most graphics are adapted from the "Ultimate Gnome" project, found at: http://code.google.com/p/ultimate-gnome/

## Background

The Game of Life is a cellular automaton created by John Conway where a grid of cells, each cell either alive or dead, evolve over time based on a simple set of rules. It was proposed by Conway to demonstrate how trivial rules and a simple starting position can evolve into complex, even self-organizing, scenarios. It has been studied in some depth due to the richness of the emergent life.

The rules of the simulation are simple. Each generation, a grid of cells is used to generate the next generation of cells which are based on the current generation's cells. A cell in the next generation is alive if (and only if):

* it was dead, but was surrounded by exactly 3 neighbors
* it was alive and had 2 or 3 neighbors

The simulation is somewhat signification - and interesting - because of the complex life that can be formed from just these two rules.

This simulation aims to be a useful tool for anyone studying cellular automata and an easy-to-use application for anyone interested in the Game of Life in general. It is currently in development, but very usable.

## Features

The Game of Life currently supports the following features:

* a dynamic, expanding grid
* simple cell creation and destruction
* easy-to-use interface
* fast(ish) simulation speed
* pre-set patterns which can be placed into the simulation
* rectangular selections which can be: rotated, copied, cleared, or used to create rectangles and ovals

## Future

The Game of Life is no longer being developed. Planned features included:

* improved pattern interface
* the ability to save/delete/rename/etc. patterns
* right-click menus

Longer-term, I was hoping to implement:

* Memory: return to the first generation
* Zombie Cells: cells that are always alive (or dead)
* Screenshots and simulation videos or animated gifs
* Intense simulation: simulate a given number of generations as quickly as possible (without displaying them)

## Installation

The Game of Life is very simple to install and run. Simply downlaod the latest JAR from the repository and run it. On some Linux/Unix systems, this file may have to be marked as executable bit.

(The JAR is in the [root repository folder](http://www.github.com/djynth/Game-of-Life) named "Game of Life X.X.XX.jar". To download it, click on it and then select "View Raw". I apologize for this inconvenience; github has recently removed Downloads and I have not yet moved to an external host).

The only requirement to run the Game of Life is a recent installation of Java (1.5 or later).

The Game of Life can also be run from the command line, with

`$ java -jar Game_of_Life.jar`

If the program is running slowly, consider adding memory to the Java Virtual Machine, with:

`$ java -jar -Xmx1024M -Xms1024M Game_of_Life.jar`

(for 1024 megabytes of RAM to be allocated). Additionally, if the program is running extremely poorly or not responding, try enabling OpenGL:

`$ java -jar -Dsun.java2d.opengl=true Game_of_Life.jar`

## How to Use

The Game of Life is designed to be simple to use. I recommend installing it and running it immediately, and figuring out the controls yourself. Have fun! If you want a little more guidance, consult the documentaion below.

### Basics

The simulation is played out on the "grid" - the black gridded background. To create cells, simply left click (and drag) on the grid. To remove cells, left click (and drag) on living cells.

You can move around the grid with the arrow keys, and zoom in and out with the mouse wheel or the + and - keys. The grid is infinite, so don't worry about running out of space.

The interface is mainly comprised of the Toolbar - a gray rectangular box containing common tools. To simulate the next generation, press the "next" arrows or the "N" key. To play or pause the simulation click the play/pause button or use the "P" key. To stop and clear the simulation, use the stop button or "S" key. The number of generations since the simulation was cleared is shown in blue, near the right of the toolbar. Finally, the Toolbar can be dragged around the screen with the blue orb at the top-right.

When playing, the simulation is constantly updated until it is paused. You can change the speed at which it is updated by dragging a blue slider at the bottom-right of the toolbar. Left is slower, right is faster.

Last, the Game of Life can be minimized with the - button in the top-right of the screen, and closed with the X button next to it, or with the Escape key.

### Selections

To select a rectangular area of the grid, hold the shift key and drag the mouse. Once a selection is complete, a gray toolbar will appear at its top-right corner. This toolbar can be used to manipulate the selected area.

First, you can drag the corners or sides of a selection to resize it. Any selection that does not encompass a single cell will disappear.

The toolbar can be hidden with the left-facing arrow at its top-right, and re-opened with the same arrow. It can be closed with the X below the hide arrow.

The selection toolbar has a number of functions. First, the save function is not yet implemented (will be soon). The copy button (or Control-C, Control-X also cuts) copies the selected area to the clipboard, which can be pasted (at the cursor's position) with Control-P. A selection can be rotated with the rotation arrows or Control-R and Control-Shift-R. A selection can be cleared with the clear X. Finally, a selection can be used to create a rectangle or oval with the matching buttons.

### Patterns

The Game of Life has a number of pre-set patterns which can be placed into the simulation. Click on the rounded right side of the main toolbar to open the pattern selector. Within this selector, click any folder to open or close it, and then click (no need to drag) on the patterns inside to select them. With a pattern selected, it will "follow" the cursor and can be placed on the grid with the left mouse button, or unselected with the right mouse button. Close the pattern selected by again clicking on the rounded cap.
