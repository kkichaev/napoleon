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
      public UserFormEx(Divisions owner) :
         base(owner)
      {
         InitControls();
      }

      private void InitControls()
      {
         Button mtxBtn = new Button();
         mtxBtn.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
         Point p = new Point(btnEditRoute.Left, btnEditRoute.Top);
         p.Offset(btnEditRoute.Width + 2, 0);
         mtxBtn.Location = p;
         mtxBtn.Name = "mtxBtn";
         mtxBtn.Size = new System.Drawing.Size(150, btnEditRoute.Height);
         mtxBtn.TabIndex = 2;
         mtxBtn.Text = "Координаты клиента";
         mtxBtn.UseVisualStyleBackColor = true;
         mtxBtn.Click += new EventHandler((o,e) => SelectOrgLocation.Open(Agent));
         
         panel3.Controls.Add(mtxBtn);
      }

   }
}