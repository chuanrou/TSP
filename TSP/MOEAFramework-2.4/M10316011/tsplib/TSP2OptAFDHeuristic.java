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
public class TSP2OptAFDHeuristic {
	
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
	public TSP2OptAFDHeuristic(TSPInstance instance) {
		super();
		this.instance = instance;
	}
	/**
	 * Applies the Greedy Algorithm heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the Greedy Algorithm heuristic
	 */
	public void applyGreedy(Tour tour) {
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
	 * Applies the 2-opt heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the 2-opt heuristic
	 */
	public void apply2opt(Tour tour) {
		DistanceTable distanceTable = instance.getDistanceTable();
		boolean modified = true;
		
		// tours with 3 or fewer nodes are already optimal
		if (tour.size() < 4) {
			return;
		}
		
		while (modified) {
			modified = false;
			
			for (int i = 0; i < tour.size(); i++) {
				for (int j = i+2; j < tour.size(); j++) {
					double d1 = distanceTable.getDistanceBetween(tour.get(i), tour.get(i+1)) +
							distanceTable.getDistanceBetween(tour.get(j), tour.get(j+1));
					double d2 = distanceTable.getDistanceBetween(tour.get(i), tour.get(j)) +
							distanceTable.getDistanceBetween(tour.get(i+1), tour.get(j+1));
					
					// if distance can be shortened, adjust the tour
					if (d2 < d1) {
						tour.reverse(i+1, j);
						modified = true;
					}
				}
			}
		}
	}
	
	/**
	 * Applies the AFD + 2-opt heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the 2-opt heuristic
	 */
	public void apply(Tour tour) {
		DistanceTable distanceTable = instance.getDistanceTable();
		boolean modified = true;
		int toursize = tour.size();
		Tour tour1 = new Tour();
		tour1.fromArray(toursize);
		for (int i = 0; i < toursize; i++) {
			tour1.reverse(i, tour.get(i));
		}
		// Applies the AFD heuristic to the specified tour.
		//歸屬關係可用下式表示。
		//Mij=1-(D(ni,nj)/D(ni,nl))
		//Mij：表示基準節點i 與其它節點j 的歸屬度。
		//D：表示節點i 與節點ｊ之間的歐幾里德距離(Euclidean distance)。
		//nl：表示離基準點最遠的節點。
		//依此規則分別將每一節點與其它節點的距離關係定義為歸屬度
		
		double[][] dmaxsum; //動態門檻值
		dmaxsum = new double[toursize][4];
		double[][][] dArray; //歸屬度表
		dArray = new double[toursize][toursize][3];
		
		//節點歸屬度
		//依據節點與該節點之最遠節點為建立歸屬度依據，定義各節點與其它節點的歸屬度，
		//並編製成歸屬度表(dArray)，每一節點皆有各自的歸屬度對應表，以做為動態適應性調整的依據。
		for (int i = 0; i < toursize; i++) {
			for (int j = 0; j < toursize; j++) {
				dArray[i][j][0] = distanceTable.getDistanceBetween(tour.get(i), tour.get(j));
				if (dmaxsum[i][0] < dArray[i][j][0])
					dmaxsum[i][0] = dArray[i][j][0]; //表示離基準點最遠的節點
			}
			dmaxsum[i][1] = 0;
			double dmaxsum1 = 0;
			for (int j = 0; j < toursize; j++) {
				if (dmaxsum[i][0]== 0)  //表示離基準點最遠的節點距離 = 0
				   dArray[i][j][1] = 1; //表示基準點歸屬度 = 1
				else
				{
				   dArray[i][j][1] = 1 - (dArray[j][1][0]/dmaxsum[i][0]); //表示基準點歸屬度 Mij=1-(D(ni,nj)/D(ni,nl))
				   if (dArray[i][j][1] > dmaxsum[i][1]){ //取節點的下限值	   
					   if (dArray[i][j][1] > dmaxsum1)
					   {
						   if (dmaxsum1 == 0)
							   dmaxsum[i][1] = dArray[i][j][1];
						   else
						       dmaxsum[i][1] = dArray[i][j][1];
					       dmaxsum1 = dArray[i][j][1];
					   }
					   else{
						   dmaxsum[i][1] = dArray[i][j][1];  
					   }
					   dmaxsum[i][2] = dmaxsum1;  //取節點的上限值
				   }  
				}
			}
		}
		double dLowAFD = 1; //各節點共用下限值
		for (int i = 0; i < toursize; i++) {
			if (dLowAFD > dmaxsum[i][1]){ //取共用節點的最小下限值	   
				dLowAFD = dmaxsum[i][1];
			   }
		}
		// tours with 3 or fewer nodes are already optimal
		if (tour.size() < 4) {
			return;
		}
		
		//在基因配對模式中，我們採用歸屬度來決定基因配對的模式，其基因重組步驟類
		//似於均勻交配法，然而本研究所採用的模式並非一定繼承親代的基因，基因交換特性
		//趨向於「最佳化導向的基因演算法」，本研究的基因交配模式如下：
		//一、隨機定義一節點做為起始點，並比較親代中該節點的連接點。
		//二、若親代中連接節點的歸屬度符合本次挑選的門檻，則直接將親代中下一連接
		//點加入。
		//三、否則就在符合挑選門檻的節點中以隨機方式取出節點加入。
		//四、若以隨機方式無法在符合歸屬條件的節點中找到可用節點，則將最接近的可
		//用節點加入，成為下一連接點。
		
		modified = false;
		int nextn = -1;   //最大歸屬度節點
		int crosn = -1;   //最後節點
		double maxdf = 0; //最大歸屬度
		double md = 0.9;  //動態門檻值
		Random rand = new Random();
		int  n = rand.nextInt(tour.size()-1);  //隨機定義一節點做為起始點
		tour.reverse(0, tour1.get(n));         //隨機定義節點取值
		if (md > dmaxsum[n][2]){               //動態門檻值設定
			   md = dmaxsum[n][2];
		}
		else{
			if (md < dLowAFD)
				md =  dLowAFD;
		}
		dmaxsum[n][3] = 1;
		for (int i = 1; i < tour.size(); i++) {
			for (int j = 0; j < tour.size(); j++) {
				if (dmaxsum[j][3]==0){ //已取節點判別
					if (maxdf < dArray[n][j][1]){
					    maxdf = dArray[n][j][1];
					    nextn = j;
					}
					else{
						crosn = j; //最後節點
					}
					modified = true;
				}
			}
			if (modified){
			   if (maxdf >= dLowAFD){    //判別歸屬度是否符合本次挑選的門檻
				   tour.reverse(i, tour1.get(nextn)); //節點加入
				   dmaxsum[nextn][3] = 1; 
				   n = nextn;
			   }
			   else{                    //未符合本次挑選的門檻
				   tour.reverse(i, tour1.get(crosn)); //將最接近的可用節點加入
				   dmaxsum[crosn][3] = 1;
				   n = crosn;
			   }
			   modified = false;
			}
		}
		apply2opt(tour);    //2-Opt Heuristic
		//applyGreedy(tour);
	}
	
	/**
	 * Applies the AFD + 2-opt heuristic to the specified tour.
	 * 
	 * @param tour the tour that is modified by the 2-opt heuristic
	 */
	public void applyold(Tour tour) {
		DistanceTable distanceTable = instance.getDistanceTable();
		boolean modified = true;
		
		// Applies the AFD heuristic to the specified tour.
		//歸屬關係可用下式表示。
		//Mij=1-(D(ni,nj)/D(ni,nl))
		//Mij：表示基準節點i 與其它節點j 的歸屬度。
		//D：表示節點i 與節點ｊ之間的歐幾里德距離(Euclidean distance)。
		//nl：表示離基準點最遠的節點。
		//依此規則分別將每一節點與其它節點的距離關係定義為歸屬度
		
		int toursize = tour.size();
		double[][] dmaxsum; //動態門檻值
		dmaxsum = new double[toursize][2];
		double[][][] dArray; //歸屬度表
		dArray = new double[toursize][toursize][3];
		
		//節點歸屬度
		//依據節點與該節點之最遠節點為建立歸屬度依據，定義各節點與其它節點的歸屬度，
		//並編製成歸屬度表(dArray)，每一節點皆有各自的歸屬度對應表，以做為動態適應性調整的依據。
		for (int i = 0; i < toursize; i++) {
			for (int j = 0; j < toursize; j++) {
				dArray[i][j][0] = distanceTable.getDistanceBetween(tour.get(i), tour.get(j));
				if (dmaxsum[i][0] < dArray[i][j][0])
					dmaxsum[i][0] = dArray[i][j][0]; //表示離基準點最遠的節點
				dmaxsum[i][1] += dArray[i][j][0];    //表示節點的總距離
			}
			for (int j = 0; j < toursize; j++) {
				if (dmaxsum[i][0]== 0)  //表示離基準點最遠的節點距離 = 0
				   dArray[i][j][1] = 1; //表示基準點歸屬度 = 1
				else
				   dArray[i][j][1] = 1 - (dArray[j][1][0]/dmaxsum[i][0]); //表示基準點歸屬度 Mij=1-(D(ni,nj)/D(ni,nl))
				
				if (dmaxsum[i][1]== 0)  //表示基準點總距離=0
				   dArray[j][1][2] = 1;	//表示基準點總歸屬度
				else
				   dArray[i][j][2] = 1 - (dArray[j][1][0]/dmaxsum[i][1]);	//表示總距離歸屬度 Mij=1-(D(ni,nj)/D(ni,nl))
			}
		}

		// tours with 3 or fewer nodes are already optimal
		if (tour.size() < 4) {
			return;
		}
		
		while (modified) {
			modified = false;
			boolean modifiedj = false;
			int inowp = 0;
			int jnowp = 0;
			double d1 = 0;
			double d2 = 0;
			double md = 0.9;
			for (int i = 0; i < tour.size(); i++) {
				modifiedj = true;
				while (modifiedj){
					modifiedj = (md != 0);
				for (int j = i+2; j < tour.size(); j++) {
					d1 = distanceTable.getDistanceBetween(tour.get(i), tour.get(i+1)) +
							distanceTable.getDistanceBetween(tour.get(j), tour.get(j+1));
					d2 = distanceTable.getDistanceBetween(tour.get(i), tour.get(j)) +
							distanceTable.getDistanceBetween(tour.get(i+1), tour.get(j+1));
					if (i+1 == tour.size())
						inowp = 0;
					else
						inowp = i+1;	
					
					if (j+1 == tour.size())
						jnowp = 0;
					else
						jnowp = j+1;		
					// if distance can be shortened, adjust the tour
					//if ((d2 < d1) && (dArray[i][j][1] + dArray[i+1][j+1][1] >= 1.2)){
					if ((d2 < d1) && ((dArray[i][j][1] + dArray[inowp][jnowp][1]) > md)){
					//if (d2 < d1){
						tour.reverse(i+1, j);
						modifiedj = false;
						modified = true;
					}
				}
				if (modifiedj){
					md = 0;
				}
				else{
					modifiedj = false;
				}		
				}
			}
		}
		//apply2opt(tour);
	}
}
