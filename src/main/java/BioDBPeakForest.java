package org.openscience.msdb;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Collection;
import java.util.Arrays;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REXPMismatchException;

/**
 * An abstract class for modeling a Mass Spectra database.
 *
 * @author Pierrick Roger
 */
public class BioDbPeakForest extends BioDb {

	private REngine rengine = null;
	private REXP pfdb = null; // BioDb R instance.
	private MzTolUnit mztolunit = MzTolUnit.PPM;

	/**
	 * Constructor.
	 *
	 * @param rengine An REngine instance.
	 * @param url An url corresponding to a biological database.
     * @param useragent The user agent string to use when contacting the database URL.
	 * @param database The database to consider
	 * @param token A character token if needed, an empty string.
	 */
	public BioDbPeakForest(REngine rengine, java.net.URL url, String useragent, String database, String token) throws REngineException, REXPMismatchException {

		this.rengine = rengine;

		// Thread safety: lock
		int lock = this.rengine.lock();
		this.rengine.parseAndEval("library(biodb)");
		this.rengine.parseAndEval("factory <- BiodbFactory$new(useragent \"" + useragent + "\")");
		this.rengine.parseAndEval("factory$createConn(class = \"" + database +", token = " + token + "\"");
		// Thread safety: unlock
		this.rengine.unlock(lock);
	}

	///////////////////////////
	// SET MZ TOLERANCE UNIT //
	///////////////////////////

	public MzTolUnit setMzTolUnit(MzTolUnit unit) {
		MzTolUnit old = this.mztolunit;
		this.mztolunit = unit;
		return(old);
	}


	////////////////////////
	// COLLECTION TO REXP //
	////////////////////////

	private static REXPDouble collectionToREXPDouble(Collection c) {

		double[] v = new double[c.size()];
		int i = 0;
		for (Double x: (Collection<Double>)c)
			v[i++] = x;

		return new REXPDouble(v);
	}

	//////////////////////////
	// Search MSMS Spectrum //
	//////////////////////////

	public Map<String, Collection> searchMsMs(Map<Field, Collection> input, double precursor, double tolerance, MzTolUnit tolunit, int npmin,  Dist fun, Mode mode, Map<String, Object> params) throws REngineException, REXPMismatchException {

		// Check that MZ is present
		if ( ! input.containsKey(Field.MZ))
			throw new IllegalArgumentException("Input map must contain MZ values.");

		// Check that all vectors in input map have the same length
		int s = -1;
        for (Field f: input.keySet())
			if (s < 0)
				s = input.get(f).size();
			else if (s != input.get(f).size())
				throw new IllegalArgumentException("All collections in input map must have the same size.");

		// Thread safety: lock
		int lock = this.rengine.lock();

		int nlines = input.keySet().size();


		// Create input stream
		this.rengine.assign("mz", collectionToREXPDouble(input.get(Field.MZ)));
		String inputstr = "mz = mz";
        this.rengine.assign("int", collectionToREXPDouble(input.get(Field.INT)));
        inputstr += ", int = int";
		this.rengine.parseAndEval("df <- data.frame(",inputstr,")");
		this.rengine.assign("prec", REXPDouble(precursor));
        this.rengine.assign("npmin", REXPDouble(npmin));
        this.rengine.assign("mztol", REXPDouble(mztol));
        this.rengine.assign("fun", REXPString(fun));
        this.rengine.parseAndEval("tolunit = " + (this.mztolunit == MzTolUnit.PPM ? "BIODB.MZTOLUNIT.PPM" : "BIODB.MZTOLUNIT.PLAIN") + ")");
		//String params = "mode = " + (mode == Mode.POSITIVE ? "BIODB.MSMODE.POS" : "BIODB.MSMODE.NEG");

        //Parsing the supplementary parameters :
        parstr = "";
        if(param.keySet().size() != 0){
            boolean first = true;
            for (Field f: param.keySet()){
                if(first){
                    parstr += ( f + " = \""+param.get(f)+"\"");
                    first = false;
                }
                else{
                    if(param.get(f).isInstance(String)){
                        parstr += (", " + f + " = \""+param.get(f)+"\"");
                    }else{ //It's a number.
                        parstr += (", " + f + " = "+param.get(f));
                    }
                }
           }
        }

        // Create the query to be evaluated in R.
        query = ("db$msmsSearch(spec = df, precursor = prec, npmin = npmin, mztol = mztol, tolunit = tolunit, fun=fun, params = list("+parstr+"))"

        //Evaluating the query.
        this.rengine.parseAndEval("res <- " + query);

        // Example of query to be made in R
        //db$msmsSearch(spec = sampspec,precursor = 254,npmin = 2,mztol=0.1,tolunit="plain",fun = "wcosine",params = list(ppm = 5, dmz = 0.005))

		this.rengine.parseAndEval("matched <- do.call(\"rbind\",res$matchedPeaks)");

		// Get output

		//TODO see if an object is better. Maybe delta ppm for the precursor.
		Map<String, Collection> output = new HashMap<String, Collection>();
		output.put("dist", Arrays.asList(this.rengine.parseAndEval("res$measure").asDoubles()));
		output.put("id", Arrays.asList(this.rengine.parseAndEval("as.numeric(res$id)").asDoubles()));
		for (int i=0; i <nlines; ++i) {
            String label = "P"+i;
            output.put(label, Arrays.asList(this.rengine.parseAndEval("matched[ ," + i + "]").asDoubles()));
		}
		// Thread safety: unlock
		this.rengine.unlock(lock);
		return output;
	}
}
