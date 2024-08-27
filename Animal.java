import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class Animal extends Sprite {
 
 private Sprite currentDestination;

 private double maxEnergy;
 private double currentEnergy;
 private int foodGathered;

 private boolean finished;

 //traits

 private boolean senseAreaEnabled;
 private double senseDistance;
 private Sprite senseArea;
 private double minSenseDistance;
 private double maxSenseDistance;

 private boolean speedEnabled;
 private double speed;
 private double minSpeed;
 private double maxSpeed;

 private boolean sizeEnabled;
 private double size;
 private double minSize;
 private double maxSize;

 
// Mutations in traits will be changed in Main.java,
// and will be added in as parameters once the offspring is made.

// make a image in a new size
// https://stackoverflow.com/questions/27894945/how-do-i-resize-an-imageview-image-in-javafx

 public Animal(double startMaxEnergy, double startSpeed, double startSize, double inheritedSenseDistance, double inheritedSpeed, double inheritedSize) {
  currentDestination = new Sprite();
  currentDestination.setHeight(1);
  currentDestination.setWidth(1);
  currentDestination.setPosition(-1, -1);
  finished = false;
  maxEnergy = startMaxEnergy;
  currentEnergy = maxEnergy;
  foodGathered = 0;

  if(inheritedSize != -1) {
   sizeEnabled = true;
   setImage("Animal60px.png", inheritedSize);
  } else {
   sizeEnabled = false;
   setImage("Animal60px.png");
  }

  if(inheritedSenseDistance != -1) {
   senseAreaEnabled = true;
   setSenseDistance(inheritedSenseDistance);
   setSenseArea();
  } else {
   senseAreaEnabled = false;
  }

  if(inheritedSpeed != -1) {
   speedEnabled = true;
   setSpeed(inheritedSpeed);
  } else {
   speedEnabled = false;
   setSpeed(startSpeed); // starting speed value
  }

 }

 public void setPosition(double x, double y) {
  super.setPosition(x, y);
  if(senseAreaEnabled) {
   senseArea.setPosition(getPositionX() - senseDistance, getPositionY() - senseDistance);
  }
 }

 public void setVelocity(double x, double y) {
  super.setVelocity(x, y);
  if(senseAreaEnabled) {
   senseArea.setVelocity(x, y);
  }
 }

 public void addVelocity(double x, double y) {
  super.addVelocity(x, y);
  if(senseAreaEnabled) {
   senseArea.addVelocity(x, y);
  }
 }

 public void update(double time) {
  super.update(time);
  if(senseAreaEnabled) {
   senseArea.update(time);
  }
 }

 public void render(GraphicsContext gc) {
  super.render(gc);
  if(senseAreaEnabled) {
   gc.strokeRect(senseArea.getPositionX(), senseArea.getPositionY(), senseArea.getWidth(), senseArea.getHeight());
  }
 }

 public void setCurrentEnergy(double value) {
  currentEnergy = value;
 }

 public double getCurrentEnergy() {
  return currentEnergy;
 }

 public double getMaxEnergy() {
  return maxEnergy;
 }

 public void setFoodGathered(int value) {
  foodGathered = value;
 }

 public void collectFood(){
  foodGathered++;
 }

 public boolean hasEaten() {
  return foodGathered >= 1;
 }

 public boolean hasSurplus() {
  return foodGathered > 1;
 }

 public Animal reproduce(){ // haha nice
  // Use Math.Random for random traits in offspring
  Animal child;
  double senseDistanceGene;
  double speedGene;
  double sizeGene;
  if(senseAreaEnabled) {
   senseDistanceGene = getSenseDistance() + (Math.random() * 10) - 5;
   if(senseDistanceGene < minSenseDistance) {
    senseDistanceGene = minSenseDistance;
   }
   if(senseDistanceGene > maxSenseDistance) {
    senseDistanceGene = maxSenseDistance;
   }
  } else senseDistanceGene = -1;

  if(speedEnabled) {
   speedGene = getSpeed() + (Math.random() * 20) - 10;
   if(speedGene < minSpeed) {
    speedGene = minSpeed;
   }
   if(speedGene > maxSpeed) {
    speedGene = maxSpeed;
   }
  } else speedGene = -1;

  if(sizeEnabled) {
   sizeGene = getWidth() + (Math.random() + 20) - 10;
   if(sizeGene < minSize) {
    sizeGene = minSize;
   }
   if(sizeGene > maxSize) {
    sizeGene = maxSize;
   }
  } else sizeGene = -1;
  child = new Animal(getMaxEnergy(), speed, size, senseDistanceGene, speedGene, sizeGene);
  child.setMinSenseDistance(getMinSenseDistance());
  child.setMaxSenseDistance(getMaxSenseDistance());
  child.setMinSpeed(getMinSpeed());
  child.setMaxSpeed(getMaxSpeed());
  child.setMinSize(getMinSize());
  child.setMaxSize(getMaxSize());
  return child;
 }

 
 public double getSpeed(){
  return speed;
 }

 public void setSpeed(double value){
  speed = value;
 }

 public void setMinSpeed(double minimum) {
  minSpeed = minimum;
 }

 public double getMinSpeed() {
  return minSpeed;
 }

 public void setMaxSpeed(double maximum) {
  maxSpeed = maximum;
 }

 public double getMaxSpeed() {
  return maxSpeed;
 }

 public double getSenseDistance() {
  return senseDistance;
 }

 public void setSenseDistance(double distance) {
  senseDistance = distance;
 }

 public double getMinSenseDistance() {
  return minSenseDistance;
 }

 public void setMinSenseDistance(double minimum) {
  minSenseDistance = minimum;
 }

 public double getMaxSenseDistance() {
  return maxSenseDistance;
 }

 public void setMaxSenseDistance(double maximum) {
  maxSenseDistance = maximum;
 }

 public Sprite getSenseArea() {
  return senseArea;
 }

 public void setSenseArea() {
  double distance = getSenseDistance();
  senseArea = new Sprite();
  senseArea.setHeight(getHeight() + 2 * distance);
  senseArea.setWidth(getWidth() + 2 * distance);
 }

 public double getMinSize() {
  return minSize;
 }

 public void setMinSize(double minimum) {
  minSize = minimum;
 }

 public double getMaxSize() {
  return maxSize;
 }

 public void setMaxSize(double maximum) {
  maxSize = maximum;
 }

 public void dayCompleted() {
  finished = true;
 }

 public void resetForNewDay() {
  finished = false;
  foodGathered = 0;
  currentEnergy = getMaxEnergy();
  setDestination(-1, -1);
 }

 public boolean isFinished() {
  return finished;
 }

 public void setDestination(double X, double Y) {
  currentDestination.setPosition(X, Y);
 }

 public Sprite getDestination() {
  if(currentDestination.getPositionX() == -1 && currentDestination.getPositionY() == -1) return null;
  return currentDestination;
 }

 public void invertVector(double[] vector) {
  vector[0] *= -1;
  vector[1] *= -1;
 }

 public double[] unitVector(double[] vector) {
  double[] unitVector = vector;
  double magnitude = Math.sqrt((unitVector[0]*unitVector[0]) + (unitVector[1]*unitVector[1]));
  unitVector[0] = unitVector[0] / magnitude;
  unitVector[1] = unitVector[1] / magnitude;
  return unitVector;
 }

 public double[] scaledRandomVector() {
  // random point on the circumference of a circle with a radius of 10 + the child's width
  double scalar = this.getWidth() + 10;
  double X = Math.random() * scalar;
  double Y = Math.sqrt((scalar*scalar) - (X * X)); // y=sqrt(1-x^2) == x^2 + y^2 = scalar^2

  // determine quadrant; is quadrant 1 if quadrant > 3/4
  double quadrant = Math.random();
  if(quadrant <= 0.25) {
   Y *= -1; // quadrant 4
  }
  else if(quadrant > 0.25 && quadrant <= 0.5) {
   X *= -1; // quadrant 3
   Y *= -1;
  }
  else if(quadrant > 0.5 && quadrant <= 0.75) {
   X *= -1; // quadrant 2
  }

  // return random angled vector of magnitude child's size + 10
  double vector[] = {X, Y};
  return vector;
 }

 public double[] fleeVector(Animal predator) {
  double[] vector = {predator.getPositionX() - getPositionX(), predator.getPositionY() - getPositionY()};
  vector = unitVector(vector);
  invertVector(vector);
  return vector;
 }

 public double[] approachFoodVector(Sprite food) {
  double[] vector = {food.getPositionX() - getPositionX(), food.getPositionY() - getPositionY()};
  vector = unitVector(vector);
  return vector;
 }

 public double[] returnHomeVector(Rectangle2D worldBounds) {
  double X = this.getPositionX();
  double Y = this.getPositionY();

  double shortestDistance = X - worldBounds.getMinX(); // north
  double vector[] = {-1, 0};
  if(worldBounds.getMaxX() - X < shortestDistance) { // south
   shortestDistance = worldBounds.getMaxX() - X;
   vector[0] = 1;
   vector[1] = 0;
  }
  if(Y - worldBounds.getMinY() < shortestDistance) { // east
   shortestDistance = Y - worldBounds.getMinY();
   vector[0] = 0;
   vector[1] = -1;
  }
  if(worldBounds.getMaxY() - Y < shortestDistance) { // west
   shortestDistance = worldBounds.getMaxY() - Y;
   vector[0] = 0;
   vector[1] = 1;
  }
  
  return vector;
 }

}
