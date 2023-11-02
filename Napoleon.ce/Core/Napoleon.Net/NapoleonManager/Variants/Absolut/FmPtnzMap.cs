using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;
using System.Globalization;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmPtnzMap : Form
   {
      DataSet<string, PotenzialOrgEx> porgs = new DataSet<string, PotenzialOrgEx>(PotenzialOrg.OBJECT_NAME, false);

      public FmPtnzMap()
      {
         InitializeComponent();
      }

      private void FmPtnzMap_Load(object sender, EventArgs e)
      {
         List<Agent> list = new List<Agent>();

         foreach (Agent a in CurrentUser.user.GetAgents().Data)
            list.Add(a);

         list.Sort(new Comparison<Agent>(delegate(Agent a1, Agent a2) { return a1.Name.CompareTo(a2.Name); }));
         cbAgents.Items.AddRange(list.ToArray());
         cbAgents.SelectedIndex = 0;

         cbFilter.Items.Add("<Все>");
         cbFilter.Items.Add("Наши");
         cbFilter.Items.Add("Ценсус");
         cbFilter.SelectedIndex = 0;
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Agent a = cbAgents.SelectedItem as Agent;

         if (a != null)
         {
            porgs.Filter = "\"userid\" in('" + a.id +"')";
            DataModule.SetDataRepsonceHandlers(DataProcessed, DataConnectionError);
            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(porgs);
            FmWait.ShowForm(this, DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(),
               updSet, FmWait.ProgressIndicator));
         }
      }

      //Окончание выборки, заполняются внутренние наборы
      void DataProcessed(System.Object setnder, EventArgs e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         ShowMap();
      }

      private void ShowMap()
      {
         
         Invoke(new EmptyParamHandler(delegate()
         {
            StringBuilder sb = new StringBuilder();
            sb.Append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            sb.Append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            sb.Append("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
            sb.Append("<script src=\"http://api-maps.yandex.ru/1.1/index.xml?key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==&\" type=\"text/javascript\"></script>\n");
            sb.Append("<script type=\"text/javascript\">\n");
            sb.Append("YMaps.jQuery(function () { \n");
            sb.Append("var map = new YMaps.Map(YMaps.jQuery(\"#YMapsID\")[0]); \n");
            sb.Append("map.addControl(new YMaps.TypeControl());\n");
            sb.Append("map.addControl(new YMaps.ToolBar()); \n");
            sb.Append("map.addControl(new YMaps.Zoom()); \n");
            sb.Append("map.addControl(new YMaps.MiniMap()); \n");
            sb.Append("map.addControl(new YMaps.ScaleLine()); \n");
            sb.Append("var placemark; \n");
            sb.Append("var point; \n");
            sb.Append("var bnds = new YMaps.GeoCollectionBounds(); \n");

            foreach (PotenzialOrgEx p in porgs.Data)
            {
               if (cbFilter.SelectedIndex == 1 && p.outer == 0 ||
                  cbFilter.SelectedIndex == 2 && p.outer == 1)
                  continue;

               if (p.latitude == 0 || p.longitude == 0)
               {
                  Location l = Route.GetLocation(p.address);

                  if (l == null)
                     continue;

                  p.latitude = l.Latitude;
                  p.longitude = l.Longitude;
               }

               sb.Append("point = new YMaps.GeoPoint(" +
                  p.longitude.ToString(CultureInfo.InvariantCulture.NumberFormat) + ", " +
                  p.latitude.ToString(CultureInfo.InvariantCulture.NumberFormat) + ");\n");
               sb.Append("bnds.add(point);\n");
               string color = p.outer == 0 ? "red" : "blue";
               sb.Append("placemark = new YMaps.Placemark(point, {style: \"default#" + color + "SmallPoint\"});\n");
               sb.Append("placemark.description=\"" + StringUtil.EscapeQuotes(p.Name) + "<br><i>" + 
                  StringUtil.EscapeQuotes(p.Address) + "</i>\";\n");
               
               //sb.Append("placemark.setIconContent(\"<font color=" + color + "><b>" + StringUtil.EscapeQuotes(p.Name) + "</b></font>\");");
               //sb.Append("var layout = placemark.getContentLayout();layout.getRootNodes().css('color', 'red');");
               sb.Append("map.addOverlay(placemark);\n");
            }

            sb.Append("map.setBounds(bnds);\n");

            sb.Append("var zoom = map.getZoom();\n");
            sb.Append("if( zoom > 0 ) { map.setZoom(zoom-1); }\n");

            sb.Append("});\n");
            sb.Append("</script><style type=\"text/css\">\n");
            sb.Append("body, html { width:100%; height:100%; margin:0; padding:0; overflow:hidden;}\n");
            sb.Append("</style>\n");
            sb.Append("</head>\n");
            sb.Append("<body>\n");
            sb.Append("<div id=\"YMapsID\" style=\"width:100%;height:100%\"></div> \n");
            sb.Append("</body> \n");
            sb.Append("</html>\n");

            //File.WriteAllText("ttt2.html", sb.ToString());
            wb.DocumentText = sb.ToString();
         }));
         
      }

      //Произошла ошибка в соединении
      private void DataConnectionError(EDataResponse e)
      {
         DataModule.ClearEvents();
         FmWait.CloseForm();

         Invoke(new EmptyParamHandler(delegate
         {
            const string TITLE = "Ошибка";

            MessageBox.Show(e.Msg, TITLE, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      private void cbFilter_DropDownClosed(object sender, EventArgs e)
      {
         ShowMap();
      }
   }

   public class PotenzialOrgEx: PotenzialOrg
   {
      public int outer;
   }
}
