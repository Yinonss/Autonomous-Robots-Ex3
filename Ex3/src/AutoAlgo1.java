

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AutoAlgo1 {
	
	int map_size = 3000;
	enum PixelState {blocked,explored,unexplored,visited};
	PixelState map[][];
	Drone drone;
	
	Point droneStartingPoint;
	
	
	ArrayList<Point> points;
	
	
	int isRotating;
	ArrayList<Double> degrees_left;
	ArrayList<Func> degrees_left_func;
	
	boolean isSpeedUp = false;
	
	Graph mGraph = new Graph();
	ArrayList<Point> suspectedPoints = new ArrayList<Point>();
	
	CPU ai_cpu;
	public AutoAlgo1(Map realMap) {
		degrees_left = new ArrayList<>();
		degrees_left_func =  new ArrayList<>();
		points = new ArrayList<Point>();
		
		drone = new Drone(realMap);
		drone.addLidar(0);
		drone.addLidar(90);
		drone.addLidar(-90);

		
		initMap();
		
		isRotating = 0;
		ai_cpu = new CPU(200,"Auto_AI");
		ai_cpu.addFunction(this::update);
	}
	
	public void initMap() {
		map = new PixelState[map_size][map_size];
		for(int i=0;i<map_size;i++) {
			for(int j=0;j<map_size;j++) {
				map[i][j] = PixelState.unexplored;
			}
		}
		
		droneStartingPoint = new Point(map_size/2,map_size/2);
	}
	
	public void play() {
		drone.play();
		ai_cpu.play();
	}

	
	public void update(int deltaTime) {
		updateVisited();
		updateMapByLidars();
		
		ai(deltaTime);
		
		
		if(isRotating != 0) {
			updateRotating(deltaTime);
		}
		if(isSpeedUp) {
			drone.speedUp(deltaTime);
		} else {
			drone.slowDown(deltaTime);
		}
		
	}
	
	public void speedUp() {
		isSpeedUp = true;
	}
	
	public void speedDown() {
		isSpeedUp = false;
	}
	
	public void updateMapByLidars() {
		Point dronePoint = drone.getOpticalSensorLocation();
		Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x,dronePoint.y + droneStartingPoint.y);
		
		for(int i=0;i<drone.lidars.size();i++) {
			Lidar lidar = drone.lidars.get(i);
			double rotation = drone.getGyroRotation() + lidar.degrees;
			//rotation = Drone.formatRotation(rotation);
			for(int distanceInCM=0;distanceInCM < lidar.current_distance;distanceInCM++) {
				Point p = Tools.getPointByDistance(fromPoint, rotation, distanceInCM);
				setPixel(p.x,p.y,PixelState.explored);
			}
			
			if(lidar.current_distance > 0 && lidar.current_distance < WorldParams.lidarLimit - WorldParams.lidarNoise) {
				Point p = Tools.getPointByDistance(fromPoint, rotation, lidar.current_distance);
				setPixel(p.x,p.y,PixelState.blocked);
				//fineEdges((int)p.x,(int)p.y);
			}
		}
	}
	
	public void updateVisited() {
		Point dronePoint = drone.getOpticalSensorLocation();
		Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x,dronePoint.y + droneStartingPoint.y);
		setPixel(fromPoint.x,fromPoint.y,PixelState.visited);
			
	}
	
	public void setPixel(double x, double y,PixelState state) {
		int xi = (int)x;
		int yi = (int)y;
		
		if(state == PixelState.visited) {
			map[xi][yi] = state; 
			return;
		}
		
		if(map[xi][yi] == PixelState.unexplored) {
			map[xi][yi] = state; 
		}
	}
	/*
	
	public void fineEdges(int x,int y) {
		int radius = 6;
		
		for(int i=y-radius;i<y+radius;i++) {
			for(int j=x-radius;j<x+radius;j++) {
				if(Math.abs(y-i) <= 1 && Math.abs(x-j) <= 1) {
					continue;
				}
				if(map[i][j] == PixelState.blocked) {
					blockLine(x,y,j,i);
				}
			}
		}
	}
	*/
	/*
	public void blockLine(int x0,int y0,int x1,int y1) {
		if(x0 > x1) {
			int tempX = x0;
			int tempY = y0;
			x0 = x1;
			y0 = y1;
			x1 = tempX;
			y1 = tempY;
		}
		
	     double deltax = x1 - x0;
	     double deltay = y1 - y0;
	     double deltaerr = Math.abs(deltay / deltax);    // Assume deltax != 0 (line is not vertical),
	     double error = 0.0; // No error at start
	     int y = y0;
	     for (int x=x0;x<x1;x++) {
	    	 setPixel(x,y,PixelState.blocked);
	         error = error + deltaerr;
	         if( 2*error >= deltax ) {
                y = y + 1;
                error=error - deltax;
	        }
	     }
	
	}
	*/
	
	public void paintBlindMap(Graphics g) {
		Color c = g.getColor();
		
		int i = (int)droneStartingPoint.y - (int)drone.startPoint.x;
		int startY = i;
		for(;i<map_size;i++) {
			int j = (int)droneStartingPoint.x - (int)drone.startPoint.y;
			int startX = j;
			for(;j<map_size;j++) {
				if(map[i][j] != PixelState.unexplored)  {
					if(map[i][j] == PixelState.blocked) {
						g.setColor(Color.RED);
					} 
					else if(map[i][j] == PixelState.explored) {
						g.setColor(Color.YELLOW);
					}
					else if(map[i][j] == PixelState.visited) {
						g.setColor(Color.BLUE);
					}
					g.drawLine(i-startY, j-startX, i-startY, j-startX);
				}
			}
		}
		g.setColor(c);
	}
	
	public void paintPoints(Graphics g) {
		for(int i=0;i<points.size();i++) {
			Point p = points.get(i);
			if(suspectedPoints.contains(p))
				g.drawRect((int)p.x + (int)drone.startPoint.x - 10, (int)p.y + (int)drone.startPoint.y-10, 20, 20);
			else
			    g.drawOval((int)p.x + (int)drone.startPoint.x - 10, (int)p.y + (int)drone.startPoint.y-10, 20, 20);
		}
	
		
	}
	
	public void paint(Graphics g) {
		if(SimulationWindow.toogleRealMap) {
			drone.realMap.paint(g);
		}
		
		paintBlindMap(g);
		paintPoints(g);
		
		drone.paint(g);
		
		
	}
	
	boolean is_init = true;
	double lastFrontLidarDis = 0;
	boolean isRotateRight = false;
	double changedRight = 0;
	double changedLeft = 0;
	boolean tryToEscape = false;
	int leftOrRight = 1;
	

	double max_rotation_to_direction = 20;
	boolean  is_finish = true;
	boolean isLeftRightRotationEnable = true;
	
	
	boolean is_risky = false;
	int max_risky_distance = 150;
	boolean try_to_escape = false;
	double  risky_dis = 0;
	int max_angle_risky = 10;
	
	boolean is_lidars_max = false;
	
	double save_point_after_seconds = 3;
	
	double max_distance_between_points = 100;
	
	boolean stuck = false;
	int waiting = 0;
	
	boolean start_return_home = false;
	boolean return_home_maneuver = false;
	boolean return_suspect_maneuver = false;
    boolean return_suspect = false;
	
	int seconds = 0;
	int last_suspect_deletion = -3;
	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		public void run() {
		    // Your database code here
			seconds++;
			//System.out.println(seconds);
		  }
		};
	Point init_point;
	public void ai(int deltaTime) {
		if(!SimulationWindow.toogleAI) {
			return;
		}
	
		
		if(is_init) {
			speedUp();
			Point dronePoint = drone.getOpticalSensorLocation();
			init_point = new Point(dronePoint);
			points.add(dronePoint);
			mGraph.addVertex(dronePoint);
			is_init = false;
			
			timer.scheduleAtFixedRate(task, 1000, 1000);
		}
		
		if(isLeftRightRotationEnable) {
			//doLeftRight();
		}
		
		
		Point dronePoint = drone.getOpticalSensorLocation();

		
		if(SimulationWindow.return_home) {
			if(!return_home_maneuver) {
			   this.drone.escapeManeuver(180);
			   return_home_maneuver = true;
			}
			if( Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) <  max_distance_between_points/5) {
				if(points.size() <= 1 && Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) <  max_distance_between_points/5) {
					speedDown();
				} else {
					removeLastPoint();
				}
				if(points.size() == 0)
					this.drone.stopDrone();
			}
		}
		// Reach to the last suspected point and then continue
		else if(return_suspect) {
			if(!return_suspect_maneuver) {
				   this.drone.escapeManeuver(180);
				   return_suspect_maneuver = true;
				}
			System.out.println(Tools.getDistanceBetweenPoints(this.suspectedPoints.get(suspectedPoints.size()-1), dronePoint) +"  ,  " + max_distance_between_points/5);
			//if(getLastPoint().equals(this.suspectedPoints.get(suspectedPoints.size()-1))) {
				//System.out.println("next up - suspect point");
			if(Tools.getDistanceBetweenPoints(this.suspectedPoints.get(suspectedPoints.size()-1), dronePoint) <  max_distance_between_points/5) {
				System.out.println("reached to the most recent suspect point");
				suspectedPoints.remove(suspectedPoints.size()-1);
				System.out.println("point deleted - changing to false");
				last_suspect_deletion = seconds;
				return_suspect = false;
				return_suspect_maneuver = false;
				
				}
			//}
			//if(!points.contains(this.suspectedPoints.get(suspectedPoints.size()-1)))
				//return_suspect = false;
			else if( Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) <  max_distance_between_points/5) {
				System.out.println("i got here instead");
				if(points.size() <= 1 && Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) <  max_distance_between_points/10) {
					speedDown();
				} else {
					removeLastPoint();
				}
				
			}
		}
		else {//change points on the map here
			if( Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) >=  max_distance_between_points && last_suspect_deletion + 2 < seconds) {
		
				points.add(dronePoint);
				mGraph.addVertex(dronePoint);
				if(this.drone.lidars.get(1).current_distance > 299 && !return_suspect || this.drone.lidars.get(2).current_distance > 299 && !return_suspect) {
					suspectedPoints.add(dronePoint);
				    //System.out.println("a suspect point!");	
				}
			}
		}
	
		
		
		if(!is_risky) {
			Lidar lidar = drone.lidars.get(0);
			if(lidar.current_distance <= max_risky_distance * 1.5 ) {
				is_risky = true;
				risky_dis = lidar.current_distance;
				
			}
			
			
			Lidar lidar1 = drone.lidars.get(1);
			if(lidar1.current_distance <= max_risky_distance/6 ) {
				is_risky = true;
			}
			
			Lidar lidar2 = drone.lidars.get(2);
			if(lidar2.current_distance <= max_risky_distance/6 ) {
				is_risky = true;
			}
			
			if(lidar.current_distance > 300)
				isSpeedUp = true;
			
		} else {
			if(!try_to_escape) {
				try_to_escape = true;
				Lidar lidar1 = drone.lidars.get(1);
				double a = lidar1.current_distance;
				
				Lidar lidar2 = drone.lidars.get(2);
				double b = lidar2.current_distance;
				
				Lidar lidar0 = drone.lidars.get(0);
				double c = lidar0.current_distance;
				
				int spin_by;
				if(SimulationWindow.return_home || return_suspect)
					spin_by = 4;
				else
					spin_by = 2;

			    if(c < 150 && c != 0) {
			    	isSpeedUp = false;
			    	
			    }
			    else
			    	speedUp();
			    
			    if(this.drone.getSpeed() == 0 && seconds > 0) {  // if stuck
					waiting++;
					if(waiting > 100) {
						//System.out.println("rotating left");
						this.drone.escapeManeuver(45);
						waiting = 0;
						speedUp();
					}
				}
			  //  System.out.println("lidar : "+c);
			    if(c < 30 && c > 0 && seconds > 0) {
			    	//System.out.println("emergency escape");
			    	this.drone.stopDrone();
			    	this.drone.escapeManeuver(60);
			    	speedUp();
			    }
			    
				if(a > 270 && b > 270) {
					is_lidars_max = true;
					Point l1 = Tools.getPointByDistance(dronePoint, lidar1.degrees + drone.getGyroRotation(), lidar1.current_distance);
					Point l2 = Tools.getPointByDistance(dronePoint, lidar2.degrees + drone.getGyroRotation(), lidar2.current_distance);
					Point last_point = getAvgLastPoint();
					double dis_to_lidar1 = Tools.getDistanceBetweenPoints(last_point,l1);
					double dis_to_lidar2 = Tools.getDistanceBetweenPoints(last_point,l2);
					
					if(SimulationWindow.return_home) {
						if( Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) <  max_distance_between_points/5) {
							removeLastPoint();
						}
					} else {
						if( Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) >=  max_distance_between_points && last_suspect_deletion + 2 < seconds) {
							points.add(dronePoint);
							mGraph.addVertex(dronePoint);
							if(this.drone.lidars.get(1).current_distance > 299 && !return_suspect || this.drone.lidars.get(2).current_distance > 299 && !return_suspect) {
								suspectedPoints.add(dronePoint);
							    //System.out.println("a suspect point!");	
							}
						}
					}
					
					if(SimulationWindow.return_home || return_suspect) {
						spin_by *= -1;						
					}
					
					
					if(dis_to_lidar1 < dis_to_lidar2) {
						spin_by *= (-1 ); 
					}
				} else {
					
					
					if(a < b ) {
					    
						spin_by *= (-1 ); 
					}
				}
				
				//System.out.println(suspectedPoints.size());
				
				spinBy(spin_by,true,new Func() { 
						@Override
						public void method() {
							try_to_escape = false;
							is_risky = false;
						}
				});
			}
		}
			
		//}
	}
	
	int counter = 0;
	
	public void doLeftRight() {
		if(is_finish) {
			leftOrRight *= -1;
			counter++;
			is_finish = false;
			
			spinBy(max_rotation_to_direction*leftOrRight,false,new Func() {
				@Override
				public void method() {
					is_finish = true;
				}
			});
		}
	}
	
	
	double lastGyroRotation = 0;
	public void updateRotating(int deltaTime) {
		
		if(degrees_left.size() == 0) {
			return;
		}
		
		double degrees_left_to_rotate = degrees_left.get(0);
		boolean isLeft = true;
		if(degrees_left_to_rotate > 0) {
			isLeft = false;
		}
		
		double curr =  drone.getGyroRotation();
		double just_rotated = 0;
		
		if(isLeft) {
			
			just_rotated = curr - lastGyroRotation;
			if(just_rotated > 0) {
				just_rotated = -(360 - just_rotated);
			}
		} else {
			just_rotated = curr - lastGyroRotation;
			if(just_rotated < 0) {
				just_rotated = 360 + just_rotated;
			}
		}
		
	
		 
		lastGyroRotation = curr;
		degrees_left_to_rotate-=just_rotated;
		degrees_left.remove(0);
		degrees_left.add(0,degrees_left_to_rotate);
		
		if((isLeft && degrees_left_to_rotate >= 0) || (!isLeft && degrees_left_to_rotate <= 0)) {
			degrees_left.remove(0);
			
			Func func = degrees_left_func.get(0);
			if(func != null) {
				func.method();
			}
			degrees_left_func.remove(0);
			
			
			if(degrees_left.size() == 0) {
				isRotating = 0;
			}
			return; 
		}
		
		int direction = (int)(degrees_left_to_rotate / Math.abs(degrees_left_to_rotate));
		drone.rotateLeft(deltaTime * direction);
		
	}
	
	public void spinBy(double degrees,boolean isFirst,Func func) {
		lastGyroRotation = drone.getGyroRotation();
		if(isFirst) {
			degrees_left.add(0,degrees);
			degrees_left_func.add(0,func);
		
			
		} else {
			degrees_left.add(degrees);
			degrees_left_func.add(func);
		}
		
		isRotating =1;
	}
	
	public void spinBy(double degrees,boolean isFirst) {
		lastGyroRotation = drone.getGyroRotation();
		if(isFirst) {
			degrees_left.add(0,degrees);
			degrees_left_func.add(0,null);
		
			
		} else {
			degrees_left.add(degrees);
			degrees_left_func.add(null);
		}
		
		isRotating =1;
	}
	
	public void spinBy(double degrees) {
		lastGyroRotation = drone.getGyroRotation();
		
		degrees_left.add(degrees);
		degrees_left_func.add(null);
		isRotating = 1;
	}
	
	public Point getLastPoint() {
		if(points.size() == 0) {
			return init_point;
		}
		
		Point p1 = points.get(points.size()-1);
		return p1;
	}
	
	public Point removeLastPoint() {
		if(points.isEmpty()) {
			return init_point;
		}
		
		return points.remove(points.size()-1);
	}
	
	
	public Point getAvgLastPoint() {
		if(points.size() < 2) {
			return init_point;
		}
		
		Point p1 = points.get(points.size()-1);
		Point p2 = points.get(points.size()-2);
		return new Point((p1.x + p2.x) /2, (p1.y + p2.y) /2);
	}
	

}
