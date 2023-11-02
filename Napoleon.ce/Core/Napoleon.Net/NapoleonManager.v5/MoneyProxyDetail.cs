using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class MoneyProxyDetail : UserControl, DataObjectViewer
    {
        public MoneyProxyDetail()
        {
            InitializeComponent();
        }

        public void SetData(GRSoft.Network.DataObject dataObject)
        {
            MoneyProxy mp = dataObject as MoneyProxy;
            lblText.Text = "Сумма доверенности " + mp.Sum().ToString("C");
        }
    }
}
