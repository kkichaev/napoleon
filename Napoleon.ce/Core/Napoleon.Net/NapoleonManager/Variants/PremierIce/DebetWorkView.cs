using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class DebetWorkView : UserControl, DataObjectViewer
    {
        public DebetWorkView()
        {
            InitializeComponent();
        }

        public void SetData(Network.DataObject dataObject)
        {
            DebetWork f = dataObject as DebetWork;

            if (f != null)
            {
                label1.Text = f.remark;
            }
        }
    }
}
