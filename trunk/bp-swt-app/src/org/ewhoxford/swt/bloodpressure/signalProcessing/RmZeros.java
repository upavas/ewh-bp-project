package org.ewhoxford.swt.bloodpressure.signalProcessing;

/**
 * 
 * @author mpimentel
 * Apply (temporary) filter to remove 'jumps' displayed in the signal
 */
public class RmZeros {
	
	/**
	 * 
	 * @param array
	 * @return array with pressure values without some artifacts
	 * @see vals
	 */
	public int[][] rmZeros(int[][] array){
		
		for (int i=0; i<array.length; i++){
			// remove 1st set of 'zeros'
			if ((array[i][1]==255 || array[i][1]==254) && array[i][0]==3){
				for (int j=0; j<50; j++){
					if (array[i+j][1]==0 && array[i+j][0]==3){
						array[i+j][1]=0;
						array[i+j][0]=2;
					}		
				}
			}
			// remove 2nd set of 'zeros'
			else if (array[i][1]==255 && array[i][0]==2){
				for (int j=0; j<20; j++){
					if (array[i+j][1]==0 && array[i+j][0]==2){
						array[i+j][1]=0;
						array[i+j][0]=1;
					}		
				}
			}
			// remove 3rd set of 'zeros'
			else if (array[i][1]==255 && array[i][0]==1){
				for (int j=0; j<20; j++){
					if (array[i+j][1]==0 && array[i+j][0]==1){
						array[i+j][1]=0;
						array[i+j][0]=0;
					}		
				}
			}
		}
		
		// output
		return array;
	}
}
