package com.DriverAssistSystem.tcp;

import com.DriverAssistSystem.tcp.Constants.APP_TYPE;

import java.util.List;


public class IoTUtility { 

	/***************************************************************************/
	/*************************** type casting & parser *************************/
	/***************************************************************************/
	public static String[] get_status(String _msg)
	{  
		String[] _spliter = _msg.split("\\|"); 

		if(_spliter == null || _spliter.length < 2)
			return null;
		
		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.GET_STATUS)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}

	public static boolean is_get_status(String _msg)
	{
		String[] _spliter = _msg.split("\\|"); 

		if(_spliter == null || _spliter.length < 2)
			return false;
		
		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.GET_STATUS;
	}

	public static String[] get_health(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.GET_HEALTH)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}

	public static String[] get_user_info(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.USER_INFO)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}
	public static boolean is_get_health(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.GET_HEALTH;
	}

    public static boolean is_set_health(String _msg)
    {
        String[] _spliter = _msg.split("\\|");

        if(_spliter == null || _spliter.length < 2)
            return false;

        return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
                Integer.parseInt(_spliter[1]) == Constants.SET_HEALTH;
    }
	public static String[] get_history(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.GET_HISTORY)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}

	public static boolean is_get_history(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.GET_HISTORY;
	}

	public static String[] get_confirm_id(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.CONFIRM_ID)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}
	public static boolean is_confirm_id(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.CONFIRM_ID;
	}

	public static String[] get_signup_id(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.SIGNUP_ID)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}
	public static boolean is_signup_id(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.SIGNUP_ID;
	}

	public static String[] get_phone_num(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.GET_PHONE_NUM)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}

	public static boolean is_get_phone_num(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.GET_PHONE_NUM;
	}
    public static boolean is_set_phone_num(String _msg)
    {
        String[] _spliter = _msg.split("\\|");

        if(_spliter == null || _spliter.length < 2)
            return false;

        return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
                Integer.parseInt(_spliter[1]) == Constants.SET_PHONE_NUM;
    }
	public static boolean is_user_info(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.USER_INFO;
	}


	public static String[] get_danger_signal(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return null;

		if(Integer.parseInt(_spliter[0]) != APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) != Constants.DANGER_SIGNAL)
		{
			return null;
		}
		else
		{
			String[] _spliter2 = _spliter[2].split(",");
			return _spliter2;
		}
	}
	public static boolean is_danger_signal(String _msg)
	{
		String[] _spliter = _msg.split("\\|");

		if(_spliter == null || _spliter.length < 2)
			return false;

		return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
				Integer.parseInt(_spliter[1]) == Constants.DANGER_SIGNAL;
	}
    public static boolean is_set_fan(String _msg)
    {
        String[] _spliter = _msg.split("\\|");

        if(_spliter == null || _spliter.length < 2)
            return false;

        return Integer.parseInt(_spliter[0]) == APP_TYPE.DRIVE.get_points() &&
                Integer.parseInt(_spliter[1]) == Constants.SET_FAN;
    }

	
	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();

		for(final byte b: a)
			sb.append(String.format("0x%02X ", b&0xff));

		return sb.toString();
	}

	public static String ByteArrayToHex(Byte[] a) {

		StringBuilder sb = new StringBuilder();
		for(final byte b: a)
			sb.append(String.format("0x%02X ", b&0xff));

		return sb.toString();
	}

	public static String charArrayToHex(char[] a) {
		StringBuilder sb = new StringBuilder(); 

		for(final char b: a)
			sb.append(String.format("0x%02X ", b&0xff));    

		return sb.toString();
	}
	public static String byteArrayToString(byte[] a) {
		StringBuilder sb = new StringBuilder();

		for(final byte b: a)
			sb.append(String.format("%c", b&0xff));

		return sb.toString();
	}

	public static String byteArrayToString(byte[] a, int offset, int length) {
		StringBuilder sb = new StringBuilder();

		for (int i = offset; i < length + offset && i <= a.length; i++) {
			sb.append(String.format("%c", a[i]&0xff));
		}

		return sb.toString();
	}

	public static String charArrayToString(char[] a) {
		StringBuilder sb = new StringBuilder(); 

		for(final char b: a)
			sb.append(String.format("%c", b&0xff));     

		return sb.toString();
	}

	public static byte[] toByteArray(List<Byte> in) {
		final int n = in.size();
		byte ret[] = new byte[n];

		for (int i = 0; i < n; i++) {
			ret[i] = in.get(i);
		}

		return ret;
	}

	/***************************************************************************/
	/********************** IoTUtility Received Data ***************************/
	/***************************************************************************/


	/***************************************************************************/
	/*************************** IoTUtility Commands ***************************/
	/***************************************************************************/

	public static byte[] mkcommand_get_status()
	{ 
		String _message = APP_TYPE.DRIVE.get_points() + "|" + Constants.GET_STATUS;

		return _message.getBytes();
	}
	public static byte[] mkcommand_get_health()
	{
		String _message = APP_TYPE.DRIVE.get_points() + "|" + Constants.GET_HEALTH;

		return _message.getBytes();
	}
	public static byte[] mkcommand_get_history()
	{
		String _message = APP_TYPE.DRIVE.get_points() + "|" + Constants.GET_HISTORY;

		return _message.getBytes();
	}
	public static byte[] mkcommand_get_phone_num()
	{
		String _message = APP_TYPE.DRIVE.get_points() + "|" + Constants.GET_PHONE_NUM;

		return _message.getBytes();
	}
	public static byte[] mkcommand_set_fan()
	{
		String _message = APP_TYPE.DRIVE.get_points() + "|" + Constants.SET_FAN;

		return _message.getBytes();
	}
	public static byte[] mkcommand_user_info()
	{
		String _message = APP_TYPE.DRIVE.get_points() + "|" + Constants.USER_INFO;

		return _message.getBytes();
	}

	public static byte[] mkcommand_set_health (String param1, String param2, int param3, int param4)
	{
		String _str = APP_TYPE.DRIVE.get_points() + "|" + Constants.SET_HEALTH + "|" + param1+ "," + param2+ "," + param3 + "," + param4;
		return _str.getBytes();
	}
	public static byte[] mkcommand_set_phonenum (String param1, String param2, int param3)
	{
		String _str = APP_TYPE.DRIVE.get_points() + "|" + Constants.SET_PHONE_NUM + "|" + param1+ "," + param2+ "," + param3;
		return _str.getBytes();
	}
	public static byte[] mkcommand_confirm_ID (String param1, String param2)
	{
		String _str = APP_TYPE.DRIVE.get_points() + "|" + Constants.CONFIRM_ID + "|" + param1+ "," + param2;
		return _str.getBytes();
	}
	public static byte[] mkcommand_signup_ID (String param1, String param2)
	{
		String _str = APP_TYPE.DRIVE.get_points() + "|" + Constants.SIGNUP_ID + "|" + param1+ "," + param2;
		return _str.getBytes();
	}
	public static byte[] mkcommand_set_fan (boolean _param)
	{
		String _str = APP_TYPE.DRIVE.get_points() + "|" + Constants.SET_FAN + "|" + (_param ? "1" : "0");
		return _str.getBytes();
	}

}
