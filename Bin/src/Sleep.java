
public class Sleep {

	public static void main(String[] args){
		long milisec = 1000L * 5;
		try {
			if (args.length> 0){
				milisec = 1000L * Integer.valueOf(args[0]);
			}
			Thread.sleep(milisec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
