# EvolutionSimulator

EvolutionSimulator is a JavaFX project I made for my senior capstone. It visualizes how a given species can evolve under a certain set of environmental factors over generations at a time.

## Installation

JavaFX is a little finicky about installation, [this](https://youtu.be/IPhqJh4ckWA?si=r3-u7F8MR22FZSUL) is one of the few tutorials I found that can explain it for VSCode.

## Usage

Each day food is placed across the plane, and animals search around to eat in order to survive to the next day.
0 food eaten = animal dies off  
1 food eaten = animal lives for the next day  
2 food eaten = animal can produce an offspring along with it  

There are 3 traits animals have that will incrementally change as animals proliferate: speed, sense, and size.  

Speed moves animals faster across the plane at the cost of more energy.  
Sense is a circlular radius that shows where food are within it, and animals will path directly to it if it senses it.  
Size allows animals to eat other animals if it is twice the prey's size, adding a secondary food source.  

Images to be added...

## Contribution and License

This was my solo project, and it will continue to do so.
My plan is to continue adding features, but no longer will I be using JavaFX. Instead I will rewrite it in another language (probably python) and continue writing then.
