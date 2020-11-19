package io.clei.java0.springdemo01;

import java.util.List;

public class Triangle implements Shape{
    private List<Point> points;

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void draw(){
        System.out.println("Drawing Triangle");
        for (Point point: points){
            System.out.println("Point = ("+point.getX()+", "+point.getY() + ")");
        }
    }

}
