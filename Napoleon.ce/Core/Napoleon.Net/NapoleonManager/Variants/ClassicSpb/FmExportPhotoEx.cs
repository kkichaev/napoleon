using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Globalization;
using System.Collections;
using System.IO;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;

namespace GRSoft.NapoleonManager
{
   public class FmExportPhotoEx : FmExportPhotoSpb
   {
      SimpleDataSet<ScriptDoc> dsScripts;
      ComboBox cbSuppl;

      Dictionary<DateTime, Boolean> scripts;
      Supplier selectedSuppl;

      public FmExportPhotoEx()
         : base()
      {
         Height += 50;

         cbSuppl = new ComboBox();
         cbSuppl.FormattingEnabled = true;
         cbSuppl.Location = new System.Drawing.Point(100, 222);
         cbSuppl.Name = "cbSuppl";
         cbSuppl.Size = new System.Drawing.Size(154, 22);
         cbSuppl.Enabled = false;

         Label lbl = new Label();
         lbl.AutoSize = true;
         lbl.Location = new System.Drawing.Point(20, 222);
         lbl.Name = "lbl";
         lbl.Size = new System.Drawing.Size(37, 14);
         lbl.Text = "поставщик";
         lbl.Visible = true;


         groupBox2.Height += 50;
         groupBox2.Controls.AddRange(new Control[] { cbSuppl, lbl });

         DataSet<string, Supplier> spl = (DataSet<string, Supplier>)DataModule.Get(Supplier.OBJECT_NAME);
         if(spl == null || spl.Count == 0)
         {
            spl = new DataSet<string, Supplier>(Supplier.OBJECT_NAME);
            Thread t = DataModule.RefreshDataSet(spl, Config.GetConfig().GetConnection(), false, null);
            t.Join();
         }

         List<Supplier> src = new List<Supplier>(spl.Values);
         src.Sort();

         Supplier ot = new Supplier();
         ot.name = "<Для всех>";
         src.Insert(0, ot);
         foreach (Supplier s in src)
            cbSuppl.Items.Add(s);

         cbSuppl.SelectedIndex = 0;

         dsScripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

         cbScript.CheckedChanged += cbScript_CheckedChanged;
      }

      private void cbScript_CheckedChanged(object sender, EventArgs e)
      {
         cbSuppl.Enabled = ((CheckBox)sender).Checked;
      }

      protected override void BeforeStarting()
      {
         selectedSuppl = cbSuppl.SelectedItem as Supplier;
      }


      protected override string ScriptFilter(string COMMON_FILTER_STR)
      {
         String res =  base.ScriptFilter(COMMON_FILTER_STR);

         if (cbScript.Checked && selectedSuppl.id.Length > 0)
         {
            dsScripts.Filter += " and  scriptId in (select id from ScriptDef where suppl = '" + selectedSuppl.id + "')";
         }

         return res;
      }

      public override string GetFolder()
      {
         string baseDir = base.GetFolder();
         if (selectedSuppl.id.Length > 0)
            baseDir += "\\" + WinChar(selectedSuppl.name);
         return baseDir;
      }
   }
}