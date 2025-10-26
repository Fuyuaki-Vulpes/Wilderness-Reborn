package com.fuyuaki.r_wilderness.world.level.levelgen.util;

import com.mojang.logging.LogUtils;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;

public record DistortionSpline(Spline points, double valueMin, double valueMax) {
    public static final org.slf4j.Logger LOGGER = LogUtils.getLogger();



    public double at(double value){

        if (points.points.getFirst().value > value){
            return valueMin;
        }
        if (points.points.getLast().value < value){
            return valueMax;
        }
        MutableInt pointA = new MutableInt(Integer.MIN_VALUE);
        MutableInt pointB = new MutableInt(Integer.MAX_VALUE);
        for (int index = 0; index < points.size() - 1; index++){
            if (points().get(index).value < value && points().get(index+1).value > value){
                if (index >= points.size() - 1){
                    pointA.setValue(index);
                }
                else {
                    pointA.setValue(index);
                    pointB.setValue(index+1);
                }
            }
        }
        if (pointA.intValue() < 0){
            return valueMin;
        }
        if ( pointB.intValue() > 9999){
            return valueMax;
        }
        double vA = points.get(pointA.intValue()).value;
        double vB = points.get(pointB.intValue()).value;
        double rA = points.get(pointA.intValue()).result;
        double rB = points.get(pointB.intValue()).result;
        double delta =
                (value - vA)
                /
                (vB - vA);

        return Mth.lerp(delta,rA,rB);
    }



    public record Point(double value, double result){

        private String toText(){
            return "Value: " + value + " // Result:" + result;
        }
    }

    public static class Spline{
        private final List<Point> points = new ArrayList<>();

        public Spline addPoint(Point point){
            if (!points.isEmpty()) {
                if (points.getLast().value > point.value) {
                    throwPointError(point);
                }
            }
            this.points.add(point);
            return this;
        }
        public Spline addPoint(double value, double result){
            Point point = new Point(value,result);
            if (!points.isEmpty()) {
                if (points.getLast().value > value) {
                    throwPointError(point);
                }
            }
            this.points.add(point);
            return this;
        }

        public int size(){
            return this.points.size();
        }
        public Point get(int index){
            return this.points.get(index);
        }

        private void throwPointError(Point point) {
            LOGGER.error("Points must be registered in ascending order. Last Point: {} New Point: {}", points.getLast().toText(), point.toText());
        }
    }

}
