using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class MainFormEx : MainForm
   {
      public DataSet<int, Matrix> dsMatrix;

      public MainFormEx()
      {
         dsMatrix = new DataSet<int, Matrix>(Matrix.OBJECT_NAME);

         ToolStripButton button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.document_export;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnRekZak";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Оценка выполнения рекомендованного заказа";
         button.Click += new System.EventHandler((s, e) => { RekZakReport.Do(dtpBeginDate.Value, this); });

         tsbConfig.Items.Add(button);

         button = new System.Windows.Forms.ToolStripButton();
         button.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         button.Image = Properties.Resources.audit_doc;
         button.ImageTransparentColor = System.Drawing.Color.Magenta;
         button.Name = "btnAudit";
         button.Size = new System.Drawing.Size(23, 22);
         button.Text = "Аудит дистрибьюции";
         button.Click += new System.EventHandler((s, e) => { AuditReport.Do(dtpBeginDate.Value, this); });

         tsbConfig.Items.Add(button);
      }

      protected override void AddUpdateDataSet(List<IDataSet> updSets)
      {
         base.AddUpdateDataSet(updSets);
         updSets.Add(dsMatrix);
      }
   }
}
