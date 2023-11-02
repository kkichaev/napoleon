using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmExportPhotoEx : FmExportPhoto
   {
      private CheckBox cbFridge;
      protected DataSet<int, InvFrgSt1> dsInvFrgSt1;
      protected DataSet<int, InvFrgSt2> dsInvFrgSt2;
      protected DataSet<int, InvFrgSt3> dsInvFrgSt3;

      public FmExportPhotoEx()
      {
         dsInvFrgSt1 = (DataSet<int, InvFrgSt1>)DataModule.Get(InvFrgSt1.OBJECT_NAME) ?? new DataSet<int, InvFrgSt1>(InvFrgSt1.OBJECT_NAME);
         dsInvFrgSt2 = (DataSet<int, InvFrgSt2>)DataModule.Get(InvFrgSt2.OBJECT_NAME) ?? new DataSet<int, InvFrgSt2>(InvFrgSt2.OBJECT_NAME);
         dsInvFrgSt3 = (DataSet<int, InvFrgSt3>)DataModule.Get(InvFrgSt3.OBJECT_NAME) ?? new DataSet<int, InvFrgSt3>(InvFrgSt3.OBJECT_NAME);

         cbFridge = new CheckBox();
         cbFridge.Text = "Холодильное оборудование";
         cbFridge.Location = new System.Drawing.Point(390, 38);
         cbFridge.Size = new System.Drawing.Size(200, 17);
         cbFridge.Name = "cbFridge";

         Controls.Add(cbFridge);
      }

      protected override void AddDataSet(List<IDataSet> upd, Agent a, DateTime start, DateTime finish)
      {
         base.AddDataSet(upd, a, start, finish);

         if (cbFridge.Checked)
         {
            dsInvFrgSt1.Filter = String.Format(COMMON_FILTER_STR, "date", start, start.AddDays(1), a.id);
            dsInvFrgSt2.Filter = String.Format(COMMON_FILTER_STR, "date", start, start.AddDays(1), a.id);
            dsInvFrgSt3.Filter = String.Format(COMMON_FILTER_STR, "date", start, start.AddDays(1), a.id);

            upd.Remove(dsVisit);
            upd.Add(dsInvFrgSt1);
            upd.Add(dsInvFrgSt2);
            upd.Add(dsInvFrgSt3);
         }
      }

      protected override void MakeVisitList(List<Visit> list)
      {
         base.MakeVisitList(list);

         if (cbFridge.Checked)
         {
            foreach (InvFrgSt1 v in dsInvFrgSt1.Values)
               list.Add(v);

            foreach (InvFrgSt2 v in dsInvFrgSt2.Values)
               list.Add(v);

            foreach (InvFrgSt3 v in dsInvFrgSt3.Values)
               list.Add(v);
         }
      }

      protected override string GetPhotoText(BaseDocument doc)
      {
         if (cbFridge.Checked) 
         {
            StringBuilder sb = new StringBuilder();

            string ex = DocTitle(doc);
            sb.Append(ex);
            sb.Append(doc.OrgName).Append(" ");
            sb.Append(doc.Address).Append(" ");
            sb.Append(doc.created.ToString("dd/MM/yyyy HH:mm"));
            return sb.ToString();
         }
         else
            return base.GetPhotoText(doc);
      }

      private static string DocTitle(BaseDocument doc)
      {
         string ex = string.Empty;

         if (doc is InvFrgSt1)
            ex = "Общий план ХО ";

         if (doc is InvFrgSt2)
            ex = "Фото дефекта";

         if (doc is InvFrgSt3)
            ex = "Фото ИН ";
         return ex;
      }

      protected override string FileName(string saveName, Visit v, int cnt, string dir)
      {
         string ex = DocTitle(v);

         dir = dir + "\\" + WinChar(v.OrgName);

         if (!Directory.Exists(dir))
            Directory.CreateDirectory(dir);

         return string.Format(saveName, dir, WinChar(ex + v.OrgName), string.Empty/*WinChar(v.OrgAddr)*/, WinChar(v.Created.ToShortDateString()), cnt);
      }
   }
}
