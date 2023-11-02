package com.ksoft.dms;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DMSParser {
    public static boolean degree = false;
    public static final int READ_LIMIT = 1024;

    public static double parse(String val) {


        double result = 0.0;

        InputStream is = new ByteArrayInputStream(val.getBytes());
        InputStreamReader r = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(r);

        result = processLow(br, false);

        return result;
    }

    private static double processLow(BufferedReader reader, boolean res) {
        double val = 0;
        boolean oper = false;

        try {
            int rs = -1;
            reader.mark(READ_LIMIT);
            while ((rs = reader.read()) != -1) {
                if (rs != -1) {
                    if (rs == ' ')
                        continue;
                    if (rs == '*')
                        val *= processHight(reader, false);
                    else if (rs == '/')
                        val /= processHight(reader, true);
                    else if (rs == '^')
                        val = Math.pow(val,processHight(reader, false));
                    else if (rs == '+')
                        val += processLow(reader, true);
                    else if (rs == '-' && oper)
                        val -= processLow(reader, true);
                    else if (rs == '-' && !oper)
                        val = processHight(reader, false) * -1;
                    else if (rs == '(') {
                        oper = true;
                        val = processLow(reader, false);

                        if (res)
                            break;
                    } else if (rs == ')') {
                        break;
                    } else if (Character.isDigit(rs)) {
                        oper = true;
                        reader.reset();
                        val = readVal(reader);

                        if (res && nextOperIsLow(reader))
                            break;
                    }else if (Character.isAlphabetic(rs)){
                        oper = true;
                        reader.reset();
                        val = readFunc(reader);

                        if (res && nextOperIsLow(reader))
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    private static double readFunc(BufferedReader reader) {
        double res = 0.0;
        try {
            StringBuilder sb = new StringBuilder();
            int c = -1;
            while ((c = reader.read()) != -1){
                if (c == '(')
                    break;
                sb.append((char)c);
            }

            String name = sb.toString();

            res = processLow(reader, false);

            if (degree)
                res = Math.toRadians(res);

            if (name.equals("cos")){
                res = Math.cos(res);
            }else if (name.equals("sin")){
                res = Math.sin(res);
            }else if (name.equals("tan")){
                res = Math.tan(res);
            }else if (name.equals("acos")){
                res = Math.acos(res);
            }else if (name.equals("asin")){
                res = Math.asin(res);
            }else if (name.equals("atan")){
                res = Math.atan(res);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    private static boolean nextOperIsLow(BufferedReader reader) {
        boolean res = true;

        try {
            reader.mark(READ_LIMIT);
            int s = -1;
            while ((s = reader.read()) != -1)
                if (s == '*' || s == '/'){
                    res = false;
                    break;
                }else if (s == '+' || s == '-')
                    break;

            reader.reset();
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    private static double readVal(BufferedReader reader) {
        double res = 0;

        try {
            StringBuilder sb = new StringBuilder();
            int rs = -1;
            while ((rs = reader.read()) != -1) {
                if (!isTerminal(rs)) {
                    reader.mark(READ_LIMIT);
                    sb.append((char) rs);
                } else {
                    reader.reset();
                    break;
                }
            }

            String val = sb.toString();
            if (isDegree(val)) {
                res = convertFromDegree(val);

                if (!degree)
                    res = Math.toRadians(res);

            }else
                res = Double.parseDouble(val);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private static double convertFromDegree(String val) {
        String sd = "0", sm = "0", ss = "0";
        int pos = -1;
        pos = val.indexOf('\u00b0');

        if (pos != -1) {
            sd = val.substring(0, pos);
            val = val.substring(pos + 1);
        }

        pos = val.indexOf('\u2032');

        if (pos != -1) {
            sm = val.substring(0, pos);
            val = val.substring(pos + 1);
        }

        pos = val.indexOf('\u2033');

        if (pos != -1) {
            ss = val.substring(0, pos);
        }

        double res = 0.0;
        double d = 0.0, m = 0.0, s = 0.0;

        try {
            d = Double.parseDouble(sd);
        } catch (Exception e) {
            e.printStackTrace();
            d = 0;
        }

        try {
            m = Double.parseDouble(sm);
        } catch (Exception e) {
            e.printStackTrace();
            m = 0;
        }

        try {
            s = Double.parseDouble(ss);
        } catch (Exception e) {
            e.printStackTrace();
            s = 0;
        }

        res = d + m / 60 + s / 3600;

        return res;
    }

    private static boolean isDegree(String value) {
        return (value.indexOf('\u00b0') != -1) || (value.indexOf('\u2032') != -1) || (value.indexOf('\u2033') != -1);
    }

    private static boolean isTerminal(int rs) {
        return rs == '*' || rs == '/' || rs == '+' || rs == '-' || rs == '(' || rs == ')' || rs == '^';
    }

    private static double processHight(BufferedReader reader, boolean res) {
        double val = 0.0;

        try {
            int rs = -1;
            reader.mark(READ_LIMIT);
            while ((rs = reader.read()) != -1) {
                if (rs != -1) {
                    if (rs == ' ')
                        continue;
                    if (rs == '*')
                        val *= processHight(reader, false);
                    else if (rs == '/')
                        val /= processHight(reader, true);
                    else if (rs == '(')
                        val = processLow(reader, true);
                    else if (rs == ')') {
                        reader.reset();
                        break;
                    } else if (rs == '+' || rs == '-') {
                        reader.reset();
                        break;
                    } else if (Character.isDigit(rs)) {
                        reader.reset();
                        val = readVal(reader);

                        if (res)
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }
}
