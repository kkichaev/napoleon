using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{ 
   class FmSlsnetEx : FmSlsnet
   {
      public FmSlsnetEx()
      {
         DataGridViewTextBoxColumn c = new DataGridViewTextBoxColumn();
         c.DataPropertyName = "Koef";
         c.HeaderText = "Коэффициент";
         c.Name = "koef";
         c.DisplayIndex = 1;
         c.AutoSizeMode = DataGridViewAutoSizeColumnMode.NotSet;
         grid.Columns.Add(c);

         //grid.Columns.Add(c);
      }

      protected override void Add()
      {
         FmSlsnetEditEx dialog = new FmSlsnetEditEx();

         if (dialog.ShowDialog() == DialogResult.OK)
         {
            Slsnet sls = new Slsnet();
            sls.id = GRSoft.Network.DataObject.GenId();
            sls.name = dialog.Slsnet;
            sls.plan = dialog.Plan;
            sls.koef = dialog.Koef;

            datasource.Add(sls);
            dsSlsnet.Add(sls.id, sls);

            lastupdateitem = sls.id;
            grid.Invalidate();
            btnSave.Enabled = true;
         }
      }

      protected override void Edit()
      {
         if (grid.CurrentRow != null)
         {
            FmSlsnetEditEx dialog = new FmSlsnetEditEx();
            Slsnet sls = grid.CurrentRow.DataBoundItem as Slsnet;

            if (sls != null)
            {
               dialog.Slsnet = sls.Name;
               dialog.Plan = sls.Plan;
               dialog.Koef = sls.Koef;

               if (dialog.ShowDialog() == DialogResult.OK)
               {
                  sls.name = dialog.Slsnet;
                  sls.plan = dialog.Plan;
                  sls.koef = dialog.Koef;
                  btnSave.Enabled = true;
                  lastupdateitem = sls.id;
                  grid.Invalidate();
               }
            }
         }
      }
   }

   
}
