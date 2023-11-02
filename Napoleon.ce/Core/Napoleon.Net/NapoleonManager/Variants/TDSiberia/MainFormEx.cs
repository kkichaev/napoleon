using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public MainFormEx() 
      {
         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.qty2report;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "qty2report";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Товар под заказ производителю";
         button.Click += new System.EventHandler(qty2Report_Click);

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.distrib_doc;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mtxtimw";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Oтчёт по времени работы в матрицах";
         button.Click += new System.EventHandler((s, e) => { MtxTimeRpt.Do(dtpBeginDate.Value.Date, dtpEndDate.Value.Date, this); });

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.abiword_3;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mtxtimw";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Oтчёт по времени пребывания в точке";
         button.Click += new System.EventHandler((s, e) => { ScriptTimeRpt.Do(dtpBeginDate.Value.Date, dtpEndDate.Value.Date, this); });

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.document_export;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "mtxremark";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Комментарий к группе";
         button.Click += new System.EventHandler((s, e) => { new FmMtxRemark().Show(); });

         tsbConfig.Items.Add(button);
      }

      private void qty2Report_Click(object sender, EventArgs e)
      {
         new FmQty2Report().Show();
      }
   }
}
