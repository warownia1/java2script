package test;

public interface Test_Interface { 	
	
	int x = 3;
	String y = "y";
	
	
  public static void main(String[] args) {
	 ClassLoader.getSystemClassLoader().setClassAssertionStatus("test.Test_Interface", true);
	 System.out.println("in the interface " + x + " " + y);
	 assert("3 y".equals(x + " " + y));
	 System.out.println("Test_Interface OK");
	 
  }
	
}