using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class TargetControl : UserControl, DataObjectViewer
   {
      public TargetControl()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         Target t = dataObject as Target;

         label1.Text = String.Format("{0:dd.MM.yyyy} {1} Статус: {2}", t.date,  t.remark, t.closed == 0 ? "Не закрыта" : "Закрыта");
      }
   }
}
