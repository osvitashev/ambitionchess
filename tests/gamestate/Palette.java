package gamestate;


public class Palette {
	
	public static final boolean aflag;
	static {
		aflag = false;
		
	}
	

	public static void main(String[] args) {
		
		System.out.println("Application is rinning with a static final flag set to: "+ aflag);
		
		
		System.out.println("It seems the Java wordsize is: " + System.getProperty("sun.arch.data.model"));
		
		

	}

}
