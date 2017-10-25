package top.lmoon.myspider.util;

public class DownloadUtil {
	
	public static enum downloadType{
		NOTSTART(0),ONGOING(1),UNFINISHED(2),FINISHED(3);
		
		private int value;
		
		downloadType(int value){
			this.value = value;
		}
		
		public int get(){
			return value;
		}
		
	}
	
	public static downloadType getDownloadType(int value){
		switch(value){
		case 0:
			return downloadType.NOTSTART;
		case 1:
			return downloadType.ONGOING;
		case 2:
			return downloadType.UNFINISHED;
		case 3:
			return downloadType.FINISHED;
		default:
			return downloadType.NOTSTART;	
		}
	}

}
