package main;

public class VerifyAttributes {
	
	public static boolean verifyEmail(String email) {
		return false;
	}

	public static boolean verifyPhone(String phone) {
		return false;
	}
	
	public static boolean verifyPassword(String password) {
		return false;
	}
	
	public static boolean verifyString(String s) {
		return s != null;
	}
	
	public static boolean notEmpty(String s) {
		return s != null && !s.trim().isEmpty();
	}
	
	
	public static boolean verifyInteger(String integer) {
		try {
			Integer.parseInt(integer);
			return true;
		} catch (NullPointerException e) {
		} catch (NumberFormatException e) {
		}
		return false;
	}
	
	
	public static boolean verifyFloat(String number) {
		try {
			Float.parseFloat(number);
			return true;
		} catch (NullPointerException e) {
		} catch (NumberFormatException e) {
		}
		return false;
	}
}
