/* Copyright 2009-2015 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package tsplib;

import java.util.Random;

/**
 * Implementation of the 2-opt heuristic for the traveling salesman problem.
 * The 2-opt heuristic searches for any two edges in a tour that can be
 * rearranged to produce a shorter tour.  For example, a tour with any edges
 * that intersect can be shortened by removing the intersection.
 */
public class TSPGreedyHeuristic {
	
	/**
	 * The traveling salesman problem instance.
	 */
	private final TSPInstance instance;

	/**
	 * Constructs a new 2-opt heuristic for the specified traveling salesman
	 * problem instance.
	 * 
	 * @param instance the traveling salesman problem instance
	 */
	public TSPGreedyHeuristic(TSPInstance instance) {
		super();
		this.instance = instance;
	}
	
	/**
	 * Applies the Greedy Algorithm heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the Greedy Algorithm heuristic
	 */
	public void apply(Tour tour) {
		DistanceTable distanceTable = instance.getDistanceTable();
		
		// tours with 2 or fewer nodes are already optimal
		if (tour.size() < 3) {
			return;
		}
			for (int i = 0; i < tour.size()-1; i++) {
					double d1 = distanceTable.getDistanceBetween(tour.get(i), tour.get(i+1));
					double d2 = distanceTable.getDistanceBetween(tour.get(i), tour.get(i+2));
					
					// if distance can be shortened, adjust the tour
					if (d2 < d1) {
						tour.reverse(i+1, i+2);
					}
			}
	}
	
	/**
	 * Applies the Greedy Algorithm heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the Greedy Algorithm heuristic
	 */
	public void applyold(Tour tour) {
		DistanceTable distanceTable = instance.getDistanceTable();
		int toursize = tour.size();
	    int startingPoint = new Random().nextInt(toursize);  //Random
	     
		//double dmax = 0;
		//double dmaxsum = 0;
		//int smaxindes = 0;
		//int emaxindes = 0;

		//for (int i = 0; i < toursize-1; i++) {
		//	for (int j = i+1; j < toursize; j++) {
		//		double distance = distanceTable.getDistanceBetween(tour.get(i), tour.get(j));
		//		if (distance < dmax){
		//			dmax = distance;
		//			smaxindes = i;
		//			emaxindes = j;
		//		}
		//	}
		//}
		//tour.reverse(0, smaxindes);
		//tour.reverse(1, emaxindes);
	    tour.reverse(0, startingPoint);
        for(int i = 2; i < toursize; i++){
                double minCost = -1;
                int index = 0;
                for(int j = i; j < toursize; j++){
                	double distance = distanceTable.getDistanceBetween(tour.get(i-1), tour.get(j));
                        if(distance < minCost || minCost == -1){
                           minCost = distance;
                           index = j;                               
                        }
                }
                tour.reverse(i, index);
        }
        //dmaxsum = tour.distance(instance);
        tour.reverse(0, 1);
        //for(int i = 2; i < toursize; i++){
        //    double minCost = -1;
        //    int index = 0;
        //    for(int j = i; j < toursize; j++){
        //    	double distance = distanceTable.getDistanceBetween(tour.get(i-1), tour.get(j));
        //            if(distance < minCost || minCost == -1){
        //               minCost = distance;
        //               index = j;                               
        //            }
        //    }
        //    tour.reverse(i, index);
        //}
        
        //if (dmaxsum < tour.distance(instance))
        //	tour.reverse(0, 1);
        
        
		
	}
}
