# Autonomous-Robots-Ex3
Drone simulator 


This is the 3rd assignment in Autonomous Robots course in Ariel University.
In this exercise I've improve the next project : https://github.com/vection/DroneSimulator

The project simulates an autonomous drone flying within a structure parallel to the mapping of the area.
The drone maneuvers , directs and avoids hitting the walls by using three lidar sensors and a gyro sensor.
For each X meters the drone will placemark his current spot and set it as a new vertex in the graph which represent the drone journey.

The improvements:

1. Suspect points - a suspect point is a place in the map which potentially  has a path to unexplored area. 
These special placemarks determined by whether or not one of the sidal lidar sensors hasn't touched the walls.

2. Speed control - the drone is able to fly 3 times faster than the original version. When the front lidar is free - it speeds up and slows down gradually
when detecting a wall.

3. Energency escape - if the drone is in a collision course in the wall it should turn away gradually. But in case it won't turn away(could be due to lidar noise)
the drone has an ability to turn away immediately if it in a danger spot. Also , in case the drone is stuck for some reason , the drone can rescue itself by turning in a 90 degrees.

4. Shortest path back home - by adding edges between closest points during the flight I use the graph to determine the shortest path to the start point.
In the original simulation the drone simply gone to the last point visited - i found it inefficient. After clicking "return home"
button it uses Dijkstra algorithm  on the graph to set the way back home.

5. Drone directing - in order to get the drone to the relevant point it will maneuver by changing the angel between it the the next point.


Instructions : 
1. Download the project
2. Change the path on SimulationWindow (line 319)
3. Click on "toogle AI"
