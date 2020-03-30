import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.Random;

public class Lab2_2AdditionalTask {
    private static final int [] arrayN = {256, 500, 910, 1054};
    private int countOfGarmonics = 12;
    private int limitFrequency = 900;
    private int N;
    private double[] signals;
    private double[][] w_coeff;
    private double[] w_coeff_new;
    private double[] F_I;
    private double[] F_II;
    private double[] F;

    public static void main(String[] args) {
        double [] xData;
        XYChart chart = new XYChartBuilder().width(1200).height(800).title("x(t)").xAxisTitle("t").yAxisTitle("x").build();
        for (int N : arrayN) {
            xData = initializationXData(N);
            chart.addSeries("FFT(N = " + N + ")", xData, new Lab2_2AdditionalTask(N).fft());
        }
        new SwingWrapper<>(chart).displayChart();
    }

    public Lab2_2AdditionalTask(int N) {
        this.N = N;
        this.w_coeff_new = new double[N];
        this.F_I = new double[N/2];
        this.F_II = new double[N/2];
        this.F = new double[N];
        this.signals = new double[N];
        this.w_coeff = new double[N/2][N/2];
    }

    public double [] fft() {
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
        Runnable T1 = () -> {
            System.out.println("T1 started");
            for (int p = 0; p < N/2; p++) {
                F[p] += F_II[p]+w_coeff_new[p]*F_I[p];
            }
            System.out.println("T1 finished");
        };
        Runnable T2 = () -> {
            System.out.println("T2 started");
            for (int p = N/2; p < N; p++) {
                F[p] += F_II[p-(N/2)] - w_coeff_new[p]*F_I[p-(N/2)];
            }
            System.out.println("T2 finished");
        };
        new Thread(T1, "T1").start();
        new Thread(T2, "T2").start();
        return F;
    }

    public void calculateSignals(){
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
