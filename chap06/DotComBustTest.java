package chap06;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import mockit.Deencapsulation;

public class DotComBustTest {
	private DotComBust dotComBust = new DotComBust();
	@Test
	public void testSetupGame() {
		Deencapsulation.invoke(dotComBust, "setUpGame");
		 ArrayList<DotCom> expectedList = new ArrayList<DotCom>();
	        DotCom one = new DotCom();
	        one.setName("Pets.com");
	        DotCom two = new DotCom();
	        two.setName("eToys.com");
	        DotCom three = new DotCom();
	        three.setName("Go2.com");
	        expectedList.add(one);
	        expectedList.add(two);
	        expectedList.add(three);
	        ArrayList<DotCom> actualList =  Deencapsulation.getField(dotComBust, "dotComsList");
	        assertEquals(actualList.size(), 3);
	        int numberOfMatches = 0;
	        for(DotCom actual:actualList){
	        	 for(DotCom expected:expectedList){
	        		 if(Deencapsulation.getField(expected, "name").equals(Deencapsulation.getField(actual, "name"))){
	        			 numberOfMatches = numberOfMatches+1; 
	        		 }
	        	 }
	        }
	        //checks it has exact match
	        assertEquals(numberOfMatches, 3);
	        //check if locations are set for each item
	        for(DotCom actual:actualList){
	        		assertNotNull(Deencapsulation.getField(actual, "locationCells"));
	        		assertEquals(((ArrayList<DotCom>) Deencapsulation.getField(actual, "locationCells")).size(),3);
	        	 }
	        }
	        
	}
