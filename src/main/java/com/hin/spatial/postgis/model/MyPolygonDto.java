package com.hin.spatial.postgis.model;

import lombok.Data;

@Data
public class MyPolygonDto {
    private double lowerLeftX;
    private double lowerLeftY;
    private double upperRightX;
    private double upperRightY;
}
