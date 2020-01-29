package pretask.test; 

public class PositionFinder {

		public static int findPosition(String[] list, String term){
			if (list==null || list.length==0)
				return(-1);
			else{ 
				int position=1;
				for (String name: list){
					if(name.compareTo(term)==0)
						break;	
					position++;
				}
				return(position > 0 ? position: 0);
			}
		}
		public static void main(String[] args){
			String[] myArray = {"Hola","Kumusta", "Hello", "Ciao"};
			System.out.println(findPosition(myArray, "Ciao"));
		}
	}

//POSITIONFINDER ANSWERS
//What is the output of the code? 
//-1, 0, 1, 2, 3
//A: 2

//At line 19, what would be the output if instead of "Ciao" we have "Hola"?
//-1, 0, 1, 2, 3
//A: 0

//At line 19, what would be the output if instead of "Ciao" we have an empty String (e.g., " ")?
//-1, 0, 1, 2, 3
//A: 3

//At line 18, what would be the output if we have "String[] myArray = {};" ?
//Empty array, NullPointer exception, ArrayOutOfBounds exception, 0
//A: Empty array

//At line 18, if we set myArray to {"Hola", null}, which line of the program will cause a NullPointer exception?
//19, 20, 6, 9, 10
//A: 9