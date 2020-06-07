package com.luvbrite.model;

import java.io.BufferedReader;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

public class Routine {

	public static double Round(double Rval, int Rpl) {
		double p = (double)Math.pow(10, Rpl);
		double tmp = Math.round(Rval * p);
		return tmp/p;
	}

	public static float Round(float Rval, int Rpl) {
		float p = (float)Math.pow(10,Rpl);
		float tmp = Math.round(Rval * p);
		return tmp/p;
	}

	public static double getDouble(String Rval) {
		
		try {
			return Double.valueOf(Rval).doubleValue();
		} catch (Exception e) {
			return  0d;
		}
	}

	public static int getInteger(String Rval) {
		
		try {
			return Integer.valueOf(Rval).intValue();
		} catch (Exception e) {
			return  0;
		}
	}

	public static float getFloat(String Rval) {
		
		try {
			return Float.valueOf(Rval).floatValue();
		} catch (Exception e) {
			return  0f;
		}
	}

	public static long getLong(String Rval) {
		
		try {
			return Long.valueOf(Rval).longValue();
		} catch (Exception e) {
			return  0l;
		}
	}
	
	public static String getTwoDigit(int i) {
		return i < 10 ? ("0" + i) : (i + "");
	}
	
	public static String getRSString(ResultSet rec, String field) {
		String ret = "", val = "";
		try{val = rec.getString(field);} catch(Exception e){System.out.println("Exception:" + e + " getting field:"+ field);}
		if(val!=null) ret = val;
		return ret;
	}
	
	public static String getValidXML(String field) {
		return field.replaceAll(" & "," &amp; ").replaceAll("<","&lt;").replaceAll(">","&gt;");
	}
	
	public static String escHTML(String field) {
		return field.replaceAll("<","&lt;").replaceAll(">","&gt;").replaceAll("javascript","").replaceAll("\"","&quot;").replaceAll("'","&#x27;").replaceAll("/","&#x2F;");
	}
	
	public static String gRP(HttpServletRequest req, String field) {
		String ret="";
		try{if(req.getParameter(field)!=null)ret=req.getParameter(field);}catch(Exception e){ret="";}
		return ret;
	}
	
	public static String gRP0(HttpServletRequest req, String field) {
		String ret="0";
		try{if(req.getParameter(field)!=null)ret=req.getParameter(field);}catch(Exception e){ret="0";}
		return ret;
	}
	
	public static String getJSONRequest(HttpServletRequest req) {
		String ret="";
		try {
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = req.getReader();
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
			} finally {
				reader.close();
			}
		}catch(Exception e){ret="";}
		
		return ret;
	}
}
