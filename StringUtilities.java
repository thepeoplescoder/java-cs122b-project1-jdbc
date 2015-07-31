
public class StringUtilities
{
	public static boolean nullOrEmptyExists(String... strings)
	{
		if (strings == null)
		{
			return false;
		}
		
		for (String s : strings)
		{
			if (isNullOrEmpty(s))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNullOrEmpty(String s)
	{
		return s == null || s.length() < 1;
	}
	
	public static String nullToEmpty(String s)
	{
		return s == null ? "" : s;
	}
	
	public static String truncate(String s, int len)
	{
		if (len < 0)
		{
			len = s.length();
		}
		
		return s.substring(0, Math.min(s.length(), len));
	}
	
	public static String repeat(String s, int num)
	{
		StringBuilder sb = new StringBuilder();
		for (int count = 0; count < num; count++)
		{
			sb.append(s);
		}
		return sb.toString();
	}
}
