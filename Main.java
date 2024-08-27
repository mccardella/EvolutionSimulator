import java.util.ArrayList;
import java.util.Iterator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application{
 
 Stage window;

  // Starting settings

 int worldLength = 1000;
 int worldHeight = 800;
 int startPlants = 30 ;
 int startAnimals = 15 ;
 int totalDays = 20 ;
 int currentDay = 1 ;

 // Animal trait settings

 double startMaxEnergy = 150000 ;

 boolean senseAreaEnabled = true ;
 double startSenseDistance = 20.0 ;
 double minSenseDistance = 0 ;
 double maxSenseDistance = 100.0 ; 

 boolean speedEnabled = true ;
 double startSpeed = 100.0 ;
 double minSpeed = 50 ;
 double maxSpeed = 200;

 boolean sizeEnabled = true ;
 double startAnimalSize = 45 ;
 double minAnimalSize = 20 ;
 double maxAnimalSize = 120 ;

 Rectangle2D worldBounds = new Rectangle2D(0, 0, worldLength, worldHeight);
 Rectangle2D feedingArea = new Rectangle2D(maxAnimalSize, maxAnimalSize, worldLength - (maxAnimalSize * 2), worldHeight - (maxAnimalSize * 2));
 
 static boolean animationIsRunning;
 
 @Override
 public void start(Stage window) { //primaryStage
  System.out.println("World bounds: " + worldBounds.toString());
  System.out.println("Feeding Area dimensions: " + feedingArea.toString());
  window.setTitle("Senior Project");
  
  Group root = new Group();
  Scene theScene = new Scene( root );
  window.setScene( theScene );
  
  Canvas canvas = new Canvas( worldLength, worldHeight );
  root.getChildren().add( canvas );

  GraphicsContext gc = canvas.getGraphicsContext2D();

  Font theFont = Font.font( "Georgia", FontWeight.BOLD, 24 );
  gc.setFont( theFont );
  gc.setFill( Color.GREEN );
  gc.setStroke( Color.BLACK );
  gc.setLineWidth(1);

  ArrayList<Plant> plantList = new ArrayList<Plant>();
  growPlants(plantList);


  // Create first generation of animals and place them in the home zone
  ArrayList<Animal> animalList = new ArrayList<Animal>();
  double senseDistanceGene = -1;
  double speedGene = -1;
  double sizeGene = -1;
  if(senseAreaEnabled) {
   senseDistanceGene = startSenseDistance;
  }
  if(speedEnabled) {
   speedGene = startSpeed;
  }
  if(sizeEnabled) {
   sizeGene = startAnimalSize;
  }
  
  for(int i = 0; i < startAnimals; i++) {
   Animal animal = new Animal(startMaxEnergy, startSpeed, startAnimalSize, senseDistanceGene, speedGene, sizeGene);
   animal.setMinSenseDistance(minSenseDistance);
   animal.setMaxSenseDistance(maxSenseDistance);
   animal.setMinSpeed(minSpeed);
   animal.setMaxSpeed(maxSpeed);
   animal.setMinSize(minAnimalSize);
   animal.setMaxSize(maxAnimalSize);
   animalList.add(animal);
  }
  
  /*
  Animal bigAnimal = new Animal(startMaxEnergy, 110, 100.0, 60.0, 150, 100);
  animalList.add(bigAnimal);

  Animal smallAnimal = new Animal(startMaxEnergy, 75, 50, senseDistanceGene, 75, 50);
  animalList.add(smallAnimal);
  */

  placeAnimals(animalList);
  

  LongValue lastNanoTime = new LongValue( System.nanoTime() );
  AnimationTimer timer = new AnimationTimer() {

   @Override
   public void handle(long now) {

    
    // calculate time since last update.
    double elapsedTime = (now - lastNanoTime.value) / 1000000000.0;
    lastNanoTime.value = now;
    
    // animal movement logic
    
    ArrayList<Animal> eatenAnimals  = new ArrayList<Animal>();
    Iterator<Animal> animalIter = animalList.iterator();  // \
    while(animalIter.hasNext()) {                         //  > boilerplate iterator code
     Animal animal = animalIter.next();                   //

     calculateAnimalPaths(animal, plantList, animalList);
    
     animal.update(elapsedTime);
    
     // collision detection
     
     // second loop for if an animal can be eaten
     Iterator<Plant> plantIter = plantList.iterator();
     while(plantIter.hasNext()) {
      Sprite plant = plantIter.next();
      if (animal.intersects(plant)) {
       plantIter.remove();
       animal.collectFood();
       /* Should there be a reward of bonus energy for finding their first food?
       if(!animal.hasSurplus()) {
        animal.setCurrentEnergy(animal.getCurrentEnergy());
       }
       */
      }
     }
     
     if(sizeEnabled) {
      Iterator<Animal> preyIter = animalList.iterator();
      while(preyIter.hasNext()) {
       Animal prey = preyIter.next();
       if((animal.getWidth() - prey.getWidth()) / (animal.getWidth() + prey.getWidth()) >= 0.20 && animal.intersects(prey)) {
        eatenAnimals.add(prey);
        animal.collectFood();
       }
      }
     }
    }
    
    Iterator<Animal> preyIter = eatenAnimals.iterator();
    while(preyIter.hasNext()) {
     Animal carcass = preyIter.next();
     if(animalList.remove(carcass)) {
      preyIter.remove();
      System.out.println("An animal has been eaten!");
     }
    }
    
    
    // render
    
    gc.clearRect(0, 0, worldLength, worldHeight);
    gc.strokeRect(feedingArea.getMinX(), feedingArea.getMinY(), feedingArea.getWidth(), feedingArea.getHeight());
    
    for (Plant plant : plantList) plant.render(gc);
    int stopped = 0;
    for (Animal animal : animalList) {
     animal.render(gc);
     if(animal.isFinished()) stopped++;
    }
    
    if(stopped == animalList.size()) {
     if(currentDay == totalDays) {
      System.out.println("Simulation Complete.");
      printAnimalStats(animalList);
      this.stop(); // stop animation
     }
     resetDay(gc, startPlants, plantList, animalList);
     if(animalList.size() == 0){
      System.out.println("All animals did not survive.");
      this.stop();
     }
    }

   }
  };

  window.show();
  timer.start();
  
 }

 public void placeAnimals(ArrayList<Animal> animalList) {
  Iterator<Animal> animalIter = animalList.iterator();  // \
  while(animalIter.hasNext()) {                         //  > boilerplate iterator code
   Animal animal = animalIter.next();
   double edge = Math.random();
   double size = animal.getWidth();
   double ax;
   double ay;

   if(edge < 0.25) { // top edge
    ax = (maxAnimalSize) + (Math.random()*feedingArea.getWidth());
    ay = (maxAnimalSize) - size;
   }
   else if(edge >= 0.25 && edge < 0.5) { // left edge
    ax = (maxAnimalSize) - size;
    ay = (maxAnimalSize) + (Math.random()*feedingArea.getHeight());
   }
   else if(edge >= 0.5 && edge < 0.75) { // right edge
    ax = (maxAnimalSize) + feedingArea.getWidth();
    ay = (maxAnimalSize) + (Math.random()*feedingArea.getHeight());
   }
   else { // bottom edge
    ax = (maxAnimalSize) + (Math.random()*feedingArea.getWidth());
    ay = (maxAnimalSize) + feedingArea.getHeight();
   }
   animal.setPosition(ax, ay);
   if(!isAnimalInStartZone(animal)){
    System.out.println(animal.toString() + " Animal did not spawn in start zone.");
    System.exit(0);
   }
   //System.out.println(animal.toString());
  }
 }

 public void calculateAnimalPaths(Animal animal, ArrayList<Plant> plantList, ArrayList<Animal> animalList) {
  if(!animal.hasEaten() || (!animal.hasSurplus() && animal.getCurrentEnergy() >= animal.getMaxEnergy() / 2 )) { // maybe change to collect more food with excess energy?
   if(animal.getDestination() == null || animal.intersects(animal.getDestination())) { // randomWalk algorithm if does not have a destination
    animal.setVelocity(0, 0);
    animal.setDestination(-1, -1);
    double[] vector;
    Sprite point;
    do {
     vector = animal.scaledRandomVector();

     animal.setDestination((vector[0]) + animal.getPositionX(), (vector[1]) + animal.getPositionY());
     point = animal.getDestination();
     
     //System.out.println("Destination is " + point.toString());
     
    } while(!feedingArea.contains(point.getPositionX(), point.getPositionY()));
    vector = animal.unitVector(vector);
    animal.addVelocity(vector[0] * animal.getSpeed(), vector[1] * animal.getSpeed());
    //System.out.println("Moving toward " + point.toString());
    //System.out.println(animal.toString());
   }
   if(senseAreaEnabled) {
    /*else {
    for each animal in animallist
     if(sensearea intersects with another animal)
      if(animal is small enough to eat)
       destination is this animal
       full sails toward animal
    }*/
    if(sizeEnabled) {
     for(Animal otherAnimal: animalList) {
      if(animal != otherAnimal
      && animal.getSenseArea().intersects(otherAnimal)
      && (animal.getWidth() - otherAnimal.getWidth()) / (animal.getWidth() + otherAnimal.getWidth()) >= 0.20) { // fine percent difference
       animal.setVelocity(0, 0);
       double preyX = otherAnimal.getPositionX();
       double preyY = otherAnimal.getPositionY();
       animal.setDestination(preyX, preyY);
       double[] vectorTowardFood = animal.approachFoodVector(otherAnimal);
       animal.addVelocity(vectorTowardFood[0] * animal.getSpeed(), vectorTowardFood[1] * animal.getSpeed());
       break;
      }
     }
    }
    for(Plant plant: plantList) {
     if(animal.getSenseArea().intersects(plant) && !animal.intersects(animal.getDestination())) {
      animal.setVelocity(0, 0);
      double plantX = plant.getPositionX();
      double plantY = plant.getPositionY();
      animal.setDestination(plantX, plantY);
      double[] vectorTowardFood = animal.approachFoodVector(plant);
      animal.addVelocity(vectorTowardFood[0] * animal.getSpeed(), vectorTowardFood[1] * animal.getSpeed());
      //System.out.println("Moving towards " + plant.toString() + ".");
      break;
     }
    }
   }
  } else { // head back home after food is collected
   animal.setVelocity(0, 0);
   double[] home = animal.returnHomeVector(feedingArea);
   animal.addVelocity(home[0] * animal.getSpeed(), home[1] * animal.getSpeed());
   if(!feedingArea.intersects(animal.getBoundary())) {
    animal.setVelocity(0, 0);
    animal.dayCompleted();
   }
  }
  if(sizeEnabled) {
   for(Animal otherAnimal: animalList) {
    if(senseAreaEnabled
    && animal != otherAnimal
    && animal.getSenseArea().intersects(otherAnimal)
    && (animal.getWidth() - otherAnimal.getWidth()) / (animal.getWidth() + otherAnimal.getWidth()) <= -0.20) {
     animal.setVelocity(0, 0);
     double[] fleeVector = animal.fleeVector(otherAnimal);
     animal.addVelocity(fleeVector[0] * animal.getSpeed(), fleeVector[1] * animal.getSpeed());
    }
   }
  }
  if(animal.getCurrentEnergy() <= 0) { // stop movement of exausted animals
   animal.setDestination(-1, -1);
   animal.setVelocity(0, 0);
   animal.dayCompleted();
  }
  if(!animal.isFinished()) {
   // moving each tick costs (size^2 * speed^2 + sense) in energy
   animal.setCurrentEnergy(animal.getCurrentEnergy() - (animal.getSenseDistance() + animal.getSpeed()*2 + animal.getHeight()*5));
  }
 }


 public void resetDay(GraphicsContext gc, int startPlants, ArrayList<Plant> plantList, ArrayList<Animal> animalList) {
  // remove remaining food
  plantList.clear();

  // remove animals that didn't make it home
  Iterator<Animal> animalIter = animalList.iterator();
  while(animalIter.hasNext()) {
   Animal animal = animalIter.next();
   if (animal.getCurrentEnergy() <= 0) {
    animalIter.remove();
   }
  }
  gc.clearRect(0, 0, worldLength, worldHeight);
  
  // regrow plants
  growPlants(plantList);

  // reset animals' variables and proliferate
  ArrayList<Animal> children = new ArrayList<Animal>();
  for(int i = 0; i <= animalList.size() - 1; i++) {
   if(animalList.get(i).hasSurplus()){
    Animal child = animalList.get(i).reproduce();
    child.setPosition(animalList.get(i).getPositionX(), animalList.get(i).getPositionY());
    children.add(child);
    child.render(gc);
   }
   animalList.get(i).resetForNewDay();
  }
  animalList.addAll(children);
  placeAnimals(animalList);
  currentDay++;
 }

 public void growPlants(ArrayList<Plant> plantList){
  for (int i = 0; i < startPlants; i++) {
   Plant plant = new Plant();
   double px = maxAnimalSize + ((feedingArea.getWidth() - plant.getWidth())*Math.random());
   double py = maxAnimalSize + ((feedingArea.getHeight() - plant.getHeight())*Math.random());
   plant.setPosition(px,py);
   plantList.add( plant );
  }
 }

 public void printAnimalStats(ArrayList<Animal> animalList) {
  double sumSenseDistance = 0;
  double sumSpeed = 0;
  double sumSize = 0;
  for(Animal eachAnimal: animalList) {
   sumSenseDistance = sumSenseDistance + eachAnimal.getSenseDistance();
   sumSpeed = sumSpeed + eachAnimal.getSpeed();
   sumSize = sumSize + eachAnimal.getWidth();
  }
  System.out.println("Average senseDistance: " + sumSenseDistance / animalList.size());
  System.out.println("Average speed: " + sumSpeed / animalList.size());
  System.out.println("Avgerage size: " + sumSize / animalList.size());
 }

 public boolean isAnimalInStartZone(Animal animal) {
  return animal.intersects(worldBounds) && !animal.intersects(feedingArea);
 }

 public static void main(String args[]) {
   launch(args);
  }
 
}
