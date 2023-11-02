using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public class UserFormEx : UserForm
    {
        public UserFormEx(Divisions owner) :
           base(owner)
        {
        }

        protected override void AdjustForm()
        {
            base.AdjustForm();

            Button refButton = btnOrgLocation == null ? btnEditRoute : btnOrgLocation;

            Button mtxBtn = new Button();
            mtxBtn.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            Point p = new Point(refButton.Left, refButton.Top);
            p.Offset(refButton.Width + 2, 0);
            mtxBtn.Location = p;
            mtxBtn.Name = "addOrgButton";
            mtxBtn.Size = new System.Drawing.Size(150, btnEditRoute.Height);
            mtxBtn.TabIndex = 2;
            mtxBtn.Text = "Загрузить...";
            mtxBtn.UseVisualStyleBackColor = true;
            mtxBtn.Click += new EventHandler((o, e) =>
            {
                FmOrgLoad f = new FmOrgLoad();
                f.SetData(Agent, this);
                f.Show();
            });

            panel3.Controls.Add(mtxBtn);
            userDetails.TabPages.Remove(udMatrix);
            //udMatrix.Hide();
        }

        public void OnLoadOrgs()
        {
            Invoke(new InvokeDelegate(
            delegate
            {
                GetDataForCurAgent(Agent.id);
            }));

        }
    }
}
