package ph.edu.msuiit.circuitlens;

import org.junit.Test;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ph.edu.msuiit.circuitlens.circuit.CircuitSimulator;
import ph.edu.msuiit.circuitlens.circuit.SiUnits;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class SiUnitsTest {
    @Test
    public void testGetUnitTextMicro() {
        double magnitude = 12.45e-6;
        String unit = "A";
        NumberFormat showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        String output = SiUnits.getUnitText(magnitude,unit,showFormat);
        String expectedOutput = "12.45uA";
        assertThat(output,is(expectedOutput));
    }

    @Test
    public void testGetUnitTextNone() {
        double magnitude = 120;
        String unit = "V";
        NumberFormat showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        String output = SiUnits.getUnitText(magnitude,unit,showFormat);
        String expectedOutput = "120V";
        assertThat(output,is(expectedOutput));
    }

    @Test
    public void testGetUnitTextTera() {
        double magnitude = 532e10;
        String unit = "V";
        NumberFormat showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        String output = SiUnits.getUnitText(magnitude,unit,showFormat);
        String expectedOutput = "5.32TV";
        assertThat(output,is(expectedOutput));
    }

    @Test
    public void testGetUnitTextVerySmall() {
        double magnitude = 0.12e-14;
        String unit = "A";
        NumberFormat showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        String output = SiUnits.getUnitText(magnitude,unit,showFormat);
        String expectedOutput = "0A";
        assertThat(output,is(expectedOutput));
    }
}