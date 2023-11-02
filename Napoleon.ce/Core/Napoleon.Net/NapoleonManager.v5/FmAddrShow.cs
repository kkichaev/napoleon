using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Globalization;
using GRSoft.NapoleonManager.Maps;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmAddrShow : Form
   {
      private string address;
      private Org org;
      private static FmAddrShow instance;
      Location loc = null;

      public FmAddrShow(string address, Org org)
      {
         this.address = address;
         this.org = org;
         InitializeComponent();
         wb.Init(true);
      }

      public FmAddrShow(Location loc, Org org)
      {
         this.loc = loc;
         this.org = org;
         InitializeComponent();
         
         wb.Init(true);
      }

      public FmAddrShow()
      {
         InitializeComponent();
         wb.Init(true);
      }

      public void ShowMap(string mapHTML)
      {
         //wb.DocumentText = mapHTML;
         wb.Navigate(mapHTML);
      }

      private void FmAddrShow_Shown(object sender, EventArgs e)
      {
         if (org == null)
            return;

         Text = String.Format("Наполеон  {0}", org.name);

         if (loc == null)
         {
            loc = Route.GetLocation(org);
            if (loc == null)
               loc = Route.GetLocation(address);
            //Location loc = Route.GetLocation(address.Replace(',', ' '));

            if (loc == null)
            {
               wb.Navigate(ErrorHtmlPage.GetErrorPage(address));
               //wb.DocumentText = ErrorHtmlPage.GetErrorPage(address);
               return;
            }
         }
         
         string txt = MapEngine.OrgAddress(Config.GetConfig().mapSource, org.name, loc);
#if MAKE_HTML_FILE
         File.WriteAllText("org.html", txt);
#endif
         //wb.DocumentText = txt;
         wb.Navigate(txt);
      }

      public static void AddrShow(string address, Org org)
      {
         if (instance == null)
         {
            instance = new FmAddrShow(address,org);
         }

         instance.Show();
      }

      public static void AddrShow(Location loc, Org org)
      {
         if (instance == null)
         {
            instance = new FmAddrShow(loc, org);
         }

         instance.Show();
      }

      private void FmAddrShow_FormClosed(object sender, FormClosedEventArgs e)
      {
         instance = null;
      }

      private class ErrorHtmlPage
      {
         private static string CONTENT_STR = "Невозможно получить данные по адресу: {0}";
         
         public static string GetErrorPage(string address)
         { 
            string c = String.Format(CONTENT_STR, address);
            return String.Format("<html><body>{0}</body></html>", c);
         }
      }

      //private class BallonHTMLPage
      //{
      //   private static string C1_STR = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
      //                                        "<html xmlns=\"http://www.w3.org/1999/xhtml\"> \n" +
      //                                        "<head>" +
      //                                        "<title>Примеры. Задание стиля для содержимого балуна.</title> \n" +
      //                                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /> \n" +
      //                                        //Ключ взят с демонстраций на сайте
      //                                        //"<script src=\"http://api-maps.yandex.ru/1.1/index.xml?key=ANpUFEkBAAAAf7jmJwMAHGZHrcKNDsbEqEVjEUtCmufxQMwAAAAAAAAAAAAvVrubVT4btztbduoIgTLAeFILaQ==\" type=\"text/javascript\"></script> \n" +
      //                                        //Ключ Дениса
      //                                        "<script src=\"http://api-maps.yandex.ru/1.1/index.xml?key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&\" type=\"text/javascript\"></script> \n" +                                  
      //                                        "<script type=\"text/javascript\"> \n" +
      //                                        "// Создание обработчика для события window.onLoad \n" +
      //                                        "YMaps.jQuery(function () { \n" +
      //                                        "// Создание экземпляра карты и его привязка к созданному контейнеру \n" +
      //                                        "var map = new YMaps.Map(YMaps.jQuery(\"#YMapsID\")[0]); \n" +

      //                                        "// Установка для карты ее центра и масштаба \n" +
      //                                        "map.setCenter(new YMaps.GeoPoint(";
      //   private static string C2_STR = "), 14); \n" +
      //                                        "// Добавление элементов управления \n" +
      //                                        "map.addControl(new YMaps.TypeControl()); \n" +
      //                                        "map.addControl(new YMaps.ToolBar()); \n" +
      //                                        "map.addControl(new YMaps.Zoom()); \n" +
      //                                        "map.addControl(new YMaps.MiniMap()); \n" +
      //                                        "map.addControl(new YMaps.ScaleLine()); \n" +

      //                                        "// Создание стиля для содержимого балуна \n" +
      //                                        "var s = new YMaps.Style(); \n" +
      //                                        "s.balloonContentStyle = new YMaps.BalloonContentStyle( "+
      //                                        "new YMaps.Template(\"<div style=\\\"color:green\\\">$[description]</div>\")" +
      //                                        "); \n" +

      //                                        "// Создание метки с пользовательским стилем и добавление ее на карту \n" +
      //                                        "var placemark = new YMaps.Placemark(new YMaps.GeoPoint(";
      //   private static string C3_STR = "), {style: s} ); \n" +
      //                                        "placemark.description = \"";
      //   private static string C4_STR = "\"; \n" +
      //                                        "map.addOverlay(placemark); \n" +

      //                                        "// Открытие балуна \n" +
      //                                        "placemark.openBalloon(); \n" +
      //                                        "}); \n" +
      //                                        "</script> \n" +
      //                                        "</head> \n" +

      //                                        "<body> \n" +
      //                                        "<div id=\"YMapsID\" style=\"width:600px;height:400px\"></div> \n" +
      //                                        "</body> \n" +

      //                                        "</html>";
      //   public static string GetContent(string org, Location loc)
      //   {
      //      CultureInfo enus = CultureInfo.GetCultureInfo("en-US");
      //      return C1_STR + String.Format(enus, "{0},{1}",loc.longitude,loc.latitude) + C2_STR +
      //         String.Format(enus, "{0},{1}", loc.longitude, loc.latitude) + C3_STR + StringUtil.EscapeQuotes(org) + C4_STR;
      //   }
      //}
   }
}