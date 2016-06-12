package ph.edu.msuiit.circuitlens;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class CircuitSimulatorTest {
    @Test
    public void testInit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        cirsim.init();
    }

    @Test
    public void testDumpCircuit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        final String expectedNetlist = "$ 1 5.0E-6 14.841315910257661 48.0 5.0 50.0\n" +
                "v 112 240 112 160 0 1 35.0 5.0 0.0 0.0 0.5\n" +
                "r 112 160 464 160 0 10.0\n" +
                "c 464 160 464 240 0 1.5E-5 17.99235566721663\n" +
                "l 112 240 464 240 0 1.0 0.0082376017397377\n" +
                "o 2 64 0 35 40.0 0.2 0 -1\n";
        cirsim.init();
        cirsim.readSetup(expectedNetlist);
        cirsim.updateCircuit();
        assertThat(cirsim.dumpCircuit(),is(expectedNetlist));
    }

    @Test
    public void testRunCircuit() throws Exception {
        CircuitSimulator cirsim = new CircuitSimulator();
        final String expectedNetlist = "$ 1 5.0E-6 14.841315910257661 48.0 5.0 50.0\n" +
                "v 112 240 112 160 0 1 35.0 5.0 0.0 0.0 0.5\n" +
                "r 112 160 464 160 0 10.0\n" +
                "c 464 160 464 240 0 1.5E-5 17.99235566721663\n" +
                "l 112 240 464 240 0 1.0 0.0082376017397377\n" +
                "o 2 64 0 35 40.0 0.2 0 -1\n";
        cirsim.readSetup(expectedNetlist);
        cirsim.runCircuit();
    }

    // A = LU
    // | 8  2  9 |   | 1     0       0 |  | 8  2  9     |
    // | 4  9  4 | = | 1/2   1       0 |  | 0  8 -1/2   |
    // | 6  7  9 |   | 3/4   11/16  1  |  | 0  0  83/32 |
    @Test
    public void testLuFactor() throws Exception {
        double[][] a = new double[][]{
                {  8, 2, 9},
                {  4, 9, 4},
                {  6, 7, 9}
        };
        double[][] lu = new double[][]{
                {  8,    2,         9       },
                {  0.5,  8,        -0.5     },
                {  0.75, 11./16.,   83./32. }
        };
        int ipvt[] = new int[3];
        int expectedIpvt[] = new int[] { 0, 1, 2};
        boolean result = CircuitSimulator.luFactor(a,3,ipvt);

        assertThat(result, is(true));
        assertThat(a, is(lu));
        assertThat(ipvt, is(expectedIpvt));
    }

    // A = LU
    // | 0  5  5 |   | 1        0       0 |  | 6  8       8      |
    // | 2  9  0 | = | 1/3      1       0 |  | 0  19/13  -8/3    |
    // | 6  8  8 |   | 0        15/19   1 |  | 0  0       135/19 |
    @Test
    public void testLuFactor2() throws Exception {
        double[][] a = new double[][]{
                {  0, 5, 5},
                {  2, 9, 0},
                {  6, 8, 8}
        };
        double[][] lu = new double[][]{
                {  6,      8,         8        },
                {  1./3.,  19./3.,   -8./3.    },
                {  0,      15./19.,   135./19. }
        };
        int ipvt[] = new int[3];
        int expectedIpvt[] = new int[] { 2, 1, 2};
        boolean success = CircuitSimulator.luFactor(a,3,ipvt);

        assertThat(success, is(true));
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                assertTrue(closeTo(a[i][j], lu[i][j], 0.001));
        assertThat(ipvt, is(expectedIpvt));
    }

    @Test
    public void testLuFactorZeroMatrix() throws Exception {
        double[][] a = new double[][]{
                {  0, 0, 0},
                {  0, 0, 0},
                {  0, 0, 0}
        };
        int ipvt[] = new int[3];
        boolean success = CircuitSimulator.luFactor(a,3,ipvt);
        assertThat(success, is(false));
    }

    // Ax = b
    // A = LU
    //
    // L = | 1  0  |  U = | 2  1 |  b = | 11 |
    //     |-1  1  |      | 0  9 |      |  9 |
    //
    // LY = b; UX = Y
    // solution [ 2, 5 ]
    @Test
    public void testLuSolve() throws Exception {
        double[][] lu = new double[][]{
                {  2, 1},
                { -1, 4}
        };
        double[] b = new double[]{ 11, 9};
        double[] solution = new double[]{ 2, 5};
        int ipvt[] = new int[]{ 1, 1};

        CircuitSimulator.luSolve(lu, 2, ipvt, b);
        for(int i=0;i<b.length;i++)
            assertTrue(closeTo(b[i], solution[i], 0.001));
    }

    // A = LU
    // | 0  5  5 |   | 1        0       0 |  | 6  8       8      |
    // | 2  9  0 | = | 1/3      1       0 |  | 0  19/13  -8/3    |
    // | 6  8  8 |   | 0        15/19   1 |  | 0  0       135/19 |
    @Test
    public void testLuSolve2() throws Exception {
        double[][] lu = new double[][]{
                {  6,      8,         8        },
                {  1./3.,  19./3.,   -8./3.    },
                {  0,      15./19.,   135./19. }
        };
        double[] b = new double[]{ 1, 2, 3};
        double[] solution = new double[]{ 7./30., 23./135., 4./135.};
        int ipvt[] = new int[]{ 2, 1, 2};

        CircuitSimulator.luSolve(lu,3,ipvt,b);
        assertThat(b, is(solution));
    }

    /*
     *  Helper function for testing similar values
     */
    private boolean closeTo(double actual, double expected, double error) {
        if(expected == 0){
            return Math.abs(actual-expected) <= error;
        }
        return Math.abs((actual-expected)/expected) <= error;
    }

    private String arrayToString(double[][] a){
        NumberFormat numberFormat = new DecimalFormat("###,###.####");
        numberFormat.setMaximumFractionDigits(4);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<a.length;i++){
            for (int j=0;j<a[0].length;j++){
                sb.append(numberFormat.format(a[i][j]));
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String arrayToString(int[] a){
        NumberFormat numberFormat = new DecimalFormat("###,###.####");
        numberFormat.setMaximumFractionDigits(4);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<a.length;i++){
            sb.append(numberFormat.format(a[i]));
            sb.append("\t");
        }
        return sb.toString();
    }

    private String arrayToString(double[] a){
        NumberFormat numberFormat = new DecimalFormat("###,###.####");
        numberFormat.setMaximumFractionDigits(4);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<a.length;i++){
            sb.append(numberFormat.format(a[i]));
            sb.append("\t");
        }
        return sb.toString();
    }
}