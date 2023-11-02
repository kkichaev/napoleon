using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmJobsTypeEdit : Form
   {
      private bool addMode = false;

      public FmJobsTypeEdit()
      {
         InitializeComponent();
      }

      public static bool ShowInstance(JobType jobtype)
      {
         FmJobsTypeEdit instance = new FmJobsTypeEdit();
         instance.addMode = jobtype == null;
         bool result = false;

         if (jobtype != null)
         {
            instance.tbName.Text = jobtype.name;
            instance.pnlColor.BackColor = jobtype.Color;
            instance.lblTip.ForeColor = instance.
               InvertAColor(instance.pnlColor.BackColor);
         }
         else
            jobtype = new JobType();

         if (instance.addMode)
            instance.Text = "Создать";
         else
            instance.Text = "Изменить";

         if (instance.ShowDialog() == DialogResult.OK)
         {
            JobType newJobType = jobtype ?? new JobType();
            newJobType.name = instance.tbName.Text;
            newJobType.Color = instance.pnlColor.BackColor;

            DsJobType dsJobType = new DsJobType(false);
            dsJobType.Add(1, newJobType);

            List<IDataSet> updSet = new List<IDataSet>();
            updSet.Add(dsJobType);

            if (instance.addMode)
               result = DataModule.InsertDataSets(updSet, Config.GetConfig().GetConnection());
            else
               result = DataModule.UpdateDataSet(updSet, null, null, Config.GetConfig().GetConnection());
         }

         return result;
      }

      private void pnlColor_Click(object sender, EventArgs e)
      {
         if (colorDialog1.ShowDialog() == DialogResult.OK)
         {
            pnlColor.BackColor = colorDialog1.Color;
            lblTip.ForeColor = InvertAColor(pnlColor.BackColor);

         }
      }

      private Color InvertAColor(Color ColorToInvert)
      {
         return Color.FromArgb((byte)~ColorToInvert.R, 
            (byte)~ColorToInvert.G, (byte)~ColorToInvert.B);
      }
   }
}
