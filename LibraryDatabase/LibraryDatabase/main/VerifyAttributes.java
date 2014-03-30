package main;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class VerifyAttributes {

	private static final String ERROR_PATTERN = "Error: ";

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

	private static final String PHONE_PATTERN = 
			"^(\\d(-| )?)?\\d{3}(-| )?\\d{3}(-| )?\\d{4}$";

	private static Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
	
	private static final String ISBN_PATTERN = "^\\d{9}[\\d|X]$";
	private static Pattern ISBNPattern = Pattern.compile(ISBN_PATTERN);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");



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

		} catch (Exception e) {}

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

	public static String verifyBID(String bid) {
		if(isEmpty(bid))
			return ERROR_PATTERN + "Borrower ID field is empty";

		if(bid.length() > 11)
			return ERROR_PATTERN + "Borrower ID must be less than 12 characters";

		if(!verifyFloat(bid))
			return ERROR_PATTERN + "Borrower ID must be numerical";

		return null;
	}

	public static String verifyBorrowerName(String name) {
		if(isEmpty(name))
			return ERROR_PATTERN + "Borrower name field is empty";

		if(name.length() > 11)
			return ERROR_PATTERN + "Borrower name must be less than 12 characters";

		return null;
	}

	public static String verifyAddress(String address) {
		if(isEmpty(address))
			return ERROR_PATTERN + "Address field is empty";

		if(address.length() > 40)
			return ERROR_PATTERN + "Address must be less than 41 characters";

		return null;
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

	public static String verifySinOrStNo(String sinOrStNo) {
		if(isEmpty(sinOrStNo))
			return ERROR_PATTERN + "SIN/Student # field is empty";

		if(sinOrStNo.length() > 9)
			return ERROR_PATTERN + "SIN/Student # must be less than 10 characters";

		if(!verifyFloat(sinOrStNo))
			return ERROR_PATTERN + "SIN/Student # must be numerical";

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
		if(isEmpty(s))
			return ERROR_PATTERN + "Date field is empty";
		
		try{
        	java.util.Date date = stringToDate(s);
        	java.util.Date today = new java.util.Date();
        	
        	if(date.before(today)){
        		return "Date " + sdf.format(date) + " must be after today";
        	}
        	
	    } catch(ParseException e ) {
	    	return ERROR_PATTERN + "Date must be in the form DD/MM/YYYY";
	    }
		return null;
	}
	
	public static java.util.Date stringToDate(String s) throws ParseException {
    	return sdf.parse(s);
	}


	public static String verifyCallNumber(String callNumber) {
		//QA76.73 J38 2004
		//primary number, secondary number, book's year
		
		if(isEmpty(callNumber))
			return ERROR_PATTERN + "Call number field is empty";
				
		String [] split = callNumber.split(" ");
		
		if (split.length != 3) 
			return ERROR_PATTERN + "Call number must have primary number, secondary number and year separated by spaces";

		String year = split[2];
		if(isEmpty(year))
			return ERROR_PATTERN + "Need to know year to verify call number";
		
		if (!verifyInteger(split[2]))
			return ERROR_PATTERN + "Call number must contain correct year at the end";
		
		if(callNumber.length() > 20)
			return ERROR_PATTERN + "Call number must be less than 21 characters";
		
		return null;
	}
	
	public static String verifyISBN(String isbn) {

		if(isEmpty(isbn))
			return ERROR_PATTERN + "ISBN field is empty";

		Matcher matcher = ISBNPattern.matcher(isbn);
		if (!matcher.matches())
			return ERROR_PATTERN + "ISBN is malformed: must be 10 characters, may end in X";
		
		return null;
	}

	public static String verifyTitle(String title) {
		
		if(isEmpty(title))
			return ERROR_PATTERN + "Title field is empty";
		
		if(title.length() > 30)
			return ERROR_PATTERN + "Title must be less than 31 characters";

		return null;
	}

	public static String verifyMainAuthor(String mainAuthor) {
		
		if(isEmpty(mainAuthor))
			return ERROR_PATTERN + "Main author field is empty";
		
		if(mainAuthor.length() > 40)
			return ERROR_PATTERN + "Main author must be less than 41 characters";

		return null;
	}

	public static String verifyPublisher(String publisher) {

		if(isEmpty(publisher))
			return ERROR_PATTERN + "Publisher field is empty";
		
		if(publisher.length() > 41)
			return ERROR_PATTERN + "Publisher must be less than 41 characters";

		return null;
	}

	/**
	 * Constraint:
	 * 0: no constraints, year can be any 4 digit number
	 * 1: year must be current year or later
	 * 2: year must be current year or earlier
	 * @param year
	 * @param constraint
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String verifyYear(String year, int constraint) {
		
		if(isEmpty(year))
			return ERROR_PATTERN + "Year field is empty";
		
		if(year.length() != 4 || !verifyInteger(year))
			return ERROR_PATTERN + "Year must be 4 characters";
		
		if(Integer.parseInt(year) < 0)
			return ERROR_PATTERN + "Year must be positive";
		
		Calendar today = Calendar.getInstance();
		Calendar date = Calendar.getInstance();
		date.set(Integer.parseInt(year), today.getTime().getMonth(), today.getTime().getDate());
		
		if(constraint == 2 && date.after(today))
			return ERROR_PATTERN + "Year must not be in the future!";
		
		if(constraint == 1 && date.before(today))
			return ERROR_PATTERN + "Year must not be in the past";
		
		return null;
	}

	public static String verifyAuthor(String author) {
		if (isEmpty(author)) {
			return ERROR_PATTERN + "Author cannot be empty";
		}
		if (author.length() > 40) {
			return ERROR_PATTERN + "Author cannot be longer than 40 characters";
		}
		return null;
	}
}