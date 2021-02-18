import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import java.net.*;
import java.util.regex.*;


import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {

    public void getData(Element tr ) {
        Element legend = tr.select("td").first();
        String text_legend = legend.text();
        System.out.print(text_legend + ":" + "  ");
        Elements td_elem = tr.select("td[class=meteo]");
        for (Element td : td_elem) {
            String text_meteo = td.text();
            System.out.print(text_meteo + "   ");

        }
    }


    public Document getPage() {
   String url = "http://www.pogoda.by/34214";
  Document page = null;
   try {
    page = Jsoup.parse(new URL(url), 10000);

    Element tablebody = page.select("table[id=forecast]").first(); //first
       System.out.print("Дата:" + "  ");
       for(int i =0; i < 10; ++i) {
         Element td = tablebody.select("td[class=dat]").get(i);
         String text = td.text();
         String[] text_set = text.split(" ");
         System.out.print(text_set[0]  + " " + text_set[1] + "   ");
     }
       System.out.println();
       System.out.print("      ");
       for(int i =0; i < 10; ++i) {
           Element td = tablebody.select("td[class=dat]").get(i);
           String text = td.text();
           String[] text_set = text.split(" ");
           System.out.print(text_set[2] + " " + text_set[3] + "  ");
       }
       System.out.println();

       Element tr1 = tablebody.select("tr").get(1);            //second

       Pattern pattern1 = Pattern.compile("Переменная облачность.Без осадков.|Ясно.Без осадков.");
       Element legend = tr1.select("td").first();
       String text_legend = legend.text();
       System.out.print(text_legend + ":" + "  ");
           Elements img = tr1.select("img");
           for(Element t : img) {
               String text_img = t.toString();
              Matcher mather1 = pattern1.matcher(text_img);
              if (mather1.find()) {
                  System.out.print(mather1.group()+ " ");
               }
       }
       System.out.println();

       for (int i = 2; i<7; ++i) {
           Element tr_e = tablebody.select("tr").get(i);
           getData(tr_e);
           System.out.println();
       }


   } catch (IOException ex) { ex.printStackTrace();}
        System.out.println(".>");
   return page;

 }


}
