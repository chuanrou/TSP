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
package org.moeaframework.core;

/**
 * Interface for evolutionary algorithms using an &epsilon;-box dominance archive.
 */
public interface EpsilonBoxEvolutionaryAlgorithm extends EvolutionaryAlgorithm {

	/**
	 * Returns the current &epsilon;-box non-dominated archive of the best
	 * solutions generated by this evolutionary algorithm.
	 * 
	 * @return the current &epsilon;-box non-dominated archive of the best
	 *         solutions generated by this evolutionary algorithm
	 */
	@Override
	public EpsilonBoxDominanceArchive getArchive();

}
