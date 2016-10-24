import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.openscience.msdb.BioDb;
import org.openscience.msdb.BioDbPeakForest;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.REXPMismatchException;

/**
 * @author Pierrick Roger
 */
public class TestMsDb {

	REngine rengine = null;
	MsPeakForestDb db = null;

	//////////////////////////
	// START RENGINE AND DB //
	//////////////////////////



	@Before
	public void startREngineAndDb() throws java.net.MalformedURLException, REngineException, REXPMismatchException {
		this.rengine = org.rosuda.REngine.JRI.JRIEngine.createEngine();
        //TODO CHANGE THE TOKEN
		this.db = new BioDBPeakForest(this.rengine, new java.net.URL("https://rest.peakforest.org/"), "java-biodb.test ; alexis.delabriere@hotmail.fr", token = "10omgahvttp9rm41agr60drmd6");
	}

	/////////////////////////
	// STOP RENGINE AND DB //
	/////////////////////////

	@After
	public void stopREngineAndDb() {
		if (rengine != null) {
			this.rengine.close();
			this.rengine = null;
			this.db = null;
		}
	}

	///////////////////////////
	// TEST SEARCH ARGUMENTS //
	///////////////////////////

//	@Test(expected=IllegalArgumentException.class)
//	public void testSearchNoMzInInput() throws REngineException, REXPMismatchException {
//		Map<MsDb.Field, Collection> input = new HashMap<MsDb.Field, Collection>();
//		Map<MsDb.Field, Collection> output = this.db.searchMzRt(input, MsDb.Mode.POSITIVE, 0.0, 5.0, Double.NaN, Double.NaN, null);
//	}


    ////////////////////////////////////
    // TESTING THAT THE CLASS IS OKAY //
    ////////////////////////////////////
	@Test
	public void testMsMSSearch() throws REngineException, REXPMismatchException {
		Map<BioDb.Field, Collection> input = new HashMap<BioDb.Field, Collection>();
		Vector<Double> mz = new Vector<Double>();
        mz.add(310.06);
        mz.add(263.1329);
        mz.add(252.1009);
        mz.add(330.1169);

        Vector<Double> rint = new Vector<Double>();
        rint.add(1.2);
        rint.add(0.78);
        rint.add(0.02);
        rint.add(0.19);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("ppm",5);
        params.put("dmz",0.005);

		Double precursor = 254;
		double tol = 0.1;
		MzTolUnit tolunit = MzTolUnit.PLAIN
		input.put(BioDb.Field.MZ, mz);
		input.put(BioDb.Field.INT, rint);
		Map<String, Collection> output = this.db.searchMSMS(input, precursor, tol, tolunit, BIODB.Dist.wcosine, BIODB.Mode.POSITIVE, Double.NaN, null);

        assertTrue(output.containsKey("id"));
		assertTrue(output.containsKey("dist"));
        assertTrue(output.containsKey("P1"));
		assertTrue(output.containsKey("P2"));
		assertTrue(output.containsKey("P3"));
		assertTrue(output.containsKey("P4"));

		// Check that we have the same number of values for each field
		int s = -1;
		for (String f: output.keySet()) {
			if (s < 0)
				s = output.get(f).size();
			else
				assertTrue(output.get(f).size() == s);
		}

		// Check that at least one line is returned.
		assertTrue(s >= 1);

	}

	//TODO add more test.
}
