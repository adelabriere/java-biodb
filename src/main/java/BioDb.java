package org.openscience.msdb;

import java.util.Map;
import java.util.Collection;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REXPMismatchException;

/**
 * An abstract class for modeling a Mass Spectra database.
 *
 * @author Pierrick Roger
 */
public abstract class MsDb {

	/**
	 * MS Mode.
	 * Used in methods to specify the wanted MS mode.
	 *
	 * @author Pierrick Roger
     */
	public enum Mode { POSITIVE, NEGATIVE }

    /**
	 * Spectral distance functions.
	 * Used in methods to specify the wanted distance to be calculated.
	 *
	 * @author Alexis Delabriere
     */

	public enum Dist { wcosine, cosine }

	/**
	 * Data fields.
	 * Used in input and output maps, as keys.
	 *
	 * @author Pierrick Roger
	 */
	public enum Field { MZ, INT, MOLID, MOLNAMES, MZTHEO, ATTR, COMP, COL, COLRT }

	/**
	 * M/Z tolerance unit.
	 * Used in an database instance to specify the unit to use for the parameters shift and prec.
	 *
	 * @author Pierrick Roger
	 */
	public enum MzTolUnit { PPM, PLAIN }

	/**
	 * Set M/Z tolerance unit.
	 *
	 * @param unit  The new M/Z tolerance unit.
	 * @return      The old M/Z tolerance unit.
	 *
	 * @author  Pierrick Roger
	 */
	public abstract MzTolUnit setMzTolUnit(MzTolUnit unit);

	/**
	 * MZ/RT matching algorithm.
	 *
	 * @param input     The input data. This is a map of collections. The key values can be MZ alone, or MZ and RT.
	 * @param mode      The MS mode to consider.
	 * @param shift
	 * @param prec
	 * @param rttolx
	 * @param rttoly
	 * @param cols
	 * @return          A map of collections. The key of type Field indicate the type of output.
	 *
	 * @author Pierrick Roger
	 */
	public abstract Map<Field, Collection> searchMzRt(Map<Field, Collection> input, Mode mode, double shift, double prec, double rttolx, double rttoly, Collection<String> cols) throws REngineException, REXPMismatchException;
}
