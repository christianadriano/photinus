package test;

import java.lang.reflect.Array;

public class ClassCastTest {

	public static <T> void testCast(T[] array,T element){
	
		Class<?> type = array != null? array.getClass() : element !=null ? element.getClass() : Object.class;

		System.out.println("type: "+type);
	}

	public static <T> T[] add(T[] array, T element) {
		Class<?> type = array != null ? array.getClass() : (element != null ? element.getClass() : Object.class);
		// TODO - this is NOT safe to ignore - see LANG-571
		System.out.println("passed here");
		T[] newArray = (T[]) copyArrayGrow1(array, type);//Line that produces the exception.
		System.out.println("did not reach here");
		newArray[newArray.length - 1] = element;
		return newArray;
	}

	private static Object copyArrayGrow1(Object array, Class<?> newArrayComponentType) {
		if (array != null) {
			int arrayLength = Array.getLength(array);
			Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
			System.arraycopy(array, 0, newArray, 0, arrayLength);
			return newArray;
		}
		return Array.newInstance(newArrayComponentType, 1);
	}

	public static void test(){
		String[] array = {"1", "2", "3"};
		String element = "4";

		//array=null;
		//element=null;
		ClassCastTest.testCast(array, element);

		System.out.println("all passed");
	}

	public static void testBug35(){
		String[] array = {"1", "2", "3"};
		String element = "4";

		element=null;
		array=null;

		String[] newArray = ClassCastTest.add(array, element);
		for(String value: newArray){
			System.out.println("value:"+ value);
		}
		System.out.println("all passed");
	}

	public static void main(String[] args){
		//ClassCastTest.test();
		ClassCastTest.testBug35();
	}
}
