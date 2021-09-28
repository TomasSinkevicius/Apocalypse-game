
import java.awt.*;
import java.awt.geom.*;

public class PlayerSprite {
    
    private double x, y, size;
    private Color color;
    
    public PlayerSprite(double a, double b, double s, Color c){
        x = a;
        y = b;
        size = s;
        color = c;
    }
    
    public void drawSprite(Graphics2D g2d){
        Rectangle2D.Double square = new Rectangle2D.Double(x,y, size, size);
        g2d.setColor(color);
        g2d.fill(square);
    }
    
    public void moveH(double n){
        x += n;
    }
    
    public void moveV(double n){
        y += n;
    }
    
    public void setX(double n){
        x = n;
    }
    
    public void setY(double n){
        y = n;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
}
