import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneN2 {
    /**
     * 返回对象集合
     * @param url
     * @return
     */
    public static ArrayList<WeatherData> getOne2(String url) {
        ArrayList<WeatherData> str = new ArrayList<WeatherData>();
        String strW = new String();
        String s = GetUrl2.getHtmlContent(url, "utf-8");
        String start = "<td class=\"cl_dt\"";
        String end = "</table>";

        int size1 = s.indexOf(start) - 5;
        int size2 = s.indexOf(end, size1);

        String neirong = s.substring(size1, size2);

        Pattern p = Pattern.compile("<tr>([\\s\\S]*?)</tr>");// 匹配所有<tr></tr>之间的内容包含换行符
        Matcher m = p.matcher(neirong);

        Pattern p2 = Pattern.compile("<td.*?>([\\s\\S]*?)</td>");
        Pattern pp2 = Pattern.compile("<div.*?>([\\s\\S]*?)</div>");
        Pattern dd = Pattern.compile(">(.*?)<");

        String strTime = new String();
        Pattern tt = Pattern.compile("<td class=\"cl_dt\".*?>(.*?)</td>");

        WeatherData d = new WeatherData();
        d.setRoad(url);
        while (m.find()) {

	   String neirong2 = m.group(1);

	   if (neirong2.indexOf(start) != -1) {
	       Matcher mt = tt.matcher(neirong2);
	       if (mt.find()) {
		  strTime = mt.group(1);
		  strW = new String();
	       }
	   }

	   Matcher m2 = p2.matcher(neirong2);
	   while (m2.find()) {
	       String neirong3 = m2.group(1);
	       Matcher m3 = pp2.matcher(neirong3);

	       if (m3.find()) {
		  String neirong4 = m3.group(1);

		  Matcher m4 = dd.matcher(neirong4);
		  if (m4.find()) {
		      // System.out.print(m4.group(1));
		      strW = strW + m4.group(1);
		      while (m4.find()) {
			 strW = strW + m4.group(1);
		      }
		      strW = strW + ",";
		  } else {
		      strW = strW + m3.group(1) + ",";
		      // System.out.print(m3.group(1)+",");
		  }

	       } else {
		  strW = strW + m2.group(1) + ",";
		  // System.out.print(m2.group(1)+",");
	       }
	   }
	   String ll0 = strW.substring(0, strW.length() - 1);

	   String ll = ll0.replaceAll("&nbsp;", "null");//将字符串中的空值替换成null

	   String[] strArray = ll.split(",");
	   d.setDate(strArray[0]);
	   d.setTime(strArray[1]);
	   d.setT(strArray[2]);
	   d.setP0(strArray[3]);
	   d.setP(strArray[4]);
	   d.setPa(strArray[5]);
	   d.setU(strArray[6]);
	   d.setDD(strArray[7]);
	   d.setFf(strArray[8]);
	   d.setFf10(strArray[9]);
	   d.setFf3(strArray[10]);
	   d.setN(strArray[11]);
	   d.setWW(strArray[12]);
	   d.setW1(strArray[13]);
	   d.setW2(strArray[14]);
	   d.setTn(strArray[15]);
	   d.setTx(strArray[16]);
	   d.setCl(strArray[17]);
	   d.setNh(strArray[18]);
	   d.setH(strArray[19]);
	   d.setCm(strArray[20]);
	   d.setCh(strArray[21]);
	   d.setVV(strArray[22]);
	   d.setTd(strArray[23]);
	   d.setRRR(strArray[24]);
	   d.settR(strArray[25]);
	   d.setE(strArray[26]);
	   d.setTg(strArray[27]);
	   d.setE1(strArray[28]);
	   d.setSss(strArray[29]);

	   str.add(d);
	   d = new WeatherData();
	   d.setRoad(url);
	   strW = new String();
	   strW = strTime + ",";
        }

        return str;
    }
}
