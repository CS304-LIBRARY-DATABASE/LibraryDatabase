package main;

import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyAttributes {

	private static final String ERROR_PATTERN = "Error: ";

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

	private static final String PHONE_PATTERN = 
			"^(\\d(-| )?)?\\d{3}(-| )?\\d{3}(-| )?\\d{4}$";

	private static Pattern phonePattern = Pattern.compile(PHONE_PATTERN);


	public static boolean notEmpty(String s) {
		return s != null && !s.trim().isEmpty();
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static boolean verifyInteger(String integer) {
		try {
			Integer.parseInt(integer);
			return true;

		} catch (Exception e) { }

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

	public static String parsePhoneNumber(String phone) {
		return phone.trim().replaceAll(" ", "").replaceAll("-", "");
	}


	public static String verifyEmail(String email) {
		if(isEmpty(email))
			return ERROR_PATTERN + "Email field is empty";

		Matcher matcher = emailPattern.matcher(email);
		if (!matcher.matches())
			return ERROR_PATTERN + "Email is malformed";

		if(email.length() > 40)
			return ERROR_PATTERN + "Email must be less than 41 characters";

		return null;
	}


	public static String verifyPhone(String phone) {
		if (isEmpty(phone))
			return ERROR_PATTERN + "Phone number field is empty";

		Matcher matcher = phonePattern.matcher(phone);
		if (!matcher.matches())
			return ERROR_PATTERN + "Phone number is malformed";

		return null;
	}


	public static String verifyPassword(String password) {
		if(isEmpty(password))
			return ERROR_PATTERN + "Password field is empty";

		if(password.length() > 16)
			return ERROR_PATTERN + "Password must be less than 17 characters";

		return null;
	}


	public static String verifyType(String type) {

		if(isEmpty(type))
			return ERROR_PATTERN + "Type field is empty";

		type = type.toLowerCase();
		if(!type.equals("borrower") && !type.equals("staff") &&
				!type.equals("faculty") && !type.equals("public"))
			return ERROR_PATTERN + "Type must be one of: borrower, staff, faculty or public";

		return null;
	}

	public static String verifyDate(String s) {
		String [] split = s.split("/");
		
		if (split.length != 3) {
			split = s.split(" ");
			if (split.length != 3) {
				return ERROR_PATTERN + "Date must be in the form \"xx xx xx\"";
			}
		}
		
		if(!verifyInteger(split[0]) || !verifyInteger(split[1]) || !verifyInteger(split[2]))
			return ERROR_PATTERN + "Date must only contain numbers";
		
		return null;
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
