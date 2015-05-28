package m10316006;
//count two points distance
public class TwoPointDistance {
 static class MyPoint {
  double x, y;


  MyPoint(double x, double y) {
   this.x = x; this.y = y;
  }


  double distance(MyPoint p) {
   return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
  }
 }


 public static void main(String[] args) {
  MyPoint p1 = new MyPoint(1, 2);
  MyPoint p2 = new MyPoint(3, 4);
  System.out.println("Distance: " + p1.distance(p2));
 }
}