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


/**
 * Implementation of the 3-opt heuristic for the traveling salesman problem.
 * The 2-opt heuristic searches for any two edges in a tour that can be
 * rearranged to produce a shorter tour.  For example, a tour with any edges
 * that intersect can be shortened by removing the intersection.
 */
public class TSP3OptHeuristic {
	
	/**
	 * The traveling salesman problem instance.
	 */
	private final TSPInstance instance;

	/**
	 * Constructs a new 3-opt heuristic for the specified traveling salesman
	 * problem instance.
	 * 
	 * @param instance the traveling salesman problem instance
	 */
	public TSP3OptHeuristic(TSPInstance instance) {
		super();
		this.instance = instance;
	}
	
	/**
	 * Applies the 3-opt heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the 2-opt heuristic
	 */
	public void apply(Tour tour) {
		DistanceTable distanceTable = instance.getDistanceTable();
		boolean modified = true;

		// tours with 5 or fewer nodes are already optimal
		if (tour.size() < 4) {
			return;
		}
		
		while (modified) {
			modified = false;
			
			for (int i = 0; i < tour.size(); i++) {
				for (int j = i+2; j < tour.size(); j++) {
					for (int k = j+2; k < tour.size(); j++) {
						double d1 = distanceTable.getDistanceBetween(tour.get(i), tour.get(i+1)) +
								distanceTable.getDistanceBetween(tour.get(j), tour.get(j+1)) +
								distanceTable.getDistanceBetween(tour.get(k), tour.get(k+1));
						double d2 = distanceTable.getDistanceBetween(tour.get(i), tour.get(j+1)) +
								distanceTable.getDistanceBetween(tour.get(i+1), tour.get(k)) +
								distanceTable.getDistanceBetween(tour.get(j), tour.get(k+1));
						
						// if distance can be shortened, adjust the tour
						if (d2 < d1) {
							tour.reverse(j, k);
							tour.reverse(j+1, k);
							tour.reverse(i+1, j+1);
							modified = true;
						}
					}
				}
			}
		}
	}

}
