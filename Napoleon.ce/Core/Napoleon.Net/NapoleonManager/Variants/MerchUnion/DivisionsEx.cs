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
         ToolStripButton btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
         btn.Name = "btnOrgProp";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Сценарии точек";
         btn.Click += new System.EventHandler((obj, arg) =>
         {
            new FmOrgProp().Show();
         });

         tb.Items.Add(btn);

         btn = new System.Windows.Forms.ToolStripButton();
         btn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         btn.Image = GRSoft.NapoleonManager.Properties.Resources.ic_perm_contact_calendar;
         btn.Name = "btnOrgs";
         btn.Size = new System.Drawing.Size(101, 22);
         btn.Text = "Клиенты";
         btn.Click += new System.EventHandler((obj, arg) =>
         {
            new FmOrgs().Show();
         });

         tb.Items.Add(btn);

      }
   }
}
