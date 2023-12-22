package util;

public class SimplePRNG{
    private long seed;   // X0, the initial seed
    private final long a; // Multiplier
    private final long c; // Increment
    private final long m; // Modulus

    public SimplePRNG(long seed) {
        this.seed = seed;
        this.a = 25214903917L; // These values are often chosen based on specific criteria
        this.c = 11;
        this.m = (1L << 48); // 2^48
    }

    public long nextLong() {
        seed = (a * seed + c) % m;
        return seed;
    }

    public int nextInt(int bound) {
        // Scale the result of nextLong() to an integer in the range [0, bound)
        return (int) (nextLong() % bound);
    }
}