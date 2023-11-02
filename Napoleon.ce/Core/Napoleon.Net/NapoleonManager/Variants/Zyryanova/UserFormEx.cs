using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    class UserFormEx : UserForm
    {
        public UserFormEx(Divisions owner) : base(owner)
        {
            Button btnContracts = new Button();

            btnContracts.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            Point p = new Point(btnOrgLocation.Left, btnOrgLocation.Top);
            p.Offset(btnOrgLocation.Width + 2, 0);
            btnContracts.Location = p;
            btnContracts.Name = "mtxBtnctr";
            btnContracts.Size = new System.Drawing.Size(150, btnEditRoute.Height);
            btnContracts.TabIndex = 3;
            btnContracts.Text = "Контракты";
            btnContracts.UseVisualStyleBackColor = true;
            btnContracts.Click += new EventHandler((o, e) => FmOrgContracts.Open(Agent));

            panel3.Controls.Add(btnContracts);
        }
    }
}