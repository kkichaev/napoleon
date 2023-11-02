using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Reflection;
using System.IO;
using System.Threading;


namespace GRSoft.NapoleonManager
{
   public partial class FmUserLocation : Form
   {
      private SimpleDataSet<UserLocation> dsUserLocation = new SimpleDataSet<UserLocation>(UserLocation.OBJECT_NAME, false);
      private MapHelper mapHelper = new MapHelper();

      public FmUserLocation()
      {
         InitializeComponent();
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

         foreach (UserLocation u in list)
         {
            if (u.Date >= DateTime.Now.Date)
            {
               UserLocationData ud = new UserLocationData();
               ud.pos = pos++;
               ud.location = u;
               data.Add(ud);
            }
         }

         grid.RowEnter -= grid_RowEnter;
         grid.DataSource = data;
         grid.RowEnter += grid_RowEnter;

         string map = cbMaps.SelectedItem.ToString();
         wb.DocumentText = mapHelper.CreateMap(map, "userlocation", data);
      }

      private void FmUserLocation_Load(object sender, EventArgs e)
      {
         mapHelper.InitControl(cbMaps);
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
               wb.Document.InvokeScript("showInfo", new object[] { u.Pos });
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
