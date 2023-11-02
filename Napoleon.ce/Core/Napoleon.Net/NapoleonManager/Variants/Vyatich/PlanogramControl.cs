using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;

namespace GRSoft.NapoleonManager
{
    public partial class PlanogramControl : UserControl, DataObjectViewer
    {
        public PlanogramControl()
        {
            InitializeComponent();
        }

        public void SetData(Network.DataObject dataObject)
        {
            PlanogramEdit doc = (PlanogramEdit)dataObject;
            label1.Text = "Просмотрена планограмма: " + doc.planogramTitle;
           
            DataSet<string, Planograms> ds = FmDetailEx.planograms;

            if (ds != null && ds.ContainsKey(doc.planogram)) 
            {
               Planograms p = ds[doc.planogram];

               if (p.photo != null)
               {
                  using (var ms = new MemoryStream(p.photo))
                  {
                     pictureBox1.Image = Image.FromStream(ms);
                  }
               }
            }
        }
    }
}
