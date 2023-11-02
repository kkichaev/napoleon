using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Maps;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmUserLocation : Form
   {
      private SimpleDataSet<UserLocation> dsUserLocation = new SimpleDataSet<UserLocation>(UserLocation.OBJECT_NAME, false);

      public FmUserLocation()
      {
         InitializeComponent();

         wb.Init(true);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsUserLocation);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         List<UserLocation> list = new List<UserLocation>();
         list.AddRange(dsUserLocation.Values);
         list.Sort((x, y) => { return x.UserName.CompareTo(y.UserName); });

         List<UserLocationData> data = new List<UserLocationData>();
         int pos = 1;

         Manager mc = CurrentUser.user as Manager;

         if (mc != null)
         {
            Agents agents = mc.GetAgents();

            foreach (UserLocation u in list)
            {
               if (u.Date >= DateTime.Now.Date && agents.ContainsKey(u.userid))
               {
                  UserLocationData ud = new UserLocationData();
                  ud.pos = pos++;
                  ud.location = u;
                  data.Add(ud);
               }
            }
         }

         grid.DataSource = data;

         wb.Navigate(MapEngine.UserLocation(data));
         //wb.DocumentText = MapEngine.UserLocation(data);
      }

      private void FmUserLocation_Load(object sender, EventArgs e)
      {
         btnRefresh.PerformClick();
      }

      private void grid_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         UserLocationData u = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as UserLocationData;
         if (u != null)
         {
            UserLocation ul = u.location;

            if (ul != null)
            {
               Type prcType = FormEntries.GetFormType(typeof(FmRoute));
               ConstructorInfo ci = prcType.GetConstructor(new Type[] { typeof(string), typeof(DateTime) });
               FmRoute route = (FmRoute)ci.Invoke(new object[] { ul.userid, ul.date.Date });
               route.Show();
            }
         }
      }

      private void grid_RowEnter(object sender, DataGridViewCellEventArgs e)
      {
         if (e.RowIndex != -1)
         {
            UserLocationData u = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as UserLocationData;
            if (u != null)
            {
               UserLocation ul = u.location;

               if(ul != null && wb.Inited)
               {
                  string txt = String.Format("ShowInfo('{0}','{1}');", ul.latitude.ToString().Replace(",", "."), ul.longitude.ToString().Replace(",", "."));
                  wb.ExecuteScript(txt);
               }
               //if (ul != null && wb.Document != null)
               //{
               //   wb.Document.InvokeScript("ShowInfo", new object[] { ul.latitude.ToString().Replace(",", "."), ul.longitude.ToString().Replace(",", ".") });
               //}
            }
         }
      }
   }

   class UserLocationData
   {
      public int pos = 0;
      public UserLocation location;
      public int Pos { get { return pos; } }
      public string UserName { get { return location != null ? location.UserName : string.Empty; } }
      public string TimeStr { get { return location != null ? location.Date.ToShortTimeString() : string.Empty; } }
   }
}
