import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {
 private Image image;
 private double positionX;
 private double positionY;    
 private double velocityX;
 private double velocityY;
 private double width;
 private double height;

 public Sprite() {
  positionX = 0;
  positionY = 0;
  velocityX = 0;
  velocityY = 0;
 }

 public void setImage(Image i) {
  image = i;
  width = i.getWidth();
  height = i.getHeight();
 }

 public void setImage(String filename) {
  Image i = new Image(filename);
  setImage(i);
 }

 public void setImage(String filename, double size){
  Image i = new Image(filename, size, size, true, false);
  setImage(i);
 }

 public double getWidth() {
  return width;
 }

 public double getHeight() {
  return height;
 }

 public void setWidth(double w) {
  width = w;
 }

 public void setHeight(double h) {
  height = h;
 }

 public double getPositionX() {
  return positionX;
 }

 public double getPositionY() {
  return positionY;
 }

 public void setPosition(double x, double y) {
  positionX = x;
  positionY = y;
 }

 public double getVelocityX() {
  return velocityX;
 }

 public double getVelocityY() {
  return velocityY;
 }

 public void setVelocity(double x, double y) {
  velocityX = x;
  velocityY = y;
 }

 public void addVelocity(double x, double y) {
  velocityX += x;
  velocityY += y;
 }

 public void update(double time) {
  positionX += velocityX * time;
  positionY += velocityY * time;
 }

 public void render(GraphicsContext gc) {
  gc.drawImage(image, positionX, positionY);
 }

 public Rectangle2D getBoundary() {
  return new Rectangle2D(positionX,positionY,width,height);
 }

 public boolean intersects(Sprite s) {
  return s.getBoundary().intersects( this.getBoundary() );
 }

 public boolean intersects(Rectangle2D rect) {
  return rect.intersects( this.getBoundary() );
 }
    
 public String toString() {
  return " Position: [" + positionX + "," + positionY + "]" 
  + " Velocity: [" + velocityX + "," + velocityY + "]";
 }
}
