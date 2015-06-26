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
		//�k�����Y�i�ΤU����ܡC
		//Mij=1-(D(ni,nj)/D(ni,nl))
		//Mij�G��ܰ�Ǹ`�Ii �P�䥦�`�Ij ���k�ݫסC
		//D�G��ܸ`�Ii �P�`�I�򤧶����ڴX���w�Z��(Euclidean distance)�C
		//nl�G���������I�̻����`�I�C
		//�̦��W�h���O�N�C�@�`�I�P�䥦�`�I���Z�����Y�w�q���k�ݫ�
		
		double[][] dmaxsum; //�ʺA���e��
		dmaxsum = new double[toursize][4];
		double[][][] dArray; //�k�ݫת�
		dArray = new double[toursize][toursize][3];
		
		//�`�I�k�ݫ�
		//�̾ڸ`�I�P�Ӹ`�I���̻��`�I���إ��k�ݫר̾ڡA�w�q�U�`�I�P�䥦�`�I���k�ݫסA
		//�ýs�s���k�ݫת�(dArray)�A�C�@�`�I�Ҧ��U�۪��k�ݫ׹�����A�H�����ʺA�A���ʽվ㪺�̾ڡC
		for (int i = 0; i < toursize; i++) {
			for (int j = 0; j < toursize; j++) {
				dArray[i][j][0] = distanceTable.getDistanceBetween(tour.get(i), tour.get(j));
				if (dmaxsum[i][0] < dArray[i][j][0])
					dmaxsum[i][0] = dArray[i][j][0]; //���������I�̻����`�I
			}
			dmaxsum[i][1] = 0;
			double dmaxsum1 = 0;
			for (int j = 0; j < toursize; j++) {
				if (dmaxsum[i][0]== 0)  //���������I�̻����`�I�Z�� = 0
				   dArray[i][j][1] = 1; //��ܰ���I�k�ݫ� = 1
				else
				{
				   dArray[i][j][1] = 1 - (dArray[j][1][0]/dmaxsum[i][0]); //��ܰ���I�k�ݫ� Mij=1-(D(ni,nj)/D(ni,nl))
				   if (dArray[i][j][1] > dmaxsum[i][1]){ //���`�I���U����	   
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
					   dmaxsum[i][2] = dmaxsum1;  //���`�I���W����
				   }  
				}
			}
		}
		double dLowAFD = 1; //�U�`�I�@�ΤU����
		for (int i = 0; i < toursize; i++) {
			if (dLowAFD > dmaxsum[i][1]){ //���@�θ`�I���̤p�U����	   
				dLowAFD = dmaxsum[i][1];
			   }
		}
		// tours with 3 or fewer nodes are already optimal
		if (tour.size() < 4) {
			return;
		}
		
		//�b��]�t��Ҧ����A�ڭ̱ĥ��k�ݫרӨM�w��]�t�諸�Ҧ��A���]���ըB�J��
		//���󧡤å�t�k�A�M�ӥ���s�ұĥΪ��Ҧ��ëD�@�w�~�ӿ˥N����]�A��]�洫�S��
		//�ͦV��u�̨ΤƾɦV����]�t��k�v�A����s����]��t�Ҧ��p�U�G
		//�@�B�H���w�q�@�`�I�����_�l�I�A�ä���˥N���Ӹ`�I���s���I�C
		//�G�B�Y�˥N���s���`�I���k�ݫײŦX�����D�諸���e�A�h�����N�˥N���U�@�s��
		//�I�[�J�C
		//�T�B�_�h�N�b�ŦX�D����e���`�I���H�H���覡���X�`�I�[�J�C
		//�|�B�Y�H�H���覡�L�k�b�ŦX�k�ݱ��󪺸`�I�����i�θ`�I�A�h�N�̱��񪺥i
		//�θ`�I�[�J�A�����U�@�s���I�C
		
		modified = false;
		int nextn = -1;   //�̤j�k�ݫ׸`�I
		int crosn = -1;   //�̫�`�I
		double maxdf = 0; //�̤j�k�ݫ�
		double md = 0.9;  //�ʺA���e��
		Random rand = new Random();
		int  n = rand.nextInt(tour.size()-1);  //�H���w�q�@�`�I�����_�l�I
		tour.reverse(0, tour1.get(n));         //�H���w�q�`�I����
		if (md > dmaxsum[n][2]){               //�ʺA���e�ȳ]�w
			   md = dmaxsum[n][2];
		}
		else{
			if (md < dLowAFD)
				md =  dLowAFD;
		}
		dmaxsum[n][3] = 1;
		for (int i = 1; i < tour.size(); i++) {
			for (int j = 0; j < tour.size(); j++) {
				if (dmaxsum[j][3]==0){ //�w���`�I�P�O
					if (maxdf < dArray[n][j][1]){
					    maxdf = dArray[n][j][1];
					    nextn = j;
					}
					else{
						crosn = j; //�̫�`�I
					}
					modified = true;
				}
			}
			if (modified){
			   if (maxdf >= dLowAFD){    //�P�O�k�ݫ׬O�_�ŦX�����D�諸���e
				   tour.reverse(i, tour1.get(nextn)); //�`�I�[�J
				   dmaxsum[nextn][3] = 1; 
				   n = nextn;
			   }
			   else{                    //���ŦX�����D�諸���e
				   tour.reverse(i, tour1.get(crosn)); //�N�̱��񪺥i�θ`�I�[�J
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
		//�k�����Y�i�ΤU����ܡC
		//Mij=1-(D(ni,nj)/D(ni,nl))
		//Mij�G��ܰ�Ǹ`�Ii �P�䥦�`�Ij ���k�ݫסC
		//D�G��ܸ`�Ii �P�`�I�򤧶����ڴX���w�Z��(Euclidean distance)�C
		//nl�G���������I�̻����`�I�C
		//�̦��W�h���O�N�C�@�`�I�P�䥦�`�I���Z�����Y�w�q���k�ݫ�
		
		int toursize = tour.size();
		double[][] dmaxsum; //�ʺA���e��
		dmaxsum = new double[toursize][2];
		double[][][] dArray; //�k�ݫת�
		dArray = new double[toursize][toursize][3];
		
		//�`�I�k�ݫ�
		//�̾ڸ`�I�P�Ӹ`�I���̻��`�I���إ��k�ݫר̾ڡA�w�q�U�`�I�P�䥦�`�I���k�ݫסA
		//�ýs�s���k�ݫת�(dArray)�A�C�@�`�I�Ҧ��U�۪��k�ݫ׹�����A�H�����ʺA�A���ʽվ㪺�̾ڡC
		for (int i = 0; i < toursize; i++) {
			for (int j = 0; j < toursize; j++) {
				dArray[i][j][0] = distanceTable.getDistanceBetween(tour.get(i), tour.get(j));
				if (dmaxsum[i][0] < dArray[i][j][0])
					dmaxsum[i][0] = dArray[i][j][0]; //���������I�̻����`�I
				dmaxsum[i][1] += dArray[i][j][0];    //��ܸ`�I���`�Z��
			}
			for (int j = 0; j < toursize; j++) {
				if (dmaxsum[i][0]== 0)  //���������I�̻����`�I�Z�� = 0
				   dArray[i][j][1] = 1; //��ܰ���I�k�ݫ� = 1
				else
				   dArray[i][j][1] = 1 - (dArray[j][1][0]/dmaxsum[i][0]); //��ܰ���I�k�ݫ� Mij=1-(D(ni,nj)/D(ni,nl))
				
				if (dmaxsum[i][1]== 0)  //��ܰ���I�`�Z��=0
				   dArray[j][1][2] = 1;	//��ܰ���I�`�k�ݫ�
				else
				   dArray[i][j][2] = 1 - (dArray[j][1][0]/dmaxsum[i][1]);	//����`�Z���k�ݫ� Mij=1-(D(ni,nj)/D(ni,nl))
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
