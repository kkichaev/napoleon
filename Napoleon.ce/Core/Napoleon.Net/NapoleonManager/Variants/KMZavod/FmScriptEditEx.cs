using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptEditEx : FmScriptEdit
   {
      CheckBox cbPhone;

      protected FmScriptEditEx(PostProcess postProcess)
         : base(postProcess)
      {
         cbPhone = new CheckBox();
         cbPhone.Text = "По телефону";
         cbPhone.Location = new Point(10,20);
         cbPhone.CheckedChanged += cbPhone_CheckedChanged;

         splitContainer1.Dock = DockStyle.None;

         panel1.Dock = DockStyle.None;
         panel1.Controls.Add(cbPhone);
         panel1.Height = 50;
         panel1.Dock = DockStyle.Top;

         splitContainer1.Top = panel1.Bottom;
         splitContainer1.Dock = DockStyle.Fill;
      }

      void cbPhone_CheckedChanged(object sender, EventArgs e)
      {
         btnSave.Enabled = true;
      }

      protected override void LoadData()
      {
         base.LoadData();

         if (script != null)
         {
            cbPhone.Checked = script.phone != 0;
         }
      }

      protected override void BeforeSaveDef()
      {
         base.BeforeSaveDef();

         script.phone = cbPhone.Checked ? 1 : 0;
      }
   }
}
