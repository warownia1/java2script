package test;

abstract public class Test_Extends_8 extends Test_Extends_9 {

	public int i0 = 8;
	
	public Test_Extends_8() {
		super();
		System.out.println("Test_Extends_8 contructor i0 super.i0 " + i0 + " " + super.i0);
		  assert((i0 + " " + super.i0).equals("8 0"));
	}
	
	{
	  System.out.println("8.1 init");
	}

	{
		//int[] test8 = new int[8];
		System.out.println("8.2 init - prepare fields");
	}


	abstract void getSuper0();
	
	public void test8(){
		System.out.println("Test_Extends_8.test8()");
		System.out.println("Test_Extends_8.test8() " + i0 + " " + super.i0);
		  assert((i0 + " " + super.i0).equals("8 0"));
	};

//	public Test_8(){
//	  System.out.println("8.3 construct()");
//	};

//	public Test_8(int... ints){
//	  System.out.println("8.3 construct()");
//		// nah, this is not the default constructor
//		System.out.println("constructor 8 [], ...)");
//	};

}
