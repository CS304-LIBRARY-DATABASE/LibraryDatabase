package main;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyAttributes {
	 
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	
	/**
	 * Check an email for validity
	 * @param email
	 * @return
	 */
	public static boolean verifyEmail(String email) {
		if (notEmpty(email)) {
			Matcher matcher = pattern.matcher(email);
			return matcher.matches();
		}
		return  false;
	}

	
	public static boolean verifyPhone(String phone) {
		phone = phone.trim().replaceAll(" ", "").replaceAll("-", "");
		return verifyInteger(phone);
	}
	
	public static Integer parsePhoneNumber(String phone) {
		phone = phone.trim().replaceAll(" ", "").replaceAll("-", "");
		return Integer.parseInt(phone);
	}
	
	public static boolean verifyPassword(String password) {
		return notEmpty(password);
	}
	
	public static boolean verifyString(String s) {
		return notEmpty(s);
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
	
	public static boolean verifyType(String type) {
		if (notEmpty(type)) {
			type = type.toLowerCase();
			return type.equals("borrower") ||
				   type.equals("clerk") ||
				   type.equals("librarian");
		}
		return false;
	}
	
	public static boolean verifyDate(String s) {
		String [] split = s.split("/");
		if (split.length != 3) {
			split = s.split(" ");
			if (split.length != 3) {
				return false;
			}
		}
		return verifyInteger(split[0]) && verifyInteger(split[1]) && verifyInteger(split[2]);
	}
	
	public static Date parseDate(String s) {
		String [] split = s.split("/");
		if (split.length != 3) {
			split = s.split(" ");
			if (split.length != 3) {
				return null;
			}
		}
		return new Date(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}
}
