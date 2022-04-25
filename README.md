# Natural selection simulator

This project was done for the Object Oriented Programing course at AGH UST. <br>
It is a simulation of a simple ecosystem with some insight providing tools.

### How it works:
The simulation takes place on a plane and works in real-time. Every tick food appears somewhere on the plane. In the middle there is also a more fertile patch of land that spawns more grass.
A starting population of animals appears at the begining of simultion. Every creature has a genotype, which determines how it moves - it impacts the probability of movement in each direction, each tick. Every tick every creature on the plane makes a move according to its genotype. Each tick creatures also lose some energy - if their energy runs out - they die. Their energy levels are indicated by colors - white is high energy, black is close to zero. Stepping on (and thus consuming) a grass patch restores some energy to the creature.
#### The evolution part:

When 2 animals of sufficiently high energy move to one field, they reproduce and create an offspring. The genotype of the offspring is a combination of its parents' genotypes. The energy of the offspring is a fraction of its parents'.

#### The goal of the simulation
...is to see how the population will behave over time, what are the results after running the simulation for a long time and seeing how tweaking all the parameters impacts the simulation.

For this purpose a number of utitity tools are built into the aplication. A stastistics panel, along with a graph allows to see all important information about the simulation. It's also possible to save statistics over a given period of time. It's also possible to skip the simulation by a given amout of ticks. When the simulation is paused, a single animal can be selected to see all information about it. If an animal is selected and we skip the simulation, we can see information about its offspring, and how long its life has lasted. 
It's also possible to highlight all animals, which share the most common genotype among the population.

![image](https://user-images.githubusercontent.com/37042650/165166636-93b6100f-0ac2-4dd8-a6b6-34c0e40a086d.png)
![image](https://user-images.githubusercontent.com/37042650/165166814-f2ab5ceb-5e31-4c0d-9e1a-7ab44952cc3b.png)

