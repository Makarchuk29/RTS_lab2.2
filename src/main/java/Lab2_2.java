import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import java.util.Random;

public class Lab2_2 {
    private static int countOfGarmonics = 12;
    private static int limitFrequency = 900;
    private static int N = 256;
    private static double[] signals = new double[N];
    private static double[][] w_coeff = new double[N/2][N/2];
    private static double[] w_coeff_new = new double[N];
    private static double[] F_I = new double[N/2];
    private static double[] F_II = new double[N/2];
    private static double[] F = new double[N];

    public static void main(String[] args) {
        calculateSignals();

        for (int p = 0; p < w_coeff.length; p++) {
            for (int k = 0; k < w_coeff[p].length; k++) {
                w_coeff[p][k] = Math.cos((4*Math.PI/N)*p*k) + Math.sin((4*Math.PI/N)*p*k);
            }
        }

        for (int p = 0; p < w_coeff_new.length; p++) {
            w_coeff_new[p] = Math.cos((2*Math.PI/N)*p) + Math.sin((2*Math.PI/4)*p);
        }

        for (int p = 0; p < N/2; p++) {
            for (int k = 0; k < N/2; k++) {
                F_II[p] += signals[2*k] *w_coeff[p][k];
                F_I[p] += signals[2*k+1]*w_coeff[p][k];
            }
        }

        for (int p = 0; p < N; p++) {
            if (p<N/2){
                F[p] += F_II[p]+w_coeff_new[p]*F_I[p];
            }else {
                F[p] += F_II[p-(N/2)] - w_coeff_new[p]*F_I[p-(N/2)];
            }
        }
        double [] xData = initializationXData(N);
        XYChart chart = new XYChartBuilder()
                .width(1200)
                .height(800)
                .title("x(t)")
                .xAxisTitle("t")
                .yAxisTitle("x")
                .build();
        chart.addSeries("FFT", xData, F);
        new SwingWrapper<>(chart).displayChart();
    }

    public static void calculateSignals(){
        Random r = new Random();
        double A = r.nextDouble();
        double fi = r.nextDouble()*Math.PI;
        for (int i = 0; i < countOfGarmonics; i++) {
            for (int j = 0; j < N; j++) {
                signals[j] += A * Math.sin(1.*limitFrequency*(i+1)/countOfGarmonics*j + fi);//
            }
        }
    }
    private static double[] initializationXData(int countOfDiscreteCalls) {
        if(countOfDiscreteCalls<0)
            return null;
        double[] result = new double[countOfDiscreteCalls];
        for (int i = 0; i < countOfDiscreteCalls; i++) {
            result[i] = i;
        }
        return result;
    }
}
