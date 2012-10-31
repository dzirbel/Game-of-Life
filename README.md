# The Game of Life

**The Game of Life** is a simulation of John Conway's Game of Life written in Java created by zirbinator [Dominic Zirbel, zirbinator@gmail.com]. This software is open-source and free to use under the GNU General Public License, Version 3 (http://www.gnu.org/licenses/gpl.html).

**Table of Contents**
1. **News**: updates and thoughts from zirbinator
2. **Background**: information on the Game of Life in general
   2-1. **Rules**: the rules of the simulation
3. **Installation**: how install and run the Game of Life
   3-1: **Advanced Instructions**: options to increase performance or troubleshoot issues
4. **Basics**: basic information
5. **The Grid**: how to manipulate the simulation itself
   5-1: **The Shift Key**
   5-2: **The Control Key**
6. **The Toolbar**: how to use the user interface
   6-1: **Hotkeys**
7. **Patterns**: how to place pre-defined configurations
   7-1: **Custom Patterns**: creating user-defined patterns
8. **The Diagnostic View**: diagnostic information
9. **The Next Generation**: upcoming features

## (1) News

I am working on a newer, much improved version of the Game of Life. It involves a complete redesign of the program as well as a number of new features. I am anticipating releasing Game of Life v3.0 around 11/10/12.

## (2) Background

The Game of Life is a cellular automaton proposed by John Conway. The Game consists of a two dimensional grid of square cells that are either alive or dead. It is a "zero player" game: after a map is created, the simulation runs without any player interference. The Game of Life was created to show that complex, even self-organizing, patterns can form from a simple set of rules and a starting position.

#### (2-1) Rules

Each generation, the next generation is simulated which then replaces the previous generation. A cell is alive in the next generation if:

1. If the cell was alive and had fewer than 2 neighbors it dies from underpopulation
2. If the cell was alive and had more than 3 neighbors it dies from overcrowding
3. If the cell was alive and had 2 or 3 neighbors it stays alive
3. If the cell was dead and has exactly three neighbors it becomes alive through reproduction

(neighbors are counted as cells that are adjacent to a given cell, including diagonally, so that a cell has a maximum of 8 neighbors)

## (3) Installation

The Game of Life can be downloaded from the github repository:
www.github.com/zirbinator/Game-of-Life
Simply go to the repository and select the "Download" tab (on the right) and download the latest version. The downloaded file is a Java Archive (JAR) and can typically be run by simply double-clicking the file. On some Linux systems it must be marked as executable-bit.

Once the JAR has been downloaded, place it in any folder. Then download the most recent "images" and "patterns" zip files from the same Downloads tab. Unzip these files and place them in the same folder as the JAR. The proper configuration is as follows:

<pre>
Any Folder
    Game_of_Life-vX.x.jar
    images
        alive.png
        broom.png
        etc.
    patterns
        Expander
            Double Zig-Zag.txt
            etc.
        etc.
    Any other files may also be in this folder
</pre>

(In an upcoming version downloading the "images" and "patterns" folders will be unnecessary.)

#### (3-1) Advanced Instructions

If desired, the Game of Life can be run via the command-line and will print diagnostic information in the case of a crash. Navigate to the folder containing the JAR and run it with Java, typically as such:
<pre>
java -jar Game_of_Life.jar
</pre>
In addition to any exceptions thrown at runtime, warning messages will be printed if rendering takes longer than the expected time. Do not be surprised if a few of the messages appear. When the program is exited, a total count of the number of times that the expected render time was exceeded will print, along with a percentage of the total render cycles. If this number is above 50%, you may experience a slower environment.

At the moment, the Game of Life runs very slowly, enough to make it unusable. Typically performance can be improved by enabling OpenGL, as such:
<pre>
java -jar -Dsun.java2d.opengl=true Game_of_Life.jar
</pre>

Performance can also be improved by increasing the amount of memory available to Java. Include the parameters
<pre>
-Xmx1024M -Xms1024M
</pre>
to chance the amount of memory allocated to Java. Place these parameters after `-jar` and before `Game_of_Life.jar`. (This example sets the memory as 1024 megabytes for the maximum (Xmx) and minimum (Xms) limits. Other sizes can be used.)

## (4) Basics

The Game of Life, when run, has two main components. The background is "The Grid" where the simulation occurs and can be modified by the user. In the bottom-right of the screen is a box containing tools to control the simulation, called "The Toolbar".

To exit the Game of Life, press the Escape key or the `X` in the top-right of the screen. To minimize, click the `_` in the top-right.

## (5) The Grid

The simulation takes place on "The Grid." The black rectangles constitute the playing field for the cellular automation. Alive cells are green and dead cells are black. Cells can be made alive or dead by left clicking and dragging. Clicking a living cell will make it dead (and all cells that are then dragged over) and clicking a dead cell will make it become alive (along with all other cells dragged over). The Grid is dynamically expanding, meaning that it has no boundaries, but will continue on forever. The area of the Grid viewed can be moved with the arrow keys. Additionally, the grid can be zoomed with the plus (+) or minus (-) keys. When zooming, the cell hovered over by the mouse will be retained, so that zooming will be centered around the mouse. When zoomed out very far, the grid lines will disappear so that the smaller sized cells can be seen.

#### (5-1) The Shift Key

