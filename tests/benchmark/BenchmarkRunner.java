package benchmark;

/**
 * 
 * To run:
 * 1. Run Maven->Update Project
 * 2. cmd: mvn package in the project folder
 * 3. Run as a java application
 *
 */
public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}