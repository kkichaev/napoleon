using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      Button editDalyRoute;

      public UserFormEx(Divisions owner) :
         base(owner)
      {
         InitControls();
      }

      private void InitControls()
      {
         editDalyRoute = new Button();
         panel3.Controls.Add(editDalyRoute);

         this.editDalyRoute.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         this.editDalyRoute.Location = new System.Drawing.Point(77, 6);
         this.editDalyRoute.Name = "editDalyRoute";
         this.editDalyRoute.Size = new System.Drawing.Size(90, 23);
         this.editDalyRoute.TabIndex = 2;
         this.editDalyRoute.Text = "Доп. маршрут";
         this.editDalyRoute.UseVisualStyleBackColor = true;
         this.editDalyRoute.Click += new System.EventHandler(this.editDalyRoute_Click);
      }

      private void editDalyRoute_Click(object sender, EventArgs e)
      {
         FmDailyRouteEditor route = new FmDailyRouteEditor();
         if (Agent != null)
            route.SetCurrentAgent(Agent.id);
         route.Show();
      }
   }
}