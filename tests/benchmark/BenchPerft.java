package benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import perft.PerftTest;

/**
 * http://tutorials.jenkov.com/java-performance/jmh.html
 * https://www.baeldung.com/java-microbenchmark-harness
 * 
 *
 */

@Warmup(iterations = 5, time = 20, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, time = 20, timeUnit = TimeUnit.SECONDS)
//@Fork(value = 3, jvmArgsAppend = {"-XX:+UseParallelGC", "-Xms1g", "-Xmx1g"})
@Fork(value = 1)
@BenchmarkMode(org.openjdk.jmh.annotations.Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(org.openjdk.jmh.annotations.Scope.Benchmark)
public class BenchPerft {
	
	
//	Result "benchmark.BenchPerft.benchmarkPerft":
//	0.604 ±(99.9%) 0.035 ops/s [Average]
//	(min, avg, max) = (0.556, 0.604, 0.674), stdev = 0.041
//	CI (99.9%): [0.568, 0.639] (assumes normal distribution)
	@Benchmark
	public void benchmarkPerft(Blackhole blackhole) {
		PerftTest.enableLogging = false;
		long ret=0;
		ret+=PerftTest.testPerft("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 4);//Kiwipete 
		ret+=PerftTest.testPerft("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 4);
		ret+=PerftTest.testPerft("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1 ", 4);
		ret+=PerftTest.testPerft("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8", 4);
		ret+=PerftTest.testPerft("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 4);
		ret+=PerftTest.testPerft("r3k2r/pb3p2/1q3npp/n2p4/1p1PPB2/4Q1P1/P2N1PBP/R3K2R b KQkq - 0 1", 4);
		ret+=PerftTest.testPerft("1n1R4/5B2/3bn3/3k1pR1/3p4/1r6/Q2PP3/K2R4 w - - 0 1", 4);
		ret+=PerftTest.testPerft("1k3r2/8/8/4KB2/8/2P5/b2N4/8 w - - 0 1", 4);
		ret+=PerftTest.testPerft("8/b1Rp2k1/4B3/8/5r2/K7/8/8 b - - 0 1", 4);
		ret+=PerftTest.testPerft("r3k2r/2p2ppp/1pn3B1/Q1b5/p1pPpqb1/PN3N1P/6P1/R1B1K2R b KQkq d3 0 1", 4);
		ret+=PerftTest.testPerft("6r1/1p2p3/2p2p1p/r4bN1/2kbQ2K/1n3P2/2pnqpP1/1BR1R3 b - - 0 1", 4);
		ret+=PerftTest.testPerft("3rr3/2N1PPpp/5Nk1/PpPq4/1bQ2b2/2B3P1/5P1P/R3K2R w KQ b6 0 1", 4);
		blackhole.consume(ret);
	}
	
	public static void main(String[] args) throws RunnerException {
		//https://www.vogella.com/tutorials/JavaMicrobenchmarking/article.html
        Options opt = new OptionsBuilder().include(BenchPerft.class.getSimpleName()).build();

        new Runner(opt).run();
    }

}