Holding the shift key will change the environment in terms of movement. With the shift key held, the up and down arrow keys will zoom in and out rather than move and the left and right arrows will do nothing. Clicking the mouse will have no effect, but dragging will pan around the Grid. The plus (+) and minus (-) keys have no effect when the shift key is held.

#### (5-2) The Control Key

The control key also has an effect when held. Pressing the arrow keys with control down will move the cursor one cell in the appropriate direction, and make the cell to which the cursor moves alive. This can be useful for counting out a line of a certain number of cells or experimenting with straight lines. The mouse's use is not affected when control is held, but the plus (+) and minus (-) keys will again have no effect.

## (6) The Toolbar

The primary aspect of the user interface (UI) is the toolbar, which will first appear as a gray oval in the bottom-left corner of the screen. The toolbar has three large buttons, which act as the primary way of manipulating the simulation. First, the play/pause button will play and pause the simulation, which will run automatically when playing. The next button will simulate the next generation. Finally, the clear button (broom) will clear the simulation and reset the generation count to 0. Which moves us to the generation counter, above and to the right of the clear button. This counter will count the number of generations run in the current simulation (since the clear button was last pressed or the program was started). Below the counter is the speed bar. Dragging the slider will adjust the speed at which the simulation runs, right will run it faster and left will slow it down. Dragging four dots will move the location of the Toolbar.

#### (6-1) Hotkeys

If you don't feel like pressing buttons, there are always options! The tooltips that appear when you hover over various buttons give the hotkey to press (control not required) in brackets, but here they are for you anyway:

Play/Pause: P
Next Generation: N
Clear: C

Not very difficult, but they can be very useful.

## (7) Patterns

I'm sure you're just having a jolly time drawing pictures on the screen and seeing them blow up (who wouldn't?) but some very smart people have come up with ways to have even more fun. I have a fairly small selection of some patterns that you can put right into the Grid. The Pattern Selector can be accessed with the small arrow on the left of the Toolbar. Inside the Pattern Selector are a bunch of folders. Click on the folders to expand them, and then pick a pattern from the list, click on it and (dragging not necessary) move it into the Grid, and then click one more time to place it (right click to get rid of the pattern following your mouse). I have the patterns that I put in organized by function. Stable patterns are, of course, stable, sometimes called "still life". Spaceship patterns move without leaving anything behind (yes, I know that the Queen Bee doesn't actually go anywhere fast without some help). Oscillator patterns switch through a series of states. Exploders start small but seem like they "explode", though they will eventually become stable(ish). Finally, Expanders will get bigger forever, whether by shooting off spaceships or pure awesomeness (aka the Spacefiller).

#### (7-1) Custom Patterns

Sooner or later, you'll get bored with the few patterns that I put in and, lucky for you, you can make your own. The way you do it isn't great right now, but it's something. Go into the patterns folder and you'll see a bunch of folders strangely names exactly the same as the pattern folders you see in the program. Inside of them are the data files corresponding to the actual patterns, saved as .txt files. You can create your own folders and rename mine, as well as create/rename/delete patterns. Inside the patterns, as you can see in any of my patterns, is a rectangle of living/dead cells. This is where your pattern goes, with living cells 't' and dead cells 'f'. After that, on a new line, comes a break (###) and then, on another line, the "expanded name" of your pattern. Usually this will be the same as the name of the pattern that you have to give the file, but if your pattern has a wicked long name, put an abbreviation for the file name and the full thing here; the expanded name shows up in a tooltip when you hover the mouse over the shorter name. When you start the Game of Life again, you'll see your patterns right along with mine, if any of them are left.

## (8) The Diagnostic View

If you're interested in how well the simulation is running on your computer or have spotted a recurring bug, there is a diagnostic view that provides the user with a variety of useful information. Simply press F3 at any time to toggle this view. First, near the top-left corner of the screen is a small box labeled "General Information" that surprising enough provides general information, in this case, the location of the pointer in both pixels and tiles. Below this box is a box labeled "Grid Information". The fields inside this box show information help by the Grid: the location of the viewing area, the zoom, the current states of dragging and creating, and the location last dragged. When the simulation is run, the times that it takes to simulate a generation are plotted inside the Grid Information box. The scale on the left of the box is in milliseconds. Next, on the left of the screen is a display showing the usage of the Java heap. The maximum, current, and used heap sizes are shown along with a small bar graph. Finally, along the bottom of the screen a graph of the rendering cycle is given. Again, the vertical scale is in milliseconds. The "period" - the time that a single render cycle should take - is given as a blue line. The time that it takes to draw (create the image to be shown) is shown in red, and the time taken to render (move this image to the screen) is in yellow. The amount of time "left over" from these two processes is spent "sleeping", allowing other processes to run. The time spent sleeping in a cycle is shown in green. The total time spent in a cycle - the sum of the drawing, rendering, and sleeping times - is shown in purple. If these purple points are consistently below the period line, then the desired framerate (~50 fps) is being achieved, if not, then the program is running below the optimal framerate.

## (9) The Next Generation

The Game of Life has a big future. Here are some of the things I'm looking forward to adding:

1. Select a group of cells and:
    1. Create a pattern out of them
    2. Rotate them
    3. Make them all alive/dead
2. Rotate patterns before placing them
3. Zombie Cells! Some cells are always alive/dead
4. More advanced drawing tools to draw lines, boxes, etc.
5. Intense simulation: simulate a certain number of generations at once
6. Time-travel: go backward in the simulation
7. Copy/Paste
