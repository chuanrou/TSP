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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EvolutionaryAlgorithm;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.LogToFile;

/**
 * Demonstration of optimizing a TSP problem using the MOEA Framework
 * optimization library (http://www.moeaframework.org/).  A window will appear
 * showing the progress of the optimization algorithm.  The window will contain
 * a visual representation of the TSP problem instance, with a thick red line
 * indicating the best tour found by the optimization algorithm.  Light gray
 * lines are the other (sub-optimal) tours in the population.
 */
public class TSP3OptExample {
	
	/**
	 * The color for population members.
	 */
	private static final Color lightGray = new Color(128, 128, 128, 64);
	
	/**
	 * Converts a MOEA Framework solution to a {@link Tour}.
	 * 
	 * @param solution the MOEA Framework solution
	 * @return the tour defined by the solution
	 */
	public static Tour toTour(Solution solution) {
		int[] permutation = EncodingUtils.getPermutation(
				solution.getVariable(0));
		
		// increment values since TSP nodes start at 1
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]++;
		}
		
		return Tour.createTour(permutation);
	}
	
	/**
	 * Saves a {@link Tour} into a MOEA Framework solution.
	 * 
	 * @param solution the MOEA Framework solution
	 * @param tour the tour
	 */
	public static void fromTour(Solution solution, Tour tour) {
		int[] permutation = tour.toArray();
		
		// decrement values to get permutation
		for (int i = 0; i < permutation.length; i++) {
			permutation[i]--;
		}
		
		EncodingUtils.setPermutation(solution.getVariable(0), permutation);
	}
	
	/**
	 * The optimization problem definition.  This is a 1 variable, 1 objective
	 * optimization problem.  The single variable is a permutation that defines
	 * the nodes visited by the salesman.
	 */
	public static class TSPProblem extends AbstractProblem {

		/**
		 * The TSP problem instance.
		 */
		private final TSPInstance instance;
		
		/**
		 * The TSP heuristic for aiding the optimization process.
		 */
		private final TSP3OptHeuristic heuristic;
		
		/**
		 * Constructs a new optimization problem for the given TSP problem
		 * instance.
		 * 
		 * @param instance the TSP problem instance
		 */
		public TSPProblem(TSPInstance instance) {
			super(1, 1);
			this.instance = instance;
			
			heuristic = new TSP3OptHeuristic(instance);
		}

		@Override
		public void evaluate(Solution solution) {
			Tour tour = toTour(solution);
			
			// apply the heuristic and save the modified tour
			heuristic.apply(tour);
			fromTour(solution, tour);

			solution.setObjective(0, tour.distance(instance));
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(1, 1);
			
			solution.setVariable(0, EncodingUtils.newPermutation(
					instance.getDimension()));
			
			return solution;
		}
		
	}
	
	/**
	 * Solves this TSPLIB instance while displaying a GUI showing the
	 * optimization progress.
	 * 
	 * @param instance the TSPLIB instance to solve
	 */
	public static void solve(TSPInstance instance) {
		TSPPanel panel = new TSPPanel(instance);
		panel.setAutoRepaint(false);
		
		// create other components on the display
		StringBuilder progress = new StringBuilder();
		JTextArea progressText = new JTextArea();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(panel);
		splitPane.setBottomComponent(new JScrollPane(progressText));
		splitPane.setDividerLocation(300);
		splitPane.setResizeWeight(1.0);
		
		// display the panel on a window
		JFrame frame = new JFrame(instance.getName());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setLocationRelativeTo(null);
		frame.setIconImages(Settings.getIconImages());
		frame.setVisible(true);
		
		// create the optimization problem and evolutionary algorithm
		Problem problem = new TSPProblem(instance);
		
		Properties properties = new Properties();
		properties.setProperty("swap.rate", "0.7");
		properties.setProperty("insertion.rate", "0.9");
		properties.setProperty("pmx.rate", "0.4");
		
		properties.setProperty("pm.rate", "1.0");
		properties.setProperty("pm.distributionIndex", "20.0");
		properties.setProperty("de.crossoverRate", "0.1");
		properties.setProperty("de.stepSize", "0.5");
		
		
		// algorithmname Model "MOEAD","GDE3","NSGAII","NSGAIII","eNSGAII","eMOEA","Random"
		
		String algorithmname = "NSGAIII";
		
		Algorithm algorithm = AlgorithmFactory.getInstance().getAlgorithm(
				algorithmname, properties, problem);
		
		Logger logger = LogToFile.setLoggerHanlder(Logger.getLogger("my.logger"), Level.FINEST, "TSP_"+algorithmname+"_3Opt_"+instance.getName()+"_");  
		logger.info("Iteration,Distance,Time"); //Log Header
		int iteration = 0;
		long stime;
		//Reset currentTime
        stime = System.currentTimeMillis();
		// now run the evolutionary algorithm
		while (frame.isVisible()) {
			algorithm.step();
			iteration++;
			
			// clear existing tours in display
			panel.clearTours();

			// display population with light gray lines
			if (algorithm instanceof EvolutionaryAlgorithm) {
				EvolutionaryAlgorithm ea = (EvolutionaryAlgorithm)algorithm;
				
				for (Solution solution : ea.getPopulation()) {
					panel.displayTour(toTour(solution), lightGray);
				}
			}
			
			// display current optimal solutions with red line
			Tour best = toTour(algorithm.getResult().get(0));
			panel.displayTour(best, Color.RED, new BasicStroke(2.0f));
			// repaint the TSP display
			panel.repaint();
			
			progress.insert(0, "Iteration " + iteration + ": " +
					best.distance(instance) +" �@�� " + (System.currentTimeMillis()-stime)/1000 + " ��"+ "\n");
			progressText.setText(progress.toString());
			
			//Log info
			logger.info(iteration + ","+ best.distance(instance) +"," + (System.currentTimeMillis()-stime)/1000); 
		}
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param file the file containing the TSPLIB instance
	 * @throws IOException if an I/O error occurred
	 */
	public static void solve(File file) throws IOException {
		solve(new TSPInstance(file));
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param reader the reader containing the TSPLIB instance
	 * @throws IOException if an I/O error occurred
	 */
	public static void solve(Reader reader) throws IOException {
		solve(new TSPInstance(reader));
	}
	
	/**
	 * Runs the example TSP optimization problem.
	 * 
	 * @param stream the stream containing the TSPLIB instance
	 * @throws IOException if an I/O error occurred
	 */
	public static void solve(InputStream stream) throws IOException {
		solve(new TSPInstance(new InputStreamReader(stream)));
	}

}
