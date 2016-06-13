package ph.edu.msuiit.circuitlens.circuit;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SiUnits {

    public static final NumberFormat showFormat, shortFormat, noCommaFormat;

    static {
        showFormat = DecimalFormat.getInstance();
        showFormat.setMaximumFractionDigits(2);
        shortFormat = DecimalFormat.getInstance();
        shortFormat.setMaximumFractionDigits(1);
        noCommaFormat = DecimalFormat.getInstance();
        noCommaFormat.setMaximumFractionDigits(10);
        noCommaFormat.setGroupingUsed(false);
    }

    public static String getUnitText(double magnitude, String unit, NumberFormat showFormat){
        SiUnit[] units = SiUnit.values();
        for(int i=0;i<units.length;i++){
            double value = units[i].value;
            String symbol = units[i].symbol;
            if(magnitude < 1e-14) return "0" + unit;
            if(magnitude < (value * 1000) || (i==units.length-1))
                return showFormat.format(magnitude / value) + symbol + unit;
        }
        return showFormat.format(magnitude) + unit;
    }

    public static String getShortUnitText(double magnitude, String unit) {
        SiUnit[] units = SiUnit.values();
        for(int i=0;i<units.length;i++){
            double value = units[i].value;
            String symbol = units[i].symbol;
            if(magnitude < 1e-14) return null;
            if(magnitude < (value * 1000) || (i==units.length-1))
                return shortFormat.format(magnitude / value) + symbol + unit;
        }
        return shortFormat.format(magnitude) + unit;
    }

    public static String getCurrentText(double i, NumberFormat numberFormat) {
        return getUnitText(i, "A", numberFormat);
    }

    public static String getCurrentDText(double i, NumberFormat numberFormat) {
        return getUnitText(Math.abs(i), "A", numberFormat);
    }

    public static String getVoltageDText(double v, NumberFormat numberFormat) {
        return getUnitText(Math.abs(v), "V", numberFormat);
    }

    public static String getVoltageText(double v, NumberFormat numberFormat) {
        return getUnitText(v, "V", numberFormat);
    }

    enum SiUnit{
        PICO  (1e-12,"p"),
        NANO  (1e-9,"n"),
        MICRO (1e-6,"u"),
        NONE  (1,""),
        MILLI (1e-3,"m"),
        KILO  (1e3,"K"),
        MEGA  (1e6,"M"),
        GIGA  (1e9, "G"),
        TERA  (1e12, "T");

        private final double value;
        private final String symbol;

        SiUnit(double value, String symbol){
            this.value = value;
            this.symbol = symbol;
        }

        public double getValue() {
            return value;
        }

        public String getSymbol() {
            return symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }
}
