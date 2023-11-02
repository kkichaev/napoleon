using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
    public partial class MerchDetail : UserControl, DataObjectViewer
    {
        public MerchDetail()
        {
            InitializeComponent();
        }

        public void SetData(Network.DataObject dataObject)
        {
            MerchDoc doc = (MerchDoc)dataObject;
            dgvItems.DataSource = doc.items;
        }
    }
}
