using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmConfigEx : FmConfig
   {
      CheckBox cbScript;
      TextBox tbScriptVal;
      CheckBox cbVisit;
      TextBox tbVisitVal;

      public FmConfigEx()
      {
         Size = new System.Drawing.Size(490, 360);
         cbScript = new CheckBox();
         cbScript.Text = "Выделять красным сценарий если время работы меньше (мин.)";
         cbScript.Location = new System.Drawing.Point(85, 145);
         cbScript.Size = new System.Drawing.Size(370, 17);

         tbScriptVal = new TextBox();
         tbScriptVal.Location = new System.Drawing.Point(85, 169);
         tbScriptVal.Size = new System.Drawing.Size(100, 17);

         cbVisit = new CheckBox();
         cbVisit.Text = "Выделять красным посещение если перерыв больше (мин.)";
         cbVisit.Location = new System.Drawing.Point(85, 193);
         cbVisit.Size = new System.Drawing.Size(370, 17);

         tbVisitVal = new TextBox();
         tbVisitVal.Location = new System.Drawing.Point(85, 217);
         tbVisitVal.Size = new System.Drawing.Size(100, 17);

         tpAdd.Controls.Add(cbScript);
         tpAdd.Controls.Add(tbScriptVal);
         tpAdd.Controls.Add(cbVisit);
         tpAdd.Controls.Add(tbVisitVal);

         cbScript.CheckedChanged += CbScript_CheckedChanged;
         cbVisit.CheckedChanged += CbVisit_CheckedChanged;

         tbVisitVal.GotFocus += TbVisitVal_GotFocus;
         tbScriptVal.GotFocus += TbVisitVal_GotFocus;
      }

      private void TbVisitVal_GotFocus(object sender, EventArgs e)
      {
         ((TextBox)sender).SelectAll();
      }

      private void CbVisit_CheckedChanged(object sender, EventArgs e)
      {
         tbVisitVal.Enabled = ((CheckBox)sender).Checked;

         if (!tbVisitVal.Enabled)
            tbVisitVal.Text = "0";
         else
            tbVisitVal.Focus();
      }

      private void CbScript_CheckedChanged(object sender, EventArgs e)
      {
         tbScriptVal.Enabled = ((CheckBox)sender).Checked;

         if (!tbScriptVal.Enabled)
            tbScriptVal.Text = "0";
         else
            tbScriptVal.Focus();
      }

      protected override void SetControlsFromConfig()
      {
         base.SetControlsFromConfig();

         cbScript.Checked = config.scriptWorkMinTime > 0;
         tbScriptVal.Text = config.scriptWorkMinTime.ToString();
         tbScriptVal.Enabled = cbScript.Checked; 

         cbVisit.Checked = config.visitBreakeMaxTime > 0;
         tbVisitVal.Text = config.visitBreakeMaxTime.ToString();
         tbVisitVal.Enabled = cbVisit.Checked;
      }

      protected override void FillConfigFromControls()
      {
         base.FillConfigFromControls();

         int swt = 0, vbt = 0;

         if (cbScript.Checked && int.TryParse(tbScriptVal.Text.Trim(), out swt))
            config.scriptWorkMinTime = swt;
         else
            config.scriptWorkMinTime = 0;


         if (cbVisit.Checked && int.TryParse(tbVisitVal.Text.Trim(), out swt))
            config.visitBreakeMaxTime = swt;
         else
            config.visitBreakeMaxTime = 0;
      }
   }
}
