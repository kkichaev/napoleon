using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class DivisionsEx : Divisions
   {
      public DivisionsEx()
      {

         //ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         //btn.Name = "btnOrgMatrix";
         //btn.Size = new System.Drawing.Size(23, 22);
         //btn.Text = "Номенклатура для закупки";
         //btn.Click += new System.EventHandler((s, e) => { new FmPurchaseTemplateEdit().Show(); });
         //btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         //tb.Items.Add(btn);

         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnPayTypes";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Типы оплат";
         btn.Click += new System.EventHandler((s, e) => { new FmPayTypes().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnClientTypes";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Типы клиентов";
         btn.Click += new System.EventHandler((s, e) => { new FmClientTypes().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnFormatTT";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "Формат ТТ";
         btn.Click += new System.EventHandler((s, e) => { new FmFormatTT().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.Name = "btnBNOper";
         btn.Size = new System.Drawing.Size(23, 22);
         btn.Text = "БН Операции";
         btn.Click += new System.EventHandler((s, e) => { new FmBNOper().Show(); });
         btn.DisplayStyle = ToolStripItemDisplayStyle.Text;

         tb.Items.Add(btn);

         Size = new System.Drawing.Size(Width + 150, Height);
      }

      protected override void CheckData()
      {
         base.CheckData();
      }
   }
}
